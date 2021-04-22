package com.doe.topwisesdk.domain.doecard.samndfelica.sam;

/**
 * Created by eshantmittal on 17/01/18.
 */

public class SamCmdCodes {

    private byte samCmdCodeMutualAuthV2RwSam = (byte) 0xe4;
    private byte samSubCmdCodeMutualAuthV2RwSam = (byte) 0x80;


    public byte getSamCmdCodeMutualAuthV2RwSam() {
        return samCmdCodeMutualAuthV2RwSam;
    }

    public byte getSamSubCmdCodeMutualAuthV2RwSam() {
        return samSubCmdCodeMutualAuthV2RwSam;
    }
}
