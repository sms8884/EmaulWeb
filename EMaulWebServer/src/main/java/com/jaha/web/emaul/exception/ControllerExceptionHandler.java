/**
 *
 */
package com.jaha.web.emaul.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 전강욱(realsnake@jahasmart.com)
 * @description Controller단에서 발생하는 에러에 대해 특별히 각 메소드에서 에러처리를 지정하지 않은 경우 에러를 처리하는 클래스
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler({EmaulWebException.class, Exception.class})
    public void handleException(Exception e) {
        LOGGER.error("[이마을웹 에러 로그]", e);
    }

    @ExceptionHandler(JsonApiException.class)
    @ResponseBody
    @Deprecated
    public Map<String, ErrorJson> handleJsonApiException(JsonApiException e) {
        LOGGER.error("[API 에러 로그]", e);

        Map<String, ErrorJson> errorMap = new HashMap<String, ErrorJson>();

        ErrorJson errorJson = new ErrorJson();
        errorJson.setCode(500);
        errorJson.setMessage(e.getMessage());

        errorMap.put("error", errorJson);

        return errorMap;
    }

}
