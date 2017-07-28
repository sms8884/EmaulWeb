package com.jaha.web.emaul.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jaha.web.emaul.mapper.CommonMapper;
import com.jaha.web.emaul.model.AppVersion;
import com.jaha.web.emaul.model.BatchSchedule;
import com.jaha.web.emaul.model.CodeGroup;
import com.jaha.web.emaul.model.CommonCode;
import com.jaha.web.emaul.model.FileInfo;
import com.jaha.web.emaul.model.SimpleAddress;
import com.jaha.web.emaul.repo.AppVersionRepository;
import com.jaha.web.emaul.repo.BatchScheduleRepository;
import com.jaha.web.emaul.repo.CommonCodeRepository;
import com.jaha.web.emaul.repo.FileInfoRepository;
import com.jaha.web.emaul.repo.SimpleAddressRepository;
import com.jaha.web.emaul.util.RandomKeys;
import com.jaha.web.emaul.util.StringUtil;
import com.jaha.web.emaul.util.TagUtils;

/**
 * Created by doring on 15. 4. 2..
 */
@Service
public class CommonServiceImpl implements CommonService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppVersionRepository appVersionRepository;

    @Autowired
    private CommonCodeRepository commonCodeRepository;

    @Autowired
    private BatchScheduleRepository batchScheduleRepository;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Value("${file.path.editor.image}")
    private String editorImagePath;

    @Value("${file.path.temp}")
    private String tempFilePath;

    @Value("${file.path.root}")
    private String rootFilePath;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private SimpleAddressRepository simpleAddressRepository;

    @Override
    public AppVersion getAppVersion(String kind) {
        return appVersionRepository.findByKind(kind);
    }

    @Override
    public List<CommonCode> findByCodeGroup(String codeGroup) {
        return commonMapper.selectCodeList(codeGroup);
        // return commonCodeRepository.findByCodeGroup(codeGroup);
    }

    @Override
    public List<CodeGroup> getCodeGroup(CodeGroup codeGroup) {
        return commonMapper.selectCodeGroupList(codeGroup);
        // return commonCodeRepository.findByCodeGroup(codeGroup);
    }

    @Override
    @Transactional
    public int insertCodeGroup(CodeGroup codeGroup) {
        return commonMapper.insertCodeGroup(codeGroup);
    }

    @Override
    @Transactional
    public int insertCommonCode(CommonCode commonCode) {
        return commonMapper.insertCommonCode(commonCode);
    }

    @Override
    public CommonCode findByCode(String newscategory) {
        return commonCodeRepository.findByCode(newscategory);
    }

    @Override
    public List<BatchSchedule> getBatchScheduleGroup(String batchGroupKey) {
        return batchScheduleRepository.findByBatchGroupKey(batchGroupKey);
    }

    /**
     * @author shavrani 2016.05.31
     */
    @Override
    public FileInfo getFileInfo(Long fileKey) {
        return fileInfoRepository.findByFileKey(fileKey);
    }

    /**
     * @author shavrani 2016.05.31
     */
    @Override
    public FileInfo getFileInfo(String category, Object fileGroupKey, Long fileKey) {
        return fileInfoRepository.findByCategoryAndFileGroupKeyAndFileKey(category, StringUtil.nvl(fileGroupKey), fileKey);
    }

    /**
     * @author shavrani 2016.05.31
     */
    @Override
    public List<FileInfo> getFileGroup(String category, Object fileGroupKey) {
        return fileInfoRepository.findByCategoryAndFileGroupKey(category, StringUtil.nvl(fileGroupKey));
    }

    /**
     * @author shavrani 2016.05.31
     */
    @Override
    public List<FileInfo> getFileGroup(String category, Object fileGroupKey, String type) {
        return fileInfoRepository.findByCategoryAndFileGroupKeyAndType(category, StringUtil.nvl(fileGroupKey), type);
    }

    /**
     * @author shavrani 2016.05.31
     * @description 메소드 인자로 전달전에 fileInfo에 기본으로 category, fileGroupKey, type(필수아님), regId 설정후 진행
     */
    @Override
    public FileInfo saveFileInfo(MultipartFile multipartFile, FileInfo fileInfo, String middlePath) {

        FileInfo saveFileInfo = null;

        if (multipartFile == null) {
            logger.info(" # saveFileInfo :: multipartFile is null !!");
        } else {
            if (fileInfo == null) {
                logger.info(" # saveFileInfo :: fileInfo is null !!");
            } else {

                String category = StringUtil.nvl(fileInfo.category);
                String fileGroupKey = StringUtil.nvl(fileInfo.fileGroupKey);

                // multipartFile, category, fileGroupKey, middlePath 는 필수 parameter ( type은 fileGroupKey내에서도 구분이 필요할 경우 )
                if (multipartFile != null && !StringUtil.isBlank(category) && !StringUtil.isBlank(fileGroupKey) && !StringUtil.isBlank(middlePath)) {
                    try {
                        String fileOriginName = multipartFile.getOriginalFilename();
                        String ext = FilenameUtils.getExtension(fileOriginName);

                        String savePath = rootFilePath + File.separator + middlePath + File.separator;

                        File dir = new File(savePath);
                        if (!dir.exists()) {
                            dir.mkdirs();
                            dir.setReadable(true, false);
                            dir.setWritable(true, false);
                        }

                        String randomFileName = String.format("%s.%s", RandomKeys.make(16), ext);
                        File dest = new File(dir, randomFileName);
                        while (dest.exists()) {
                            randomFileName = String.format("%s.%s", RandomKeys.make(16), ext);
                            dest = new File(dir, randomFileName);
                        }

                        dest.createNewFile();
                        dest.setReadable(true, false);
                        dest.setWritable(true, false);

                        long size = multipartFile.getSize() / 1024; // kb 단위로 저장

                        multipartFile.transferTo(dest);// 물리파일 저장

                        fileInfo.filePath = savePath;
                        fileInfo.fileName = randomFileName;
                        fileInfo.fileOriginName = fileOriginName;
                        fileInfo.ext = ext;
                        fileInfo.size = size;

                        saveFileInfo = fileInfoRepository.save(fileInfo);
                    } catch (Exception e) {
                        logger.info(" # saveFileInfo :: File Save Exception");
                    }
                }

            }
        }

        return saveFileInfo;
    }

    private boolean deleteFile(FileInfo fileInfo) {
        boolean result = false;
        if (fileInfo != null) {
            fileInfoRepository.delete(fileInfo.fileKey);
            File file = new File(fileInfo.filePath + fileInfo.fileName);
            result = file.delete();
        }
        return result;
    }

    /**
     * @author shavrani 2016.05.31
     */
    @Override
    public boolean deleteFileInfo(Long fileKey) {
        FileInfo fileInfo = fileInfoRepository.findByFileKey(fileKey);
        return deleteFile(fileInfo);
    }

    /**
     * @author shavrani 2016.05.31
     */
    @Override
    public boolean deleteFileInfo(String category, Object fileGroupKey, Long fileKey) {
        FileInfo fileInfo = fileInfoRepository.findByCategoryAndFileGroupKeyAndFileKey(category, StringUtil.nvl(fileGroupKey), fileKey);
        return deleteFile(fileInfo);
    }

    /**
     * @author shavrani 2016.05.31
     */
    @Override
    public boolean deleteFileInfo(String category, Object fileGroupKey, String type, Long fileKey) {
        FileInfo fileInfo = fileInfoRepository.findByCategoryAndFileGroupKeyAndTypeAndFileKey(category, StringUtil.nvl(fileGroupKey), type, fileKey);
        return deleteFile(fileInfo);
    }

    /**
     * 리스트 파일 모두 삭제
     *
     * @param fileInfoList
     * @return
     */
    private boolean deleteFileList(List<FileInfo> fileInfoList) {
        boolean result = false;
        if (fileInfoList != null && fileInfoList.size() > 0) {
            int size = fileInfoList.size();
            int deleteCnt = 0;
            for (int i = 0; i < size; i++) {
                FileInfo fileInfo = fileInfoList.get(i);
                fileInfoRepository.delete(fileInfo.fileKey);
                File file = new File(fileInfo.filePath + fileInfo.fileName);
                if (file.delete() == true) {
                    deleteCnt++;
                }
            }
            if (size == deleteCnt) {
                result = true;
            }
        }
        return result;
    }

    /**
     * @author shavrani 2016.05.31
     */
    @Override
    public boolean deleteFileGroup(String category, Object fileGroupKey) {
        List<FileInfo> fileInfoList = fileInfoRepository.findByCategoryAndFileGroupKey(category, StringUtil.nvl(fileGroupKey));
        return deleteFileList(fileInfoList);
    }

    @Override
    public boolean deleteFileGroup(String category, Object fileGroupKey, String type) {
        List<FileInfo> fileInfoList = fileInfoRepository.findByCategoryAndFileGroupKeyAndType(category, StringUtil.nvl(fileGroupKey), type);
        return deleteFileList(fileInfoList);
    }

    /**
     * @author shavrani 2016.05.31
     */
    @Override
    public String saveEditorImageFiles(final String middlePath, final String id, String formattedContent) {
        try {
            File dir = new File(String.format(editorImagePath + "/%s/%s", middlePath, id));

            if (!dir.exists()) {
                dir.mkdirs();
            }

            List<String> fileNames = new ArrayList<String>();// editor img src로 사용하는 파일목록정리

            // 나머지 이미지 게시글 폴더로 이동
            List<String> imgSrc = TagUtils.getImgSrc(formattedContent);
            for (String src : imgSrc) {
                String fileName = FilenameUtils.getName(src);
                fileNames.add(fileName);
                if (src.startsWith("/api/common/editor/image")) {
                    continue;
                }

                String renamedSrc = src.replace("/api/common/temp/image/", "/api/common/editor/image/" + middlePath + "/" + id + "/");

                File tempImageFile = new File(tempFilePath, fileName);
                File destImageFile = new File(dir, fileName);
                FileUtils.copyFile(tempImageFile, destImageFile);
                tempImageFile.delete();

                formattedContent = formattedContent.replace(src, renamedSrc);
            }
            // formattedContent = TagUtils.replaceImageSizeToHeightNull(formattedContent);
            // formattedContent = TagUtils.replaceImageSizeToWidth100(formattedContent);

            /** editor에서 img src로 사용하지않는데 존재하는 파일 제거. ( 이전에 저장했다가 editor에서 이미지제거한 물리파일 ) */
            File[] files = dir.listFiles();
            if (files != null && files.length != 0) {
                for (File file : files) {
                    boolean useFile = false;
                    for (String fileName : fileNames) {
                        if (file.getName().equals(fileName)) {
                            useFile = true;
                            break;
                        }
                    }
                    if (useFile == false) {
                        file.delete();
                    }
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return formattedContent;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<HashMap> getGugunList() {
        return commonMapper.getGugunList();
    }

    @Override
    public List<Map<String, Object>> selectAddressAptList(Map<String, Object> params) {
        return commonMapper.selectAddressAptList(params);
    }

    @Override
    @Transactional
    public List<SimpleAddress> findByDongListBySido(String sido) throws Exception {
        return this.simpleAddressRepository.findDongListBySido(sido);
    }

    @Override
    @Transactional
    public List<SimpleAddress> findDongListBySidoAndSigungu(String sido, String sigungu) throws Exception {
        if (StringUtils.isEmpty(sigungu)) {
            return this.simpleAddressRepository.findDongListBySido(sido);
        } else {
            return this.simpleAddressRepository.findDongListBySidoAndSigungu(sido, sigungu);
        }
    }

    @Override
    @Transactional
    public List<SimpleAddress> findSigunguListBySido(String sido) throws Exception {
        return this.simpleAddressRepository.findSigunguListBySido(sido);
    }

    @Override
    @Transactional
    public void saveFileInfo(FileInfo fileInfo) {
        this.fileInfoRepository.saveAndFlush(fileInfo);
    }

    @Override
    public int deleteCode(Map<String, Object> params) {
        return commonMapper.deleteCode(params);
    }

    @Override
    public int insertCode(CommonCode commonCode) {
        return commonMapper.insertCode(commonCode);
    }

    @Override
    public Date selectDate() {
        return commonMapper.selectDate();
    }

}
