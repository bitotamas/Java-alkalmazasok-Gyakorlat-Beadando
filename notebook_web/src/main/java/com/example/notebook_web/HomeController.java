package com.example.notebook_web;

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


@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index";
    }


    @GetMapping("/products")
    public String products() {
        return "products";
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
}


