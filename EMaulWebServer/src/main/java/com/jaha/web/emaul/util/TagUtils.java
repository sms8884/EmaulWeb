package com.jaha.web.emaul.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by doring on 15. 5. 7..
 */
public class TagUtils {
    public static List<String> getImgSrc(String str) {
        Pattern nonValidPattern = Pattern.compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>");

        List<String> result = new ArrayList<String>();
        Matcher matcher = nonValidPattern.matcher(str);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    public static String replaceImageSizeToWidth100(String src) {
        StringBuilder sb = new StringBuilder(src);

        Pattern nonValidPattern = Pattern.compile("<img[^>]*(width=[\"']?[^>\"']+[\"']?)[^>]*>");

        int start = 0;
        Matcher matcher = nonValidPattern.matcher(src);
        while (matcher.find(start)) {
            String strWidth = matcher.group(1);
            start = matcher.start(1);
            if (strWidth != null) {
                int end = matcher.end(1);

                sb.replace(start, end, "width=\"100%\"");
                matcher = nonValidPattern.matcher(sb.toString());
            }
            start += 1;
        }

        src = sb.toString();

        nonValidPattern = Pattern.compile("(<img(?!.*?width=(['\"]).*?\\2)[^>]*)(>)");

        start = 0;
        matcher = nonValidPattern.matcher(src);
        while (matcher.find(start)) {
            String strWidth = matcher.group(2);
            start = matcher.start(1) + 4;

            if (strWidth == null) {
                sb.replace(start, start, " width=\"100%\"");
                matcher = nonValidPattern.matcher(sb.toString());
            }

            start += 1;
        }
        return sb.toString();
    }

    public static String replaceImageSizeToHeightNull(String src) {
        StringBuilder sb = new StringBuilder(src);

        Pattern nonValidPattern = Pattern.compile("<img[^>]*(height=[\"']?[^>\"']+[\"']?)[^>]*>");

        int start = 0;
        Matcher matcher = nonValidPattern.matcher(src);
        while (matcher.find(start + 1)) {
            String strWidth = matcher.group(1);
            if (strWidth != null) {
                start = matcher.start(1);
                int end = matcher.end(1);

                sb.replace(start, end, "");
                matcher = nonValidPattern.matcher(sb.toString());
            }
        }
        return sb.toString();
    }

    public static String extractBody(String content) {
        if (!StringUtils.isEmpty(content)) {
            int start = content.indexOf("<body>") + 6;
            int end = content.indexOf("</body>");
            if (end > -1) {
                return content.substring(start, end);
            }
        }
        return content;
    }

    /**
     * HTML 태그 제거
     *
     * @author 전강욱(realsnake@jahasmart.com), 2016. 7. 4.
     * @description
     *
     * @param html
     * @return
     */
    public static String removeTag(String html) {
        try {
            return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", StringUtils.EMPTY);
        } catch (Exception e) {
            return html;
        }
    }

}
