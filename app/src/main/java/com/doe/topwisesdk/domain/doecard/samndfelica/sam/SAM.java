package com.doe.topwisesdk.domain.doecard.samndfelica.sam;

import android.annotation.SuppressLint;
import android.os.RemoteException;
import android.util.Log;

import com.doe.topwisesdk.domain.doecard.nfcCardReader.Util;
import com.doe.topwisesdk.domain.doecard.samndfelica.SamResponseAndCodeModelClass;
import com.doe.topwisesdk.domain.doecard.samndfelica.encryptiondecryption.AES;
import com.topwise.cloudpos.aidl.psam.AidlPsam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

import timber.log.Timber;

import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.APP_SUCCESS;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.DECRYPTION_FAILED;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.DECRYPTION_SUCCEEDED;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.SAM_COMMAND_SUCCEEDED;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.SAM_DATA_TRANSMIT_CLA_ERROR;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.SAM_DATA_TRANSMIT_INS_ERROR;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.SAM_DATA_TRANSMIT_LCLE_ERROR;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.SAM_DATA_TRANSMIT_P1P2_ERROR;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.SAM_DATA_TRANSMIT_SUCCEEDED;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.SAM_RESPONSE_NO_SYNTAX_ERROR;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.SNR_VERIFICATION_FAILED;
import static com.doe.topwisesdk.domain.doecard.samndfelica.Utils.bytesToHexString;

public class SAM {

    private AidlPsam mSmartcardReader;
    private SAMAuthentication mSamAuthentication;

    private byte[] mEncryptedPayLoad;
    private byte[] mEncryptedMac;
    private byte[] mSnr = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    private byte[] mRcr;
    private byte[] mRar;
    private byte[] mKytr;


    public SAM(AidlPsam mSmartcardReader) {
        this.mSmartcardReader = mSmartcardReader;
    }


    public void authenticateSAM() throws IOException, RemoteException {

        mSamAuthentication = new SAMAuthentication(mSmartcardReader, this);

        mRar = mSamAuthentication.getRar();
        mRcr = mSamAuthentication.getRcr();
        mKytr = mSamAuthentication.getKYtr();
        mSnr = mSamAuthentication.getSnr();

        Timber.d("Rar==%s", bytesToHexString(mRar));
        Timber.d("Rcr==%s", bytesToHexString(mRcr));
        Timber.d("KYtr==%s", bytesToHexString(mKytr));
        Timber.d("Snr==%s", bytesToHexString(mSnr));

    }


    @SuppressLint("LongLogTag")
    public SamResponseAndCodeModelClass askFelicaCommandFromSAM(byte commandCode, byte subCommandCode, byte[] felicaCmdParams) throws IOException, RemoteException {

        long _ret;
        SamResponseAndCodeModelClass modelClass = new SamResponseAndCodeModelClass();

        long returnValue = mSamAuthentication.checkAndHandleForSNROverFlow();
        if (returnValue != APP_SUCCESS) {
            modelClass.setErrorCode(returnValue);
            return modelClass;
        }


        Log.e("STEP", "== encryptPayload");
        // Encrypt command payload data
        encryptPayload(felicaCmdParams, commandCode, subCommandCode);


        //construct command packet send to SAM
        ByteArrayOutputStream sendBuf = new ByteArrayOutputStream();
        sendBuf.write((byte) 0x00);                                 // Dispatcher
        sendBuf.write((byte) 0x00);                                 // Reserved
        sendBuf.write((byte) 0x00);                                 // Reserved
        sendBuf.write(commandCode);                                 // Command Code
        sendBuf.write(subCommandCode);                              // Sub Command Code
        sendBuf.write((byte) 0x00);                                 // Reserved
        sendBuf.write((byte) 0x00);                                 // Reserved
        sendBuf.write((byte) 0x00);                                 // Reserved
        sendBuf.write(mSnr);                                        // Snr
        sendBuf.write(mEncryptedPayLoad);                           // encrypted payload data
        sendBuf.write(mEncryptedMac);                               // encrypted MAC


        Timber.e(bytesToHexString(sendBuf.toByteArray()));
        // Send packets to SAM

        SamResponseAndCodeModelClass samResponseAndCodeModelClass = transmitDataToSAM(sendBuf, mSmartcardReader);

        byte[] sendBufResponse = samResponseAndCodeModelClass.getResponseByte();

        _ret = samResponseAndCodeModelClass.getErrorCode();
        if (_ret != SAM_DATA_TRANSMIT_SUCCEEDED) {
            Log.e("AskFeliCaCmdToSAMSC - TransmitDataToSAM; failed with error code %ld", "" + _ret);
            modelClass.setErrorCode(_ret);
            return modelClass;
        }

        long hasSyntaxError = mSamAuthentication.checkAndHandleSyntaxErrorInSamResponse(sendBufResponse);
        if (hasSyntaxError != SAM_RESPONSE_NO_SYNTAX_ERROR) {
            modelClass.setErrorCode(hasSyntaxError);
            return modelClass;
        }

        modelClass.setResponseByte(Arrays.copyOfRange(sendBufResponse, 3, sendBufResponse.length));
        modelClass.setErrorCode(SAM_COMMAND_SUCCEEDED);

        // Extract FeliCa command packets from SAM response
        return modelClass;
    }


