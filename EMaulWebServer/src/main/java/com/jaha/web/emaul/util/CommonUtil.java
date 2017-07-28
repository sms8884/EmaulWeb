package com.jaha.web.emaul.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jaha.web.emaul.constants.Constants;

public class CommonUtil {

    public static void setPagingParams(Map<String, Object> params) {

        int pageNum = StringUtil.nvlInt(params.get("pageNum"), 1);
        int pageSize = StringUtil.nvlInt(params.get("pageSize"), 10);

        int startNum = (pageNum - 1) * pageSize;
        int endNum = pageNum * pageSize;

        params.put("startNum", startNum);
        params.put("endNum", endNum);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
    }

    public static void setSkipPagingParams(Map<String, Object> params) {

        int pageNum = StringUtil.nvlInt(params.get("pageNum"), 1);
        int pageSize = StringUtil.nvlInt(params.get("pageSize"), 5);
        int skipNum = StringUtil.nvlInt(params.get("skipNum"), 4);

        int startNum = ((pageNum - 1) * pageSize) + skipNum;
        int endNum = pageSize;

        params.put("startNum", startNum);
        params.put("endNum", endNum);
    }

    /**
     * @author 전강욱(realsnake@jahasmart.com)
     * @description 현재 날짜와 시간을 date format 형태로 변환하여 반환한다.
     *
     * @param dateFormat
     * @return
     */
    public static String getNowDatetime(String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(new Date());
    }

    /**
     * @author 전강욱(realsnake@jahasmart.com)
     * @description String 형태의 날짜를 Date로 변환하여 반환한다.
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static Date getDatetime(String date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        try {
            return sdf.parse(date);
        } catch (ParseException pe) {
            // logger.error("String 형태의 날짜를 Date로 변환 중 에러!", pe);
            return null;
        }
    }

    /**
     * @author 전강욱(realsnake@jahasmart.com)
     * @description
     *
     * @param date
     * @param dateFormat
     * @param interval
     * @return
     */
    public static String getNextDatetime(String date, String dateFormat, int interval) {
        if (StringUtils.isBlank(dateFormat)) {
            dateFormat = Constants.DEFAULT_DATE_FORMAT;
        }

        Date dt = getDatetime(date, dateFormat);

        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.MINUTE, interval);

        return new SimpleDateFormat(dateFormat).format(calendar.getTime());
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
