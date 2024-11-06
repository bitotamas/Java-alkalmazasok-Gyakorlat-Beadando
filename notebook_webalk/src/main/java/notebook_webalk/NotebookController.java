package notebook_webalk;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotebookController {
    @GetMapping("/home")
    public String notebookHome(Model model) { // Model model: Dependency injection
        model.addAttribute("attr1", new notebookClass());
        return "home";
    }
}
