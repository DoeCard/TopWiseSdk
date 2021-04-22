package com.doe.topwisesdk.domain.doecard.samndfelica;

public class ConstantCode {

    public static final long CARD_DATA_TRANSMIT_SUCCEEDED = 10000;
    public static final long CARD_COMMAND_SUCCEEDED = 10001;

    public static final long APP_SUCCESS = 0;
    public static final long APP_ERROR = -1;
    public static final long APP_CANCEL = -2;
    public static final long DATA_INCONSISTENCY_ERROR = -3;
    public static final long INSUFFICIENT_BALANCE_ERROR = -4;


    // SAM Related constants.
    public static final long SAM_DATA_TRANSMIT_SUCCEEDED = 20000;
    public static final long SAM_COMMAND_SUCCEEDED = 20001;
    public static final long DECRYPTION_SUCCEEDED = 20002;
    public static final long SAM_RESPONSE_NO_SYNTAX_ERROR = 20003;
    public static final long SAM_RECONNECTION_SUCCEEDED = 20004;


    public static final long DECRYPTION_FAILED = -20000;
    public static final long SNR_VERIFICATION_FAILED = -20001;
    public static final long SAM_RESPONSE_BUFFER_ERROR = -20002;
    public static final long SAM_DATA_TRANSMIT_UNKOWN_ERROR = -20003;
    public static final long SAM_DATA_TRANSMIT_LCLE_ERROR = -20004;
    public static final long SAM_DATA_TRANSMIT_P1P2_ERROR = -20005;
    public static final long SAM_DATA_TRANSMIT_INS_ERROR = -20006;
    public static final long SAM_DATA_TRANSMIT_CLA_ERROR = -20007;
    public static final long SAM_DATA_TRANSMIT_MAC_ERROR = -20008;
    public static final long SAM_BRIDGE_ERROR = -20009;
    public static final long SAM_RESPONSE_HAS_SYNTAX_ERROR = -20010;
    public static final long SAM_ERROR_REASON_FETCH_ERROR = -20011;
    public static final long SAM_DRIVER_SNR_OVERFLOW_ERROR = -20012;
    public static final long SAM_RECONNECTION_FAILED = -20013;

}
