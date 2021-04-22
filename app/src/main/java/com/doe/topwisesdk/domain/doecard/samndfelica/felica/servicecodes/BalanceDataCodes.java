package com.doe.topwisesdk.domain.doecard.samndfelica.felica.servicecodes;

import com.doe.topwisesdk.domain.doecard.samndfelica.Utils;
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.ServiceKeyVersions;

import java.io.IOException;

public class BalanceDataCodes {

    //Service 2010 Purse direct access (with security)
    private byte[] srvCodeBalanceDAEnc = new byte[]{
            (byte) 0x10,
            (byte) 0x20
    };

    //Service 2014 Purse decrement (with security)
    private byte[] srvCodeBalancePurseEnc = new byte[]{
            (byte) 0x14,
            (byte) 0x20
    };

    //Service 2017 Purse read only (non security)
    private byte[] srvCodeBalanceReadWOEnc = new byte[]{
            (byte) 0x17,
            (byte) 0x20
    };

    public byte[] getSrvCodeBalanceDAEnc() throws IOException {
        return new Utils().concatByteArrays(srvCodeBalanceDAEnc, new ServiceKeyVersions().getSrvKeyVerBalanceDA());
    }

    public byte[] getSrvCodeBalancePurseEnc() throws IOException {
        return new Utils().concatByteArrays(srvCodeBalancePurseEnc, new ServiceKeyVersions().getSrvKeyVerBalanceDebit());
    }

    public byte[] getSrvCodeBalanceReadWOEnc() {
        return srvCodeBalanceReadWOEnc;
    }


}
