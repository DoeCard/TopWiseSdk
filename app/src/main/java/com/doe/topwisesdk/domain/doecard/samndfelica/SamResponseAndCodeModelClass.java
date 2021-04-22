package com.doe.topwisesdk.domain.doecard.samndfelica;

public class SamResponseAndCodeModelClass {

    private byte[] responseByte;
    private long errorCode;

    public byte[] getResponseByte() {
        return responseByte;
    }

    public void setResponseByte(byte[] responseByte) {
        this.responseByte = responseByte;
    }

    public long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(long errorCode) {
        this.errorCode = errorCode;
    }
}
