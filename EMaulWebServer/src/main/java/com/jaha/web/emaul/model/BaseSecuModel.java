package com.jaha.web.emaul.model;

import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.softforum.xdbe.XdspNative;

/**
 * Created by pns on 16. 2. 17..
 */
public class BaseSecuModel {

    @Transient
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Transient
    private static final String SECU_POOL_NAME = "pool1";
    @Transient
    private static final String SECU_DB_NAME = "jaha_db";
    @Transient
    private static final String SECU_OWNER_NAME = "jaha_ow";
    @Transient
    private static final String SECU_TABLE_NAME = "jaha_tb";
    @Transient
    private static final String SECU_COLUMN_NAME = "normal";

    public String encString(String text) {
        if (text == null) {
            return null;
        }

        try {
            text = XdspNative.fast_sync_encrypt64(SECU_POOL_NAME, SECU_DB_NAME, SECU_OWNER_NAME, SECU_TABLE_NAME, SECU_COLUMN_NAME, text.getBytes());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return text;
    }

    public String descString(String text) {
        if (text == null) {
            return null;
        }

        try {
            byte[] sDecrypt = XdspNative.fast_sync_decrypt64(SECU_POOL_NAME, SECU_DB_NAME, SECU_OWNER_NAME, SECU_TABLE_NAME, SECU_COLUMN_NAME, text);
            text = new String(sDecrypt);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return text;
    }
}