    @SuppressLint("LongLogTag")
    public long sendFelicaPollingResponseToSAM(byte[] IDm, byte[] PMm) throws IOException, RemoteException {

        long _ret = 0;

        //Send back the response from FeliCa Card to RW-SAM(RC-S500)
        ByteArrayOutputStream sendBuff = new ByteArrayOutputStream();
        sendBuff.write((byte) 0x01);                                    // Dispatcher
        sendBuff.write((byte) 0x00);                                    // Reserved
        sendBuff.write((byte) 0x00);                                    // Reserved
        sendBuff.write((byte) 0x01);                                    // Reserved
        sendBuff.write((byte) 0x01);                                    // Number of Target 1<=n<=4
        sendBuff.write(IDm);                                            // IDm
        sendBuff.write(PMm);                                            // PMm

        Log.e("STEP", "== transmitDataToSAM");
        // Send packets to SAM
        SamResponseAndCodeModelClass samResponseAndCodeModelClass = transmitDataToSAM(sendBuff, mSmartcardReader);

        //Check Result and return Result
        _ret = samResponseAndCodeModelClass.getErrorCode();
        if (_ret != SAM_DATA_TRANSMIT_SUCCEEDED) {
            Log.e("SendPollingResToSAM-transmitDataToSAM()", "" + _ret);
            return _ret;
        }
        byte[] sendBuffResponse = samResponseAndCodeModelClass.getResponseByte();

        long hasSyntaxError = mSamAuthentication.checkAndHandleSyntaxErrorInSamResponse(sendBuffResponse);
        if (hasSyntaxError != SAM_RESPONSE_NO_SYNTAX_ERROR) {
            return hasSyntaxError;
        }

        _ret = decryptSamResponse(sendBuffResponse).getErrorCode();
        if (_ret != DECRYPTION_SUCCEEDED) {
//            PrintText("SendPollingResToSAM-_decrypt_sam_response() - %ld", _ret);
            return _ret;
        }

        return SAM_COMMAND_SUCCEEDED;
    }


