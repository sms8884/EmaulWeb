package com.jaha.web.emaul.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.google.common.io.ByteStreams;
import com.jaha.web.emaul.constants.Constants;

/**
 * Created by doring on 15. 3. 20..
 */
public class Responses {

    private final static Logger LOGGER = LoggerFactory.getLogger(Responses.class);

    public static ResponseEntity<byte[]> getFileEntity(File toServeUp, String fileName) {
        final HttpHeaders headers = new HttpHeaders();

        InputStream inputStream;

        try {
            inputStream = new FileInputStream(toServeUp);
        } catch (FileNotFoundException e) {
            String msg = "ERROR: File not found.";
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<>(msg.getBytes(), headers, HttpStatus.NOT_FOUND);
        }

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileName, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Long fileSize = toServeUp.length();
        headers.setContentLength(fileSize.intValue());

        try {
            return new ResponseEntity<>(ByteStreams.toByteArray(inputStream), headers, HttpStatus.OK);
        } catch (Exception e) {
            String msg = "ERROR: Unknown.";
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<>(msg.getBytes(), headers, HttpStatus.NOT_FOUND);
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 엑셀파일을 다운로드한다.
     *
     * @param excelFileName
     * @param excelFileBuilder
     * @param request
     * @param response
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static void downloadEexcel(String excelFileName, ExcelFileBuilder excelFileBuilder, HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        OutputStream os = null;

        try {
            String fileName = getFileName(request, excelFileName);

            response.setHeader("Cache-Control", "no-cache"); // http 1.1
            response.setHeader("Pragma", "no-cache"); // http 1.0
            response.setDateHeader("Expires", -1); // proxy server에 cache방지

            response.setContentType("application/vnd.ms-excel; charset=" + Constants.DEFAULT_ENCODING);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // 파일명 지정
            response.setHeader("Content-Transfer-Encoding", "binary");

            os = response.getOutputStream();
            excelFileBuilder.write(os);
        } catch (UnsupportedEncodingException uee) {
            LOGGER.error("<<엑셀파일명 인코딩 변환 오류>>", uee);
            throw uee;
        } catch (IOException ioe) {
            LOGGER.error("<<엑셀파일 다운로드 오류>>", ioe);
            throw ioe;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ioe) {
                }
            }
        }
    }

    /**
     * 파일명 반환
     *
     * @param request
     * @param paramFileName
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String getFileName(HttpServletRequest request, String paramFileName) throws UnsupportedEncodingException {
        String userAgent = request.getHeader("User-Agent");
        String fileName = null;

        if (userAgent.indexOf("MSIE") > -1 || userAgent.indexOf("Trident") > -1) {
            fileName = URLEncoder.encode(paramFileName, Constants.DEFAULT_ENCODING);
        } else {
            fileName = new String(paramFileName.getBytes(Constants.DEFAULT_ENCODING), "ISO-8859-1");
        }

        return fileName;
    }

}
