package com.aaron.passwordlist.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class SymEncrypt {

    public static Key getKey(byte[] arrBTmp, String alg) {
        if (!(alg.equals("DES") || alg.equals("DESede") || alg.equals("AES"))) {
            System.out.println("alg type not find: " + alg);
            return null;
        }
        byte[] arrB;
        if (alg.equals("DES")) {
            arrB = new byte[8];
        } else if (alg.equals("DESede")) {
            arrB = new byte[24];
        } else {
            arrB = new byte[16];
        }
        int i = 0;
        int j = 0;
        while (i < arrB.length) {
            if (j > arrBTmp.length - 1) {
                j = 0;
            }
            arrB[i] = arrBTmp[j];
            i++;
            j++;
        }
        Key key = new javax.crypto.spec.SecretKeySpec(arrB, alg);
        return key;
    }

    /**
     * 加密
     *
     * @param s      待加密的字符串
     * @param strKey key
     * @param alg    加密类型 （DES，DESede，AES）
     * @return
     */
    public static String encrypt(String s, String strKey, String alg) {
        if (!(alg.equals("DES") || alg.equals("DESede") || alg.equals("AES"))) {
            System.out.println("alg type not find: " + alg);
            return null;
        }
        byte[] r = null;
        try {
            Key key = getKey(strKey.getBytes(), alg);
            Cipher c;
            c = Cipher.getInstance(alg);
            c.init(Cipher.ENCRYPT_MODE, key);
            r = c.doFinal(s.getBytes("utf-8"));
            //System.out.println("加密后的二进串:" + FileDigest.byte2Str(r));
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(r, Base64.DEFAULT);//此处使用BASE64做转码。;
    }

    /**
     * 解密
     *
     * @param code   待解密的byte数组
     * @param strKey key
     * @param alg    加密类型 （DES，DESede，AES）
     * @return
     */
    public static String decrypt(String code, String strKey, String alg) {
        if (!(alg.equals("DES") || alg.equals("DESede") || alg.equals("AES"))) {
            System.out.println("alg type not find: " + alg);
            return null;
        }
        String r = null;
        try {
            Key key = getKey(strKey.getBytes(), alg);
            Cipher c;
            c = Cipher.getInstance(alg);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] encrypted1 = Base64.decode(code, Base64.DEFAULT);//先用base64解密
            byte[] clearByte = c.doFinal(encrypted1);
            r = new String(clearByte,"utf-8");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            System.out.println("not padding");
            r = null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //System.out.println("解密后的信息:"+r);
        return r;
    }

}
