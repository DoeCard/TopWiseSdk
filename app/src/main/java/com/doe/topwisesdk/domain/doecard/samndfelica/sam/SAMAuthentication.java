package com.doe.topwisesdk.domain.doecard.samndfelica.sam;

import android.annotation.SuppressLint;
import android.os.RemoteException;
import android.util.Log;

import com.doe.topwisesdk.domain.doecard.samndfelica.SamResponseAndCodeModelClass;
import com.doe.topwisesdk.domain.doecard.samndfelica.encryptiondecryption.AES;
import com.topwise.cloudpos.aidl.psam.AidlPsam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.APP_ERROR;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.APP_SUCCESS;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.SAM_COMMAND_SUCCEEDED;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.SAM_DATA_TRANSMIT_SUCCEEDED;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.SAM_ERROR_REASON_FETCH_ERROR;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.SAM_RESPONSE_HAS_SYNTAX_ERROR;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.SAM_RESPONSE_NO_SYNTAX_ERROR;
import static com.doe.topwisesdk.domain.doecard.samndfelica.Utils.bytesToHexString;
import static com.doe.topwisesdk.domain.doecard.samndfelica.Utils.charArrayToIntLE;


/**
 * Created by eshantmittal on 08/12/17.
 */

public class SAMAuthentication {

    private SAM mSam;
    private AidlPsam mSmartcardReader;

    private byte[] mRwSamNumber;
    private byte[] mRar;
    private byte[] mRbr;
    private byte[] mRcr;
    private byte[] mKab;
    private byte[] mKYtr;
    private byte[] mSnr;

    private byte[] mIV = new byte[]{
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00
    };


    SAMAuthentication(AidlPsam mSmartcardReader, SAM sam) throws IOException, RemoteException {
        this.mSmartcardReader = mSmartcardReader;

        this.mSam = sam;

        initializeSAM();
    }

    long initializeSAM() throws IOException, RemoteException {

        long _ret = 0;

        //Set SAM Normal Mode

        _ret = sendSetNormalMode();
        if (_ret != SAM_COMMAND_SUCCEEDED) {
            return _ret;
        }

        //Send Attention
        _ret = sendAttention();
        if (_ret != SAM_COMMAND_SUCCEEDED) {

            return _ret;
        }

        //Mutual Authentication 1

        _ret = sendAuth1();
        if (_ret != APP_SUCCESS) {
            return _ret;
        }


        //Mutual Authentication 2

        _ret = sendAuth2();
        if (_ret != SAM_COMMAND_SUCCEEDED) {
            return _ret;
        }


        return APP_SUCCESS;
    }


    private long sendSetNormalMode() throws IOException, RemoteException {

        long _ret = 0;

        //Send Set RWSAM Mode command
        byte[] rwSamModeNoramlCommand = new byte[]{
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0xE6,
                (byte) 0x02,
                (byte) 0x02
        };

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(rwSamModeNoramlCommand);

        SamResponseAndCodeModelClass samResponseAndCodeModelClass = mSam.transmitDataToSAM(byteArrayOutputStream, mSmartcardReader);

        _ret = samResponseAndCodeModelClass.getErrorCode();
        if (_ret != SAM_DATA_TRANSMIT_SUCCEEDED) {
            return _ret;

        }

        long hasSyntaxError = checkAndHandleSyntaxErrorInSamResponse(samResponseAndCodeModelClass.getResponseByte());
        if (hasSyntaxError != SAM_RESPONSE_NO_SYNTAX_ERROR) {
            return hasSyntaxError;
        }

        return SAM_COMMAND_SUCCEEDED;
    }

