package com.doe.topwisesdk.domain.doecard.samndfelica.felica.servicecodes;

/**
 * Created by eshantmittal on 23/01/18.
 */

public class CardSpDataCodes {

    //Service 2010 Purse direct access (with security)
    private byte[] srvCodeCardSpDataEnc = new byte[]{
            (byte) 0x08,
            (byte) 0x30
    };

    //Service 2014 Purse decrement (with security)
    private byte[] srvCodeCardSpDataWOEnc = new byte[]{
            (byte) 0x0B,
            (byte) 0x30
    };

}
