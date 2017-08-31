package com.ethan.morephone.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by truongnguyen on 8/24/17.
 */
public class DateUtils {

    public static Date getDate(String date) {
        SimpleDateFormat in = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z");

        try {
            Date time = in.parse(date);
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatDate(long date) {
        SimpleDateFormat out = new SimpleDateFormat("dd-MM HH:mm:ss");
        Date time = new Date(date);
        return out.format(time);
    }



}
