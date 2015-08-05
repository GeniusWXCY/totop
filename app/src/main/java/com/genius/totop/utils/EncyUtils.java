package com.genius.totop.utils;

import com.google.gson.Gson;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 加密相关的工具类
 */
public class EncyUtils {

    /**
     * 对数据进行加密
     * @param data
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String ency(String data) {

        try {
            String key = Constants.DES_KEY;
            Cipher cipher = Cipher.getInstance("DES");
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = secretKeyFactory.generateSecret(new DESKeySpec(
                    key.getBytes()));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new SecureRandom());
            byte[] encyData = cipher.doFinal(data.getBytes());
            return binToHex(encyData);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static String ency(long data) {
        return ency(String.valueOf(data));
    }

    private static String binToHex(byte[] md) {
        StringBuffer sb = new StringBuffer("");
        int read = 0;
        for (int i = 0; i < md.length; i++) {
            read = md[i];
            if (read < 0)
                read += 256;
            if (read < 16)
                sb.append("0");
            sb.append(Integer.toHexString(read));
        }
        return sb.toString();
    }
}
