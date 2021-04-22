package com.doe.topwisesdk.domain.doecard.samndfelica.felica.servicecodes;


import com.doe.topwisesdk.domain.doecard.samndfelica.Utils;
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.ServiceKeyVersions;

import java.io.IOException;

/**
 * Created by eshantmittal on 17/01/18.
 */

public class TransactionDataCodes {


    //Service 204F cyclic read only (non-security)
    private byte[] srvCodeLogWOEnc = new byte[]{
            (byte) 0x4F,
            (byte) 0x20
    };

    //Service 204C cyclic read/write (with security)
    private byte[] srvCodeLogEnc = new byte[]{
            (byte) 0x4C,
            (byte) 0x20
    };



    public byte[] getSrvCodeLogWOEnc() {
        return srvCodeLogWOEnc;
    }

    public byte[] getSrvCodeLogEnc() throws IOException {
        return new Utils().concatByteArrays(srvCodeLogEnc, new ServiceKeyVersions().getSrvKeyVerLog());
    }

}
