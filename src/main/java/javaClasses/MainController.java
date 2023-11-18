package javaClasses;

import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;


@Controller
public class MainController {

    private static final List<BigInteger> keys = RSA.getKeys();
    private static final BigInteger N = keys.get(0);
    private static final BigInteger E = keys.get(1);
    private static final BigInteger S = keys.get(2);

    @GetMapping("/")
    public String menu(){
        return "menu";
    }

    @GetMapping("/encryptText")
    public String encryptTextGet(){
        return "encryptText";
    }

    @PostMapping("/encryptText")
    public String encryptTextPost(Model model, @RequestParam(value = "sourceText") String sourceText){
        model.addAttribute("sourceText", sourceText);
        List<List<BigInteger>> result = RSA.encryptText(sourceText, S, N);
        String resultStr = "";
        for (List<BigInteger> list: result) {
            for (BigInteger value: list) {
                resultStr = resultStr.concat(value.toString() + ",");
            }
        }
        resultStr = resultStr.substring(0, resultStr.length() - 1);
        model.addAttribute("editedText", resultStr);
        return "redirect:/resultEncrypt";
    }

    @GetMapping("/resultEncrypt")
    public String resultEncryptGet(Model model,
                            @ModelAttribute("editedText") String encryptText,
                            @ModelAttribute(value = "sourceText") String sourceText){
        model.addAttribute("sourceText",  sourceText);
        model.addAttribute("text", "Зашифрованный");
        model.addAttribute("editedText", encryptText);
        return "result";
    }

    @GetMapping("/resultDecrypt")
    public String resultDecryptGet(Model model,
                            @ModelAttribute(value = "decryptText") String decryptText,
                            @ModelAttribute(value = "sourceText") String sourceText){
        model.addAttribute("sourceText",  sourceText);
        model.addAttribute("text", "Расшифрованный");
        model.addAttribute("decryptText", decryptText);
        return "result";
    }

    @GetMapping("/decryptText")
    public String decryptTextGet(){
        return "decryptText";
    }

    @PostMapping("/decryptText")
    public String decryptTextPost(Model model, @RequestParam(value = "decryptText") String textDecrypt){
        model.addAttribute("sourceText", textDecrypt);
        List<List<BigInteger>> bigIntegerList = RSA.makeIntBlocks(textDecrypt);
        model.addAttribute("decryptText", RSA.decryptText(bigIntegerList, E, N));
        return "result";
    }

    @GetMapping("/decryptFile")
    public String decryptFileGet(){
        return "decryptFile";
    }

    @GetMapping("/encryptFile")
    public String encryptFileGet(){
        return "encryptFile";
    }


}
