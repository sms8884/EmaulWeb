package com.jaha.web.emaul.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.model.AptAp;
import com.jaha.web.emaul.model.AptApAccessLog;
import com.jaha.web.emaul.model.AptApMonitoringAlive;
import com.jaha.web.emaul.model.AptApMonitoringNoti;
import com.jaha.web.emaul.model.SimpleUser;

/**
 * Created by shavrani on 16-06-17
 */
@Mapper
public interface AptApMapper {

    public List<AptAp> selectAptApList(Map<String, Object> params);

    public List<AptAp> selectAptApAllList(Map<String, Object> params);

    public int selectAptApListCount(Map<String, Object> params);

    public AptAp selectAptAp(Map<String, Object> params);

    public int updateAptAp(AptAp aptAp);

    public int updateAptApFirmwareVersion(AptAp aptAp);

    public List<Map<String, Object>> selectAptApAccessLogInspectionList(Map<String, Object> params);

    public List<AptApAccessLog> selectAptApAccessLogInspectionDetailList(Map<String, Object> params);

    public List<AptApAccessLog> selectAptApAccessLogList(Map<String, Object> params);

    public int selectAptApAccessLogListCount(Map<String, Object> params);

    public List<SimpleUser> selectAptApInspAccountList(String type);

    public int saveInspAccount(Map<String, Object> params);

    public int deleteInspAccount(String type);

    public List<Map<String, Object>> selectAptApInspDailyList(Map<String, Object> params);

    public int selectAptApInspDailyListCount(Map<String, Object> params);

    public List<Map<String, Object>> selectAptApInspDataLimitList(Map<String, Object> params);

    public int selectAptApInspDataLimitListCount(Map<String, Object> params);

    public List<Map<String, Object>> selectAptApInspWarningAptList(Map<String, Object> params);

    public int selectAptApInspWarningAptListCount(Map<String, Object> params);

    public List<Map<String, Object>> selectAptApInspNotiList(Map<String, Object> params);

    public int selectAptApInspNotiListCount(Map<String, Object> params);

    public List<Map<String, Object>> selectAptApInspNotiWarningAptList(Map<String, Object> params);

    public int selectAptApInspNotiWarningAptListCount(Map<String, Object> params);

    public List<AptApMonitoringNoti> selectAptApMonitoringNotiList(Map<String, Object> params);

    public int insertApMonitoringAlive(AptApMonitoringAlive aptApMonitoringAlive);

    public int deleteApMonitoringAlive(Integer storagePeriod);

    public List<Map<String, Object>> selectAptApHealthCheckList(Map<String, Object> params);

    public int selectAptApHealthCheckListCount(Map<String, Object> params);

    public List<SimpleUser> selectPeriodApAccessLogUserNApAccessAuthUserList(Map<String, Object> params);

}
