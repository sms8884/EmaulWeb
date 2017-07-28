/**
 *
 */
package com.jaha.web.emaul.util;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.jaha.web.emaul.constants.Constants;

/**
 * @author 전강욱(realsnake@jahasmart.com)
 * @description
 *
 */
@Component
public class HttpUtils {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @author 전강욱(realsnake@jahasmart.com)
     * @description URL 호출
     *
     * @param reqUrl
     * @param httpMethod
     * @param params
     * @param headers
     */
    public HttpEntity connectPost(String reqUrl, Map<String, String> headers, Map<String, String> params, File targetFile) throws Exception {
        HttpClient httpClient = null;
        HttpEntity httpEntity = null;

        try {
            httpClient = HttpClientBuilder.create().build();

            HttpPost httpPost = new HttpPost(reqUrl);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }

            ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    postParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }

            httpPost.setEntity(new UrlEncodedFormEntity(postParams, Constants.DEFAULT_ENCODING));
            logger.debug("<<Request URL>> {}", reqUrl);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            int httpStatus = httpResponse.getStatusLine().getStatusCode();
            logger.info("<<HTTP 응답({})>>", httpStatus);

            if (HttpStatus.SC_OK == httpStatus) {
                httpEntity = httpResponse.getEntity();
            }
        } catch (Exception e) {
            logger.error("<<HTTP Connection 에러>>", e);
            throw e;
        }

        return httpEntity;
    }

    /**
     * @author 전강욱(realsnake@jahasmart.com)
     * @description 텍스트를 음성 파일로 저장
     *
     * @param text
     * @param targetFile
     */
    @Async
    public Future<String> saveMp3OfVocalware(String text, File targetFile) {
        String connUrl = "http://www.vocalware.com/tts/gen.php";

        Map<String, String> params = new ConcurrentHashMap<String, String>();
        params.put("EID", "3");
        params.put("LID", "13");
        params.put("VID", "1");
        params.put("TXT", text);
        params.put("EXT", "mp3");
        params.put("FX_TYPE", "");
        params.put("FX_LEVEL", "");
        params.put("ACC", "5847458");
        params.put("API", "2495595");
        params.put("SESSION", "");
        params.put("HTTP_ERR", "");

        StringBuilder sb = new StringBuilder();
        // sb.append(params.get("EID")).append(params.get("LID")).append(params.get("VID")).append(URLEncoder.encode(params.get("TXT"), Constants.DEFAULT_ENCODING));
        sb.append(params.get("EID")).append(params.get("LID")).append(params.get("VID")).append(params.get("TXT"));
        sb.append(params.get("EXT")).append(params.get("FX_TYPE")).append(params.get("FX_LEVEL")).append(params.get("ACC")).append(params.get("API")).append(params.get("SESSION"))
                .append(params.get("HTTP_ERR"));
        sb.append("dad907f5d97d0adc6c1ed65d185999c9"); // SECRET

        // logger.debutg(URLEncoder.encode(params.get("TXT"), Constants.DEFAULT_ENCODING));
        String cs = DigestUtils.md5Hex(sb.toString());
        // logger.debug(cs);
        params.put("CS", cs);

        // this.connectPost(connUrl, null, params, new File("C:\\temp", "tts-1.mp3"));

        HttpEntity httpEntity = null;

        try {
            httpEntity = this.connectPost(connUrl, null, params, targetFile);
            FileUtils.writeByteArrayToFile(targetFile, EntityUtils.toByteArray(httpEntity));
            logger.info("<<Vocalware 연동 및 오디오 파일[{}] 저장 성공>>", targetFile.getCanonicalFile());
        } catch (Exception e) {
            logger.error("<<Vocalware mp3파일 저장 중 에러>>", e);
        }

        return new AsyncResult<String>("SUCCESS");
    }

    private static final String VOICE_MIJIN = "mijin";
    private static final String VOICE_JINHO = "jinho";

    /**
     * @author 전강욱(realsnake@jahasmart.com)
     * @description 텍스트를 음성 파일로 저장
     *
     * @param clientId
     * @param clientSecret
     * @param maleOrFemale
     * @param text
     * @param targetFile
     */
    @Async
    public Future<String> saveMp3OfNaver(String clientId, String clientSecret, String maleOrFemale, String text, File targetFile) {
        String connUrl = "https://openapi.naver.com/v1/voice/tts.bin";
        Map<String, String> headers = new ConcurrentHashMap<String, String>();
        headers.put("X-Naver-Client-Id", clientId);
        headers.put("X-Naver-Client-Secret", clientSecret);
        Map<String, String> params = new ConcurrentHashMap<String, String>();
        params.put("speaker", ("female".equals(maleOrFemale)) ? VOICE_MIJIN : VOICE_JINHO); // mijin:미진(한국어, 여성), jinho:진호(한국어, 남성)
        params.put("speed", "0"); // -5 ~ 5 사이 정수로 -5면 0.5배 빠른, 5면 0.5배 느린, 0이면 정상 속도의 목소리로 합성
        params.put("text", text);

        HttpEntity httpEntity = null;

        try {
            httpEntity = this.connectPost(connUrl, headers, params, targetFile);
            if (httpEntity != null) {
                FileUtils.writeByteArrayToFile(targetFile, EntityUtils.toByteArray(httpEntity));
                logger.info("<<Naver 연동 및 오디오 파일[{}] 저장 성공>>", targetFile.getCanonicalFile());
            }
        } catch (Exception e) {
            logger.error("<<Naver mp3파일 저장 중 에러>>", e);
        }

        return new AsyncResult<String>("SUCCESS");
    }

    // public static void main(String[] args) throws Exception {
    // HttpUtils hu = new HttpUtils();
    // String clientId = "m48ceQLVHlCHbMWzUnyH";
    // String clientSecret = "pR3_a5Nngd";
    // String text = "안녕하세요? 대한민국의 신바람 박사 전강욱입니다. 이히~ 조아~조아~조아~";
    //
    // hu.saveMp3OfNaver(clientId, clientSecret, "female", text, new File("C:\\temp", "tts-2.mp3"));
    // }

    /**
     * create by shavrani 2016-08-31
     */
    @SuppressWarnings("unchecked")
    public String multipart(String reqUrl, Map<String, String> headers, Map<String, Object> params, Map<String, Object> fileParams, Integer timeout) throws Exception {
        HttpClient httpClient = null;
        String responseText = "";

        logger.info("<< {} 주소로 HTTP multipart 요청 >>", reqUrl);

        try {

            if (timeout == null || timeout < 5) {
                timeout = 5;// 기본 timeout 시간
            }
            RequestConfig httpConfig = RequestConfig.custom().setConnectTimeout(timeout * 1000).setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();
            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(httpConfig).build();

            HttpPost httpPost = new HttpPost(reqUrl);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }

            MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
            mpEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            mpEntity.setCharset(Charset.forName("UTF-8"));

            if (params != null) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    if (entry.getValue() instanceof String[]) {
                        String[] arrStr = (String[]) entry.getValue();
                        for (String strParam : arrStr) {
                            mpEntity.addTextBody(entry.getKey(), StringUtil.nvl(strParam));
                        }
                    } else if (entry.getValue() instanceof List) {
                        List<String> strParamList = (List<String>) entry.getValue();
                        for (String strParam : strParamList) {
                            mpEntity.addTextBody(entry.getKey(), StringUtil.nvl(strParam));
                        }
                    } else {
                        mpEntity.addTextBody(entry.getKey(), StringUtil.nvl(entry.getValue()));
                    }
                }
            }

            if (fileParams != null) {
                for (Map.Entry<String, Object> entry : fileParams.entrySet()) {
                    Object objectValue = entry.getValue();
                    if (objectValue != null) {
                        if (objectValue instanceof List) {
                            List<Object> fileList = (List<Object>) objectValue;
                            for (Object object : fileList) {
                                if (object instanceof File) {
                                    if (object != null) {
                                        File file = (File) object;
                                        if (!StringUtil.isBlank(file.getName())) {
                                            mpEntity.addPart(entry.getKey(), new FileBody(file));
                                        }
                                    }
                                } else if (object instanceof MultipartFile) {
                                    if (object != null) {
                                        MultipartFile multipart = (MultipartFile) object;
                                        if (!multipart.isEmpty() && !StringUtil.isBlank(multipart.getOriginalFilename())) {
                                            mpEntity.addPart(entry.getKey(), new ByteArrayBody(multipart.getBytes(), multipart.getOriginalFilename()));
                                        }
                                    }
                                }
                            }
                        } else if (objectValue instanceof File[]) {
                            File[] fileList = (File[]) objectValue;
                            for (File file : fileList) {
                                if (file != null && !StringUtil.isBlank(file.getName())) {
                                    mpEntity.addPart(entry.getKey(), new FileBody(file));
                                }
                            }
                        } else if (objectValue instanceof MultipartFile[]) {
                            MultipartFile[] fileList = (MultipartFile[]) objectValue;
                            for (MultipartFile multipart : fileList) {
                                if (multipart != null && !multipart.isEmpty() && !StringUtil.isBlank(multipart.getOriginalFilename())) {
                                    mpEntity.addPart(entry.getKey(), new ByteArrayBody(multipart.getBytes(), multipart.getOriginalFilename()));
                                }
                            }
                        } else if (objectValue instanceof File) {
                            File file = (File) objectValue;
                            if (!StringUtil.isBlank(file.getName())) {
                                mpEntity.addPart(entry.getKey(), new FileBody(file));
                            }
                        } else if (objectValue instanceof MultipartFile) {
                            MultipartFile multipart = (MultipartFile) objectValue;
                            if (!multipart.isEmpty() && !StringUtil.isBlank(multipart.getOriginalFilename())) {
                                mpEntity.addPart(entry.getKey(), new ByteArrayBody(multipart.getBytes(), multipart.getOriginalFilename()));
                            }
                        }
                    }
                }
            }

            httpPost.setEntity(mpEntity.build());

            HttpResponse httpResponse = httpClient.execute(httpPost);
            int httpStatus = httpResponse.getStatusLine().getStatusCode();
            logger.info("<<HTTP 응답({})>>", httpStatus);

            if (HttpStatus.SC_OK == httpStatus) {
                HttpEntity httpEntity = httpResponse.getEntity();
                responseText = EntityUtils.toString(httpEntity);
                logger.debug("<<HTTP response 성공>> responseText : {} ", responseText);
            } else {
                logger.error("<<HTTP response 에러>> status : {} ", httpStatus);
                responseText = "error status " + httpStatus;
            }
        } catch (Exception e) {
            logger.error("<<HTTP Connection 에러>>", e);
            responseText = "fail";
        }

        return responseText;
    }
}
