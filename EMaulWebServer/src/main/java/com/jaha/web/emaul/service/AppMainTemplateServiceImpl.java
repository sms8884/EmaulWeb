/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 10. 6.
 */
package com.jaha.web.emaul.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jaha.web.emaul.constants.Constants;
import com.jaha.web.emaul.mapper.AppMainTemplateMapper;
import com.jaha.web.emaul.model.AppMainTemplate;
import com.jaha.web.emaul.model.AppMainTemplateDetail;
import com.jaha.web.emaul.model.FileInfo;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.util.StringUtil;

/**
 * <pre>
 * Class Name : AppMainTemplateServiceImpl.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 6.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 10. 6.
 * @version 1.0
 */
@Service
public class AppMainTemplateServiceImpl implements AppMainTemplateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppMainTemplateServiceImpl.class);

    @Autowired
    AppMainTemplateMapper appMainTemplateMapper;

    @Autowired
    private CommonService commonService;

    @Override
    public AppMainTemplate getAppMainTemplate(Integer id) {

        AppMainTemplate result = appMainTemplateMapper.selectAppMainTemplate(id);
        LOGGER.debug("AppMainTemplate " + result.toString());

        return result;
    }

    @Override
    public List<AppMainTemplate> getAppMainTemplateList(String tapId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("display_yn", "Y");
        map.put("AppHead", tapId);
        List<AppMainTemplate> result = appMainTemplateMapper.selectAppMainTemplateList(map);
        LOGGER.debug("AppMainTemplateList  " + result.toString());
        return result;
    }


    @Override
    public List<AppMainTemplateDetail> getAppMainTemplateDetailList(Map<String, Object> map) {

        List<AppMainTemplateDetail> result = appMainTemplateMapper.selectAppMainTemplateDetailList(map);
        LOGGER.debug("AppMainTemplateDetailUseList  " + result.toString());

        return result;
    }

    @Override
    public List<AppMainTemplateDetail> getAppMainTemplateDetailListDisplay(String tapId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("AppHead", tapId);
        List<AppMainTemplateDetail> result = appMainTemplateMapper.selectAppMainTemplateDetailDisplay(map);
        LOGGER.debug("AppMainTemplateDetailUseList  " + result.toString());

        return result;
    }


    @Override
    @Transactional
    public boolean saveAndFlushTemplateAndDetail(List<AppMainTemplate> appMainTemplate, List<AppMainTemplateDetail> appMainTemplateDetail, Map<String, MultipartFile> fileMap, User user) {


        List<String> fileList = fileMap.keySet().stream().filter(key -> key.startsWith("fileVal")).collect(Collectors.toList());
        for (int i = 0; i < fileList.size(); i++) {
            fileMap.get(fileList.get(i));
            // System.out.println(" ##### key : " + fileList.get(i));
        }
        for (int i = 0; i < appMainTemplate.size(); i++) {
            if (appMainTemplate.get(i).getId() > 1000) { // 새로추가된 Template

                AppMainTemplate appMain = appMainTemplate.get(i);
                appMain.setRegId(user.id);

                if (appMain.getCode().equals("BEST_COMMUNITY_POST")) {
                    appMain.setTemplateTitle(appMain.getCode());
                    appMain.setDisplayTemplateName("커뮤니티 인기글");

                } else if (appMain.getCode().equals("METRO_TODAY_NEWS")) {
                    appMain.setTemplateTitle(appMain.getCode());
                    appMain.setDisplayTemplateName("오늘의 뉴스 (메트로신문)");
                } else if (appMain.getCode().equals("ALARM_BANNER")) {
                    appMain.setTemplateTitle(appMain.getCode());

                } else if (appMain.getCode().equals("ADVERT_BANNER")) {
                    appMain.setTemplateTitle(appMain.getCode());

                }


                int browserKey = appMainTemplate.get(i).getId();

                appMainTemplateMapper.insertAppMainTemplate(appMain); // 새로추가된 Template PK아이디를 얻어옵니다
                int mainTemplateId = appMain.getId();

                int fileNum = 0;

                for (int j = 0; j < appMainTemplateDetail.size(); j++) { // appMainDetail 추가

                    if (browserKey == (appMainTemplateDetail.get(j).getMainTemplateId())) { // 브라우저에서 사용하던 아이디로 Detail 값을 찾는다

                        appMainTemplateDetail.get(j).setMainTemplateId(mainTemplateId); // 위에서 생성된 main pk를 FK 로 셋팅

                        AppMainTemplateDetail appMainDetail = appMainTemplateDetail.get(j);
                        appMainDetail.setRegId(user.id);
                        appMainDetail.setDirection("CENTER");
                        String fileKey = "fileVal" + browserKey + "-" + fileNum;

                        appMainTemplateMapper.insertAppMainTemplateDetail(appMainDetail); // fileGroupkey 에사용될 detatil pk

                        int mainDetailId = appMainDetail.getId();

                        if (fileMap.get(fileKey) != null) {
                            String middlePath = Constants.FILE_APP_MAIN + File.separator + StringUtil.nvl(mainDetailId);

                            FileInfo fileInfo = new FileInfo();
                            fileInfo.category = Constants.FILE_CATEGORY_APP_MAIN;
                            fileInfo.fileGroupKey = String.valueOf(mainDetailId);
                            fileInfo.regId = user.id;
                            commonService.saveFileInfo(fileMap.get(fileKey), fileInfo, middlePath);
                            fileNum++;
                        }
                    }
                }

            } else {
                AppMainTemplate appMain = appMainTemplate.get(i);
                appMain.setModId(user.id);

                appMainTemplateMapper.updateAppMainTemplate(appMain);

                int fileNum = 0;
                for (int j = 0; j < appMainTemplateDetail.size(); j++) {

                    int appMainId = appMain.getId();
                    int appMainDetailMainId = appMainTemplateDetail.get(j).getMainTemplateId();

                    if (appMainId == appMainDetailMainId) {
                        AppMainTemplateDetail appMainDetail = appMainTemplateDetail.get(j);
                        appMainDetail.setModId(user.id);
                        if (appMainDetail.getRegId() == null) {
                            appMainDetail.setRegId(user.id);
                            appMainTemplateMapper.insertAppMainTemplateDetail(appMainDetail);
                        } else {

                            appMainTemplateMapper.updateAppMainTemplateDetail(appMainDetail);
                        }
                        String fileKey = "fileVal" + appMain.getId() + "-" + fileNum;


                        // LOGGER.info("fileKEy" + fileKey);
                        if (fileMap.get(fileKey) != null) {

                            if ((appMain.getCode().equals("A_TYPE_BANNER") && appMainDetail.getFileGroupKey() == 0)
                                    || (appMain.getCode().equals("B_TYPE_BANNER") && appMainDetail.getFileGroupKey() == 0)) {
                                continue;
                            }
                            // direction 이비어있을경우
                            if (appMainDetail.getDirection() == null) {
                                appMainDetail.setDirection("CENTER");
                            }

                            // 파일 순서와 디테일순서가 맞지않아 업로드 되는 디테일이 달라져서 예외처리
                            if ((appMainDetail.getDirection().equals("LEFT") && fileNum == 2) || (appMainDetail.getDirection().equals("RIGHT") && fileNum == 3 && j == 6)) {
                                continue;
                            }
                            boolean deleteFile = commonService.deleteFileGroup("main", appMainDetail.getId());
                            int deleteFileSize = commonService.getFileGroup("main", appMainDetail.getId()).size();
                            if (deleteFile == true || deleteFileSize == 0) {
                                String middlePath = Constants.FILE_APP_MAIN + File.separator + StringUtil.nvl(appMainDetail.getId());
                                FileInfo fileInfo = new FileInfo();
                                fileInfo.category = Constants.FILE_CATEGORY_APP_MAIN;
                                fileInfo.fileGroupKey = String.valueOf(appMainDetail.getId());
                                fileInfo.regId = user.id;
                                commonService.saveFileInfo(fileMap.get(fileKey), fileInfo, middlePath);

                            }
                        }
                        fileNum++;
                    }
                }
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.service.AppMainTemplateService#getAppMenuIdList()
     */
    @Override
    public List<String> getAppMenuIdList() {
        return appMainTemplateMapper.selectAppMenuIdList();
    }



}
