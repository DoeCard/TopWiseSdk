package com.doe.topwisesdk.domain.doecard.samndfelica.felica.servicecodes;

import android.util.Log;

import com.doe.topwisesdk.domain.doecard.samndfelica.Utils;
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.ServiceKeyVersions;

import java.io.IOException;

/**
 * Created by eshantmittal on 17/01/18.
 */

public class GenericDataCodes {

    //Service 1008	random read/write (with security)
    private final byte[] srvCodeUserData1Enc = new byte[]{
            (byte) 0x08,
            (byte) 0x10
    };

    //Service 100B	random read only (non-security)
    private byte[] srvCodeUserData1WOEnc = new byte[]{
            (byte) 0x0B,
            (byte) 0x10
    };

    //Service 1048	random read/write (with security)
    private byte[] srvCodeUserData2Enc = new byte[]{
            (byte) 0x48,
            (byte) 0x10
    };

    //Service 1088	random read/write (with security)
    private byte[] srvCodeCardNumEnc = new byte[]{
            (byte) 0x88,
            (byte) 0x10
    };

    //Service 108B	random read only (non security)
    private byte[] srvCodeCardNumWOEnc = new byte[]{
            (byte) 0x8B,
            (byte) 0x10
    };


    public byte[] getSrvCodeUserData1Enc() throws IOException {
        Log.e("getSrvCodeUserData1Enc","getSrvCodeUserData1Enc");
        return new Utils().concatByteArrays(srvCodeUserData1Enc, new ServiceKeyVersions().getSrvKeyVerGenData1());
    }

    public byte[] getSrvCodeUserData1WOEnc() {
        return srvCodeUserData1WOEnc;
    }

    public byte[] getSrvCodeUserData2Enc() throws IOException {
        Log.e("getSrvCodeUserData2Enc","getSrvCodeUserData2Enc");
        return new Utils().concatByteArrays(srvCodeUserData2Enc, new ServiceKeyVersions().getSrvKeyVerGenData2());
    }

    public byte[] getSrvCodeCardNumEnc() throws IOException {
        return new Utils().concatByteArrays(srvCodeCardNumEnc, new ServiceKeyVersions().getSrvKeyVerCardNum());
    }

    public byte[] getSrvCodeCardNumWOEnc() {
        return srvCodeCardNumWOEnc;
    }

}
