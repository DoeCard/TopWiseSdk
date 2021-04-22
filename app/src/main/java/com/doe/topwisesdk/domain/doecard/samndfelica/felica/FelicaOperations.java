package com.doe.topwisesdk.domain.doecard.samndfelica.felica;

import android.annotation.SuppressLint;
import android.os.RemoteException;
import android.util.Log;

import com.doe.topwisesdk.domain.doecard.samndfelica.SamResponseAndCodeModelClass;
import com.doe.topwisesdk.domain.doecard.samndfelica.Utils;
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.genericdatautils.GenericDataUtils;
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.servicecodes.BalanceDataCodes;
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.servicecodes.GenericDataCodes;
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.servicecodes.TollSpecificDataCodes;
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.servicecodes.TransactionDataCodes;
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.servicecodes.TransactionalLogsDataCodes;
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.tollpassdetailsutils.StaticTollPassDetailsUtils;
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.tollpassdetailsutils.TemporaryTollPassUtils;
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.transactionallogs.TransactionalLogsUtils;
import com.doe.topwisesdk.domain.doecard.samndfelica.sam.SAM;
import com.doe.topwisesdk.domain.doecard.samndfelica.sam.SamCmdCodes;
import com.doe.topwisesdk.domain.models.CardDataModel;
import com.doe.topwisesdk.domain.models.FelicaResponse;
import com.doe.topwisesdk.domain.models.TollDataModelClasses.TollDataModelClass;
import com.doe.topwisesdk.domain.models.TollDataModelClasses.TollTransactionDataModelClass;
import com.doe.topwisesdk.domain.models.genericdata.GenericDataPojoResponse;
import com.doe.topwisesdk.domain.models.tollpass.StaticTollPassDetailPojo;
import com.doe.topwisesdk.domain.models.tollpass.StaticTollPassDetailsResponse;
import com.doe.topwisesdk.domain.models.tollpass.TemporaryTollPassPojo;
import com.doe.topwisesdk.domain.models.tollpass.TemporaryTollPassResponse;
import com.doe.topwisesdk.domain.models.transactionallogs.TransactionalLogsResponse;
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import timber.log.Timber;

import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.APP_ERROR;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.CARD_COMMAND_SUCCEEDED;
import static com.doe.topwisesdk.domain.doecard.samndfelica.ConstantCode.SAM_COMMAND_SUCCEEDED;
import static com.doe.topwisesdk.domain.doecard.samndfelica.Utils.byteToHex;
import static com.doe.topwisesdk.domain.doecard.samndfelica.Utils.bytesToHexString;
import static com.doe.topwisesdk.domain.doecard.samndfelica.felica.transactionallogs.TransactionalLogsUtils.setTransactionalLogError;


/**
 * Created by eshantmittal on 02/01/18.
 */

public class FelicaOperations {

    private final FelicaCmdCodes mFelicaCmdCodes;
    private final SamCmdCodes mSamCmdCodes;
    private final SAM mSam;
    AidlRFCard mRfcard;

    private byte[] IDm;
    private byte[] IDt;
    private byte[] executionId;


    public FelicaOperations(SAM sam, AidlRFCard rfcard) {

        mFelicaCmdCodes = new FelicaCmdCodes();
        mSamCmdCodes = new SamCmdCodes();
        mSam = sam;
        mRfcard = rfcard;
    }

    public long pollingFelicaCard() throws IOException, RemoteException {

        long _ret = 0;

        byte[] pollingCommand;

        ByteArrayOutputStream felicaCmdParams = new ByteArrayOutputStream();
        felicaCmdParams.write(mFelicaCmdCodes.getFelicaSystemCode());     //System Code
        felicaCmdParams.write((byte) 0x00);     //Time slot

        //Get polling command from SAM
        SamResponseAndCodeModelClass samResponseAndCodeModelClass = mSam.askFelicaCommandFromSAM(mFelicaCmdCodes.getFelicaCmdCodePolling(),
                mFelicaCmdCodes.getFelicaSubCmdCodePolling(), felicaCmdParams.toByteArray());

        _ret = samResponseAndCodeModelClass.getErrorCode();
        pollingCommand = samResponseAndCodeModelClass.getResponseByte();
        if (_ret != SAM_COMMAND_SUCCEEDED) {
            //     PrintText("File:%s Line:%d Function:%s AskFeliCaCmdToSAM() failed with error Code:%ld",__FILENAME__, __LINE__,__func__,_ret);
            return _ret;
        }

        ByteBuffer buff = ByteBuffer.wrap(new byte[pollingCommand.length + 1]);
        buff.put((byte) (pollingCommand.length + 1));
        buff.put(pollingCommand);

        Timber.d(" polling command %s", bytesToHexString(buff.array()));
        byte[] data = mRfcard.apduComm(buff.array());

        byte[] iDM = Arrays.copyOfRange(data, 2, 10);
        byte[] pMM = Arrays.copyOfRange(data, 10, 18);

        Timber.d("iDM=%s", bytesToHexString(iDM));
        Timber.d("pMM=%s", bytesToHexString(pMM));

        _ret = mSam.sendFelicaPollingResponseToSAM(iDM, pMM);
        if (_ret != SAM_COMMAND_SUCCEEDED) {
//            PrintText("File:%s Line:%d Function:%s SendPollingResToSAM() failed with error Code:%ld", __FILENAME__, __LINE__, __func__, _ret);
            return _ret;
        }

        this.IDm = iDM;
        Log.e("IDm", "= " + bytesToHexString(IDm));

        return CARD_COMMAND_SUCCEEDED;

    }


    private byte[] readDataWOAuth(int numOfService, byte[] serviceList,
                                  int numOfBlocks, byte[] blockList) throws IOException, RemoteException {

        ByteArrayOutputStream felicaCmdParamsStream = new ByteArrayOutputStream();
        felicaCmdParamsStream.write((byte) 0x06);
        felicaCmdParamsStream.write(IDm);
        felicaCmdParamsStream.write((byte) numOfService);
        felicaCmdParamsStream.write(serviceList);
        felicaCmdParamsStream.write((byte) numOfBlocks);
        felicaCmdParamsStream.write(blockList);

        byte[] felicaCmdParams = felicaCmdParamsStream.toByteArray();

        //Send Command to FeliCa Card
        //Send Command to FeliCa Card
        ByteBuffer buff = ByteBuffer.wrap(new byte[felicaCmdParams.length + 1]);
        buff.put((byte) (felicaCmdParams.length + 1));
        buff.put(felicaCmdParams);

        Log.e("pollingCommand", " " + bytesToHexString(buff.array()));
        byte[] data = mRfcard.apduComm(buff.array());

        Log.e("readDataWOAuth", " " + bytesToHexString(data));

        return Arrays.copyOfRange(data, 13, data.length);
    }


    private byte[] writeDataBlock(int numOfBlocks, byte[] blockList, byte[] blockData) throws IOException, RemoteException {

        // Generate params sent to SAM
        ByteArrayOutputStream felicaCmdParamsStream = new ByteArrayOutputStream();
        felicaCmdParamsStream.write(IDt);                                        //IDt                                        //IDt
        felicaCmdParamsStream.write((byte) numOfBlocks);                                       //Num of blocks
        felicaCmdParamsStream.write(blockList);                                         //BlockList
        felicaCmdParamsStream.write(blockData);                                         //Block Data

        byte[] felicaCmdParams = felicaCmdParamsStream.toByteArray();

        // Ask to SAM to generate FeliCa Command
        byte[] felicaCmd = mSam.askFelicaCommandFromSAM(mFelicaCmdCodes.getFelicaCmdCodeWriteEnc(),
                mFelicaCmdCodes.getFelicaSubCmdCodeWriteEnc(), felicaCmdParams).getResponseByte();

        //Send Command to FeliCa Card
        ByteBuffer buff = ByteBuffer.wrap(new byte[felicaCmd.length + 1]);
        buff.put((byte) (felicaCmd.length + 1));
        buff.put(felicaCmd);

        Log.e("pollingCommand", " " + bytesToHexString(buff.array()));
        byte[] data = mRfcard.apduComm(buff.array());

        byte[] felicaResult = data;

        //Send FeliCa Response to SAM
        return mSam.sendCardResultToSAM(felicaResult);
    }


