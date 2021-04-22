package com.doe.topwisesdk.domain.doecard.samndfelica.felica;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.view.View.LAYER_TYPE_HARDWARE;
import static android.view.View.LAYER_TYPE_NONE;

/**
 * Created by eshantmittal on 12/07/17.
 */

public class Utility {

    public static final int RECHARGE_INFO = 0;
    public static final int ACTIVATION_INFO= 1;
    public static final int PARKING_INFO = 2;

    public static long getUTCSecond()
    {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        long time = cal.getTimeInMillis()/1000;

        Log.e("time","=="+time);

        return time;
    }

    public static long getAddedTimeUTC(int seconds){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        long time = cal.getTimeInMillis()/1000 + seconds;

        Log.e("time","=="+time);

        return time;
    }

    public static String localToUtcConverter() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date curDate = cal.getTime();

        String dateAsString = format.format(curDate);
        Log.e("dateAsString", "==" + dateAsString);
        Log.e("curDate", "==" + curDate);

        return dateAsString;
    }

    public static String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
       // long daysInMilli = hoursInMilli * 24;

//        long elapsedDays = different / daysInMilli;
//        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                " %d hours, %d minutes, %d seconds%n",
                 elapsedHours, elapsedMinutes, elapsedSeconds);

        return  String.format("%02d", elapsedHours) + ":" + String.format("%02d", elapsedMinutes);

    }

    public static String utcToLocalConverter(long seconds) {

        SimpleDateFormat format = new SimpleDateFormat(
                "HH:mm", Locale.US);


        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        format.setTimeZone(calendar.getTimeZone());
        calendar.setTimeInMillis(seconds*1000);
        return format.format(calendar.getTime());

    }


    public void setGreyscale(View v, boolean greyscale) {
        if (greyscale) {
            // Create a paint object with 0 saturation (black and white)
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            Paint greyscalePaint = new Paint();
            greyscalePaint.setColorFilter(new ColorMatrixColorFilter(cm));
            // Create a hardware layer with the greyscale paint
            v.setLayerType(LAYER_TYPE_HARDWARE, greyscalePaint);
        } else {
            // Remove the hardware layer
            v.setLayerType(LAYER_TYPE_NONE, null);
        }
    }



}
