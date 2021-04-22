package com.doe.topwisesdk.domain.doecard.samndfelica.felica.servicecodes;

import com.doe.topwisesdk.domain.doecard.samndfelica.Utils;
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.ServiceKeyVersions;

import java.io.IOException;

public class TransactionalLogsDataCodes {
    //Service 204C cyclic read/write (with security)
    private static final byte[] srvCodeTransactionalLogsEnc = new byte[]{
            (byte) 0x4C,
            (byte) 0x20
    };
    //Service 204F cyclic read only (non-security)
    public static final byte[] srvCodeTransactionalLogs= new byte[]{
            (byte) 0x4F,
            (byte) 0x20
    };
    public static byte[] getSrvCodeTransationalLogsEnc() throws IOException {
        return new Utils().concatByteArrays(srvCodeTransactionalLogsEnc, new ServiceKeyVersions().getSrvKeyVerBalanceDebit());
    }

}
