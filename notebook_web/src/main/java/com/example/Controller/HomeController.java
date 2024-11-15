package com.example.Controller;

import com.example.Model.*;
import com.example.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/admin/home")
    public String admin() {
        return "admin";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model){
        if (error != null) {
            model.addAttribute("loginError", "Hibás email cím vagy jelszó");
        }
        return "login";
    }


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("reg", new User());
        return "register";
    }

    @Autowired
    private UserRepository userRepo;
    @PostMapping("/register_process")
    public String Register(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
        for(User felhasznalo2: userRepo.findAll())
            if(felhasznalo2.getEmail().equals(user.getEmail())){
                redirectAttributes.addFlashAttribute("regMessage", "A megadott Email-cím már foglalt!");
                return "redirect:/register";
            }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Regisztrációkor minden felhasználónak USER szerepet adunk:
        user.setRole("ROLE_USER");
        userRepo.save(user);
        model.addAttribute("id", user.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres regisztráció! Kérjük jelentkezzen be!");
        return "redirect:/login";
    }


    @GetMapping("/contact")
    public String contactForm(Model model) {// Model model: Dependency injection
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ellenőrzés, hogy a felhasználó be van-e jelentkezve és nem anonim
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String email = authentication.getName(); // Ha a felhasználó email címként van azonosítva
            model.addAttribute("userEmail", email);
        } else {
            model.addAttribute("userEmail", ""); // Üres, ha nincs bejelentkezve
        }

        model.addAttribute("attr1", new NewContact());
        return "contact";
    }
    @Autowired
    private NewContactRepository newContactRepo;
    @PostMapping("/contactResult")
    public String contactSubmit(@ModelAttribute NewContact msg, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("attr2", msg);

        if(SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser"))
        {
            msg.setName("Guest");
        }
        else
        {
            for(User felhasznalo2: userRepo.findAll()){
                if(felhasznalo2.getEmail().equals(msg.getEmail())){
                    msg.setName(felhasznalo2.getName());
                }
            }
        }

        // Űrlap mentése a databasebe
        newContactRepo.save(msg);
        redirectAttributes.addFlashAttribute("successContact", "Válaszát sikeresen elküldtük!");
        return "redirect:/contact";
    }
    @Autowired
    private ContactRepository contactRepo;
    @GetMapping("/messages")
    public String getContactResult(Model model){

        try {
            // Adatok lekérdezése az adatbázisból
            List<Contact> contacts = StreamSupport.stream(contactRepo.findAll().spliterator(), false).toList();

            if (contacts != null && !contacts.isEmpty()) {
                // Csak nem üres lista esetén adjuk hozzá a modellhez
                List<Contact> sortedContacts = contacts.stream()
                        .sorted((c1, c2) -> c2.getCreated_at().compareTo(c1.getCreated_at()))
                        .collect(Collectors.toList());

                model.addAttribute("Contacts", sortedContacts);
            } else {
                model.addAttribute("Contacts", List.of()); // Üres lista, ha nincs adat
            }
        } catch (Exception e) {
            // Hiba esetén logolás és üres lista hozzáadása
            System.err.println("Hiba történt a /messages elérésénél: " + e.getMessage());
            model.addAttribute("Contacts", List.of());
        }

        return "/contactList";
    }

    @Autowired
    private NotebookRepository notebookRepository;
    @GetMapping("/products")
    public String products(Model model) {
        // Összes notebook lekérdezése
        List<Notebook> notebooks = StreamSupport
                .stream(notebookRepository.findAll().spliterator(), false)
                .toList();

        // Ellenőrizzük, hogy van-e adat
        if (notebooks.isEmpty()) {
            model.addAttribute("Products", List.of()); // Üres lista, ha nincs adat
            return "products";
        }

        // Véletlen notebookok kiválasztása
        List<Notebook> randomNotebooks = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            int randomIndex = (int) (Math.random() * notebooks.size());
            randomNotebooks.add(notebooks.get(randomIndex));
        }

        // Adatok átadása a modellnek
        model.addAttribute("Products", randomNotebooks);
        return "products";
    }



}


