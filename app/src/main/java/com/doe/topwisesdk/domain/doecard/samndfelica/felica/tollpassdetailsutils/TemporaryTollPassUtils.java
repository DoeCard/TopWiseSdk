package com.doe.topwisesdk.domain.doecard.samndfelica.felica.tollpassdetailsutils;

import com.doe.topwisesdk.domain.doecard.samndfelica.Utils;
import com.doe.topwisesdk.domain.models.tollpass.TemporaryTollPassPojo;
import com.doe.topwisesdk.domain.models.tollpass.TemporaryTollPassResponse;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

public class TemporaryTollPassUtils {
    public static TemporaryTollPassResponse getTemporaryTollPass(byte[] data) {
        Timber.d("getTemporaryTollPass data= %s", Utils.bytesToHexString(data));
        List<TemporaryTollPassPojo> list = new LinkedList<>();
        int blockNumber = 0;
        for (int i = 0, index = 0; i < data.length; i += 16, index++) {
            TemporaryTollPassPojo temporaryTollPassPojo = new TemporaryTollPassPojo();

            temporaryTollPassPojo.setBranchId(Utils.bytesToLong(Arrays.copyOfRange(data, i, i + 4), 4));
            temporaryTollPassPojo.setInDateTime(Utils.bytesToLong(Arrays.copyOfRange(data, i + 4, i + 8), 4));
            temporaryTollPassPojo.setExpiryDateTime(Utils.bytesToLong(Arrays.copyOfRange(data, i + 8, i + 12), 4));
            temporaryTollPassPojo.setBlockNumber(blockNumber++);
            list.add(temporaryTollPassPojo);
        }
        return getTemporaryTollPass(list);
    }

    private static TemporaryTollPassResponse getTemporaryTollPass(List<TemporaryTollPassPojo> list) {
        TemporaryTollPassResponse response = new TemporaryTollPassResponse();
        response.setTemporaryTollPassPojoList(list);
        response.setMessage("success");
        return response;
    }

    public static TemporaryTollPassResponse setTemporaryTollPassError(String message) {
        TemporaryTollPassResponse temporaryTollPassResponse = new TemporaryTollPassResponse();
        temporaryTollPassResponse.setMessage(message);
        temporaryTollPassResponse.setTemporaryTollPassPojoList(null);
        return temporaryTollPassResponse;
    }
}
