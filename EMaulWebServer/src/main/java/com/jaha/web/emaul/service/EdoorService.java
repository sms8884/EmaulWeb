package com.jaha.web.emaul.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.jaha.web.emaul.model.AptAp;
import com.jaha.web.emaul.model.AptApAccessAuth;
import com.jaha.web.emaul.model.AptApAccessDevice;
import com.jaha.web.emaul.model.AptApAccessLog;
import com.jaha.web.emaul.model.AptApMonitoringNoti;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.model.User;


/**
 * @author shavrani
 * @since 2016. 9. 2.
 * @version 1.0
 */
public interface EdoorService {

    public Map<String, Object> getAptApList(Map<String, Object> params);

    public List<AptAp> getAptApAllList(Map<String, Object> params);

    public AptAp saveAptAp(AptAp aptAp);

    public AptAp getAptAp(Map<String, Object> params);

    public List<AptApAccessAuth> getAptApAccessAuthList(Long aptApId, String type);

    public AptApAccessAuth getAptApAccessAuth(Long aptApId, String type, String dong, String ho, String hoType);

    public AptApAccessAuth getAptApAccessAuth(Long aptApId, String type, Long userId);

    public AptApAccessAuth saveAptApAccessAuth(AptApAccessAuth aaaa);

    void deleteAptApAccessAuth(Long id);

    void deleteAptApAccessAuthType(Long aptApId, String type);

    void deleteAptApAccessAuthTypeDong(Long aptApId, String type, String dong);

    void deleteAptApAccessAuthTypeDongHoLine(Long aptApId, String type, String dong, String ho);

    void deleteAptAp(Long id);

    void deactiveAptAp(Long id);

    Page<AptApAccessDevice> getAptApAccessDeviceList(Map<String, Object> params, Pageable pageable);

    AptApAccessDevice getAptApAccessDevice(Map<String, Object> params);

    AptApAccessDevice existAptApAccessDevice(Map<String, Object> params);

    AptApAccessDevice saveAptApAccessDevice(AptApAccessDevice aptApAccessDevice);

    int deleteAptApAccessDevice(Long id);

    Map<String, Object> aptApFirmwareUpdate(User user, List<String> apList, String[] firmwareList);

    /**
     * AP 시스템 설정 업데이트
     *
     * @return
     */
    Map<String, Object> aptApSystemDataUpdate(User user, List<String> apList, Integer rssi, Integer keepon, Integer gpiodelay);

    Long changeApt(User user, Long id, Long aptId);

    Map<String, Object> aptApSystemRemoteControl(User user, List<String> apList, String fwVersion, List<String> fwTypeList, List<String> cmdList, List<String> pathList, List<MultipartFile> fileList);

    /**
     * @author shavrani 2016-11-21
     * @desc Apt Ap Access Log List
     */
    public List<AptApAccessLog> selectAptApAccessLogList(Map<String, Object> params);

    public int selectAptApAccessLogListCount(Map<String, Object> params);

    /**
     * @author shavrani 2016-11-21
     * @desc Apt Ap Access Log Inspection List
     */
    public List<Map<String, Object>> selectAptApAccessLogInspectionList(Map<String, Object> params);

    /**
     * @author shavrani 2016-11-21
     * @desc Apt Ap Access Log Inspection Detail List
     */
    public List<AptApAccessLog> selectAptApAccessLogInspectionDetailList(Map<String, Object> params);

    /**
     * @author shavrani 2016-11-21
     * @desc Apt Ap Inspection Account List
     */
    public List<SimpleUser> selectAptApInspAccountList(String type);

    /**
     * @author shavrani 2016-11-21
     * @desc Apt Ap Inspection Account Save
     */
    public int saveInspAccount(List<Map<String, Object>> inspList);

    /**
     * @author shavrani 2016-12-07
     * @desc Apt Ap Inspection Mail Account Save
     */
    public int saveInspMailAccount(List<Map<String, Object>> inspList);

    /**
     * @author shavrani 2016-11-28
     * @desc Apt Ap Inspection Daily List
     */
    public List<Map<String, Object>> selectAptApInspDailyList(Map<String, Object> params);

    public int selectAptApInspDailyListCount(Map<String, Object> params);


    /**
     * @author shavrani 2016-11-28
     * @desc Apt Ap Inspection Data limit List
     */
    public List<Map<String, Object>> selectAptApInspDataLimitList(Map<String, Object> params);

    public int selectAptApInspDataLimitListCount(Map<String, Object> params);

    /**
     * @author shavrani 2016-11-29
     * @desc Apt Ap Inspection Noti List
     */
    public List<Map<String, Object>> selectAptApInspNotiList(Map<String, Object> params);

    public int selectAptApInspNotiListCount(Map<String, Object> params);

    /**
     * @author shavrani 2016-11-29
     * @desc Apt Ap Inspection Warning Apt List
     */
    public List<Map<String, Object>> selectAptApInspWarningAptList(Map<String, Object> params);

    public int selectAptApInspWarningAptListCount(Map<String, Object> params);

    /**
     * @author shavrani 2016-11-29
     * @desc Apt Ap Inspection Noti Warning Apt List
     */
    public List<Map<String, Object>> selectAptApInspNotiWarningAptList(Map<String, Object> params);

    public int selectAptApInspNotiWarningAptListCount(Map<String, Object> params);

    /**
     * @author shavrani 2016-12-07
     * @desc Apt Ap Monitoring Noti List
     */
    public List<AptApMonitoringNoti> selectAptApMonitoringNotiList(Map<String, Object> params);

    public int saveSettingDataLimit(String[] code, String[] dataLimit, String[] warningPercent);

    public Map<String, Object> saveAptApAccessAuthFile(User user, MultipartFile file, Long authSaveAptId);

    public int aptApApplyApt(User user, Long[] apList, Long aptId);

    /**
     * @author shavrani 2017-01-13
     * @desc Apt Ap Health Check Process
     */
    public Map<String, Object> aptApRealTimeHealthCheck(User user, List<AptAp> apList);

    public Map<String, Object> aptApMonitoringHealthCheck(User user, List<AptAp> apList, Integer storagePeriod);

    public List<Map<String, Object>> selectAptApHealthCheckList(Map<String, Object> params);

    public int selectAptApHealthCheckListCount(Map<String, Object> params);

    /**
     * @author shavrani 2017-02-10
     * @desc AP 고장여부 변경시 gcm 보낼 대상 List ( 최근 ?일동안에 출입로그가있는사람이면서 현재 해당 ap의 출입권한이 있는사람 )
     */
    public List<SimpleUser> selectPeriodApAccessLogUserNApAccessAuthUserList(Map<String, Object> params);


}
