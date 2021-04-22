package com.doe.topwisesdk.domain.doecard.samndfelica.felica.transactionallogs;


import com.doe.topwisesdk.domain.doecard.samndfelica.Utils;
import com.doe.topwisesdk.domain.models.transactionallogs.TransactionalLogPojo;
import com.doe.topwisesdk.domain.models.transactionallogs.TransactionalLogsResponse;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

public class TransactionalLogsUtils {
    public static TransactionalLogsResponse getTransactionalLogList(@NotNull byte[] data) {
        //single data 08 60 4F 5A 85 00 00 00 00 00 19 07 5B CD 15 00
        List<TransactionalLogPojo> list = new LinkedList<TransactionalLogPojo>();

        for (int i = 0, index = 0; i < data.length; i += 16, index++) {
            TransactionalLogPojo transactionalLogPojo = new TransactionalLogPojo();

            transactionalLogPojo.setTransactionType((int) Utils.bytesToLong(Arrays.copyOfRange(data, i, i + 1), 1));
            transactionalLogPojo.setTxnDateTimeInMilliSeconds(Utils.bytesToLong(Arrays.copyOfRange(data, i + 1, i + 7), 4));
            transactionalLogPojo.setAmount((int) Utils.bytesToLong(Arrays.copyOfRange(data, i + 7, i + 11), 4));
            transactionalLogPojo.setTerminalId(Utils.bytesToLong(Arrays.copyOfRange(data, i + 11, i + 15), 4));

            list.add(transactionalLogPojo);
        }
        Timber.d(list.toString());
        return getTransactionalLogs(list);
    }

    private static TransactionalLogsResponse getTransactionalLogs(List<TransactionalLogPojo> list) {

        TransactionalLogsResponse response = new TransactionalLogsResponse();
        response.setTransactionalLogPojoList(list);
        response.setMessage("success");
        return response;
    }

    public static TransactionalLogsResponse setTransactionalLogError(String message) {
        TransactionalLogsResponse transactionalLogsResponse = new TransactionalLogsResponse();
        transactionalLogsResponse.setMessage(message);
        transactionalLogsResponse.setTransactionalLogPojoList(null);
        return transactionalLogsResponse;
    }
}
