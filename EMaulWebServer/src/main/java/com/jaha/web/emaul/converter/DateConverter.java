package com.jaha.web.emaul.converter;

import com.jaha.web.emaul.exception.EmaulWebException;
import com.mysql.jdbc.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        if(StringUtils.isEmptyOrWhitespaceOnly(source))
            return null;

        try {
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date to = transFormat.parse(source);

            return to;
        } catch (ParseException e) {
            throw new EmaulWebException("DATE 형식이 잘못되었습니다");
        }
    }
}
