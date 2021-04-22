package com.doe.topwisesdk.domain.doecard.samndfelica.felica;

/**
 * Created by eshantmittal on 16/01/18.
 */

public class ServiceKeyVersions {

    private byte[] srvKeyVerGenData1 = {
            (byte) 0x02,
            (byte) 0x01
    };

    private byte[] srvKeyVerGenData2 = {
            (byte) 0x02,
            (byte) 0x01
    };


    private byte[] srvKeyVerCardNum = {
            (byte) 0x02,
            (byte) 0x01
    };


    private byte[] srvKeyVerBalanceDA = {
            (byte) 0x02,
            (byte) 0x01
    };

    private byte[] srvKeyVerBalanceDebit = {
            (byte) 0x02,
            (byte) 0x01
    };

    private byte[] srvKeyVerLog = {
            (byte) 0x02,
            (byte) 0x01
    };

    private byte[] srvKeyTollPass = {
            (byte) 0x02,
            (byte) 0x01
    };

    private byte[] srvKeyTollTransaction = {
            (byte) 0x02,
            (byte) 0x01
    };


    public byte[] getSrvKeyVerGenData1() {
        return srvKeyVerGenData1;
    }

    public byte[] getSrvKeyVerGenData2() {
        return srvKeyVerGenData2;
    }

    public byte[] getSrvKeyVerCardNum() {
        return srvKeyVerCardNum;
    }

    public byte[] getSrvKeyVerBalanceDA() {
        return srvKeyVerBalanceDA;
    }

    public byte[] getSrvKeyVerBalanceDebit() {
        return srvKeyVerBalanceDebit;
    }

    public byte[] getSrvKeyVerLog() {
        return srvKeyVerLog;
    }

    public byte[] getSrvKeyTollPass() {
        return srvKeyTollPass;
    }

    public byte[] getSrvKeyTollTransaction() {
        return srvKeyTollTransaction;
    }
}
