package com.jaha.web.emaul.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by imac1 on 2016. 3. 7..
 */
public class Debug {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

    public static void log(String title, String msg) {
        try {
            System.out.println(String.format("[%s] %s - %s", getTimeStampString(), title, msg));
        } catch (Exception e) {
        }
    }

    public static void log(String title, int msg) {
        log(title, Integer.toString(msg));
    }

    public static void log(int msg) {
        log(String.valueOf(msg));
    }

    public static void log(long msg) {
        log(String.valueOf(msg));
    }

    public static String getTimeStampString() {
        return format.format(new Date());
    }

    private static boolean _sql = true;

    public static void sql(String sql) {
        if (_sql == true) {
            log(sql);
        }
    }

    public static void log(String msg) {
        try {
            System.out.println(String.format("[%s] - %s", getTimeStampString(), msg));
        } catch (Exception e) {

        }
    }

    public static void query(String query) {
        try {
            System.out.println(String.format("[%s]\n%s", getTimeStampString(), query));
        } catch (Exception e) {

        }
    }

    public static void err(Exception e) {
        err(e, "");
    }

    public static void err(Exception e, String msg) {
        try {
            StackTraceElement ste[] = e.getStackTrace();
            String msg1 = String.format("********Exception %s******\n" + "%s.%s(%s:%d)\nError Message:%s\n", msg, ste[1].getClassName(), ste[1].getMethodName(), ste[1].getFileName(),
                    ste[1].getLineNumber(), e.getMessage());
            log(msg1);
            e.printStackTrace(System.err);
        } catch (Exception e1) {

        }
    }

}
