package com.doe.topwisesdk.domain.doecard.samndfelica.encryptiondecryption;

import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.doe.topwisesdk.domain.doecard.samndfelica.Utils.bytesToHexString;


/**
 * Created by eshantmittal on 08/12/17.
 */

public class AES {

    /**
     * Decryption CBC Mode
     *
     * @param data
     * @param key
     * @param ivs
     * @return
     */
    public byte[] decryptCBC(byte[] data, byte[] key, byte[] ivs) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            byte[] finalIvs = new byte[16];
            int len = ivs.length > 16 ? 16 : ivs.length;
            System.arraycopy(ivs, 0, finalIvs, 0, len);
            IvParameterSpec ivps = new IvParameterSpec(finalIvs);
            Log.e("CBC Decrypt IVPS", bytesToHexString(ivps.getIV()));
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivps);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Encryption CBC Mode
     *
     * @param data
     * @param key
     * @param ivs
     * @return
     */
    public byte[] encryptCBC(byte[] data, byte[] key, byte[] ivs) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            byte[] finalIvs = new byte[16];
            int len = ivs.length > 16 ? 16 : ivs.length;
            System.arraycopy(ivs, 0, finalIvs, 0, len);
            IvParameterSpec ivps = new IvParameterSpec(finalIvs);
            Log.e("CBC Encrypt IVPS", bytesToHexString(ivps.getIV()));
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivps);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Decryption CTR Mode
     *
     * @param key
     * @param buffer
     * @param ivData
     * @return
     */
    public byte[] decryptCTR(byte[] key, byte[] buffer, byte[] ivData) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            IvParameterSpec iv;
            SecretKeySpec secret = new SecretKeySpec(key, "AES");
            iv = new IvParameterSpec(ivData);
            Log.e("CTR Decrypt IV", bytesToHexString(iv.getIV()));
            cipher.init(Cipher.DECRYPT_MODE, secret, iv);
            return cipher.doFinal(buffer);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Encryption CTR Mode
     *
     * @param key
     * @param buffer
     * @param ivData
     * @return
     */
    public byte[] encryptCTR(byte[] key, byte[] buffer, byte[] ivData) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            IvParameterSpec iv;
            SecretKeySpec secret = new SecretKeySpec(key, "AES");
            iv = new IvParameterSpec(ivData);
            Log.e("CTR Encrypt IV", bytesToHexString(iv.getIV()));
            cipher.init(Cipher.ENCRYPT_MODE, secret, iv);
            return cipher.doFinal(buffer);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