    private byte[] readDataViaAuth(int numOfBlocks, byte[] blockList) throws IOException, RemoteException {

        // Generate params sent to SAM
        ByteArrayOutputStream felicaCmdParamsStream = new ByteArrayOutputStream();
        felicaCmdParamsStream.write(IDt);
        felicaCmdParamsStream.write((byte) numOfBlocks);
        felicaCmdParamsStream.write(blockList);

        Log.e("numOfBlocks hex", "=" + byteToHex((byte) numOfBlocks));

        byte[] felicaCmdParams = felicaCmdParamsStream.toByteArray();

        Log.e("readDataViaAuth", "felicaCmdFromSAM start");
        Log.e("readDataViaAuth", "felicaCmdFromSAM =" + bytesToHexString(felicaCmdParams));
        // Ask to SAM to generate FeliCa Command.
        byte[] felicaCmdFromSAM = mSam.askFelicaCommandFromSAM(mFelicaCmdCodes.getFelicaCmdCodeReadEnc(),
                mFelicaCmdCodes.getFelicaSubCmdCodeReadEnc(), felicaCmdParams).getResponseByte();
        Log.e("readDataViaAuth", "felicaCmdFromSAM =" + bytesToHexString(felicaCmdFromSAM));
        Log.e("readDataViaAuth", "felicaCmdFromSAM end");

        Log.e("readDataViaAuth", "polling start");
        ByteBuffer buff = ByteBuffer.wrap(new byte[felicaCmdFromSAM.length + 1]);
        buff.put((byte) (felicaCmdFromSAM.length + 1));
        buff.put(felicaCmdFromSAM);

        Log.e("pollingCommand", " " + bytesToHexString(buff.array()));
        byte[] data = mRfcard.apduComm(buff.array());
        Log.e("readDataViaAuth", "polling end");

        byte[] cardResponse = data;
        Log.e("readDataViaAuth", "cardResponse = " + bytesToHexString(cardResponse));

        Log.e("readDataViaAuth", "sendCardResultToSAM start");
        byte[] decryptedCardResponse = mSam.sendCardResultToSAM(cardResponse);
        Log.e("readDataViaAuth", "sendCardResultToSAM end");


        Log.e("readDataViaAuth", "=" + bytesToHexString(decryptedCardResponse));

        return Arrays.copyOfRange(decryptedCardResponse, 3, decryptedCardResponse.length);
    }


    @SuppressLint("LongLogTag")
    private boolean mutualAuthWithFelicaV2(int numOfService, byte[] serviceCodeList) {

        // Diversification code(All Zero)
        byte[] diversificationCode = new byte[]{
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

        try {

            // Generate params sent to SAM
            ByteArrayOutputStream felicaCmdParamStream = new ByteArrayOutputStream();
            felicaCmdParamStream.write(IDm);                                        // IDm
            felicaCmdParamStream.write((byte) 0x00);                                // Reserved
            felicaCmdParamStream.write((byte) 0x03);                                // Key Type(Node key)
            felicaCmdParamStream.write(mFelicaCmdCodes.getFelicaSystemCode());  // SystemCode(Big endian)
            felicaCmdParamStream.write((byte) 0x00);                                // Operation Parameter(No Diversification, AES128)
            felicaCmdParamStream.write(diversificationCode);                        // Diversification code(All Zero)
            felicaCmdParamStream.write((byte) numOfService);                        // Number of Service
            felicaCmdParamStream.write(serviceCodeList);                            // Service Code List

            // Ask to SAM to generate FeliCa Command
            byte[] felicaMutualAuthSAMCmd = new byte[0];


            Log.e("mutualAuthWithFelicaV2", "askFelicaCommandFromSAM1 start");
            Log.e("mutualAuthWithFelicaV2", "constructed felicaMutualAuth1SAMCmd = " + bytesToHexString(felicaCmdParamStream.toByteArray()));

            felicaMutualAuthSAMCmd = mSam.askFelicaCommandFromSAM(mSamCmdCodes.getSamCmdCodeMutualAuthV2RwSam(),
                    mSamCmdCodes.getSamSubCmdCodeMutualAuthV2RwSam(), felicaCmdParamStream.toByteArray()).getResponseByte();

            Log.e("mutualAuthWithFelicaV2", "from SAM felicaMutualAuth1SAMCmd = " + bytesToHexString(felicaMutualAuthSAMCmd));
            Log.e("mutualAuthWithFelicaV2", "askFelicaCommandFromSAM1 End");

            ByteBuffer buff = ByteBuffer.wrap(new byte[felicaMutualAuthSAMCmd.length + 1]);
            buff.put((byte) (felicaMutualAuthSAMCmd.length + 1));
            buff.put(felicaMutualAuthSAMCmd);

            Timber.d("mutualAuthWithFelicaV2 pollingCommand1=%s", bytesToHexString(buff.array()));
            byte[] data = mRfcard.apduComm(buff.array());
            Timber.d("rfcard open =%s", mRfcard.open());
            if (mRfcard.isExist()) {
                data = mRfcard.apduComm(buff.array());
                Timber.d("from card response mutualAuthWithFelicaV2 data to pollingCommand1=%s", bytesToHexString(data));
            } else {
                Timber.d("rfcard does not exist");
            }


            byte[] cardResponse = data;

            Log.e("mutualAuthWithFelicaV2", "sendAuth1V2ResultToSAM start");

            byte[] auth1V2SAMResponse = mSam.sendAuth1V2ResultToSAM(cardResponse);
            Log.e("mutualAuthWithFelicaV2 from SAM auth1V2SAMResponse", bytesToHexString(auth1V2SAMResponse));
            Log.e("mutualAuthWithFelicaV2", "sendAuth1V2ResultToSAM end");

            Log.e("mutualAuthWithFelicaV2", "polling2 start");
            buff = ByteBuffer.wrap(new byte[auth1V2SAMResponse.length + 1]);
            buff.put((byte) (auth1V2SAMResponse.length + 1));
            buff.put(auth1V2SAMResponse);

            Log.e("mutualAuthWithFelicaV2 pollingCommand2", " " + bytesToHexString(buff.array()));
            data = mRfcard.apduComm(buff.array());
            Timber.d("from card response mutualAuthWithFelicaV2 data to pollingCommand2=%s", bytesToHexString(data));
            Log.e("mutualAuthWithFelicaV2", "polling2 end");
            byte[] cardResponse2 = data;

            Log.e("mutualAuthWithFelicaV2", "sendCardResultToSAM start");
            byte[] auth2V2SAMResponse = mSam.sendCardResultToSAM(cardResponse2);
            Log.e("mutualAuthWithFelicaV2 from SAM auth2V2SAMResponse", bytesToHexString(auth2V2SAMResponse));
            Log.e("mutualAuthWithFelicaV2", "sendCardResultToSAM2 end");


            if (auth2V2SAMResponse[0] == 0) {
                //Get IDt
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(auth2V2SAMResponse[17]);
                byteArrayOutputStream.write(auth2V2SAMResponse[18]);

                this.IDt = byteArrayOutputStream.toByteArray();

                Log.e("mutualAuthWithFelicaV2 IDt", "=" + IDt);

                return true;

            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;
    }


//////////////////////////***********************************************//////////////////////////////////

    public byte[] readCardNumWOAuth() throws IOException, RemoteException {

        pollingFelicaCard();

        int numOfService = 1;
        int numOfBlocks = 1;

        GenericDataCodes genericDataCodes = new GenericDataCodes();

        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();
        serviceList.write(genericDataCodes.getSrvCodeCardNumWOEnc());

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
        blockList.write((byte) 0x80);            //Card Number(16)
        blockList.write((byte) 0x00);

        return readDataWOAuth(numOfService, serviceList.toByteArray(), numOfBlocks, blockList.toByteArray());

    }

    public byte[] readGenericData() throws IOException, RemoteException {

        pollingFelicaCard();

        int numOfService = 3;
        int numOfBlocks = 7;

        GenericDataCodes genericDataCodes = new GenericDataCodes();

        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();
        serviceList.write(genericDataCodes.getSrvCodeUserData1Enc());
        serviceList.write(genericDataCodes.getSrvCodeUserData2Enc());
        serviceList.write(genericDataCodes.getSrvCodeCardNumEnc());

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x00);
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x01);
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x02);
        blockList.write((byte) 0x80);            //Vehicle type(1), Status(1), Version(1), Gender(1), DOB(4), Expiry DateTime(4), LastSyncedTime(4)
        blockList.write((byte) 0x03);
        blockList.write((byte) 0x80);            //Vehicle Number(16)
        blockList.write((byte) 0x04);

        blockList.write((byte) 0x81);            //Card issue date time(4), Ph no(6), Aadhaar id(5), Unused(1)
        blockList.write((byte) 0x00);

        blockList.write((byte) 0x82);            //Card Number(16)
        blockList.write((byte) 0x00);

        //Mutual authentication between SAM and Felica Card
        mutualAuthWithFelicaV2(numOfService, serviceList.toByteArray());

        return readDataViaAuth(numOfBlocks, blockList.toByteArray());


    }