    public SamResponseAndCodeModelClass transmitDataToSAM(ByteArrayOutputStream samCmdBuf, AidlPsam smartcardReader) throws IOException, RemoteException {

        /*byte samCmdLength = (byte) samCmdBuf.toByteArray().length;
        Log.e("Special", "Int = " + samCmdBuf.toByteArray().length);
        Log.e("\nSpecial", "Bytes = " + byteToHex(samCmdLength));*/

        byte[] samCmdLength;

        if (samCmdBuf.toByteArray().length > 255) {
            samCmdLength = LongToCharArrayLen(samCmdBuf.toByteArray().length, 3);
            Log.e("Special", "Int = " + samCmdBuf.toByteArray().length);
            Log.e("\nSpecial", "Bytes = " + bytesToHexString(samCmdLength));
        } else {
            samCmdLength = LongToCharArrayLen(samCmdBuf.toByteArray().length, 1);
            Log.e("Special", "Int = " + samCmdBuf.toByteArray().length);
            Log.e("\nSpecial", "Bytes = " + bytesToHexString(samCmdLength));
        }

        ByteArrayOutputStream sendBuffStream = new ByteArrayOutputStream();
        sendBuffStream.write((byte) 0xA0);                                            //CLA
        sendBuffStream.write((byte) 0x00);                                            //INS
        sendBuffStream.write((byte) 0x00);                                            //P1
        sendBuffStream.write((byte) 0x00);                                            //P2
        sendBuffStream.write(samCmdLength);                                           //Lc
        sendBuffStream.write(samCmdBuf.toByteArray());                                //Sam Command

        if (samCmdBuf.toByteArray().length > 255) {
            sendBuffStream.write((byte) 0x00);                                            //Le
            sendBuffStream.write((byte) 0x00);                                            //Le
        } else {
            sendBuffStream.write((byte) 0x00);                                            //Le
        }

        Log.e("sendBuffStream", "==" + bytesToHexString(sendBuffStream.toByteArray()));
        byte[] responseDataToSAM = smartcardReader.apduComm(sendBuffStream.toByteArray());
        Log.e("responseDataToSAM", "==" + bytesToHexString(responseDataToSAM));

        byte sw1 = responseDataToSAM[responseDataToSAM.length - 2];
        byte sw2 = responseDataToSAM[responseDataToSAM.length - 1];

        SamResponseAndCodeModelClass samResponseAndCodeModelClass = new SamResponseAndCodeModelClass();

        if ((sw1 == (byte) 0x67) && (sw2 == (byte) 0x00)) {
            samResponseAndCodeModelClass.setErrorCode(SAM_DATA_TRANSMIT_LCLE_ERROR);
        }
        if ((sw1 == (byte) 0x6A) && (sw2 == (byte) 0x86)) {
            samResponseAndCodeModelClass.setErrorCode(SAM_DATA_TRANSMIT_P1P2_ERROR);
        }
        if ((sw1 == (byte) 0x6D) && (sw2 == (byte) 0x00)) {
            samResponseAndCodeModelClass.setErrorCode(SAM_DATA_TRANSMIT_INS_ERROR);
        }
        if ((sw1 == (byte) 0x6E) && (sw2 == (byte) 0x00)) {
            samResponseAndCodeModelClass.setErrorCode(SAM_DATA_TRANSMIT_CLA_ERROR);
        }
        if ((sw1 == (byte) 0x90) && (sw2 == (byte) 0x00)) {
            samResponseAndCodeModelClass.setErrorCode(SAM_DATA_TRANSMIT_SUCCEEDED);
            samResponseAndCodeModelClass.setResponseByte(Arrays.copyOfRange(responseDataToSAM, 0, responseDataToSAM.length - 2));  // Remove SW1/SW2
        }

        return samResponseAndCodeModelClass;
    }


    public byte[] sendCardResultToSAM(byte[] felicaResponse) throws IOException, RemoteException {

        //Send back the response from FeliCa Card to RW-SAM(RC-S500)
        ByteArrayOutputStream sendBuff = new ByteArrayOutputStream();
        sendBuff.write((byte) 0x01);                                    // Dispatcher
        sendBuff.write((byte) 0x00);                                    // Reserved
        sendBuff.write((byte) 0x00);                                    // Reserved
        sendBuff.write(Arrays.copyOfRange(felicaResponse, 1, felicaResponse.length));                        //Response of FeliCa Card

        Log.e("sendBuff", "== " + bytesToHexString(sendBuff.toByteArray()));
        // Send packets to SAM
        byte[] sendBuffResponse = transmitDataToSAM(sendBuff, mSmartcardReader).getResponseByte();
        Log.e("sendBuffResponse", "==" + bytesToHexString(sendBuffResponse));

        //Check Result and return Result
        return decryptSamResponse(sendBuffResponse).getResponseByte();

    }


    public byte[] sendAuth1V2ResultToSAM(byte[] felicaResponse) throws IOException, RemoteException, NullPointerException {


        //Send back the response from FeliCa Card to RW-SAM(RC-S500)
        ByteArrayOutputStream sendBuff = new ByteArrayOutputStream();
        sendBuff.write((byte) 0x01);                                    // Dispatcher
        sendBuff.write((byte) 0x00);                                    // Reserved
        sendBuff.write((byte) 0x00);                                    // Reserved


        sendBuff.write(Arrays.copyOfRange(felicaResponse, 1, felicaResponse.length));                        //Response of FeliCa Card

        Log.e("sendBuff", "== " + bytesToHexString(sendBuff.toByteArray()));
        // Send packets to SAM
        byte[] sendBuffResponse = transmitDataToSAM(sendBuff, mSmartcardReader).getResponseByte();
        Log.e("sendBuffResponse", "==" + bytesToHexString(sendBuffResponse));

        return Arrays.copyOfRange(sendBuffResponse, 3, sendBuffResponse.length);

    }


