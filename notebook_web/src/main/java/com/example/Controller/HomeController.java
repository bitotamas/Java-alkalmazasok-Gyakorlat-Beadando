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
                redirectAttributes.addFlashAttribute("regError", "A megadott Email-cím már foglalt!");
                return "redirect:/register";
            }else if(felhasznalo2.getName().equals(user.getName())){
                redirectAttributes.addFlashAttribute("regError", "A megadott név már foglalt!");
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

        model.addAttribute("attr1", new Contact());
        return "contact";
    }
    @Autowired
    private ContactRepository contactRepo;
    @PostMapping("/contactResult")
    public String contactSubmit(@ModelAttribute Contact msg, Model model, RedirectAttributes redirectAttributes) {
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
        contactRepo.save(msg);
        redirectAttributes.addFlashAttribute("successContact", "Válaszát sikeresen elküldtük!");
        return "redirect:/contact";
    }

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
    private CPURepository cpuRepo;
    @Autowired
    private OSRepository osRepo;

    @Autowired
    private NotebookRepository notebookRepository;
    @GetMapping("/products")
    public String products(Model model) {
        // Összes notebook, cpu és oprendszer lekérdezése
        List<Notebook> notebooks = StreamSupport
                .stream(notebookRepository.findAll().spliterator(), false)
                .toList();
        List<CPU> cpus = StreamSupport
                .stream(cpuRepo.findAll().spliterator(), false)
                .toList();
        List<OS> oss = StreamSupport
                .stream(osRepo.findAll().spliterator(), false)
                .toList();

        // Ellenőrizzük, hogy van-e adat
        if (notebooks.isEmpty()) {
            model.addAttribute("Products", List.of()); // Üres lista, ha nincs adat
            return "products";
        }

        // Véletlen notebookok kiválasztása
        List<Notebook> randomNotebooks = new ArrayList<>();
        List<CPU> randomNotebooks_cpu = new ArrayList<>();
        List<OS> randomNotebooks_os = new ArrayList<>();
        int index=0;
        int randomIndex=0;
        do {
            randomIndex = (int) (Math.random() * notebooks.size());
            //Csak azok a notebookok kiválasztása, amikből raktáron nem 0db található
            if(notebooks.get(randomIndex).getDb()!=0)
            {

                for(var item : cpus){
                    if(notebooks.get(randomIndex).getProcesszorid().equals(item.getId()))
                    {
                        randomNotebooks_cpu.add(item);
                    }
                }
                for(var item : oss){
                    if(notebooks.get(randomIndex).getOprendszerid().equals(item.getId()))
                    {
                        randomNotebooks_os.add(item);
                    }
                }

                randomNotebooks.add(notebooks.get(randomIndex));
                index++;

            }
        }while(index<16);

        // Adatok átadása a modelleknek
        model.addAttribute("Products", randomNotebooks);
        model.addAttribute("CPUs", randomNotebooks_cpu);
        model.addAttribute("OSs", randomNotebooks_os);
        return "products";
    }



}


