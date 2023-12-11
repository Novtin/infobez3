package javaClasses;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RSA {
    private static final int encryptBlockFile = 14;
    private static final int decryptBlockFile = 17;
    private static final int blockReadText = 15;
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
        BigInteger result = (s.multiply(e)).mod(d);
        System.out.println("E * S mod D  = " + result);
        List<BigInteger> keys = new ArrayList<>();
        keys.add(e);
        keys.add(s);
        keys.add(n);
        return keys;
    }

    public static List<BigInteger> encryptBlocks(List<List<Byte>> blocks, BigInteger s, BigInteger n) {
        List<BigInteger> blocksCoded = new ArrayList<>();
        for (List<Byte> list : blocks) {
            byte[] byteArray = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) {
                byteArray[i] = list.get(i);
            }
            BigInteger value = new BigInteger(1, byteArray);
            value = value.modPow(s, n);
            blocksCoded.add(value);
        }
        return blocksCoded;
    }

    public static List<BigInteger> decryptBlocks(List<BigInteger> blocks, BigInteger e, BigInteger n) {
        List<BigInteger> blocksCoded = new ArrayList<>();
        for (BigInteger element : blocks) {
            element = element.modPow(e, n);
            byte[] bytes = deleteStartZero(element.toByteArray());
            blocksCoded.add(new BigInteger(bytes));
        }
        return blocksCoded;
    }

    public static List<List<Byte>> makeByteBlocks(byte[] textBytes, int size) {
        List<List<Byte>> blocks = new ArrayList<>();
        int i = 0;

        while (i < textBytes.length) {
            int end = Math.min(i + size, textBytes.length);
            List<Byte> block = new ArrayList<>();

            for (int j = i; j < end; j++) {
                block.add(textBytes[j]);
            }
            blocks.add(block);
            i = end;
        }

        return blocks;
    }

    public static List<BigInteger> encryptText(String plainText, BigInteger s, BigInteger n) {
        byte[] textBytes = plainText.getBytes(StandardCharsets.UTF_8);
        return encryptBlocks(makeByteBlocks(textBytes, blockReadText), s, n);
    }

    public static String decryptText(List<BigInteger> blocksCoded, BigInteger e, BigInteger n) {
        List<BigInteger> blocksDecoded = decryptBlocks(blocksCoded, e, n);
        byte[] listByte = listBigIntToBytes(blocksDecoded);
        return new String(listByte, StandardCharsets.UTF_8);
    }

    public static byte[] listBigIntToBytes(List<BigInteger> bigIntegerList){
        List<byte[]> resultByte = new ArrayList<>();
        for (BigInteger value : bigIntegerList) {
            resultByte.add(value.toByteArray());
        }
        List<Byte> oneList = new ArrayList<>();
        for (byte[] el: resultByte) {
            for (byte element: el) {
                oneList.add(element);
            }
        }
        byte[] listByte = new byte[oneList.size()];
        for (int i = 0; i < oneList.size(); i++){
            listByte[i] = oneList.get(i);
        }
        return listByte;
    }

    public static boolean checkAllZerosAndOne(byte[] bytes){
        for (int i = 0; i < bytes.length; i++){
            if (i == bytes.length - 1 && bytes[i] == (byte) 1){
                return true;
            }
            if (bytes[i] != (byte) 0){
                return false;
            }
        }
        return true;
    }

    public static byte[] encryptFileBlock(byte[] bytes, BigInteger s, BigInteger n){
        int countZeros = 0;
        for (byte element: bytes){
            if (element == (byte) 0){
                countZeros++;
            } else {
                break;
            }
        }
        BigInteger value = new BigInteger(1, bytes);
        value = value.modPow(s, n);
        byte[] valueBytes = value.toByteArray();
        byte[] result = new byte[valueBytes.length + 1];
        System.arraycopy(valueBytes, 0, result, 0, valueBytes.length);
        result[result.length - 1] = (byte) countZeros;
        if (result.length < decryptBlockFile){
            byte[] newBytes = new byte[decryptBlockFile];
            System.arraycopy(result, 0, newBytes, newBytes.length - result.length, result.length);
            result = newBytes;
        }
        return result;
    }

    public static byte[] decryptFileBlock(byte[] bytes, BigInteger e, BigInteger n){
        int countZeros = bytes[bytes.length - 1];
        byte[] newBytes = new byte[bytes.length - 1];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length - 1);
        BigInteger value = new BigInteger(newBytes);
        value = value.modPow(e, n);
        byte[] valueBytes = deleteStartZero(value.toByteArray());
        if (countZeros > 0){
            newBytes = new byte[countZeros + valueBytes.length];
            System.arraycopy(valueBytes, 0, newBytes, countZeros, valueBytes.length);
            valueBytes = newBytes;
        }
        return valueBytes;
    }

    public static byte[] deleteStartZero(byte[] bytes){
        if (bytes.length > 1 && bytes[0] == (byte) 0 && bytes[1] < (byte) 0){
            byte[] valueBytesWithoutZero = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, valueBytesWithoutZero, 0, bytes.length - 1);
            bytes = valueBytesWithoutZero;
        }
        return bytes;
    }

    public static int encryptFile(MultipartFile fileInput, BigInteger s, BigInteger n, File fileOutput) {
        try (
                InputStream inputStream = fileInput.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(fileOutput, true)
        ) {
            byte[] buffer = new byte[Math.min(inputStream.available(), encryptBlockFile)];
            while (inputStream.read(buffer) != -1) {
                if (buffer.length < encryptBlockFile && checkAllZerosAndOne(buffer)){
                    outputStream.write(buffer, 0, buffer.length);
                    break;
                }
                if (checkAllZerosAndOne(buffer)){
                    byte[] newBuffer = new byte[decryptBlockFile];
                    System.arraycopy(buffer, 0, newBuffer, 3, buffer.length);
                    outputStream.write(newBuffer, 0, newBuffer.length);
                } else {
                    byte[] bytes = encryptFileBlock(buffer, s, n);
                    outputStream.write(bytes, 0, bytes.length);
                }
                buffer = new byte[Math.min(inputStream.available(), encryptBlockFile)];
                if (buffer.length == 0){
                    break;
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            return 0;
        }
        return 1;
    }

    public static int decryptFile(MultipartFile fileInput, BigInteger e, BigInteger n, File fileOutput) {
        try (
                InputStream inputStream = fileInput.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(fileOutput, true)
        ) {
            byte[] buffer = new byte[decryptBlockFile];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (bytesRead < decryptBlockFile){
                    byte[] newBytes = new byte[bytesRead];
                    System.arraycopy(buffer, 0, newBytes, 0, bytesRead);
                    if (checkAllZerosAndOne(newBytes)) {
                        outputStream.write(newBytes, 0, bytesRead);
                        break;
                    }
                }
                if (checkAllZerosAndOne(buffer)){
                    byte[] newBuffer = new byte[encryptBlockFile];
                    System.arraycopy(buffer, 3, newBuffer, 0, buffer.length - 3);
                    outputStream.write(newBuffer, 0, newBuffer.length);
                } else {
                    byte[] result = decryptFileBlock(buffer, e, n);
                    outputStream.write(result, 0, result.length);
                }
                buffer = new byte[decryptBlockFile];
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            return 0;
        }
        return 1;
    }
}