/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.service;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Class Name : MonitoringService.java
 * Description : 모니터링 서비스
 * 
 * Modification Information
 * 
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * </pre>
 *
 * @version 1.0
 */
public interface MonitoringService {
    /**
     * E도어 사용자 모니터링 List
     * 
     * @param params
     * @return
     */
    public List<Map<String, Object>> selectAptApUserMonitoringList(Map<String, Object> params);

    /**
     * E도어 사용자 모니터링 totalList
     * 
     * @param params
     * @return
     */
    public int selectAptApUserMonitoringTotalList(Map<String, Object> params);

    /**
     * E도어 가입자 통계 List
     * 
     * @param params
     * @return
     */
    public List<Map<String, Object>> selectAptMemberListByDate(Map<String, Object> params);

    /**
     * E도어 가입자 통계TotalList
     * 
     * @param params
     * @return
     */
    public int selectAptMemberListByDateCount(Map<String, Object> params);

    /**
     * 일자별 가입자 및 앱다운로드 현황 Data
     * 
     * @param param
     * @return
     */
    List<Map<String, Object>> selectAppDownkloadUserList(Map<String, Object> param);

    /**
     * @author shavrani 2017-01-31
     * @param params
     * @desc 아파트별 가입자 현황 리스트
     */
    public List<Map<String, Object>> selectAptMembershipList(Map<String, Object> params);

    public int selectAptMembershipListCount(Map<String, Object> params);

    public int selectAppDownkloadUserListCount(Map<String, Object> params);

    public List<String> selectExceptApt(Map<String, Object> params);



}
