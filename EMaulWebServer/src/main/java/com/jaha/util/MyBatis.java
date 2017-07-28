package com.jaha.util;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Class Name : MyBaits.java
 * Description : MyBatis Custom Tag
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 13.     조영태      Generation
 * </pre>
 *
 * @author 조영태
 * @since 2016. 10. 13.
 * @version 1.0
 */
public class MyBatis {

    /**
     * mybatis isEmpty <if test="obj == null or obj == ''"></if> 대체
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {

        if (obj instanceof String) {
            return obj == null || "".equals(obj.toString().trim());
        } else if (obj instanceof List) {
            return obj == null || ((List<?>) obj).isEmpty();
        } else if (obj instanceof Map) {
            return obj == null || ((Map<?, ?>) obj).isEmpty();
        } else if (obj instanceof Object[]) {
            return obj == null || Array.getLength(obj) == 0;
        } else {
            return obj == null;
        }
    }

    /**
     * mybatis isEmpty <if test="obj != null or obj != ''"></if> 대체
     *
     * @param s
     * @return
     */
    public static boolean isNotEmpty(Object obj) {

        return !isEmpty(obj);
    }

}
