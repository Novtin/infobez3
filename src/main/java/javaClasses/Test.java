package javaClasses;

import java.math.BigInteger;
import java.util.List;

public class Test {
    public String test(){
        List<BigInteger> list = RSA.getKeys();
        BigInteger e = list.get(0);
        BigInteger s = list.get(1);
        BigInteger n = list.get(2);
        List<List<BigInteger>> encryptText = RSA.encryptText("hello", s, n);
        System.out.println(encryptText.toString());
        return "";
    }
}
