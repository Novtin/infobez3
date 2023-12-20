package javaClasses.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import javaClasses.DiffieHellman;
import javaClasses.entity.Client;
import javaClasses.entity.Human;
import javaClasses.service.HumanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigInteger;


@Controller
@RequestMapping("/menu")
public class MenuController {
    private final HumanService humanService;

    @Autowired
    public MenuController(HumanService humanService) {
        this.humanService = humanService;
    }

    @GetMapping("/")
    public String showMenu(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if((Client) session.getAttribute("user") != null) {
            return "index";
        } else {
            System.out.println("Клиента нет в сессии");
            return "redirect:/user/login";
        }
    }

    @GetMapping("/table")
    public String showTable(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if((Client) session.getAttribute("user") != null) {
            model.addAttribute("humanList", humanService.show());
            return "table";
        } else {
            System.out.println("Клиента нет в сессии");
            return "redirect:/user/login";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, HttpServletRequest request){
        HttpSession session = request.getSession();
        if((Client) session.getAttribute("user") != null) {
            if (humanService.delete(id) == 0) {
                System.out.println("Ошибка удаления");
            }
            return "index";
        } else {
            System.out.println("Клиента нет в сессии");
            return "redirect:/user/login";
        }
    }

    @GetMapping("/save")
    public String saveGet(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if((Client) session.getAttribute("user") != null) {
            model.addAttribute("human", new Human());
            return "save";
        } else {
            System.out.println("Клиента нет в сессии");
            return "redirect:/user/login";
        }
    }

    @PostMapping("/save")
    public String savePost(@ModelAttribute @Valid Human human,
                           @RequestParam(value = "openKeyClient") String openKeyClient,
                           HttpServletRequest request) {
        HttpSession session = request.getSession();
        BigInteger secretKeyServer = (BigInteger) session.getAttribute("secretKeyServer");
        BigInteger P = (BigInteger) session.getAttribute("P");
        String secretCommonKey = DiffieHellman.getCommonSecretKey(new BigInteger(openKeyClient), secretKeyServer, P)
                .toString();
        if (humanService.save(human, secretCommonKey) == 0){
            System.out.println("Ошибка сохранения");
        }
        return "redirect:/menu/";
    }

    @GetMapping("/profile")
    public String profileGet(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("user");
        if (client != null) {
            model.addAttribute("client", client);
            return "profile";
        } else {
            System.out.println("Клиента нет в сессии");
            return "redirect:/user/login";
        }
    }


}
