package com.doe.topwisesdk.domain.doecard.samndfelica.felica.tollpassdetailsutils;


import com.doe.topwisesdk.domain.doecard.samndfelica.Utils;
import com.doe.topwisesdk.domain.models.tollpass.StaticTollPassDetailPojo;
import com.doe.topwisesdk.domain.models.tollpass.StaticTollPassDetailsResponse;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

public class StaticTollPassDetailsUtils {

    //From branch id(4),To branch id(4),Num trips(2),Expiry DateTime(4),Pass type(1),Limit periodicity(1)

    //Limits max count(2),limits remaining count(2),Limits start DateTime(4),
    // Limits max count return journey(2),limits remaining count return journey(2),limits start DateTime return journey(4)

    public static StaticTollPassDetailsResponse getStaticTollPassDetails(byte[] data) {
        Timber.d("getStaticTollPassDetails data=%s ", Utils.bytesToHexString(data));
        List<StaticTollPassDetailPojo> list = new LinkedList<>();
        int blockNumber=0;

        for (int i = 0, index = 0; i < data.length; i += 32, index++) {
            StaticTollPassDetailPojo staticTollPassDetailPojo = new StaticTollPassDetailPojo();

            staticTollPassDetailPojo.setFromBranchId(Utils.bytesToLong(Arrays.copyOfRange(data, i, i + 4), 4));
            staticTollPassDetailPojo.setToBranchId(Utils.bytesToLong(Arrays.copyOfRange(data, i + 4, i + 8), 4));
            staticTollPassDetailPojo.setNumOfTrips((int) Utils.bytesToLong(Arrays.copyOfRange(data, i + 8, i + 10), 2));
            staticTollPassDetailPojo.setExpiryDate(Utils.bytesToLong(Arrays.copyOfRange(data, i + 10, i + 14), 4));
            staticTollPassDetailPojo.setPassType((int) Utils.bytesToLong(Arrays.copyOfRange(data, i + 14, i + 15), 1));
            staticTollPassDetailPojo.setLimitPeriodicity((int) Utils.bytesToLong(Arrays.copyOfRange(data, i + 15, i + 16), 1));

            staticTollPassDetailPojo.setLimitMaxCount((int) Utils.bytesToLong(Arrays.copyOfRange(data, i + 16, i + 18), 2));
            staticTollPassDetailPojo.setLimitRemainingCount((int) Utils.bytesToLong(Arrays.copyOfRange(data, i + 18, i + 20), 2));
            staticTollPassDetailPojo.setLimitStartDateTIme(Utils.bytesToLong(Arrays.copyOfRange(data, i + 20, i + 24), 4));

            staticTollPassDetailPojo.setLimitMaxCountReturnJourney((int) Utils.bytesToLong(Arrays.copyOfRange(data, i + 24, i + 26), 2));
            staticTollPassDetailPojo.setLimitRemainingCountReturnJourney((int) Utils.bytesToLong(Arrays.copyOfRange(data, i + 26, i + 28), 2));
            staticTollPassDetailPojo.setLimitStartDateTImeReturnJourney(Utils.bytesToLong(Arrays.copyOfRange(data, i + 28, i + 32), 4));
            staticTollPassDetailPojo.setBlockNumber(blockNumber++);

            list.add(staticTollPassDetailPojo);
        }


        return getStaticTollPassDetails(list);
    }

    private static StaticTollPassDetailsResponse getStaticTollPassDetails(List<StaticTollPassDetailPojo> list) {

        StaticTollPassDetailsResponse response = new StaticTollPassDetailsResponse();
        response.setStaticTollPassDetailPojoList(list);
        response.setMessage("success");
        return response;
    }


    public static StaticTollPassDetailsResponse setTollPassDetailsError(String message) {
        StaticTollPassDetailsResponse transactionalLogsResponse = new StaticTollPassDetailsResponse();
        transactionalLogsResponse.setMessage(message);
        transactionalLogsResponse.setStaticTollPassDetailPojoList(null);
        return transactionalLogsResponse;
    }
}
