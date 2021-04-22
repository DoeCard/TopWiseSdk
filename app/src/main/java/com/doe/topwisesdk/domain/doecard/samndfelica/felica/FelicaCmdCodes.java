package com.doe.topwisesdk.domain.doecard.samndfelica.felica;

import java.util.Arrays;

/**
 * Created by eshantmittal on 16/01/18.
 */

public class FelicaCmdCodes {

    private final byte[] ALL_ZERO_ARRAY_256 = new byte[256];

    //System Code
    private final byte[] felicaSystemCode = new byte[]{
            (byte) 0x92,
            (byte) 0xCF
    };


    private final byte felicaCmdCodePolling = (byte) 0x80;
    private final byte felicaSubCmdCodePolling = (byte) 0x00;

    private final byte felicaCmdCodeReadWOEnc = (byte) 0x98;
    private final byte felicaSubCmdCodeReadWOEnc = (byte) 0x00;

    private final byte felicaCmdCodeReadEnc = (byte) 0x88;
    private final byte felicaSubCmdCodeReadEnc = (byte) 0x00;

    private final byte felicaCmdCodeWriteWOEnc = (byte) 0x9a;
    private final byte felicaSubCmdCodeWriteWOEnc = (byte) 0x00;

    private final byte felicaCmdCodeWriteEnc = (byte) 0x8a;
    private final byte felicaSubCmdCodeWriteEnc = (byte) 0x00;


    public byte[] getALL_ZERO_ARRAY_256() {
        Arrays.fill(ALL_ZERO_ARRAY_256, (byte) 0x00);
        return ALL_ZERO_ARRAY_256;
    }

    public byte[] getFelicaSystemCode() {
        return felicaSystemCode;
    }

    public byte getFelicaCmdCodePolling() {
        return felicaCmdCodePolling;
    }

    public byte getFelicaSubCmdCodePolling() {
        return felicaSubCmdCodePolling;
    }

    public byte getFelicaCmdCodeReadWOEnc() {
        return felicaCmdCodeReadWOEnc;
    }

    public byte getFelicaSubCmdCodeReadWOEnc() {
        return felicaSubCmdCodeReadWOEnc;
    }

    public byte getFelicaCmdCodeReadEnc() {
        return felicaCmdCodeReadEnc;
    }

    public byte getFelicaSubCmdCodeReadEnc() {
        return felicaSubCmdCodeReadEnc;
    }

    public byte getFelicaCmdCodeWriteWOEnc() {
        return felicaCmdCodeWriteWOEnc;
    }

    public byte getFelicaSubCmdCodeWriteWOEnc() {
        return felicaSubCmdCodeWriteWOEnc;
    }

    public byte getFelicaCmdCodeWriteEnc() {
        return felicaCmdCodeWriteEnc;
    }

    public byte getFelicaSubCmdCodeWriteEnc() {
        return felicaSubCmdCodeWriteEnc;
    }
}