    private long sendAttention() throws IOException, RemoteException {

        long _ret = 0;

        //Send Attention Command
        byte[] sendAttentionCommand = new byte[]{
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00
        };

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //Set Attention Command
        byteArrayOutputStream.write(sendAttentionCommand);

        SamResponseAndCodeModelClass samResponseAndCodeModelClass = mSam.transmitDataToSAM(byteArrayOutputStream, mSmartcardReader);

        byte[] responseAttention = samResponseAndCodeModelClass.getResponseByte();

        _ret = samResponseAndCodeModelClass.getErrorCode();

        if (_ret != SAM_DATA_TRANSMIT_SUCCEEDED) {
            return _ret;
        }

        long hasSyntaxError = checkAndHandleSyntaxErrorInSamResponse(responseAttention);
        if (hasSyntaxError != SAM_RESPONSE_NO_SYNTAX_ERROR) {
            return hasSyntaxError;
        }

        mRwSamNumber = Arrays.copyOfRange(responseAttention, 4, 12);

        return SAM_COMMAND_SUCCEEDED;


    }

    private long sendAuth1() throws IOException, RemoteException {

        long _ret = 0;

        mRar = new byte[16];

        //Send Authentication1 command
        byte[] auth1Command = new byte[]{
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x02,
                (byte) 0x00,
                (byte) 0x00
        };

        //Generate Rar
        new Random().nextBytes(mRar);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //Set Authentication1 command
        byteArrayOutputStream.write(auth1Command);

        //Set RW SAM ID
        byteArrayOutputStream.write(mRwSamNumber);

        //Set Rar
        byteArrayOutputStream.write(mRar);

        SamResponseAndCodeModelClass samResponseAndCodeModelClass = mSam.transmitDataToSAM(byteArrayOutputStream, mSmartcardReader);

        byte[] auth1Response = samResponseAndCodeModelClass.getResponseByte();
        _ret = samResponseAndCodeModelClass.getErrorCode();

        if (_ret != SAM_DATA_TRANSMIT_SUCCEEDED) {
            return _ret;
        }

        long hasSyntaxError = checkAndHandleSyntaxErrorInSamResponse(auth1Response);
        if (hasSyntaxError != SAM_RESPONSE_NO_SYNTAX_ERROR) {
            return hasSyntaxError;
        }


        //Check Mutual Authentication 1 Result
        _ret = checkAuth1Result(auth1Response);
        if (_ret != APP_SUCCESS) {
            return _ret;
        }


        return APP_SUCCESS;

    }

    private long checkAuth1Result(byte[] bytesData) {

        byte[] MUTUAL_AUTHENTICATION_KEY = new byte[]{
                (byte) 0x12,
                (byte) 0x34,
                (byte) 0x12,
                (byte) 0x34,
                (byte) 0x12,
                (byte) 0x34,
                (byte) 0x12,
                (byte) 0x34,
                (byte) 0x12,
                (byte) 0x34,
                (byte) 0x12,
                (byte) 0x34,
                (byte) 0x12,
                (byte) 0x34,
                (byte) 0x12,
                (byte) 0x34
        };


        mKab = new byte[16];
        byte[] DECRYPTED_M2R;
        byte[] RECEIVED_RAR;

        //extract M2r(encrypted)
        byte[] ENCRYPTED_M2R = Arrays.copyOfRange(bytesData, 4, 36);

        //extract Rcr
        mRcr = Arrays.copyOfRange(bytesData, 36, 40);

        // generate Kab
        for (int i = 0; i < 4; i++) {
            mKab[i] = (byte) (MUTUAL_AUTHENTICATION_KEY[i] ^ mRcr[i]);
        }

        // generate Kab Continued
        System.arraycopy(MUTUAL_AUTHENTICATION_KEY, 4, mKab, 4, MUTUAL_AUTHENTICATION_KEY.length - 4);

        //decrypt M2r
        DECRYPTED_M2R = new AES().decryptCBC(ENCRYPTED_M2R, mKab, mIV);

        //extract Rar
        RECEIVED_RAR = Arrays.copyOfRange(DECRYPTED_M2R, 16, 32);

        //compare Rar, and extract Rbr
        if (Arrays.equals(mRar, RECEIVED_RAR)) {
            mRbr = Arrays.copyOfRange(DECRYPTED_M2R, 0, 16);
            return APP_SUCCESS;
        } else {
            System.out.printf("mRbr ERROR", "Error Getting mRbr");
            return APP_ERROR;
        }
    }

