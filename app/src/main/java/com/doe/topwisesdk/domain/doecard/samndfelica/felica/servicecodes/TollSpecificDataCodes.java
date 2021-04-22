package com.doe.topwisesdk.domain.doecard.samndfelica.felica.servicecodes;

import com.doe.topwisesdk.domain.doecard.samndfelica.Utils;
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.ServiceKeyVersions;

import java.io.IOException;

/**
 * Created by eshantmittal on 05/03/18.
 */

public class TollSpecificDataCodes {

    //Service 4008 random read/write (with security)
    private byte[] srvCodeTollPassEnc = new byte[]{
            (byte) 0x08,
            (byte) 0x40
    };

    //Service 404C random read/write (with security)
    private byte[] srvCodeTollTransEnc = new byte[]{
            (byte) 0x48,
            (byte) 0x40
    };


    public byte[] getSrvCodeTollPassEnc() throws IOException {
        return new Utils().concatByteArrays(srvCodeTollPassEnc, new ServiceKeyVersions().getSrvKeyTollPass());
    }

    public byte[] getSrvCodeTollTransEnc() throws IOException {
        return new Utils().concatByteArrays(srvCodeTollTransEnc, new ServiceKeyVersions().getSrvKeyTollTransaction());
    }

}