    private SamResponseAndCodeModelClass decryptSamResponse(byte[] buffResponse) throws IOException {

        SamResponseAndCodeModelClass samResponseAndCodeModelClass = new SamResponseAndCodeModelClass();

        Log.e("STEP", "== decryptSamResponse");
        byte[] receivedSnr;
        byte[] b0;
        byte[] b1;
        byte[] ctrBlock;
        byte[] workBuffer;
        byte[] mac;
        byte[] receivedMac = new byte[8];

        int num;
        int snrValue;
        int receivedSnrValue;
        int encDataLen;

        Log.e("STEP", "== buffResponse = " + bytesToHexString(buffResponse));
        byte[] sendBuffResponse = Arrays.copyOfRange(buffResponse, 3, buffResponse.length);
        Log.e("STEP", "== sendBuffResponse = " + bytesToHexString(sendBuffResponse));


        // Extract Snr
        receivedSnr = Arrays.copyOfRange(sendBuffResponse, 5, 9);
        Log.e("STEP", "== receivedSnr == " + bytesToHexString(receivedSnr));

        // Calc encrypted packtes data length
        encDataLen = sendBuffResponse.length - (1 + 1 + 3 + 4) - 8;
        Log.e("STEP", "== encDataLen == " + encDataLen);

        Log.e("STEP", "== Ctr1 D");
        // Generate Ctr1
        ByteArrayOutputStream ctr1Stream = new ByteArrayOutputStream();
        ctr1Stream.write((byte) 0x01);
        ctr1Stream.write(receivedSnr);
        ctr1Stream.write(mRcr);
        ctr1Stream.write(Arrays.copyOfRange(mRar, 0, 5));
        ctr1Stream.write((byte) 0x00);
        ctr1Stream.write((byte) 0x01);

        ctrBlock = ctr1Stream.toByteArray();


        Log.e("STEP", "== decryptedData");
        // Decrypt packet data
        byte[] decryptedData = new AES().decryptCTR(mKytr,
                Arrays.copyOfRange(sendBuffResponse, 9, 9 + encDataLen),
                ctrBlock);


        // Decrypt MAC
        ctrBlock[14] = (byte) 0x00;
        ctrBlock[15] = (byte) 0x00;

        Log.e("STEP", "== decryptedMac");
        byte[] decryptedMac = new AES().decryptCTR(mKytr,
                Arrays.copyOfRange(sendBuffResponse, sendBuffResponse.length - 8, sendBuffResponse.length),
                ctrBlock);


        Log.e("STEP", "== B0 D");
        // Create B0
        ByteArrayOutputStream b0Stream = new ByteArrayOutputStream();
        b0Stream.write((byte) 0x59);                                        // Flags
        b0Stream.write(receivedSnr);                                        // Snr
        b0Stream.write(mRcr);                                               // Rcr
        b0Stream.write(Arrays.copyOfRange(mRar, 0, 5));           // Rar
        b0Stream.write(Arrays.copyOfRange(Util.toBytes(encDataLen / 256), 3, 4));                               // Packet data size
        b0Stream.write(Arrays.copyOfRange(Util.toBytes(encDataLen % 256), 3, 4));                               // Packet data size

        b0 = b0Stream.toByteArray();

        Log.e("STEP", "== B1 D");
        // Create B1
        ByteArrayOutputStream b1Stream = new ByteArrayOutputStream();
        b1Stream.write((byte) 0x00);                                            // Associated Data Size
        b1Stream.write((byte) 0x09);                                            // Associated Data Size
        b1Stream.write(Arrays.copyOfRange(sendBuffResponse, 0, 9));    // Response Code(1), Sub Response Code(1), Reserved(3), Snr(4)
        b1Stream.write((byte) 0x00);
        b1Stream.write((byte) 0x00);
        b1Stream.write((byte) 0x00);
        b1Stream.write((byte) 0x00);
        b1Stream.write((byte) 0x00);

        b1 = b1Stream.toByteArray();

        // Calc CBC-MAC
        ByteArrayOutputStream workBufferStream = new ByteArrayOutputStream();
        workBufferStream.write(b0);
        workBufferStream.write(b1);
        workBufferStream.write(decryptedData);

        workBuffer = workBufferStream.toByteArray();

        Log.e("STEP", "== calcMac D");
        mac = calcMac(mKytr, workBuffer);


        Log.e("test mac", "" + bytesToHexString(mac));
        Log.e("test decmac", "" + bytesToHexString(decryptedMac));


        receivedSnrValue = char2BytesArrayToIntLE(receivedSnr);
        snrValue = char2BytesArrayToIntLE(mSnr);
        if (receivedSnrValue != snrValue + 1) {
            samResponseAndCodeModelClass.setErrorCode(SNR_VERIFICATION_FAILED);
            return samResponseAndCodeModelClass;
        }
//
//
//        // Update Snr
//        // If _received_snr_value = 0xffffffff, incrementing _snr_value will take it to zero. Semantically is not correct.
//        // Sending next command without incrementing snr, will cause SAM to throw syntax error.
//        // This syntax error is handled and treated as snr overflow error.
//        // Motivation was to have snr over flow error check at one place.
//        if (receivedSnrValue != 0xffffffff) {
//            snrValue += 2;
//        }
//
//        IntToCharArrayLE(snrValue, 4);
//
        // Verify Snr
//        receivedSnrValue = charArrayToIntLE(receivedSnr, 4);
//        snrValue = char2BytesArrayToIntLE(mSnr);
        Log.e("mSnr Before", snrValue + "==" + bytesToHexString(mSnr));
        snrValue += 2;

        mSnr = IntToCharArrayLE(snrValue, 4);

        Log.e("mSnr After", snrValue + "==" + bytesToHexString(mSnr));

//        // Verify MAC
        if (Arrays.equals(decryptedMac, Arrays.copyOfRange(mac, 0, 8))) {
            samResponseAndCodeModelClass.setErrorCode(DECRYPTION_SUCCEEDED);
            samResponseAndCodeModelClass.setResponseByte(decryptedData);
            return samResponseAndCodeModelClass;

        } else {
            samResponseAndCodeModelClass.setErrorCode(DECRYPTION_FAILED);
            return samResponseAndCodeModelClass;

        }

    }


