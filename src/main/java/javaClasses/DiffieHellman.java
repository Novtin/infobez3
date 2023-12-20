package javaClasses;

import org.mindrot.jbcrypt.BCrypt;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.util.*;

public class DiffieHellman {
    public static BigInteger randBetween(BigInteger min, BigInteger max) {
        Random rand = new Random();
        BigInteger range = max.subtract(min).add(BigInteger.ONE);
        if (range.signum() <= 0) {
            throw new IllegalArgumentException("Минимальное значение должно быть меньше или равно максимальному");
        }
        BigInteger result = new BigInteger(range.bitLength(), rand);

        if (result.compareTo(range) >= 0) {
            result = result.mod(range).add(min);
        } else {
            result = result.add(min);
        }

        return result;
    }
    public static BigInteger getPrimeNumber() {
        BigInteger min = BigInteger.TEN.pow(15);
        BigInteger max = new BigInteger("9".repeat(16));
        BigInteger randomNumber = randBetween(min, max);

        if (randomNumber.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            randomNumber = randomNumber.add(BigInteger.ONE);
        }

        while (true) {
            if (randomNumber.isProbablePrime(100)
                    && (randomNumber.subtract(BigInteger.ONE).divide(BigInteger.TWO))
                    .isProbablePrime(100)) {
                return randomNumber;
            }
            randomNumber = randomNumber.add(BigInteger.valueOf(2));
        }
    }

    public static BigInteger getPrimitiveRoot(BigInteger p) {
        if (!p.isProbablePrime(100)) {
            throw new IllegalArgumentException("p must be a prime number");
        }

        BigInteger phi = p.subtract(BigInteger.ONE);
        List<BigInteger> factors = factorize(phi);

        for (BigInteger g = BigInteger.TWO; g.compareTo(phi) < 0; g = g.add(BigInteger.ONE)) {
            boolean isPrimitiveRoot = true;
            if (g.modPow(phi.divide(BigInteger.TWO), p).equals(p.subtract(BigInteger.ONE))) {
                for (BigInteger factor : factors) {
                    if (g.modPow(phi.divide(factor), p).equals(BigInteger.ONE)) {
                        isPrimitiveRoot = false;
                        break;
                    }
                }

                if (isPrimitiveRoot && g.isProbablePrime(100)) {
                    return g;
                }
            }
        }

        throw new IllegalStateException("Primitive root not found");
    }

    public static List<BigInteger> factorize(BigInteger n)
    {
        BigInteger two = BigInteger.valueOf(2);
        List<BigInteger> factors = new ArrayList<>();

        while (n.mod(two).equals(BigInteger.ZERO))
        {
            factors.add(two);
            n = n.divide(two);
        }

        if (n.compareTo(BigInteger.ONE) > 0)
        {
            BigInteger f = BigInteger.valueOf(3);
            while (f.multiply(f).compareTo(n) <= 0)
            {
                if (n.mod(f).equals(BigInteger.ZERO))
                {
                    factors.add(f);
                    n = n.divide(f);
                }
                else
                {
                    f = f.add(two);
                }
            }
            factors.add(n);
        }

        return factors;
    }

    public static BigInteger getP(){
        return getPrimeNumber();
    }

    public static BigInteger getG(BigInteger p){
        return getPrimitiveRoot(p);
    }

    public static BigInteger getSecretKey(){
        return getPrimeNumber();
    }

    public static BigInteger getOpenKey(BigInteger p, BigInteger g, BigInteger secretKey){
        return g.modPow(secretKey, p);
    }

    public static BigInteger getCommonSecretKey(BigInteger openKey, BigInteger secretKey, BigInteger p){
        return openKey.modPow(secretKey, p);
    }

    public static String decrypt(String encryptedMessage, String secretKeyBytes) throws Exception {
        while (secretKeyBytes.getBytes().length % 16 != 0){
            secretKeyBytes = "0".concat(secretKeyBytes);
        }
        SecretKey key = new SecretKeySpec(
                secretKeyBytes.getBytes(), "AES");
        AlgorithmParameterSpec iv = new IvParameterSpec(
                "0000000000000000".getBytes("UTF-8"));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes, "UTF-8");
    }

    public static String hashMessage(String plainText) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(plainText, salt);
    }

    public static boolean checkMessage(String enteredMessage, String hashedMessage) {
        return BCrypt.checkpw(enteredMessage, hashedMessage);
    }

}