    private long sendAuth2() throws IOException, RemoteException {

        long _ret = 0;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // concatinate Rar, Rbr
        byteArrayOutputStream.write(mRar);
        byteArrayOutputStream.write(mRbr);

        byte[] buffer = byteArrayOutputStream.toByteArray();

        //encrypt M3r
        byte[] M3R = Arrays.copyOfRange(new AES().encryptCBC(buffer, mKab, mIV), 0, 32);

        //Send Authentication2 command
        byte[] auth2Command = new byte[]{
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x04,
                (byte) 0x00,
                (byte) 0x00
        };

        byteArrayOutputStream = new ByteArrayOutputStream();

        //Concatenate auth2Command, mRwSamNumber, M3R
        byteArrayOutputStream.write(auth2Command);
        byteArrayOutputStream.write(mRwSamNumber);
        byteArrayOutputStream.write(M3R);

        SamResponseAndCodeModelClass samResponseAndCodeModelClass = mSam.transmitDataToSAM(byteArrayOutputStream, mSmartcardReader);

        //Check Mutual Authentication 2 Result
        byte[] auth2Response = samResponseAndCodeModelClass.getResponseByte();


        _ret = samResponseAndCodeModelClass.getErrorCode();

        if (_ret != SAM_DATA_TRANSMIT_SUCCEEDED) {
            return _ret;
        }

        long hasSyntaxError = checkAndHandleSyntaxErrorInSamResponse(auth2Response);
        if (hasSyntaxError != SAM_RESPONSE_NO_SYNTAX_ERROR) {
            return hasSyntaxError;
        }

        //Check Mutual Authentication 2 Result
        _ret = checkAuth2Result(auth2Response);
        if (_ret != APP_SUCCESS) {
            return APP_ERROR;
        }
        return APP_SUCCESS;
    }

    private long checkAuth2Result(byte[] bytesData) {

        Log.e("M_Auth2_Response", "==" + bytesToHexString(bytesData));

        if (bytesData[4] == (byte) 0x00) {

            // Initialize Snr (Initial value for RC-S500 driver is 1 )
            mSnr = new byte[]{
                    (byte) 0x01,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00
            };

            mKYtr = mRbr;

            return APP_SUCCESS;

        } else {
            System.out.printf("Auth2Result Error", "Auth2Result Error");
            return APP_ERROR;
        }
    }


    public byte[] getRcr() {
        return mRcr;
    }

    public byte[] getRar() {
        return mRar;
    }

    public byte[] getKYtr() {
        return mKYtr;
    }

    public byte[] getSnr() {
        return mSnr;
    }

    long hasSyntaxErrorInSamResponse(byte[] samResBuf) {
        if (samResBuf[3] == (byte) 0x7f) {
            return SAM_RESPONSE_HAS_SYNTAX_ERROR;
        } else {
            return SAM_RESPONSE_NO_SYNTAX_ERROR;
        }
    }

    @SuppressLint("LongLogTag")
    long checkAndHandleSyntaxErrorInSamResponse(byte[] _sam_res) throws IOException, RemoteException {
        long hasSyntaxError = hasSyntaxErrorInSamResponse(_sam_res);
        if (hasSyntaxError == SAM_RESPONSE_HAS_SYNTAX_ERROR) {
            Log.e("Syntax Error occured.", "Syntax Error occured.");
            long errorReasonCode = _get_error_reason(_sam_res);
            if (errorReasonCode != SAM_COMMAND_SUCCEEDED) {
                Log.e("Unable to fetch error reason.", "Unable to fetch error reason.");
            } else {
                // Syntax error reasone here.
                // Sample response: 00 0000 29 0000 (Dispatcher(1), Reserved(2), ResponseCode(1), ErrorCode(2))
                hasSyntaxError = charArrayToIntLE(Arrays.copyOfRange(_sam_res, 4, 6));
                Log.e("Error Reason Code:%0x", String.valueOf(hasSyntaxError));
            }
        }

        return hasSyntaxError;
    }