    private void encryptPayload(byte[] felicaCmdParams, byte commandCode, byte subCommandCode) throws IOException {

        Log.e("STEP", "== B0");
        // Create B0
        ByteArrayOutputStream b0 = new ByteArrayOutputStream();
        b0.write((byte) 0x59);                                  // Flags
        b0.write(mSnr);                                         // Snr
        b0.write(mRcr);                                         // Rcr
        b0.write(Arrays.copyOfRange(mRar, 0, 5));     // Rar
        b0.write(Util.toBytes(felicaCmdParams.length / 256)[3]);             // Packet data size
        b0.write(Util.toBytes(felicaCmdParams.length % 256)[3]);             // Packet data size
        Log.e("B0", "==" + bytesToHexString(b0.toByteArray()));

        Log.e("STEP", "== B1");
        // Create B1
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        b1.write((byte) 0x00);                                  // Associated Data Size
        b1.write((byte) 0x09);                                  // Associated Data Size
        b1.write(commandCode);                                  // Command Code
        b1.write(subCommandCode);                               // Sub Command Code
        b1.write((byte) 0x00);                                  // Reserved
        b1.write((byte) 0x00);                                  // Reserved
        b1.write((byte) 0x00);                                  // Reserved
        b1.write(mSnr);                                         // Snr
        b1.write((byte) 0x00);                                  // All Zero
        b1.write((byte) 0x00);                                  // All Zero
        b1.write((byte) 0x00);                                  // All Zero
        b1.write((byte) 0x00);                                  // All Zero
        b1.write((byte) 0x00);                                  // All Zero
        Log.e("B1", "==" + bytesToHexString(b1.toByteArray()));


        Log.e("STEP", "== Ctr1");
        // Generate Ctr1
        ByteArrayOutputStream ctr1Stream = new ByteArrayOutputStream();
        ctr1Stream.write((byte) 0x01);                                // Flags
        ctr1Stream.write(mSnr);                                       // Snr
        ctr1Stream.write(mRcr);                                       // Rcr
        ctr1Stream.write(Arrays.copyOfRange(mRar, 0, 5));   // Rar
        ctr1Stream.write((byte) 0x00);                                // i
        ctr1Stream.write((byte) 0x01);                                // i

        byte[] ctr1 = ctr1Stream.toByteArray();
        Log.e("CTR1", "==" + bytesToHexString(ctr1));


        Log.e("STEP", "== mEncryptedPayLoad");
        // Encrypt packet data with CTR1
        mEncryptedPayLoad = new AES().encryptCTR(mKytr, felicaCmdParams, ctr1);
        Log.e("mEncryptedPayLoad", "==" + bytesToHexString(mEncryptedPayLoad));


        // Calc CBC-MAC
        ByteArrayOutputStream workBuffer = new ByteArrayOutputStream();
        workBuffer.write(b0.toByteArray());
        workBuffer.write(b1.toByteArray());
        workBuffer.write(felicaCmdParams);
        Log.e("WorkBuffer", "==" + bytesToHexString(workBuffer.toByteArray()));

        Log.e("STEP", "== calcMac");
        byte[] mac = calcMac(mKytr, workBuffer.toByteArray());
        Log.e("MAC", "==" + bytesToHexString(mac));

        ctr1[14] = (byte) 0x00;
        ctr1[15] = (byte) 0x00;
        Log.e("CTR0", "==" + bytesToHexString(ctr1));

        //Encrypt MAC with CTR0
        byte[] encryptedMac = new AES().encryptCTR(mKytr, mac, ctr1);

        //Get first 8 bytes as Encrypted MAC
        mEncryptedMac = Arrays.copyOfRange(encryptedMac, 0, 8);
        Log.e("EncryptedMac", "==" + bytesToHexString(mEncryptedMac));
    }


