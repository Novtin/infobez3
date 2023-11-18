package javaClasses;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RSA {
    private static final int blockBytes = 15;
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
        BigInteger min = BigInteger.TEN.pow(18);
        BigInteger max = new BigInteger("9".repeat(19));
        BigInteger randomNumber = randBetween(min, max);

        if (randomNumber.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            randomNumber = randomNumber.add(BigInteger.ONE);
        }

        while (true) {
            if (randomNumber.isProbablePrime(100)) {
                return randomNumber;
            }
            randomNumber = randomNumber.add(BigInteger.valueOf(2));
        }
    }
    public static BigInteger findModularInverse(BigInteger s, BigInteger n) {
        BigInteger newr = s;
        BigInteger r = n;
        BigInteger t = BigInteger.ZERO;
        BigInteger newt = BigInteger.ONE;

        while (newr.compareTo(BigInteger.ZERO) != 0) {
            BigInteger quotient = r.divide(newr);
            BigInteger temp = newt;
            newt = t.subtract(quotient.multiply(newt));
            t = temp;
            temp = newr;
            newr = r.subtract(quotient.multiply(newr));
            r = temp;
        }

        if (r.compareTo(BigInteger.ONE) > 0) {
            return null;
        }

        if (t.compareTo(BigInteger.ZERO) < 0) {
            t = t.add(n);
        }

        return t;
    }

    public static List<BigInteger> getKeys() {
        BigInteger q = getPrimeNumber();
        System.out.println("Q: " + q);
        BigInteger p = getPrimeNumber();
        System.out.println("P: " + p);
        BigInteger n = p.multiply(q);
        System.out.println("N: " + n);
        p = p.subtract(BigInteger.ONE);
        q = q.subtract(BigInteger.ONE);
        BigInteger d = p.multiply(q);
        System.out.println("D: " + d);

        BigInteger s = randBetween(BigInteger.valueOf(2), d.subtract(BigInteger.ONE));
        while (!d.gcd(s).equals(BigInteger.ONE)) {
            s = randBetween(BigInteger.valueOf(2), d.subtract(BigInteger.ONE));
        }
        System.out.println("S: " + s);
        BigInteger e = findModularInverse(s, d);
        System.out.println("E: " + e);
        assert e != null;
        BigInteger result = s.mod(d).multiply(e.mod(d)).mod(d);
        System.out.println("E * S mod N  = " + result);
        List<BigInteger> keys = new ArrayList<>();
        keys.add(e);
        keys.add(s);
        keys.add(n);
        return keys;
    }

    public static List<List<BigInteger>> encryptBlocks(List<List<Byte>> blocks, BigInteger s, BigInteger n) {
        List<List<BigInteger>> blocksCoded = new ArrayList<>();

        for (List<Byte> element : blocks) {
            List<BigInteger> oneBlock = new ArrayList<>();

            for (Byte el : element) {
                oneBlock.add(new BigInteger(String.valueOf(el)).modPow(s, n));
            }

            blocksCoded.add(oneBlock);
        }
        return blocksCoded;
    }

    public static List<List<BigInteger>> decryptBlocks(List<List<BigInteger>> blocks, BigInteger e, BigInteger n) {
        List<List<BigInteger>> blocksCoded = new ArrayList<>();

        for (List<BigInteger> element : blocks) {
            List<BigInteger> oneBlock = new ArrayList<>();

            for (BigInteger el : element) {
                oneBlock.add(el.modPow(e, n));
            }

            blocksCoded.add(oneBlock);
        }
        return blocksCoded;
    }

    public static List<List<Byte>> makeByteBlocks(byte[] textBytes) {
        List<List<Byte>> blocks = new ArrayList<>();
        int i = 0;

        while (i < textBytes.length) {
            int end = i + blockBytes;
            if (end > textBytes.length) {
                end = textBytes.length;
            }
            List<Byte> block = new ArrayList<>();

            for (int j = i; j < end; j++) {
                block.add(textBytes[j]);
            }

            while (block.size() < 15) {
                block.add((byte) 0);
            }

            blocks.add(block);
            i = end;
        }

        return blocks;
    }

    public static List<List<BigInteger>> encryptText(String plainText, BigInteger s, BigInteger n) {
        byte[] textBytes = plainText.getBytes(StandardCharsets.UTF_8);
        return encryptBlocks(makeByteBlocks(textBytes), s, n);
    }

    public static String decryptText(List<List<BigInteger>> blocksCoded, BigInteger e, BigInteger n) {
        List<List<BigInteger>> blocksDecoded = decryptBlocks(blocksCoded, e, n);
        System.out.println(blocksDecoded);
        List<byte[]> resultByte = new ArrayList<>();
        for (List<BigInteger> list : blocksDecoded) {
            for (BigInteger value : list) {
                resultByte.add(value.toByteArray());
            }
        }
        List<Byte> oneList = new ArrayList<>();
        System.out.println(resultByte);
        for (byte[] el: resultByte) {
            for (byte element: el) {
                oneList.add(element);
            }
        }
        byte[] listByte = new byte[oneList.size()];
        for (int i = 0; i < oneList.size(); i++){
            listByte[i] = oneList.get(i);
        }
        for (int i = listByte.length - 1; i > 0; i--){
            if (listByte[i] != (byte) 0){
                byte[] result = new byte[i + 1];
                System.arraycopy(listByte, 0, result, 0, i + 1);
                return new String(result);
            }
        }
        return String.valueOf((byte) 0);
    }

    public static List<List<BigInteger>> makeIntBlocks(String textInput){
        String[] listText = textInput.split(",");
        List<BigInteger> listBigInt = new ArrayList<>();
        for (String str: listText) {
            listBigInt.add(new BigInteger(str));
        }
        List<List<BigInteger>> blocks = new ArrayList<>();
        for (int i = 0; i < listBigInt.size(); i += blockBytes){
            blocks.add(listBigInt.subList(i, i + blockBytes));
            while (blocks.get(blocks.size() - 1).size() < 15){
                blocks.get(blocks.size() - 1).add(new BigInteger(String.valueOf(0)));
            }
        }
        return blocks;
    }

}
