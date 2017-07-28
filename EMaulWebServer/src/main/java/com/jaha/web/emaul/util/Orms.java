package com.jaha.web.emaul.util;

import java.lang.reflect.Field;
import java.sql.ResultSet;

/**
 * Created by doring on 15. 4. 15..
 */
public class Orms {
    public static <T> T mapping(ResultSet rs, Class<T> classOfT) throws IllegalAccessException, InstantiationException {
        T object = classOfT.newInstance();
        Field[] fields = classOfT.getDeclaredFields();
        for (Field field : fields) {
            Object value = null;

            try {
                value = rs.getObject(field.getName());
            } catch (Exception e) {
                // do nothing
            }

            if (value != null) {
                try {
                    field.set(object, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return object;
    }
}
