/**
 *
 */
package com.jaha.web.emaul.util;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import diotts.Pttsnet;

/**
 * @author 전강욱(realsnake@jahasmart.com)
 * @description
 *
 */
@Component
public class MultimediaUtils {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${tts.server.ip}")
    private String ttsServerIp;
    @Value("${tts.server.port}")
    private String ttsServerPort;

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 5. 31.
     * @description TTS WAV 파일을 생성한다.
     *
     * @param text
     * @param targetFile C:\\Temp\\tts.wav
     */
    @Async
    public Future<String> makeTts(String text, File targetFile) {
        Pttsnet tts = new Pttsnet();

        // korean 0
        // speaker id 0 (0~9)
        // pitch 100 (80~120)
        // speed 100 (50~200)
        // volume 100 (50~150)

        // 오디오포맷 548
        // <select name="sformat">
        // <option value="273">8K 16bit Linear PCM
        // <option value="274">8K 8bit Linear PCM
        // <option value="275">8K u-Law PCM
        // <option value="276">8K a-Law PCM
        // <option value="277">8K VOX
        // <option value="289">8K 16bit Linear WAVE
        // <option value="290">8K 8bit Linear WAVE
        // <option value="291">8K u-Law WAVE
        // <option value="292">8K a-Law WAVE
        // <option value="305">8K 16bit Linear AU
        // <option value="306">8K 8bit Linear AU
        // <option value="307">8K u-Law AU
        // <option value="308">8K a-Law AU
        // <option value="529">16K 16bit Linear PCM
        // <option value="530">16K 8bit Linear PCM
        // <option value="531">16K u-Law PCM
        // <option value="532">16K a-Law PCM
        // <option value="533">16K VOX
        // <option value="545" selected>16K 16bit Linear WAVE
        // <option value="546">16K 8bit Linear WAVE
        // <option value="547">16K u-Law WAVE
        // <option value="548">16K a-Law WAVE
        // <option value="561">16K 16bit Linear AU
        // <option value="562">16K 8bit Linear AU
        // <option value="563">16K u-Law AU
        // <option value="564">16K a-Law AU
        // </select>

        try {
            int ret = tts.PTTSNET_FILE_A_EX(this.ttsServerIp, this.ttsServerPort, 60, 60, text, targetFile.getPath(), "", "", 0, 0, 548, 100, 100, 100, 0x00, Pttsnet.PTTSNET_CONTENT_PLAIN,
                    Pttsnet.PTTSNET_CHARSET_UTF8, -1, "", 0);

            if (ret == 0) {
                this.convert(targetFile, "mp3");
            }

            logger.info("* TTS 파일 생성 처리 결과: " + ret + ", " + tts.errorMsg);
        } catch (Exception e) {
            logger.error("* TTS 파일 생성 중 오류", e);
        }

        return new AsyncResult<String>("SUCCESS");
    }

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 5. 31.
     * @description 멀티미디어 파일 변환
     *
     * @param sourceFile
     * @param format
     * @throws Exception
     */
    public void convert(File sourceFile, String format) throws Exception {
        if (sourceFile == null) {
            logger.info("* 변환할 파일경로와 이름이 존재하지 않습니다!");
            return;
        }
        if (StringUtils.isBlank(format)) {
            logger.info("* 변환할 파일 포맷(예: mp3)이 존재하지 않습니다!");
            return;
        }

        String outputName = sourceFile.getPath().substring(0, sourceFile.getPath().indexOf(".")) + "." + format;
        File outFile = new File(outputName);

        List<String> commands = new ArrayList<String>();
        // commands.add(FFMPEG_EXEC_FILE);
        commands.add("ffmpeg");
        // 중복된 파일이 존재할 경우 에러 없이 process가 멈추는 현상 발생. 파일명이 중복되지 않는 방향으로 코딩할 것.
        commands.add("-y"); // 인코딩한 파일(출력 동영상 등)과 이름이 같은 파일이 있을 경우 덮어쓰기
        commands.add("-i"); // Input file name
        commands.add(sourceFile.getPath());
        commands.add("-ar");
        // Audio sampling rate in Hz (audio sampling frequency. default = 44100
        // Hz) 샘플링시 얼마만큼 정교하게 샘플링하는지에 대한 주파수를 말하며 1초 동안 몇번을 샘플링하는지를 계산하여 나타냄.
        // 11KHz(11025), 22KHz(22050), 44KHz(44100)로 분류되며 값이 클수록 원음에 가깝다.
        commands.add("44100");
        // commands.add("-s"); // 동영상 파일인 경우
        // Frame size (해상도, 가로x세로, default = 160x128)
        // sqcit 128x96 qcif 176x44 cif 352x288 4cif 704x576 16cif 1408x1152
        // qqvga 160x120 qvga 320x240 vga 640x480 svga 800x600 wvga 852x480
        // xga 1024x768 uxga 1600x1200 qxga 2048x1536 sxga 1280x1024 qsxga 2560x2048 hsxga 5120x4096
        // wxga 1366x768 wsxga 1600x1024 wuxga 1920x1200 woxga 2560x1600
        // wqsxga 3200x2048 wquxga 3840x2400 whsxga 6400x4096 whuxga 7680x4800
        // cga 320x200 ega 640x350 hd480 852x480 hd720 1280x720 hd1080 1920x1080
        // (예) ffmpeg -i in.flv -vframes 1 -s hd720 out.jpg
        // (예) ffmpeg -i in.flv -vframes 1 -s 640x320 out.jpg
        // commands.add("cga"); // 동영상 파일인 경우
        commands.add(outFile.getPath());
        // // 파일명이 연속으로 나올 경우 연속된 파일명으로 인코딩함. 그러나 각각 인코딩 하는 것보다 속도는 현저하게 느려짐.
        // commands.add("C:\\download\\converted\\" + outputName + ".ogg");
        // commands.add("C:\\download\\converted\\" + outputName + ".webm");

        logger.info(commands.toString());

        // [비디오/오디오 포맷/코덱]
        // - 대부분의 스마트폰은 mp4 지원
        // - 인코딩 시간 오래걸리는 포맷: mp4, mkv, webm 등
        // - 비디오 포맷: AVI, FLV, M4V, MKV, MOV, MP4, MPG, OGG, OGV, RM, SWF, TP, TS, WEBM, WMV 기타 등등.
        // - 비디오 코덱: H.263, H.264 (x264), Mpeg2, Mpeg4 (DivX, XviD and its own), RV10, Theora, VP8,
        // WMV (v7, v8) 기타 등등.
        // - 오디오 포맷 /코덱: 3G2, 3GP, AAC, AC3, ADPCM, AIFF, AMR (NB, WB), AU, FLAC, GSM, M4A, MP3,
        // MP4, OGG, PCM, RA, VORBIS, WAVE, WMA 기타 등등.

        Process p = null;
        try {
            ProcessBuilder pb = new ProcessBuilder();
            // 에러 스트림을 분리하지않음(stderr > stdout)
            pb.redirectErrorStream(true);
            pb.directory(new File(sourceFile.getParent()));
            pb.command(commands);
            // 프로세스 작업을 실행함.
            p = pb.start();

            // 자식 프로세스에서 발생되는 인풋 스트림+에러 스트림(FFMPEG이 콘솔로 보내는 표준출력 및 표준에러) 처리
            exhaustWithScannerInputStream(p.getInputStream());

            // p의 자식 프로세스의 작업(동영상 등 변환 작업)이 완료될 동안 p를 대기시킴
            int exitValue = p.waitFor();

            if (exitValue == 0) {
                if (outFile.length() == 0) {
                    throw new Exception("* 변환된 파일의 사이즈가 0임!");
                }
            } else {
                throw new Exception("* 변환 중 에러 발생(Probably FFMPEG option error)!");
            }
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            try {
                if (p != null)
                    p.destroy();
            } catch (Exception e) {
            }
        }
    }

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 5. 31.
     * @description
     *
     * @param is
     */
    private void exhaustWithScannerInputStream(final InputStream is) {
        // InputStream.read() 에서 블럭 상태에 빠지기 때문에 따로 쓰레드를 구현하여 스트림을 소비한다.
        new Thread() {
            @Override
            public void run() {
                try {
                    @SuppressWarnings("resource")
                    Scanner sc = new Scanner(is);
                    // Find duration
                    Pattern durPattern = Pattern.compile("(?<=Duration: )[^,]*");
                    String dur = sc.findWithinHorizon(durPattern, 0);
                    if (dur == null) {
                        throw new RuntimeException("Could not parse duration.");
                    }

                    String[] hms = dur.split(":");
                    // System.out.println(ArrayUtils.toString(hms));

                    if (hms != null && hms.length > 1) { // 동영상/오디오/jpg 파일일 경우에만...
                        double totalSecs = Integer.parseInt(hms[0]) * 3600 + Integer.parseInt(hms[1]) * 60 + Double.parseDouble(hms[2]);
                        System.out.println("* Total duration: " + totalSecs + " seconds.");

                        // Find time as long as possible.
                        Pattern timePattern = Pattern.compile("(?<=time=)[^ ]*");

                        String match = null;
                        while (null != (match = sc.findWithinHorizon(timePattern, 0))) {
                            String[] times = match.split(":");
                            double progress = (Integer.parseInt(times[0]) * 3600 + Integer.parseInt(times[1]) * 60 + Double.parseDouble(times[2])) / totalSecs;
                            logger.debug("* Progress: {}", progress * 100);
                        }
                    }
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }.start();
    }

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 5. 31.
     * @description 이미지 파일 변환
     *
     * @param sourceFile
     * @param format
     * @throws Exception
     */
    public void convertImage(File sourceFile, String format) throws Exception {
        if (sourceFile == null) {
            logger.info("* 변환할 파일경로와 이름이 존재하지 않습니다!");
            return;
        }
        if (StringUtils.isBlank(format)) {
            logger.info("* 변환할 파일 포맷(예: mp3)이 존재하지 않습니다!");
            return;
        }

        String outputName = sourceFile.getPath().substring(0, sourceFile.getPath().indexOf(".")) + "-1." + format;
        File outFile = new File(outputName);

        List<String> commands = new ArrayList<String>();
        commands.add("ffmpeg");
        // 중복된 파일이 존재할 경우 에러 없이 process가 멈추는 현상 발생. 파일명이 중복되지 않는 방향으로 코딩할 것.
        commands.add("-y"); // 인코딩한 파일(출력 동영상 등)과 이름이 같은 파일이 있을 경우 덮어쓰기
        commands.add("-i"); // 변환할 파일명
        commands.add(sourceFile.getPath());
        commands.add("-quality");
        commands.add("100");
        commands.add("-format");
        commands.add("png");
        commands.add("-compression_level");
        commands.add("100");
        commands.add(outFile.getPath());
        logger.info(commands.toString());

        Process p = null;
        try {
            ProcessBuilder pb = new ProcessBuilder();
            // 에러 스트림을 분리하지않음(stderr > stdout)
            pb.redirectErrorStream(true);
            pb.directory(new File(sourceFile.getParent()));
            pb.command(commands);
            // 프로세스 작업을 실행함.
            p = pb.start();

            // 자식 프로세스에서 발생되는 인풋 스트림+에러 스트림(FFMPEG이 콘솔로 보내는 표준출력 및 표준에러) 처리
            exhaustWithScannerInputStream(p.getInputStream());

            // p의 자식 프로세스의 작업(동영상 등 변환 작업)이 완료될 동안 p를 대기시킴
            int exitValue = p.waitFor();

            if (exitValue == 0) {
                if (outFile.length() == 0) {
                    throw new Exception("* 변환된 파일의 사이즈가 0임!");
                }
            } else {
                throw new Exception("* 변환 중 에러 발생(Probably FFMPEG option error)!");
            }
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        } finally {
            try {
                if (p != null)
                    p.destroy();
            } catch (Exception e) {
            }
        }
    }

    // public static void main(String[] args) throws Exception {
    // MultimediaUtils mu = new MultimediaUtils();
    // // mu.convertImage(new File("C:\\Users\\A\\Desktop\\이미지\\banner.png"), "png");
    // mu.convertImage(new File("C:\\Users\\A\\Desktop\\이미지\\전효성.jpg"), "png");
    // }

}