    long checkAndHandleForSNROverFlow() throws IOException, RemoteException {

        byte[] maxValueSNR = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xfd};
        int maxValue = (charArrayToIntLE(maxValueSNR));

        int snr_value = charArrayToIntLE(mSnr);
        if (snr_value > maxValue) {
            Log.e("Eshant1   ", "Ehsna");

            long returnValue = ReAuthenticateSAM();
            if (returnValue != APP_SUCCESS) {
                Log.e("Eshant2   ", "Ehsna");

                return returnValue;
            }
        }

        return APP_SUCCESS;
    }


    long _get_error_reason(byte[] samResBuf) throws IOException, RemoteException {
        long _ret;
        long _send_len;

        ByteArrayOutputStream sendBuffStream = new ByteArrayOutputStream();
        //CLA
        sendBuffStream.write((byte) 0x00);                                            //INS
        sendBuffStream.write((byte) 0x00);                                            //P1
        sendBuffStream.write((byte) 0x00);
        sendBuffStream.write((byte) 0x28);
        sendBuffStream.write((byte) 0x00);                                            //P1
        sendBuffStream.write((byte) 0x00);


        SamResponseAndCodeModelClass samResponseAndCodeModelClass = mSam.transmitDataToSAM(sendBuffStream, mSmartcardReader);
        _ret = samResponseAndCodeModelClass.getErrorCode();
        if (_ret != SAM_DATA_TRANSMIT_SUCCEEDED) {
            return SAM_ERROR_REASON_FETCH_ERROR;
        }

        long hasSyntaxError = hasSyntaxErrorInSamResponse(samResBuf);
        if (hasSyntaxError == SAM_RESPONSE_HAS_SYNTAX_ERROR) {
            return hasSyntaxError;
        }

        return SAM_COMMAND_SUCCEEDED;
    }


    long ReAuthenticateSAM() throws IOException, RemoteException {
        long _ret = 0;
        byte[] _sam_res = new byte[262];

        //Send Attention
        _ret = sendAttention();
        if (_ret != SAM_COMMAND_SUCCEEDED) {

            return _ret;
        }

        //Mutual Authentication 1

        _ret = sendAuth1();
        if (_ret != APP_SUCCESS) {
            return _ret;
        }


        //Mutual Authentication 2

        _ret = sendAuth2();
        if (_ret != SAM_COMMAND_SUCCEEDED) {
            return _ret;
        }


        return APP_SUCCESS;
    }

    long SendCardErrorToSAM() throws IOException, RemoteException {

        long _ret = 0;

        ByteArrayOutputStream felicaCmdParamsStream = new ByteArrayOutputStream();
        // "card No Response packet"
        felicaCmdParamsStream.write(0x01); // Dispatcher
        felicaCmdParamsStream.write(0x00); // Reserved
        felicaCmdParamsStream.write(0x00); // Reserved

        // Send packets to SAM
        SamResponseAndCodeModelClass samResponseAndCodeModelClass = mSam.transmitDataToSAM(felicaCmdParamsStream, mSmartcardReader);
        _ret = samResponseAndCodeModelClass.getErrorCode();

        if (_ret != SAM_DATA_TRANSMIT_SUCCEEDED) {
            return _ret;
        }

        long hasSyntaxError = checkAndHandleSyntaxErrorInSamResponse(samResponseAndCodeModelClass.getResponseByte());
        if (hasSyntaxError != SAM_RESPONSE_NO_SYNTAX_ERROR) {
            return hasSyntaxError;
        }


        //   memcpy(result, &_sam_res[3], *resultLen);

        return SAM_COMMAND_SUCCEEDED;
    }
}
