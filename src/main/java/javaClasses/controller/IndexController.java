package javaClasses.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/")
    public String showMenu(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("user") != null) {
            return "redirect:/menu/";
        } else {
            System.out.println("Клиента нет в сессии");
            return "redirect:/user/login";
        }
    }
}
