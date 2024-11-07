// HomeController.java
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";  // főoldal (index.html)
    }

    @GetMapping("/login")
    public String login() {
        return "login";  // bejelentkezési oldal (login.html)
    }
}
