/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 10. 25.
 */
package com.jaha.web.emaul.v2.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.stereotype.Component;

import com.jaha.web.emaul.model.GcmSendForm;

/**
 * <pre>
 * Class Name : PushQueue.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 25.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 10. 25.
 * @version 1.0
 */
@Component
public class PushQueue {

    private static BlockingQueue<GcmSendForm> PUSH_QUEUE = new ArrayBlockingQueue<GcmSendForm>(1000, true);

    /**
     * 푸시큐에 담는다.
     *
     * @param gcmSendForm
     * @return
     */
    public boolean offerPushFrom(GcmSendForm gcmSendForm) {
        return PUSH_QUEUE.offer(gcmSendForm);
    }

    /**
     * 푸시큐에서 GcmSendForm을 반환한다.
     *
     * @return
     */
    public GcmSendForm pollPushFrom() {
        return PUSH_QUEUE.poll();
    }

    /**
     * 푸시큐 사이즈 반환.
     *
     * @return
     */
    public int getPushQueueSize() {
        return (PUSH_QUEUE.isEmpty()) ? 0 : PUSH_QUEUE.size();
    }

}
