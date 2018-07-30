package com.example.hamzah.modulediary.Helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * list of helper functions used throughout the application
 */

public class AppUtils {

    public static String dateToFullString(Date date, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String sDate = sdf.format(date);
        return sDate;
    }

    public static int getIntfromLong(Long value){
        int streak = ((Long) value).intValue();
        return streak;
    }

    public static int getAveragefromLong(Long total, Long numberOf){
        double v1Double = Long.valueOf(total).doubleValue();
        double v2Double = Long.valueOf(numberOf).doubleValue();
        double eq = v1Double / v2Double;
        long average = Math.round(eq);

        return Long.valueOf(average).intValue();
    }

    public static int getPercentageFromLongs(Long value, Long total){
        double v1Double = Long.valueOf(value).doubleValue();
        double v2Double = Long.valueOf(total).doubleValue();
        double eq = (v1Double / v2Double) * 100;
        long average = Math.round(eq);

        return Long.valueOf(average).intValue();
    }

    public static Date getDateFromString(String sDate){
        String[] temp = sDate.split("-");
        int day = Integer.parseInt(temp[0]);
        int month = Integer.parseInt(temp[1]);
        int year = Integer.parseInt(temp[2]);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        // TODO: 25/04/2017 check if this month is correct
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    public static int getGraduationYear(int difference){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, difference);
        return calendar.get(Calendar.YEAR);
    }
}