    public byte[] readGenericDataWithBalance() throws IOException, RemoteException {

        pollingFelicaCard();

        int numOfService = 4;
        int numOfBlocks = 8;

        GenericDataCodes genericDataCodes = new GenericDataCodes();
        TransactionDataCodes ptDataCodes = new TransactionDataCodes();

        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();
        serviceList.write(genericDataCodes.getSrvCodeUserData1Enc());
        serviceList.write(genericDataCodes.getSrvCodeUserData2Enc());
        serviceList.write(genericDataCodes.getSrvCodeCardNumEnc());
        serviceList.write(new BalanceDataCodes().getSrvCodeBalanceDAEnc());

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x00);
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x01);
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x02);
        blockList.write((byte) 0x80);            //Vehicle type(1), Status(1), Version(1), Gender(1), DOB(4), Expiry DateTime(4), LastSyncedTime(4)
        blockList.write((byte) 0x03);
        blockList.write((byte) 0x80);            //Vehicle Number(16)
        blockList.write((byte) 0x04);

        blockList.write((byte) 0x81);            //Card issue date time(4), Ph no(6), Aadhaar id(5), Unused(1)
        blockList.write((byte) 0x00);

        blockList.write((byte) 0x82);            //Card Number(16)
        blockList.write((byte) 0x00);

        blockList.write((byte) 0x83);            //Card Balance(16)
        blockList.write((byte) 0x00);


        //Mutual authentication between SAM and Felica Card
        mutualAuthWithFelicaV2(numOfService, serviceList.toByteArray());

        return readDataViaAuth(numOfBlocks, blockList.toByteArray());

    }

    public boolean updateCard(CardDataModel cardDataModel) throws IOException, RemoteException {

        //Log.e("cardDataModel========================================================", "==" + new Gson().toJson(cardDataModel));


        pollingFelicaCard();

        int numOfService = 1;
        int numOfBlocks = 5;

        GenericDataCodes genericDataCodes = new GenericDataCodes();

        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();
        serviceList.write(genericDataCodes.getSrvCodeUserData1Enc());
//        serviceList.write(genericDataCodes.getSrvCodeUserData2Enc());

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x00);
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x01);
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x02);
        blockList.write((byte) 0x80);            //Vehicle type(1), Status(1), Version(1), Gender(1), DOB(4), Expiry DateTime(4), LastSyncedTime(4)
        blockList.write((byte) 0x03);
        blockList.write((byte) 0x80);            //Vehicle Number(16)
        blockList.write((byte) 0x04);
//
//        blockList.write((byte) 0x81);            //Card issue date time(4), Ph no(6), Aadhaar id(5), Unused(1)
//        blockList.write((byte) 0x00);

        Utils utils = new Utils();

        ByteArrayOutputStream blockData = new ByteArrayOutputStream();

        //Name(48)
        if (cardDataModel.getCardName_String().trim().getBytes().length < 48) {
            blockData.write(cardDataModel.getCardName_String().trim().getBytes());
            utils.appendZeroStream(blockData,
                    48 - cardDataModel.getCardName_String().trim().getBytes().length);                        //Remaining Blank bits of Name
        } else if (cardDataModel.getCardName_String().trim().getBytes().length > 48) {
            blockData.write(Arrays.copyOfRange(cardDataModel.getCardName_String().trim().getBytes(), 0, 48));
        } else {
            blockData.write(cardDataModel.getCardName_String().trim().getBytes());
        }

        //Vehicle type(1), Status(1), Version(1), Gender(1), DOB(4), Expiry Date(4), LastSyncedTime(4)
//        blockData.write(mSam.bigIntToByteArray(cardDataModel.getVehicleType_Int()));
//        blockData.write(mSam.bigIntToByteArray(cardDataModel.getCardStatus_Int()));
//        blockData.write(mSam.bigIntToByteArray(cardDataModel.getCardVersion_Int()));
//        blockData.write(mSam.bigIntToByteArray(cardDataModel.getGender_Int()));

        blockData.write(mSam.bigIntToByteArray(cardDataModel.getVehicleType_Int()));
        blockData.write((byte) cardDataModel.getCardStatus_Int());
        blockData.write(mSam.bigIntToByteArray(cardDataModel.getCardVersion_Int()));
        blockData.write(mSam.bigIntToByteArray(cardDataModel.getGender_Int()));

        blockData.write(cardDataModel.getDob_String().getBytes());
        if (cardDataModel.getDob_String().trim().getBytes().length < 4) {
            utils.appendZeroStream(blockData,
                    4 - cardDataModel.getDob_String().getBytes().length);        //Remaining Blank bits of DOB
        }
        blockData.write(cardDataModel.getCardExpiryDate_String().getBytes());
        if (cardDataModel.getCardExpiryDate_String().trim().getBytes().length < 4) {
            utils.appendZeroStream(blockData,
                    4 - cardDataModel.getCardExpiryDate_String().getBytes().length);        //Remaining Blank bits of Expiry Date
        }
        blockData.write(cardDataModel.getLastSyncTime_String().getBytes());
        if (cardDataModel.getLastSyncTime_String().trim().getBytes().length < 4) {
            utils.appendZeroStream(blockData,
                    4 - cardDataModel.getLastSyncTime_String().getBytes().length);        //Remaining Blank bits of LastSyncedTime
        }

        // Log.e("1 blockData============================================== " + blockData.size(), "===" + bytesToHexString(blockData.toByteArray()));

        //Vehicle Number(16)
        if (cardDataModel.getVehicleNumber_String().trim().getBytes().length < 16) {
            blockData.write(cardDataModel.getVehicleNumber_String().trim().getBytes());
            utils.appendZeroStream(blockData,
                    16 - cardDataModel.getVehicleNumber_String().trim().getBytes().length);
        } else if (cardDataModel.getVehicleNumber_String().trim().getBytes().length > 16) {
            blockData.write(Arrays.copyOfRange(cardDataModel.getVehicleNumber_String().trim().getBytes(), 0, 16));
        } else {
            blockData.write(cardDataModel.getVehicleNumber_String().trim().getBytes());
        }
//
//
//        //Card issue date time(4), Ph no(6), Aadhaar id(5), Unused(1)
//        blockData.write(mSam.LongToCharArrayLen(cardDataModel.getCardIssueDateTime_Long(), 4));
////        if (cardDataModel.getCardIssueDateTime_String().getBytes().length < 4) {
////            utils.appendZeroStream(blockData,
////                    4 - cardDataModel.getCardIssueDateTime_String().getBytes().length);        //Remaining Blank bits of Card issue date time
////        }
//        blockData.write(mSam.LongToCharArrayLen(cardDataModel.getMobileNumber_Long(), 6));
//        blockData.write(mSam.LongToCharArrayLen(cardDataModel.getAadhaarNumber_Long(), 5));
//        blockData.write((byte) 0);


        if (!mutualAuthWithFelicaV2(numOfService, serviceList.toByteArray())) {
            return false;
        }

        // Log.e("blockData============================================== " + blockData.size(), "===" + bytesToHexString(blockData.toByteArray()));

        byte[] res = writeDataBlock(numOfBlocks, blockList.toByteArray(), blockData.toByteArray());

        return res[0] == (byte) 0x00 && res[1] == (byte) 0x00;

