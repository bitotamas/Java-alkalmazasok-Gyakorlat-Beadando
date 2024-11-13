package com.example.notebook_web;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
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
}


