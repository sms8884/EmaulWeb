package com.jaha.web.emaul.service;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.jaha.web.emaul.model.AppVersion;
import com.jaha.web.emaul.model.BatchSchedule;
import com.jaha.web.emaul.model.CodeGroup;
import com.jaha.web.emaul.model.CommonCode;
import com.jaha.web.emaul.model.FileInfo;
import com.jaha.web.emaul.model.SimpleAddress;

/**
 * Created by doring on 15. 4. 2..
 */
public interface CommonService {
    AppVersion getAppVersion(String kind);

    List<CommonCode> findByCodeGroup(String codeGroup);

    /**
     * 코드 그룹 정보를 조회한다.
     *
     * @param codeGroup
     * @return
     */
    List<CodeGroup> getCodeGroup(CodeGroup codeGroup);

    /**
     * 코드 그룹 정보를 등록한다.
     *
     * @param codeGrouup
     * @return
     */
    int insertCodeGroup(CodeGroup codeGroup);

    /**
     * 공통 코드를 등록한다.
     *
     * @param commonCode
     * @return
     */
    int insertCommonCode(CommonCode commonCode);

    CommonCode findByCode(String newscategory);

    public List<BatchSchedule> getBatchScheduleGroup(String batchGroupKey);

    FileInfo getFileInfo(Long fileKey);

    FileInfo getFileInfo(String category, Object fileGroupKey, Long fileKey);

    List<FileInfo> getFileGroup(String category, Object fileGroupKey);

    List<FileInfo> getFileGroup(String category, Object fileGroupKey, String type);

    FileInfo saveFileInfo(MultipartFile multipartFile, FileInfo fileInfo, String middlePath);

    boolean deleteFileInfo(Long fileKey);

    boolean deleteFileInfo(String category, Object fileGroupKey, Long fileKey);

    boolean deleteFileInfo(String category, Object fileGroupKey, String type, Long fileKey);

    boolean deleteFileGroup(String category, Object fileGroupKey);

    boolean deleteFileGroup(String category, Object fileGroupKey, String type);

    String saveEditorImageFiles(final String middlePath, final String id, String formattedContent);

    @SuppressWarnings("rawtypes")
    List<HashMap> getGugunList();

    List<Map<String, Object>> selectAddressAptList(Map<String, Object> params);

    /**
     * 시도로 읍면동 목록을 조회한다.<br />
     *
     * @param sido
     * @return
     * @throws Exception
     */
    List<SimpleAddress> findByDongListBySido(String sido) throws Exception;

    /**
     * 시도와 시군구로 읍면동 목록을 조회한다.<br />
     * 시군구가 파라미터가 없을 경우, 시도의 동을 반환한다.<br />
     *
     * @param sido
     * @param sigungu
     * @return
     * @throws Exception
     */
    List<SimpleAddress> findDongListBySidoAndSigungu(String sido, String sigungu) throws Exception;

    /**
     * 시도로 시군구 목록을 조회한다.<br />
     *
     * @param sido
     * @return
     * @throws Exception
     */
    List<SimpleAddress> findSigunguListBySido(String sido) throws Exception;

    /**
     * 파일정보를 저장한다.
     *
     * @param fileInfo
     */
    void saveFileInfo(FileInfo fileInfo);

    public int deleteCode(Map<String, Object> params);

    public int insertCode(CommonCode commonCode);

    public Date selectDate();

}
