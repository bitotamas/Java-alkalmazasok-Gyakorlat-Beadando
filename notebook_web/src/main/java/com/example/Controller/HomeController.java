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

    //location:8080/ útvonal
    @GetMapping("/")
    public String home() {
        return "index";
    }
    //location:8080/admin útvonal
    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("usersList",userRepo.findAll());
        return "admin";
    }
    //location:8080/login útvonal
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model){
        //Ha a felhasználó elrontja az email címet, vagy a jelszót, vagy nem létezik a felhasználó, hibát dob
        if (error != null) {
            model.addAttribute("loginError", "Hibás email cím vagy jelszó");
        }
        return "login";
    }

    //location:8080/register útvonal
    @GetMapping("/register")
    public String register(Model model) {
        //az űrlap ellenőrzött kitöltése után egy reg modelként létre lesz hozva a felhasználó
        model.addAttribute("reg", new User());
        return "register";
    }

    @Autowired
    private UserRepository userRepo;
    @PostMapping("/register_process")
    public String Register(@ModelAttribute User newUser, Model model, RedirectAttributes redirectAttributes) {

        //Összegyűjti az ősszes regisztrált felhasználót
        for(User users: userRepo.findAll())
            //Megnézi a beírt email cím már foglalt-e
            if(users.getEmail().equals(newUser.getEmail())){
                redirectAttributes.addFlashAttribute("regError", "A megadott Email-cím már foglalt!");
                return "redirect:/register";
                //Megnézi a beírt felhasználónév már foglalt-e
            }else if(users.getName().equals(newUser.getName())){
                redirectAttributes.addFlashAttribute("regError", "A megadott név már foglalt!");
                return "redirect:/register";
            }

        //Ha a szűrés sikeres volt titkosítjuk a beírt jelszót
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        // Regisztrációkor minden felhasználónak USER szerepet adunk:
        newUser.setRole("ROLE_USER");

        //Elmentjük az új felhasználót az adatbázisba
        userRepo.save(newUser);
        model.addAttribute("id", newUser.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres regisztráció! Kérjük jelentkezzen be!");
        return "redirect:/login";
    }


    @GetMapping("/contact")
    public String contactForm(Model model) {// Model model: Dependency injection

        //Megnézzük az oldalt használó jelenlegi autentikációs értékét
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ellenőrzés, hogy a felhasználó be van-e jelentkezve és nem anonim
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String email = authentication.getName();

            // Ha be van jelentkezve az oldal használója, autómatikusan ki lesz töltve az email része a contact űrlapnak
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


        //Ha a felhasználó aki az üzenetet írja nincs bejelentkezve, a username Quest értéket fog felvenni
        if(SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser"))
        {
            msg.setName("Guest");
        }
        else
        {
            //Ha pedig be van jelentkezve, kikeresi a felhasználónevét és azt adja át az üzenetben szereplő névnek
            for(User user: userRepo.findAll()){
                if(user.getEmail().equals(msg.getEmail())){
                    msg.setName(user.getName());
                    break;
                }
            }
        }

        // Űrlap adatainak mentése az adatbázisba
        contactRepo.save(msg);
        redirectAttributes.addFlashAttribute("successContact", "Válaszát sikeresen elküldtük!");
        return "redirect:/contact";
    }
    //location:8080/messages útvonal
    @GetMapping("/messages")
    public String getContactResult(Model model){

        try {
            // Adatok lekérdezése az adatbázisból és listába mentése
            List<Contact> contacts = StreamSupport.stream(contactRepo.findAll().spliterator(), false).toList();

            // Csak nem üres lista esetén adjuk hozzá a modellhez
            if (contacts != null && !contacts.isEmpty()) {

                //Beküldési idő szerinti csökkenő sorrendbe rendezés
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

    //location:8080/products útvonal
    @GetMapping("/products")
    public String products(Model model) {

        // Összes notebook, cpu és oprendszer lekérdezése és listákba mentése
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

        //Addig megy a ciklus, ameddig 16 olyan notebookot nem talál, aminél a darabszám nem egyenlő 0-val
        do {
            randomIndex = (int) (Math.random() * notebooks.size());
            //Csak azok a notebookok kiválasztása, amikből raktáron nem 0db található
            if(notebooks.get(randomIndex).getDb()!=0)
            {

                //Minden notebookhoz eltároljuk a hozzá tartozó processzor adatait
                for(var item : cpus){
                    if(notebooks.get(randomIndex).getProcesszorid().equals(item.getId()))
                    {
                        randomNotebooks_cpu.add(item);
                    }
                }
                //Minden notebookhoz eltároljuk a hozzá tartozó operációs rendszer adatait
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


