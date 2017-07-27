package com.goalsmaster.goalsmaster.utils;

import java.util.Date;

/**
 * Created by tudor on 6/28/2017.
 */

public class StringUtils {
    public static Date dateFromCharSequence(CharSequence cs) {
        try {
            String txt = cs.toString();
            String[] parts = txt.split("-");
            int day = Integer.valueOf(parts[0]);
            int month = Integer.valueOf(parts[1])-1;
            int year = Integer.valueOf(parts[2])-1900;
            return new Date(year, month, day);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dateToString(Date date) {
        int year = date.getYear() + 1900;
        int month = date.getMonth() + 1;
        int day = date.getDate();
        return String.format("%02d-%02d-%04d", day, month, year);
    }
}
