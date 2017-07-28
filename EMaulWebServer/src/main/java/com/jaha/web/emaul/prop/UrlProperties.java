package com.jaha.web.emaul.prop;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by doring on 15. 4. 29..
 */
@Component
@ConfigurationProperties(locations = "classpath:/url.properties", ignoreUnknownFields = false, prefix = "url")
public class UrlProperties {

    @Value("${gcm.server.url}")
    private String gcmServerUrl;

    private String gcmServer;

    public String getGcmServer() {
        gcmServer = gcmServerUrl;
        return gcmServer;
    }

    public void setGcmServer(String gcmServer) {
        // this.gcmServer = gcmServer;
        this.gcmServer = gcmServerUrl;
    }

    @Value("${gcm.common.send.url}")
    private String commonGcmSendUrl;

    public String getCommonGcmSendUrl() {
        return commonGcmSendUrl;
    }

    public void setCommonGcmSendUrl(String commonGcmSendUrl) {
        this.commonGcmSendUrl = commonGcmSendUrl;
    }

    @Value("${gcm.common.send.param}")
    private String commonGcmSendParam;

    public String getCommonGcmSendParam() {
        return commonGcmSendParam;
    }

    public void setCommonGcmSendParam(String commonGcmSendParam) {
        this.commonGcmSendParam = commonGcmSendParam;
    }

}
