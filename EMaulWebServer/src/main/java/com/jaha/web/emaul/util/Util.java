package com.jaha.web.emaul.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import com.jaha.web.emaul.constants.Constants;

/**
 * Created by Administrator on 2015-06-28.
 */
public class Util {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    public static String getRandomString(int length) {
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        char data[] =
                {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'A', 'B', 'C', 'd', 'E', 'F', 'G', 'H', 'x', 'J', 'K', 'b', 'M', 'N', 'y', 'P', 'r', 'R', 'S', 'T', 'u', 'V', 'W', 'X', 'Y', 'Z'};

        for (int i = 0; i < length; i++) {
            buffer.append(data[random.nextInt(data.length)]);
        }
        return buffer.toString();
    }


    /**
     * 현재 날짜를 가져온다.
     *
     * @return "yyyy-MM-dd"
     */
    public static String getDateString() {
        return format.format(new Date());
    }

    public static String getTimeString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        return simpleDateFormat.format(new Date());
    }

    public static String strTrim(String str) {
        if (str != null) {
            return str.trim();
        }
        return "";
    }

    public static String strTrim(String str, String defValue) {
        if (str != null && !str.isEmpty()) {
            return str.trim();
        }
        return defValue;
    }


    public static int getInt(String str) {
        try {
            return Integer.parseInt(strTrim(str));
        } catch (Exception e) {
            return 0;
        }
    }

    public static short getShort(String str) {
        return (short) (getInt(str) & 0xffff);
    }

    public static float getFloat(String str) {
        try {
            return Float.parseFloat(strTrim(str));
        } catch (Exception e) {
            return 0;
        }
    }

    public static double getDouble(String str) {
        try {
            return Double.parseDouble(strTrim(str));
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isEmpty(String str) {
        return (str == null || str.trim().isEmpty());
    }

    public static Date convertString2Date(String param) {
        try {
            return Constants.DEFAULT_SDF.parse(param);
        } catch (ParseException pe) {
            // logger.error("String 형태의 날짜를 Date로 변환 중 에러!", pe);
            return null;
        }

    }

    /**
     * 현재 날짜(yyyyMMdd) 반환
     *
     * @return
     */
    public static String getNowDate() {
        return Constants.SHORT_DATE_SDF.format(new Date());
    }

    /**
     * 현재 일시(yyyyMMddHHmmss) 반환
     *
     * @return
     */
    public static String getNowDatetime() {
        return Constants.DEFAULT_SDF.format(new Date());
    }

    /**
     * @author 전강욱(realsnake@jahasmart.com)
     * @description
     *
     * @param interval
     * @return
     */
    public static String getNextDatetime(int interval) {
        Date dt = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.MINUTE, interval);

        return new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT).format(calendar.getTime());
    }

}
