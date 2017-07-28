package com.jaha.web.emaul.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface MonitoringMapper {
    public List<Map<String, Object>> selectAptApUserMonitoringList(Map<String, Object> params);

    public int selectAptApUserMonitoringTotalCount(Map<String, Object> params);

    public int insertAptApUserMonitroing(Map<String, Object> params);

    public int selectAptApMonitoringTotalUser(Map<String, Object> params);

    /**
     * 일자별 가입자 및 앱다운로드 현황 Data
     * 
     * @param param
     * @return
     */
    List<Map<String, Object>> selectAppDownkloadUserList(Map<String, Object> param);

    public List<Map<String, Object>> selectAptMembershipList(Map<String, Object> params);

    public int selectAptMembershipListCount(Map<String, Object> params);

    public Map<String, Object> selectAptApUserUniqueList(Map<String, Object> param);

    public int selectAptMemberListByDateCount(Map<String, Object> params);

    public List<Map<String, Object>> selectAptMemberListByDate(Map<String, Object> params);

    public int selectAppDownkloadUserListCnt(Map<String, Object> params);

    public Map<String, Object> SelectAptMemberListAllEmpty(Map<String, Object> param);

    public Map<String, Object> selectExceptApt(Map<String, Object> param);



}
