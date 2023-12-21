package javaClasses.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import javaClasses.DiffieHellman;
import javaClasses.entity.Client;
import javaClasses.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Objects;

@Controller
@RequestMapping("/user")
public class ClientController {
    private final ClientService clientService;
    private final BigInteger P;
    private final BigInteger G;
    private final BigInteger secretKeyServer;
    private final BigInteger openKeyServer;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
        this.P = DiffieHellman.getP();
        System.out.println("P: " + P);
        this.G = DiffieHellman.getG(P);
        System.out.println("G: " + G);
        this.secretKeyServer = DiffieHellman.getSecretKey();
        this.openKeyServer = DiffieHellman.getOpenKey(P, G, secretKeyServer);
        System.out.println("openKeyServer: " + openKeyServer);
        Client client = new Client(DiffieHellman.hashMessage("admin"),
                DiffieHellman.hashMessage("admin"));
        this.clientService.saveAdmin(client);
    }


    @GetMapping("/login")
    public String loginGet(Model model, HttpServletRequest request){
        HttpSession session = request.getSession(true);
        session.setAttribute("P", P);
        session.setAttribute("G", G);
        session.setAttribute("openKeyServer", openKeyServer);
        session.setAttribute("secretKeyServer", secretKeyServer);
        model.addAttribute("client", new Client());
        return "login";
    }

    @GetMapping("/registration")
    public String registrationGet(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute("P", P);
        session.setAttribute("G", G);
        session.setAttribute("openKeyServer", openKeyServer);
        session.setAttribute("secretKeyServer", secretKeyServer);
        model.addAttribute("client", new Client());
        return "registration";
    }

    @PostMapping("/login")
    public String loginPost(@ModelAttribute @Valid Client client,
                            @RequestParam(value = "openKeyClient") String openKeyClient,
                            HttpServletRequest request){
        String secretCommonKey = DiffieHellman.getCommonSecretKey(new BigInteger(openKeyClient), secretKeyServer, P).toString();
        Client clientInput = clientService.login(client, secretCommonKey);
        if (clientInput != null){
            HttpSession session = request.getSession();
            session.setAttribute("user", clientInput);
            if (DiffieHellman.checkMessage("admin", clientInput.getLogin())){
                session.setAttribute("admin", true);
            }
            return "redirect:/menu/";
        } else {
            return "redirect:/user/login?error=true";
        }

    }

    @PostMapping("/registration")
    public String registrationPost(@ModelAttribute @Valid Client client,
                                   @RequestParam(value = "openKeyClient") String openKeyClient){
        String secretCommonKey = DiffieHellman.getCommonSecretKey(new BigInteger(openKeyClient), secretKeyServer, P).toString();
        if (clientService.save(client, secretCommonKey)) {
            return "redirect:/user/login";
        } else {
            return "redirect:/user/registration?error";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        if (session.getAttribute("admin") != null){
            session.removeAttribute("admin");
        }
        return "redirect:/user/login?logout";
    }
}