//        return true;
    }


    private byte[] readGenericDataForToll() throws IOException, RemoteException {

        int numOfBlocks = 5;

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
//        blockList.write((byte) 0x80);            //Name(16)
//        blockList.write((byte) 0x00);
//        blockList.write((byte) 0x80);            //Name(16)
//        blockList.write((byte) 0x01);
//        blockList.write((byte) 0x80);            //Name(16)
//        blockList.write((byte) 0x02);
        blockList.write((byte) 0x80);            //Vehicle type(1), Status(1), Version(1), Gender(1), DOB(4), Expiry DateTime(4), LastSyncedTime(4)
        blockList.write((byte) 0x03);
        blockList.write((byte) 0x80);            //Vehicle Number(16)
        blockList.write((byte) 0x04);

        blockList.write((byte) 0x81);            //Card issue date time(4), Ph no(6), Aadhaar id(5), Unused(1)
        blockList.write((byte) 0x00);

        blockList.write((byte) 0x82);            //Card Number(16)
        blockList.write((byte) 0x00);

        blockList.write((byte) 0x83);            //Card Balance(16)
        blockList.write((byte) 0x00);

        return readDataViaAuth(numOfBlocks, blockList.toByteArray());
    }

    private CardDataModel readGenericDataForTollFromBytes(byte[] readData) {

        Log.e("readData==", "==" + readData.length);


        CardDataModel cardDataModel = new CardDataModel();

        cardDataModel.setVehicleType_Int(mSam.byteToInt(Arrays.copyOfRange(readData, 0, 1)[0]));
        cardDataModel.setCardStatus_Int(mSam.byteToInt(Arrays.copyOfRange(readData, 1, 2)[0]));
        cardDataModel.setCardVersion_Int(mSam.byteToInt(Arrays.copyOfRange(readData, 2, 3)[0]));
        cardDataModel.setGender_Int(mSam.byteToInt(Arrays.copyOfRange(readData, 3, 4)[0]));
        cardDataModel.setDob_String(new Utils().hexToString(bytesToHexString(Arrays.copyOfRange(readData, 4, 8))));
        cardDataModel.setCardExpiryDate_String(new Utils().hexToString(bytesToHexString(Arrays.copyOfRange(readData, 8, 12))));
        cardDataModel.setLastSyncTime_String(new Utils().hexToString(bytesToHexString(Arrays.copyOfRange(readData, 12, 16))));
        cardDataModel.setVehicleNumber_String(new Utils().hexToString(bytesToHexString(Arrays.copyOfRange(readData, 16, 32))));
        cardDataModel.setCardIssueDateTime_Long(new Utils().bytesToLong(Arrays.copyOfRange(readData, 32, 36), 4));
        cardDataModel.setMobileNumber_Long(new Utils().bytesToLong(Arrays.copyOfRange(readData, 36, 42), 6));
        cardDataModel.setAadhaarNumber_Long(new Utils().bytesToLong(Arrays.copyOfRange(readData, 42, 47), 5));
        cardDataModel.setCardNumber_Long(new Utils().bytesToLong(Arrays.copyOfRange(readData, 48, 64), 16));
        cardDataModel.setCardBalance_Int(ByteBuffer.wrap(Arrays.copyOfRange(readData, 64, 68)).order(ByteOrder.LITTLE_ENDIAN).getInt());

        executionId = Arrays.copyOfRange(readData, 78, 80);

        Log.e("executionId ", bytesToHexString(executionId));

        return cardDataModel;
    }

    private byte[] readTollRelatedData() throws IOException, RemoteException {

        int numOfBlocks = 7;

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
//        blockList.write((byte) 0x85);             // Toll Passes(16)
//        blockList.write((byte) 0x00);
//        blockList.write((byte) 0x85);             // Toll Passes(16)
//        blockList.write((byte) 0x01);
//        blockList.write((byte) 0x85);             // Toll Passes(16)
//        blockList.write((byte) 0x02);
//        blockList.write((byte) 0x85);             // Toll Passes(16)
//        blockList.write((byte) 0x03);
//        blockList.write((byte) 0x85);             // Toll Passes(16)
//        blockList.write((byte) 0x04);
//        blockList.write((byte) 0x85);             // Toll Passes(16)
//        blockList.write((byte) 0x05);

        blockList.write((byte) 0x86);               // Toll Transaction(16)
        blockList.write((byte) 0x00);
        blockList.write((byte) 0x86);               // Toll Transaction(16)
        blockList.write((byte) 0x01);
        blockList.write((byte) 0x86);               // Toll Transaction(16)
        blockList.write((byte) 0x02);
        blockList.write((byte) 0x86);               // Toll Transaction(16)
        blockList.write((byte) 0x03);
        blockList.write((byte) 0x86);               // Toll Transaction(16)
        blockList.write((byte) 0x04);
        blockList.write((byte) 0x86);               // Toll Transaction(16)
        blockList.write((byte) 0x05);
        blockList.write((byte) 0x86);               // Toll Transaction(16)
        blockList.write((byte) 0x06);


        return readDataViaAuth(numOfBlocks, blockList.toByteArray());
    }

    private TollDataModelClass readTollRelatedDataFromBytes(byte[] readData, long branchId) {

        TollDataModelClass tollDataModelClass = null;

        for (int i = 0, index = 0; i < 112; i += 16, index++) {
            //Log.e("TollTransactionDataModelClass", "TollTransactionDataModelClass");

            if (branchId == new Utils().bytesToLong(Arrays.copyOfRange(readData, i, i + 4), 4) && Utility.getUTCSecond() < new Utils().bytesToLong(Arrays.copyOfRange(readData, i + 8, i + 12), 4)) {

                TollTransactionDataModelClass tollTransactionDataModelClass = new TollTransactionDataModelClass();

                tollTransactionDataModelClass.setBranchId(new Utils().bytesToLong(Arrays.copyOfRange(readData, i, i + 4), 4));
                tollTransactionDataModelClass.setInDateTime(new Utils().bytesToLong(Arrays.copyOfRange(readData, i + 4, i + 8), 4));
                tollTransactionDataModelClass.setExpiryDateTime(new Utils().bytesToLong(Arrays.copyOfRange(readData, i + 8, i + 12), 4));

                tollDataModelClass = new TollDataModelClass();
                tollDataModelClass.setTollTransactionDataModelClasses(tollTransactionDataModelClass);
            }


        }


        return tollDataModelClass;
    }

    public void updateExecutionID() {
        int executionIdValue = Utils.charArrayToIntLE(executionId);
        // Update Snr
        // If _received_snr_value = 0xffffffff, incrementing _snr_value will take it to zero. Semantically is not correct.
        // Sending next command without incrementing snr, will cause SAM to throw syntax error.
        // This syntax error is handled and treated as snr overflow error.
        // Motivation was to have snr over flow error check at one place.
        if (executionIdValue != 0xffff) {
            executionIdValue += 1;
        } else {
            executionIdValue = 0;
        }

        executionId = Utils.IntToCharArrayLE(executionIdValue, 2);

    }

    private byte[] readTollDataPart2() throws IOException, RemoteException {

        int numOfBlocks = 13;

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
        blockList.write((byte) 0x85);            // Toll Passes(16)
        blockList.write((byte) 0x00);
        blockList.write((byte) 0x85);            // Toll Passes(16)
        blockList.write((byte) 0x01);
        blockList.write((byte) 0x85);            // Toll Passes(16)
        blockList.write((byte) 0x02);
        blockList.write((byte) 0x85);            // Toll Passes(16)
        blockList.write((byte) 0x03);
        blockList.write((byte) 0x85);            // Toll Passes(16)
        blockList.write((byte) 0x04);
        blockList.write((byte) 0x85);            // Toll Passes(16)
        blockList.write((byte) 0x05);

        blockList.write((byte) 0x86);            //Toll Transaction(16)
        blockList.write((byte) 0x00);
        blockList.write((byte) 0x86);            //Toll Transaction(16)
        blockList.write((byte) 0x01);
        blockList.write((byte) 0x86);            //Toll Transaction(16)
        blockList.write((byte) 0x02);
        blockList.write((byte) 0x86);            //Toll Transaction(16)
        blockList.write((byte) 0x03);
        blockList.write((byte) 0x86);            //Toll Transaction(16)
        blockList.write((byte) 0x04);
        blockList.write((byte) 0x86);            //Toll Transaction(16)
        blockList.write((byte) 0x05);
        blockList.write((byte) 0x86);            //Toll Transaction(16)
        blockList.write((byte) 0x06);


        return readDataViaAuth(numOfBlocks, blockList.toByteArray());
    }


    private CardDataModel writeTollDataToIndex(int index, CardDataModel cardDataModel, double amtToDeduct) throws IOException, RemoteException {

        Log.e("ALPARTransport_Time", "writeTollDataToIndex start");

        int numOfBlocks = 2;
        ByteArrayOutputStream blockList = new ByteArrayOutputStream();

        blockList.write((byte) 0x83);            //Card Balance(16)
        blockList.write((byte) 0x00);

        switch (index) {
            case 0:
                blockList.write((byte) 0x86);            //Temporary Toll passes(16)
                blockList.write((byte) 0x00);
                break;
            case 1:
                blockList.write((byte) 0x86);            //Temporary Toll passes(16)
                blockList.write((byte) 0x01);
                break;
            case 2:
                blockList.write((byte) 0x86);            //Temporary Toll passes(16)
                blockList.write((byte) 0x02);
                break;
            case 3:
                blockList.write((byte) 0x86);            //Temporary Toll passes(16)
                blockList.write((byte) 0x03);
                break;
            case 4:
                blockList.write((byte) 0x86);            //Temporary Toll passes(16)
                blockList.write((byte) 0x04);
                break;
            case 5:
                blockList.write((byte) 0x86);            //Temporary Toll passes(16)
                blockList.write((byte) 0x05);
                break;
            case 6:
                blockList.write((byte) 0x86);            //Temporary Toll passes(16)
                blockList.write((byte) 0x06);
                break;
        }


        ByteArrayOutputStream blockData = new ByteArrayOutputStream();

        int newBalance = cardDataModel.getCardBalance_Int() - (int) amtToDeduct;
        blockData.write(mSam.IntToCharArrayLE(newBalance, 4));
        new Utils().appendZeroStream(blockData, 12);

        if (cardDataModel.getBranchId_String().isEmpty()) {
            new Utils().appendZeroStream(blockData, 16);
        } else {
            blockData.write(cardDataModel.getBranchId_String().getBytes());
            new Utils().appendZeroStream(blockData, 4 - cardDataModel.getBranchId_String().getBytes().length);

            blockData.write(mSam.LongToCharArrayLen(cardDataModel.getInDateTime_Long(), 4));

            blockData.write(mSam.LongToCharArrayLen(cardDataModel.getTollFeeExpiryDateTime_Long(), 4));

            byte[] singleJourney = mSam.IntToCharArrayLE(cardDataModel.getSingleJourneyAmt_Int(), 2);
            Log.e("singleJourney", "=" + cardDataModel.getSingleJourneyAmt_Int());
            Log.e("singleJourney", "=" + bytesToHexString(singleJourney));
            blockData.write(singleJourney);

            new Utils().appendZeroStream(blockData, 2);
        }

        byte[] res = writeDataBlock(numOfBlocks, blockList.toByteArray(), blockData.toByteArray());

        if (res[0] == (byte) 0x00 && res[1] == (byte) 0x00) {
            cardDataModel.setCardBalance_Int(newBalance);
        }

        Log.e("ALPARTransport_Time", "writeTollDataToIndex end");

        return cardDataModel;
    }

    public GenericDataPojoResponse getGenericData() throws IOException, RemoteException {

        long val = pollingFelicaCard();
        if (val == APP_ERROR) {
            return GenericDataUtils.setGenericDataPojoResponseError("Polling error");
        }

        int numOfService = 4;
        int numOfBlocks = 8;

        GenericDataCodes genericDataCodes = new GenericDataCodes();
        TransactionDataCodes ptDataCodes = new TransactionDataCodes();

        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();
        serviceList.write(genericDataCodes.getSrvCodeUserData1Enc());
        serviceList.write(genericDataCodes.getSrvCodeUserData2Enc());
        serviceList.write(genericDataCodes.getSrvCodeCardNumEnc());
        serviceList.write(new BalanceDataCodes().getSrvCodeBalanceDAEnc());

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x00);
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x01);
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x02);
        blockList.write((byte) 0x80);            //Vehicle type(1), Status(1), Version(1), Gender(1), DOB(4), Expiry DateTime(4), LastSyncedTime(4)
        blockList.write((byte) 0x03);
        blockList.write((byte) 0x80);            //Vehicle Number(16)
        blockList.write((byte) 0x04);

        blockList.write((byte) 0x81);            //Card issue date time(4), Ph no(6), Aadhaar id(5), Unused(1)
        blockList.write((byte) 0x00);

        blockList.write((byte) 0x82);            //Card Number(16)
        blockList.write((byte) 0x00);

        blockList.write((byte) 0x83);            //Card Balance(16)
        blockList.write((byte) 0x00);


        //Mutual authentication between SAM and Felica Card
        if (!mutualAuthWithFelicaV2(numOfService, serviceList.toByteArray())) {
            Timber.e("getGenericData mutual authentication failed");
            return GenericDataUtils.setGenericDataPojoResponseError("mutual authentication failed");
        }

        byte[] response = readDataViaAuth(numOfBlocks, blockList.toByteArray());
        if (response.length > 0) {
            Timber.d("getGenericData successful");
            return GenericDataUtils.getGenericDataPojoResponse(response);
        } else {
            Timber.d("getGenericData successful but response length is 0");
            return GenericDataUtils.setGenericDataPojoResponseError("unknown error");
        }

    }

    public boolean activateCard(CardDataModel cardDataModel) throws IOException, RemoteException {


        //Log.e("cardDataModel========================================================", "==" + new Gson().toJson(cardDataModel));

        long val = pollingFelicaCard();
        if (val == APP_ERROR) {
            return false;
        }

        int numOfService = 4;
        int numOfBlocks = 8;

        GenericDataCodes genericDataCodes = new GenericDataCodes();
        TransactionDataCodes ptDataCodes = new TransactionDataCodes();

        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();
        serviceList.write(genericDataCodes.getSrvCodeUserData1Enc());
        serviceList.write(genericDataCodes.getSrvCodeUserData2Enc());
        serviceList.write(new BalanceDataCodes().getSrvCodeBalanceDAEnc());
        serviceList.write(TransactionalLogsDataCodes.getSrvCodeTransationalLogsEnc());

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x00);
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x01);
        blockList.write((byte) 0x80);            //Name(16)
        blockList.write((byte) 0x02);
        blockList.write((byte) 0x80);            //Vehicle type(1), Status(1), Version(1), Gender(1), DOB(4), Expiry DateTime(4), LastSyncedTime(4)
        blockList.write((byte) 0x03);
        blockList.write((byte) 0x80);            //Vehicle Number(16)
        blockList.write((byte) 0x04);

        blockList.write((byte) 0x81);            //Card issue date time(4), Ph no(6), Aadhaar id(5), Unused(1)
        blockList.write((byte) 0x00);

        blockList.write((byte) 0x82);            //Card Balance(16)
        blockList.write((byte) 0x00);

        blockList.write((byte) 0x83);            //cyclic logs
        blockList.write((byte) 0x00);

        //blockList.write((byte) 0x83);            //Card Number(16)
        //blockList.write((byte) 0x00);

        Utils utils = new Utils();

        ByteArrayOutputStream blockData = new ByteArrayOutputStream();

        //Name(48)
        if (cardDataModel.getCardName_String().trim().getBytes().length < 48) {
            blockData.write(cardDataModel.getCardName_String().trim().getBytes());
            utils.appendZeroStream(blockData,
                    48 - cardDataModel.getCardName_String().trim().getBytes().length);                        //Remaining Blank bits of Name
        } else if (cardDataModel.getCardName_String().trim().getBytes().length > 48) {
            blockData.write(Arrays.copyOfRange(cardDataModel.getCardName_String().trim().getBytes(), 0, 48));
        } else {
            blockData.write(cardDataModel.getCardName_String().trim().getBytes());
        }

        Log.e("CardName " + blockData.size(), bytesToHexString(blockData.toByteArray()));

        //Vehicle type(1), Status(1), Version(1), Gender(1), DOB(4), Expiry Date(4), LastSyncedTime(4)
        blockData.write(mSam.bigIntToByteArray(cardDataModel.getVehicleType_Int()));
        blockData.write((byte) cardDataModel.getCardStatus_Int());
        blockData.write(mSam.bigIntToByteArray(cardDataModel.getCardVersion_Int()));
        blockData.write(mSam.bigIntToByteArray(cardDataModel.getGender_Int()));
        utils.appendZeroStream(blockData, 4);
        utils.appendZeroStream(blockData, 4);
        utils.appendZeroStream(blockData, 4);

        Log.e("Vehicle Info " + blockData.size(), bytesToHexString(blockData.toByteArray()));


        //Vehicle Number(16)
        if (cardDataModel.getVehicleNumber_String().trim().getBytes().length < 16) {
            blockData.write(cardDataModel.getVehicleNumber_String().trim().getBytes());
            utils.appendZeroStream(blockData,
                    16 - cardDataModel.getVehicleNumber_String().trim().getBytes().length);
        } else if (cardDataModel.getVehicleNumber_String().trim().getBytes().length > 16) {
            blockData.write(Arrays.copyOfRange(cardDataModel.getVehicleNumber_String().trim().getBytes(), 0, 16));
        } else {
            blockData.write(cardDataModel.getVehicleNumber_String().trim().getBytes());
        }

        Log.e("Vehicle Number " + blockData.size(), bytesToHexString(blockData.toByteArray()));


        //Card issue date time(4), Ph no(6), Aadhaar id(5), Unused(1)
        blockData.write(mSam.LongToCharArrayLen(cardDataModel.getCardIssueDateTime_Long(), 4));
