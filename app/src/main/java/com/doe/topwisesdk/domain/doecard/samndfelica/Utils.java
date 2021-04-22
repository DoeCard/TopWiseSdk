package com.doe.topwisesdk.domain.doecard.samndfelica;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by eshantmittal on 18/01/18.
 */

public class Utils {

    public byte[] concatByteArrays(byte[]... byteArrays) throws IOException {

        ByteArrayOutputStream result = new ByteArrayOutputStream();

        for (byte[] byteArray :
                byteArrays) {
            result.write(byteArray);
        }

        return result.toByteArray();

    }

    public static int charArrayToIntLE(byte value[]) {
        return ((value[1] & 0xff) << 8) + (value[0] & 0xff);
    }

    public static byte[] IntToCharArrayLE(int value, int length) {

        byte[] result = new byte[length];
        Arrays.fill(result, (byte) 0x00);

        result[0] = (byte) value;
        for (int i = 1; i < length; i++) {
            value = value >> 8;
            result[i] = (byte) value;
        }

        return result;
    }

    public ByteArrayOutputStream appendZeroStream(ByteArrayOutputStream byteArrayOutputStream, int zeroByteCount) throws IOException {

        if (zeroByteCount > 0) {
            byte[] byteArray = new byte[zeroByteCount];
            Arrays.fill(byteArray, (byte) 0x00);
            byteArrayOutputStream.write(byteArray);
        }

        return byteArrayOutputStream;

    }

    public String hexToString(String txtInHex) {

        txtInHex = txtInHex.replace(" ", "");

        byte[] txtInByte = new byte[txtInHex.length() / 2];
        int j = 0;
        for (int i = 0; i < txtInHex.length(); i += 2) {
            txtInByte[j++] = Byte.parseByte(txtInHex.substring(i, i + 2), 16);
        }
        return new String(txtInByte);
    }


    public static long bytesToLong(byte[] b, int len) {
        long result = 0;
        for (int i = 0; i < len; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

    public static String bytesToHexString(byte[] b, int size) {
        StringBuffer sb = new StringBuffer(size);

        for(int i = 0; i < size; ++i) {
            String s = String.format("%02X ", 255 & b[i]);
            sb.append(s);
        }

        return sb.toString();
    }

    public static String bytesToHexString(byte[] b) {
        return bytesToHexString(b, b.length);
    }

    public static String byteToHex(byte b) {
        return String.format("%02X", 255 & b);
    }


}
