package javaClasses;

import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Controller
public class MainController {
    private static BigInteger N;
    private static BigInteger E;
    private static BigInteger S;
    public MainController() {
        List<BigInteger> keys = RSA.getKeys();
        E = keys.get(0);
        S = keys.get(1);
        N = keys.get(2);
    }

    @GetMapping("/")
    public String menu(){
        return "menu";
    }

    @GetMapping("/encryptText")
    public String encryptTextGet(){
        return "encryptText";
    }

    @PostMapping("/encryptText")
    public String encryptTextPost(RedirectAttributes redirectAttributes, @RequestParam(value = "sourceText") String sourceText){
        List<BigInteger> result = RSA.encryptText(sourceText, S, N);
        String editedText = result.stream()
                .map(BigInteger::toString)
                .collect(Collectors.joining(","));

        redirectAttributes.addAttribute("sourceText", sourceText);
        redirectAttributes.addAttribute("editedText", editedText);
        return "redirect:/resultEncrypt";
    }

    @GetMapping("/resultEncrypt")
    public String resultEncryptGet(Model model,
                                   @ModelAttribute("sourceText") String sourceText,
                                   @ModelAttribute("editedText") String editedText) {
        model.addAttribute("sourceText",  sourceText);
        model.addAttribute("text", "Зашифрованный");
        model.addAttribute("editedText",  editedText);
        return "result";
    }

    @GetMapping("/resultDecrypt")
    public String resultDecryptGet(Model model,
                            @ModelAttribute(value = "editedText") String decryptText,
                            @ModelAttribute(value = "sourceText") String sourceText){
        model.addAttribute("sourceText",  sourceText);
        model.addAttribute("text", "Расшифрованный");
        model.addAttribute("editedText", decryptText);
        return "result";
    }

    @GetMapping("/decryptText")
    public String decryptTextGet(){
        return "decryptText";
    }

    @PostMapping("/decryptText")
    public String decryptTextPost(RedirectAttributes redirectAttributes, @RequestParam(value = "sourceText") String textDecrypt){
        redirectAttributes.addAttribute("sourceText", textDecrypt);
        List<BigInteger> bigIntegerList = Arrays.stream(textDecrypt.split(","))
                .map(String::trim)
                .map(BigInteger::new)
                .toList();
        redirectAttributes.addAttribute("editedText", RSA.decryptText(bigIntegerList, E, N));
        return "redirect:/resultDecrypt";
    }

    @GetMapping("/decryptFile")
    public String decryptFileGet(){
        return "decryptFile";
    }

    @GetMapping("/encryptFile")
    public String encryptFileGet(){
        return "encryptFile";
    }

    @PostMapping("/decryptFile")
    public String decryptFilePost(@RequestParam("file") MultipartFile file){
        if (Objects.requireNonNull(file.getOriginalFilename()).matches(".*\\.txt$")){
            File output = new File(file.getOriginalFilename().substring(0, file.getOriginalFilename().length() - 4));
            if (RSA.decryptFile(file, E, N, output) == 1) {
                return "redirect:/decryptFile?process=true";
            } else {
                return "redirect:/decryptFile?error=true";
            }
        }
        return "decryptFile";
    }

    @PostMapping("/encryptFile")
    public String encryptFilePost(@RequestParam("file") MultipartFile file, Model model){
        File output = new File(file.getOriginalFilename() + ".txt");
        if (RSA.encryptFile(file, S, N, output) == 1) {
            return "redirect:/encryptFile?process=true";
        } else {
            return "redirect:/encryptFile?error=true";
        }
    }


}