    private byte[] calcMac(byte[] kytr, byte[] workBuffer) throws IOException {

        int remainingBytes = 0;
        byte[] lastBlock = new byte[16];
        byte[] mac = new byte[16];

        Log.e("STEP", "== last block");
        //Get the last block of 16 bytes or less
        if (workBuffer.length % 16 == 0) {
            remainingBytes = workBuffer.length - 16;
            lastBlock = Arrays.copyOfRange(workBuffer, remainingBytes, workBuffer.length);
        } else {
            remainingBytes = workBuffer.length % 16;
            remainingBytes = workBuffer.length - remainingBytes;
            lastBlock = Arrays.copyOfRange(workBuffer, remainingBytes, workBuffer.length);
        }

        ByteArrayOutputStream lastBlockStream = new ByteArrayOutputStream();
        lastBlockStream.write(lastBlock);

        //If the last block is less than 16 bytes then append 0x00 to make it 16 bytes
        if (lastBlock.length < 16) {
            for (int i = lastBlock.length; i < 16; i++) {
                lastBlockStream.write((byte) 0x00);
            }
        }

        lastBlock = lastBlockStream.toByteArray();
        Log.e("lastBlock", bytesToHexString(lastBlock));


        // CBC encryption
        for (int i = 0; i < remainingBytes; i += 16) {
            byte[] partWorkBuffer = Arrays.copyOfRange(workBuffer, i, i + 16);
            Log.e("partWorkBuffer", bytesToHexString(partWorkBuffer));
            mac = new AES().encryptCBC(partWorkBuffer, kytr, mac);
        }

        Log.e("STEP", "== mac");
        mac = new AES().encryptCBC(lastBlock, kytr, mac);

        return mac;
    }


    public int char2BytesArrayToIntLE(byte value[]) {
        return ((value[1] & 0xff) << 8) + (value[0] & 0xff);
    }

    public byte[] IntToCharArrayLE(int value, int length) {

        byte[] result = new byte[length];
        Arrays.fill(result, (byte) 0x00);

        result[0] = (byte) value;
        for (int i = 1; i < length; i++) {
            value = value >> 8;
            result[i] = (byte) value;
        }

        return result;
    }

    public byte[] LongToCharArrayLE(long value) {

        byte[] result = new byte[8];
        Arrays.fill(result, (byte) 0x00);

        int i;
        for (i = 0; i < 7; i++) {
            result[i] = (byte) value;
            value = value >> 8;
        }
        result[i] = (byte) value;

        return result;
    }


    public byte[] LongToCharArrayLen(long value, int len) {

        byte[] result = new byte[len];
        Arrays.fill(result, (byte) 0x00);

        int i;
        for (i = len - 1; i > 0; i--) {
            result[i] = (byte) value;
            value = value >> 8;
        }
        result[i] = (byte) value;

        return result;
    }

    public int byteArrayToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public int byteToInt(byte byteValue) {
        return Byte.valueOf(byteValue).intValue();
    }

    public byte[] bigIntToByteArray(final int i) {
        BigInteger bigInt = BigInteger.valueOf(i);
        return bigInt.toByteArray();
    }

}
