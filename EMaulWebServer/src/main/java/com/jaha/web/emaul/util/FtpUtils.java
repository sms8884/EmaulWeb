/**
 *
 */
package com.jaha.web.emaul.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jaha.web.emaul.constants.Constants;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * <pre>
 * Class Name : FtpUtils.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 8. 25.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 8. 25.
 * @version 1.0
 */
@Component
public class FtpUtils {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.profiles.active}")
    private String springProfilesActive;

    /**
     * @author 전강욱(realsnake@jahasmart.com)
     * @description FTP 다운로드
     *
     * @param saveDir
     * @param ip
     * @param port
     * @param id
     * @param pw
     * @param changeDir
     * @param afterCal
     */
    public List<File> downloadFtpFile(String saveDir, String ip, int port, String id, String pw, String changeDir, Long afterTime) {
        FTPClient ftpClient = null;

        InputStream is = null;
        FileOutputStream fos = null;

        List<File> resultFileList = null;

        try {
            // ftpClient = new FTPSClient("SSL");
            ftpClient = new FTPClient();
            // ftpClient.setControlEncoding("ISO-8859-1");
            ftpClient.connect(ip, port);

            int reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                logger.debug("<<FTP 서버 접속 거부>>");
                ftpClient.disconnect();
            }

            ftpClient.setSoTimeout(10000);

            if (!ftpClient.login(id, pw)) {
                logger.debug("<<FTP 로그인 실패! id 또는 pw 확인 요망>>");
                ftpClient.disconnect();
            }

            // 패시브 모드
            ftpClient.enterLocalPassiveMode();
            // 파일타입
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            // 디렉토리 변경
            if (StringUtils.isNotBlank(changeDir)) {
                ftpClient.changeWorkingDirectory(changeDir);
            }

            FTPFile[] ftpFiles = ftpClient.listFiles();

            if (ftpFiles == null) {
                logger.info("<<파일이 존재하지 않아 FTP 파일을 저장하지 못함>>");
            } else {
                String yyyyMMddHHmmss = Constants.DEFAULT_SDF.format(new Date());
                String ymd = yyyyMMddHHmmss.substring(0, 8);
                String hh = yyyyMMddHHmmss.substring(8, 10);

                String filePath = saveDir + File.separator + ymd + File.separator + hh;

                File dir = new File(filePath);
                if (!dir.exists()) {
                    logger.debug("<<메트로뉴스 FTP 경로 생성>> {}", filePath);
                    dir.mkdirs();
                }

                resultFileList = new ArrayList<File>();

                for (FTPFile ftpFile : ftpFiles) {
                    if (!ftpFile.isFile()) {
                        logger.debug("<<파일이 아닌 디렉토리이므로 통과>>");
                        continue;
                    }

                    logger.debug("<<FTP 파일 정보>> {}", ftpFile.toString());

                    if (afterTime != null) { // 메트로신문사 FTP에서 뉴스를 가져오기위한...
                        String[] names = ftpFile.getName().split("[.]", -1);
                        String[] times = names[0].split("[_]", -1);

                        Long newsCrtTime = new Long(times[1]);

                        if (newsCrtTime < afterTime) {
                            logger.debug("<<뉴스생성시간>>{}, <<비교시간>> {}, 보다 전이다, 통과>>", newsCrtTime, afterTime);
                            continue;
                        }
                    }

                    try {
                        File file = new File(filePath, ftpFile.getName());

                        is = ftpClient.retrieveFileStream(file.getName());
                        fos = new FileOutputStream(file);

                        IOUtils.copy(is, fos);
                        fos.flush();

                        resultFileList.add(file);

                        logger.debug("<<FTP 파일 저장 여부>> {}", ftpClient.completePendingCommand());

                        if ("real".equals(springProfilesActive)) { // FTP 서버의 파일 삭제
                            ftpClient.deleteFile(ftpFile.getName());
                        }
                    } catch (IOException ioe) {
                        logger.error("<<FTP 파일 저장 오류>>", ioe);
                    } finally {
                        if (fos != null) {
                            IOUtils.closeQuietly(fos);
                        }
                        if (is != null) {
                            IOUtils.closeQuietly(is);
                        }
                    }
                }
            }

            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException ioe) {
            logger.error("<<FTP 통신 오류>>", ioe);
        } finally {
            if (ftpClient != null && ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ioe) {
                }
            }
        }

        return resultFileList;
    }

    /**
     * @author 전강욱(realsnake@jahasmart.com)
     * @description SFTP 다운로드
     *
     * @param savingDir
     * @param ip
     * @param port
     * @param id
     * @param pw
     * @param changeDir
     */
    @SuppressWarnings("unchecked")
    public void downloadSftpFile(String savingDir, String ip, int port, String id, String pw, String changeDir) {
        Session session = null;
        Channel channel = null;

        InputStream is = null;
        FileOutputStream fos = null;

        // 1. JSch 객체를 생성한다.
        JSch jsch = new JSch();

        boolean result = false;

        try {
            // 2. 세션 객체를 생성한다(사용자 이름, 접속할 호스트, 포트를 인자로 전달한다.)
            session = jsch.getSession(id, ip, port);

            // 3. 패스워드를 설정한다.
            session.setPassword(pw);

            // 4. 세션과 관련된 정보를 설정한다.
            Properties config = new Properties();
            // 4-1. 호스트 정보를 검사하지 않는다.
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            // 5. 접속한다.
            session.connect();

            // 6. sftp 채널을 연다.
            channel = session.openChannel("sftp");

            // 7. 채널에 연결한다.
            channel.connect();

            // 8. 채널을 FTP용 채널 객체로 캐스팅한다.
            ChannelSftp channelSftp = (ChannelSftp) channel;
            if (StringUtils.isNotBlank(changeDir)) {
                channelSftp.cd(changeDir);
            }

            Vector<ChannelSftp.LsEntry> list = channelSftp.ls(".");

            if (list == null || list.isEmpty()) {
                logger.info("<<메트로 FTP서버에 파일이 존재하지 않아 다운로드 종료>>");
                session.disconnect();
                return;
            }

            String yyyyMMddHHmmss = Constants.DEFAULT_SDF.format(new Date());
            String ymd = yyyyMMddHHmmss.substring(0, 8);
            String hh = yyyyMMddHHmmss.substring(8, 10);
            String filePath = savingDir + File.separator + ymd + File.separator + hh;

            File dir = new File(filePath);
            if (!dir.exists()) {
                logger.debug("<<메트로뉴스 FTP 경로 생성>> {}", filePath);
                dir.mkdirs();
            }

            for (ChannelSftp.LsEntry items : list) {
                if (items.getAttrs().isDir()) {
                    logger.debug("<<파일이 아닌 디렉토리이므로 통과>> {}", items.getFilename());
                    continue;
                }

                logger.info("<<{} 파일을 다운로드 합니다>>", items.getFilename());

                is = channelSftp.get(items.getFilename());

                File file = new File(filePath, items.getFilename());
                fos = new FileOutputStream(file);

                int i = 0;

                while ((i = is.read()) != -1) {
                    fos.write(i);
                }

                logger.debug("<<FTP 파일 저장 여부>> {}", result);
            }
        } catch (Exception e) {
            logger.error("<<SFTP 통신 오류>>", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                }
            }
            if (session != null && session.isConnected()) {
                try {
                    session.disconnect();
                } catch (Exception e) {
                }
            }
        }
    }

    // public static void main(String[] args) throws Exception {
    // FtpUtils fu = new FtpUtils();
    // fu.downloadFtpFile("C:\\nas\\EMaul\\metro-news", Constants.METRO_FTP_SERVER_IP, Constants.METRO_FTP_SERVER_PORT, Constants.METRO_FTP_ID, Constants.METRO_FTP_PW, Constants.METRO_FTP_ROOT_DIR,
    // 20160826153000L);
    // }

}