//        if (cardDataModel.getCardIssueDateTime_String().getBytes().length < 4) {
//            utils.appendZeroStream(blockData,
//                    4 - cardDataModel.getCardIssueDateTime_String().getBytes().length);        //Remaining Blank bits of Card issue date time
//        }
        blockData.write(mSam.LongToCharArrayLen(cardDataModel.getMobileNumber_Long(), 6));
        blockData.write(mSam.LongToCharArrayLen(cardDataModel.getAadhaarNumber_Long(), 5));
        blockData.write((byte) 0);


        Log.e("Issue Date Time " + blockData.size(), bytesToHexString(blockData.toByteArray()));


        //Card Balance(16)
        blockData.write(mSam.IntToCharArrayLE(cardDataModel.getRechargeAmount_Int(), 4));

        Log.e("Balance", "" + bytesToHexString(mSam.IntToCharArrayLE(cardDataModel.getRechargeAmount_Int(), 4)));

        new Utils().appendZeroStream(blockData, 12);

        Log.e("Balance " + blockData.size(), bytesToHexString(blockData.toByteArray()));

        //writing transactional logs
        blockData.write((byte) cardDataModel.getTransactionType());
        blockData.write(mSam.LongToCharArrayLen(Utility.getUTCSecond(), 4));
        new Utils().appendZeroStream(blockData, 2);
        // mSam.LongToCharArrayLen(amount, 4)
        blockData.write(mSam.LongToCharArrayLen(cardDataModel.getRechargeAmount_Int(), 4));
        blockData.write(mSam.LongToCharArrayLen(cardDataModel.getTerminalId(),4));
        blockData.write((byte) 0);


        //Card Number(16)
        //blockData.write(mSam.LongToCharArrayLen(1234567890123456L, 16));


        if (!mutualAuthWithFelicaV2(numOfService, serviceList.toByteArray())) {
            return false;
        }

        //Log.e("blockData============================================== " + blockData.size(), "===" + bytesToHexString(blockData.toByteArray()));

        byte[] res = writeDataBlock(numOfBlocks, blockList.toByteArray(), blockData.toByteArray());

        return res[0] == (byte) 0x00 && res[1] == (byte) 0x00;
    }

    public boolean rechargeCard(RechargeRequestClass rechargeRequestClass) throws IOException, RemoteException {

        int numOfService = 2;
        int numOfBlocks = 2;

        TransactionDataCodes ptDataCodes = new TransactionDataCodes();

        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();
        serviceList.write(new BalanceDataCodes().getSrvCodeBalanceDAEnc());
        serviceList.write(TransactionalLogsDataCodes.getSrvCodeTransationalLogsEnc());

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
        blockList.write((byte) 0x80);            //Card Balance(16)
        blockList.write((byte) 0x00);

        blockList.write((byte) 0x81);//cyclic log
        blockList.write((byte) 0x00);

        ByteArrayOutputStream blockData = new ByteArrayOutputStream();

        blockData.write(mSam.IntToCharArrayLE(Integer.parseInt(rechargeRequestClass.getAmount().getValue()), 4));
        new Utils().appendZeroStream(blockData, 12);

        //writing transactional logs
        blockData.write((byte) rechargeRequestClass.getTransactionType());
        blockData.write(mSam.LongToCharArrayLen(Utility.getUTCSecond(), 4));
        new Utils().appendZeroStream(blockData, 2);
        //mSam.LongToCharArrayLen(amount, 4)
        long amount= Long.parseLong(rechargeRequestClass.getAmount().getValue());
        blockData.write(mSam.LongToCharArrayLen(amount, 4));
        blockData.write(mSam.LongToCharArrayLen(rechargeRequestClass.getTerminalId(),4));
        blockData.write((byte) 0);

        if (!mutualAuthWithFelicaV2(numOfService, serviceList.toByteArray())) {
            return false;
        }

        byte[] res = writeDataBlock(numOfBlocks, blockList.toByteArray(), blockData.toByteArray());

        return res[0] == (byte) 0x00 && res[1] == (byte) 0x00;

    }

    public TransactionalLogsResponse getTransactionalLog() throws IOException, RemoteException {

        long val = pollingFelicaCard();
        if (val == APP_ERROR) {
            Timber.e("polling error while getting transaction log");
            return setTransactionalLogError("polling error while getting transaction log");
        }

        int numOfService = 1;
        int numOfBlocks = 5;

        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();
        serviceList.write(TransactionalLogsDataCodes.srvCodeTransactionalLogs);

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
        blockList.write((byte) 0x80);            //transactional log 1
        blockList.write((byte) 0x00);

        blockList.write((byte) 0x80);            //transactional log 2
        blockList.write((byte) 0x01);

        blockList.write((byte) 0x80);            //transactional log 3
        blockList.write((byte) 0x02);

        blockList.write((byte) 0x80);            //transactional log 4
        blockList.write((byte) 0x03);

        blockList.write((byte) 0x80);            //transactional log 5
        blockList.write((byte) 0x04);

        byte[] response = readDataWOAuth(numOfService, serviceList.toByteArray(), numOfBlocks, blockList.toByteArray());
        if (response.length > 0) {
            return TransactionalLogsUtils.getTransactionalLogList(response);
        } else {
            return setTransactionalLogError("unknown Error");
        }

    }

    public TemporaryTollPassResponse readAllTemporaryPass() throws IOException, RemoteException {

        long val = pollingFelicaCard();
        if (val == APP_ERROR) {
            return TemporaryTollPassUtils.setTemporaryTollPassError("polling error");
        }
        int numOfService = 1;
        int numOfBlocks = 6;
        TollSpecificDataCodes serviceCode = new TollSpecificDataCodes();
        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();
        serviceList.write(serviceCode.getSrvCodeTollTransEnc());//getSrvCodeTollPassEnc

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
        blockList.write((byte) 0x80); // branch id(4),In DateTime(4),Expiry DateTime(4)
        blockList.write((byte) 0x00);
        blockList.write((byte) 0x80); // branch id(4),In DateTime(4),Expiry DateTime(4)
        blockList.write((byte) 0x01);

        blockList.write((byte) 0x80); // branch id(4),In DateTime(4),Expiry DateTime(4)
        blockList.write((byte) 0x02);
        blockList.write((byte) 0x80); // branch id(4),In DateTime(4),Expiry DateTime(4)
        blockList.write((byte) 0x03);

        blockList.write((byte) 0x80); // branch id(4),In DateTime(4),Expiry DateTime(4)
        blockList.write((byte) 0x04);
        blockList.write((byte) 0x80);// branch id(4),In DateTime(4),Expiry DateTime(4)
        blockList.write((byte) 0x05);


        //Mutual authentication between SAM and Felica Card
        if (!mutualAuthWithFelicaV2(numOfService, serviceList.toByteArray())) {
            Timber.e("readAllTemporaryPass mutual authentication failed");
            return TemporaryTollPassUtils.setTemporaryTollPassError("mutual authentication failed");
        }
        byte[] response = readDataViaAuth(numOfBlocks, blockList.toByteArray());
        if (response.length > 0) {
            Timber.d("readAllTemporaryPass successful");
            return TemporaryTollPassUtils.getTemporaryTollPass(response);
        } else {
            Timber.d("readAllTemporaryPass successful but response length is 0");
            return TemporaryTollPassUtils.setTemporaryTollPassError("unknown error");
        }

    }

    public StaticTollPassDetailsResponse readStaticTollPassDetails() throws IOException, RemoteException {
        long val = pollingFelicaCard();
        if (val == APP_ERROR) {
            return StaticTollPassDetailsUtils.setTollPassDetailsError("polling error");
        }
        int numOfService = 1;
        int numOfBlocks = 6;
        TollSpecificDataCodes serviceCode = new TollSpecificDataCodes();
        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();
        serviceList.write(serviceCode.getSrvCodeTollPassEnc());

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
        blockList.write((byte) 0x80); //From branch id(4),To branch id(4),Num trips(2),Expiry DateTime(4),Pass type(1),Limit periodicity(1)
        blockList.write((byte) 0x00);
        blockList.write((byte) 0x80); //Limits max count(2),limits remaining count(2),Limits start DateTime(4),Limits max count(2),limits remaining count(2),limits start DateTime(4)
        blockList.write((byte) 0x01);

        blockList.write((byte) 0x80); //From branch id(4),To branch id(4),Num trips(2),Expiry DateTime(4),Pass type(1),Limit periodicity(1)
        blockList.write((byte) 0x02);
        blockList.write((byte) 0x80); //Limits max count(2),limits remaining count(2),Limits start DateTime(4),Limits max count(2),limits remaining count(2),limits start DateTime(4)
        blockList.write((byte) 0x03);

        blockList.write((byte) 0x80); //From branch id(4),To branch id(4),Num trips(2),Expiry DateTime(4),Pass type(1),Limit periodicity(1)
        blockList.write((byte) 0x04);
        blockList.write((byte) 0x80); //Limits max count(2),limits remaining count(2),Limits start DateTime(4),Limits max count(2),limits remaining count(2),limits start DateTime(4)
        blockList.write((byte) 0x05);


        //Mutual authentication between SAM and Felica Card
        if (!mutualAuthWithFelicaV2(numOfService, serviceList.toByteArray())) {
            Timber.e("readTollPassDetails mutual authentication failed");
            return StaticTollPassDetailsUtils.setTollPassDetailsError("mutual authentication failed");
        }
        byte[] response = readDataViaAuth(numOfBlocks, blockList.toByteArray());
        if (response.length > 0) {
            Timber.d("readTollPassDetails successful");
            return StaticTollPassDetailsUtils.getStaticTollPassDetails(response);
        } else {
            Timber.d("readTollPassDetails successful but response length is 0");
            return StaticTollPassDetailsUtils.setTollPassDetailsError("unknown error");
        }

    }

    public FelicaResponse updatePass(StaticTollPassDetailPojo data) throws IOException, RemoteException {
        Timber.d("updatePass StaticTollPassDetailPojo %s", data.toString());

        long val = pollingFelicaCard();
        if (val == APP_ERROR) {
            return setFelicaResponse(false, "polling error");
        }
        int numOfService = 1;
        int numOfBlocks = 2;
        ByteArrayOutputStream blockList = new ByteArrayOutputStream();

        if (data.getBlockNumber() == 0) {
            Timber.d("updatePass writing in block 0 and 1");
            blockList.write((byte) 0x80); //From branch id(4),To branch id(4),Num trips(2),Expiry DateTime(4),Pass type(1),Limit periodicity(1)
            blockList.write((byte) 0x00);
            blockList.write((byte) 0x80); //Limits max count(2),limits remaining count(2),Limits start DateTime(4),Limits max count(2),limits remaining count(2),limits start DateTime(4)
            blockList.write((byte) 0x01);

        } else if (data.getBlockNumber() == 1) {
            Timber.d("updatePass writing in block 2 and 3");
            blockList.write((byte) 0x80); //From branch id(4),To branch id(4),Num trips(2),Expiry DateTime(4),Pass type(1),Limit periodicity(1)
            blockList.write((byte) 0x02);
            blockList.write((byte) 0x80); //Limits max count(2),limits remaining count(2),Limits start DateTime(4),Limits max count(2),limits remaining count(2),limits start DateTime(4)
            blockList.write((byte) 0x03);
        } else {
            Timber.d("updatePass writing in block 4 and 5");
            blockList.write((byte) 0x80); //From branch id(4),To branch id(4),Num trips(2),Expiry DateTime(4),Pass type(1),Limit periodicity(1)
            blockList.write((byte) 0x04);
            blockList.write((byte) 0x80); //Limits max count(2),limits remaining count(2),Limits start DateTime(4),Limits max count(2),limits remaining count(2),limits start DateTime(4)
            blockList.write((byte) 0x05);
        }

        TollSpecificDataCodes tollSpecificDataCodes = new TollSpecificDataCodes();
        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();
        serviceList.write(tollSpecificDataCodes.getSrvCodeTollPassEnc());
        if (!mutualAuthWithFelicaV2(numOfService, serviceList.toByteArray())) {
            return setFelicaResponse(false, "mutual authentication failed");
        }

        byte[] res = writeDataBlock(numOfBlocks, blockList.toByteArray(), getStaticTollBlockData(data).toByteArray());

        if (res[0] == (byte) 0x00 && res[1] == (byte) 0x00) {
            return setFelicaResponse(true, "success");
        } else {
            return setFelicaResponse(false, "unknown error");
        }

    }

    public FelicaResponse clearTemporaryTollBlock(int blockNo) throws IOException, RemoteException {
        Timber.d("clearTemporaryTollBlock %s", blockNo);
        long val = pollingFelicaCard();
        if (val == APP_ERROR) {
            return setFelicaResponse(false, "polling error");
        }
        int numOfService = 1;
        int numOfBlocks = 1;

        TollSpecificDataCodes tollSpecificDataCodes = new TollSpecificDataCodes();

        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();
        serviceList.write(tollSpecificDataCodes.getSrvCodeTollTransEnc());

        if (!mutualAuthWithFelicaV2(numOfService, serviceList.toByteArray())) {
            Timber.e("clearTemporaryTollBlock mutualAuthWithFelicaV2 failed");
            return setFelicaResponse(false, "mutual authentication failed");
        }

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
        blockList.write((byte) 0x80);// branch id(4),In DateTime(4),Expiry DateTime(4)
        blockList.write((byte) blockNo);

        ByteArrayOutputStream blockData = new ByteArrayOutputStream();

        new Utils().appendZeroStream(blockData, 16);

        Timber.e("block data to be written %s", bytesToHexString(blockData.toByteArray()));
        byte[] res = writeDataBlock(numOfBlocks, blockList.toByteArray(), blockData.toByteArray());
        if (res[0] == (byte) 0x00 && res[1] == (byte) 0x00) {
            return setFelicaResponse(true, "success");
        } else {
            return setFelicaResponse(false, "unknown error");
        }

    }

    private ByteArrayOutputStream getStaticTollBlockData(StaticTollPassDetailPojo data) throws IOException {
        ByteArrayOutputStream blockData = new ByteArrayOutputStream();

        //From branch id(4),To branch id(4),Num trips(2),Expiry DateTime(4),Pass type(1),Limit periodicity(1)

        //Limits max count(2),limits remaining count(2),Limits start DateTime(4),
        // Limits max countReturn(2),limits remaining countReturn(2),limits start DateTimeReturn(4)

        blockData.write(mSam.LongToCharArrayLen(data.getFromBranchId(), 4));
        blockData.write(mSam.LongToCharArrayLen(data.getToBranchId(), 4));
        blockData.write(mSam.LongToCharArrayLen(data.getNumOfTrips(), 2));
        blockData.write(mSam.LongToCharArrayLen(data.getExpiryDate(), 4));
        blockData.write(mSam.LongToCharArrayLen(data.getPassType(), 1));
        blockData.write(mSam.LongToCharArrayLen(data.getLimitPeriodicity(), 1));

        blockData.write(mSam.LongToCharArrayLen(data.getLimitMaxCount(), 2));
        blockData.write(mSam.LongToCharArrayLen(data.getLimitRemainingCount(), 2));
        blockData.write(mSam.LongToCharArrayLen(data.getLimitStartDateTIme(), 4));

        blockData.write(mSam.LongToCharArrayLen(data.getLimitMaxCountReturnJourney(), 2));
        blockData.write(mSam.LongToCharArrayLen(data.getLimitRemainingCountReturnJourney(), 2));
        blockData.write(mSam.LongToCharArrayLen(data.getLimitStartDateTImeReturnJourney(), 4));

        return blockData;
    }

    public FelicaResponse createTempPassWithDeduction(TemporaryTollPassPojo tollPassPojo, int amount, long terminalId) throws IOException, RemoteException {
        Timber.d("createTempPassWithDeduction %s", tollPassPojo.toString() + "amount=" + amount + "terminalId=" + terminalId);
        long val = pollingFelicaCard();
        getExecutionIdFromCard();
        if (val == APP_ERROR) {
            return setFelicaResponse(false, "polling error");
        }
        int numOfService = 3;
        int numOfBlocks = 3;

        BalanceDataCodes balanceDataCodes = new BalanceDataCodes();
        TollSpecificDataCodes tollSpecificDataCodes = new TollSpecificDataCodes();

        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();

        serviceList.write(tollSpecificDataCodes.getSrvCodeTollTransEnc());
        serviceList.write(balanceDataCodes.getSrvCodeBalancePurseEnc());
        serviceList.write(TransactionalLogsDataCodes.getSrvCodeTransationalLogsEnc());

        if (!mutualAuthWithFelicaV2(numOfService, serviceList.toByteArray())) {
            Timber.e("createTempPassWithDeduction mutualAuthWithFelicaV2 failed");
            return setFelicaResponse(false, "mutual authentication failed");
        }
        updateExecutionID();
        Timber.d("createTempPassWithDeduction updated execution id=%s", bytesToHexString(executionId));

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();

        blockList.write((byte) 0x80);// branch id(4),In DateTime(4),Expiry DateTime(4)
        blockList.write((byte) tollPassPojo.getBlockNumber());

        blockList.write((byte) 0x81);//balance decrement
        blockList.write((byte) 0x00);

        blockList.write((byte) 0x82);//cyclic log
        blockList.write((byte) 0x00);

        ByteArrayOutputStream blockData = new ByteArrayOutputStream();
        //temporary toll pass
        blockData.write(mSam.LongToCharArrayLen(tollPassPojo.getBranchId(), 4));
        blockData.write(mSam.LongToCharArrayLen(tollPassPojo.getInDateTime(), 4));
        blockData.write(mSam.LongToCharArrayLen(tollPassPojo.getExpiryDateTime(), 4));
        new Utils().appendZeroStream(blockData, 4);


        //decrementing balance
        blockData.write(mSam.IntToCharArrayLE(amount, 4));
        new Utils().appendZeroStream(blockData, 10);
        blockData.write(executionId);

        //writing transactional logs
        blockData.write((byte) tollPassPojo.getTransactionType());
        blockData.write(mSam.LongToCharArrayLen(Utility.getUTCSecond(), 4));
        new Utils().appendZeroStream(blockData, 2);
        blockData.write(mSam.LongToCharArrayLen(amount, 4));
        blockData.write(mSam.LongToCharArrayLen(terminalId, 4));
        blockData.write((byte) 0);

        Timber.e("block data to be written %s", bytesToHexString(blockData.toByteArray()));
        byte[] res = writeDataBlock(numOfBlocks, blockList.toByteArray(), blockData.toByteArray());
        if (res[0] == (byte) 0x00 && res[1] == (byte) 0x00) {
            return setFelicaResponse(true, "success");
        } else {
            return setFelicaResponse(false, "unknown error");
        }

    }

    public FelicaResponse createTempPassWODeduction(TemporaryTollPassPojo tollPassPojo) throws IOException, RemoteException {
        Timber.d("createTempPassWODeduction %s", tollPassPojo.toString());
        long val = pollingFelicaCard();
        if (val == APP_ERROR) {
            return setFelicaResponse(false, "polling error");
        }
        int numOfService = 1;
        int numOfBlocks = 1;

        TollSpecificDataCodes tollSpecificDataCodes = new TollSpecificDataCodes();

        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();
        serviceList.write(tollSpecificDataCodes.getSrvCodeTollTransEnc());

        if (!mutualAuthWithFelicaV2(numOfService, serviceList.toByteArray())) {
            Timber.e("createTempPassWODeduction mutualAuthWithFelicaV2 failed");
            return setFelicaResponse(false, "mutual authentication failed");
        }

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
        blockList.write((byte) 0x80);// branch id(4),In DateTime(4),Expiry DateTime(4)
        blockList.write((byte) tollPassPojo.getBlockNumber());

        ByteArrayOutputStream blockData = new ByteArrayOutputStream();
        blockData.write(mSam.LongToCharArrayLen(tollPassPojo.getBranchId(), 4));
        blockData.write(mSam.LongToCharArrayLen(tollPassPojo.getInDateTime(), 4));
        blockData.write(mSam.LongToCharArrayLen(tollPassPojo.getExpiryDateTime(), 4));
        new Utils().appendZeroStream(blockData, 4);

        Timber.e("block data to be written %s", bytesToHexString(blockData.toByteArray()));
        byte[] res = writeDataBlock(numOfBlocks, blockList.toByteArray(), blockData.toByteArray());
        if (res[0] == (byte) 0x00 && res[1] == (byte) 0x00) {
            return setFelicaResponse(true, "success");
        } else {
            return setFelicaResponse(false, "unknown error");
        }

    }

    private void getExecutionIdFromCard() throws IOException, RemoteException {
        int numOfService = 1;
        int numOfBlocks = 1;
        BalanceDataCodes genericDataCodes = new BalanceDataCodes();
        ByteArrayOutputStream serviceList = new ByteArrayOutputStream();
        serviceList.write(genericDataCodes.getSrvCodeBalanceReadWOEnc());

        ByteArrayOutputStream blockList = new ByteArrayOutputStream();
        blockList.write((byte) 0x80);            //card balance
        blockList.write((byte) 0x00);

        byte[] b = readDataWOAuth(numOfService, serviceList.toByteArray(), numOfBlocks, blockList.toByteArray());
        executionId = Arrays.copyOfRange(b, 14, 16);
        Timber.d("getExecutionIdFromCard executionId= %s", bytesToHexString(executionId));
    }

    private FelicaResponse setFelicaResponse(boolean val, String message) {
        FelicaResponse felicaResponse = new FelicaResponse();
        felicaResponse.setSuccess(val);
        felicaResponse.setMessage(message);
        Timber.d("setFelicaResponse Felicaresponse %s", felicaResponse.toString());
        return felicaResponse;
    }
}
