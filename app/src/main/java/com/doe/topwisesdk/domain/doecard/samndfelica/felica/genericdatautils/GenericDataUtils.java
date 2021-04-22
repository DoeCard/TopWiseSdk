package com.doe.topwisesdk.domain.doecard.samndfelica.felica.genericdatautils;

import android.util.Log;


import com.doe.topwisesdk.domain.doecard.samndfelica.Utils;
import com.doe.topwisesdk.domain.models.genericdata.GenericDataPojo;
import com.doe.topwisesdk.domain.models.genericdata.GenericDataPojoResponse;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.doe.topwisesdk.domain.doecard.samndfelica.Utils.bytesToHexString;

public class GenericDataUtils {

    public static GenericDataPojoResponse getGenericDataPojoResponse(byte[] data) {
        Log.d("GenericDataUtils byte data=",bytesToHexString(data));
        List<GenericDataPojo> list = new LinkedList<>();

        GenericDataPojo modelClass = new GenericDataPojo();

        modelClass.setCardName_String(new Utils().hexToString(
                bytesToHexString(
                        Arrays.copyOfRange(
                                data,
                                0,
                                48
                        )
                )
        ));
        modelClass.setVehicleType_Int((int)Arrays.copyOfRange(data, 48, 49)[0]);
        modelClass.setCardStatus_Int((int) Arrays.copyOfRange(data, 49, 50)[0]);
        modelClass.setCardVersion_Int((int)Arrays.copyOfRange(data, 50, 51)[0]);
        modelClass.setGender_Int((int)Arrays.copyOfRange(data, 51, 52)[0]);
        modelClass.setDobLong(Utils.bytesToLong(Arrays.copyOfRange(data, 52, 56),4));
        modelClass.setCardExpiryDateLong(Utils.bytesToLong(Arrays.copyOfRange(data, 56, 60),4));
        modelClass.setLastSyncTimeLong(Utils.bytesToLong(Arrays.copyOfRange(data, 60, 64),4));

        modelClass.setVehicleNumber_String(new Utils().hexToString(bytesToHexString(Arrays.copyOfRange(data, 64, 80))));

        modelClass.setCardIssueDateTime_Long(Utils.bytesToLong(Arrays.copyOfRange(data, 80, 84),4));

        modelClass.setMobileNumber_Long(Utils.bytesToLong(Arrays.copyOfRange(data, 84, 90), 6));
        modelClass.setAadhaarNumber_Long(Utils.bytesToLong(Arrays.copyOfRange(data, 90, 95), 5));
        modelClass.setCardNumber_Long(Utils.bytesToLong(Arrays.copyOfRange(data, 96, 112), 16));
        modelClass.setCardBalance_Int(ByteBuffer.wrap(Arrays.copyOfRange(data, 112, 116))
                .order(ByteOrder.LITTLE_ENDIAN).getInt());

        list.add(modelClass);

        return getGenericDataPojoResponse(list);
    }

    private static GenericDataPojoResponse getGenericDataPojoResponse(List<GenericDataPojo> list) {
        GenericDataPojoResponse response = new GenericDataPojoResponse();
        response.setGenericDataPojoList(list);
        response.setMessage("success");
        return response;
    }

    public static GenericDataPojoResponse setGenericDataPojoResponseError(String message) {
        GenericDataPojoResponse transactionalLogsResponse = new GenericDataPojoResponse();
        transactionalLogsResponse.setMessage(message);
        transactionalLogsResponse.setGenericDataPojoList(null);
        return transactionalLogsResponse;
    }
}
