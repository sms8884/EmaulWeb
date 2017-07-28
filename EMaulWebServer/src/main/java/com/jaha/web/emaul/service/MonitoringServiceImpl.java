/**
 * Copyright (c) 2017 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2017. 2. 1.
 */
package com.jaha.web.emaul.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jaha.web.emaul.mapper.MonitoringMapper;

/**
 * <pre>
 * Class Name : MonitoringServiceImpl.java
 * Description : Description
 *  
 * Modification Information
 * 
 * Mod Date     Modifier    		  Description
 * -----------      -----------       ---------------------
 * 2017. 2. 1.        MyoungSeop       Generation
 * </pre>
 * 
 * * @author AAA
 * 
 * @since 2017. 2. 1.
 * @version 1.0
 */

@Service
public class MonitoringServiceImpl implements MonitoringService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    MonitoringMapper monitoringMapper;

    /**
     * 이도어 사용자 모니터링 데이터
     */
    @Override
    public List<Map<String, Object>> selectAptApUserMonitoringList(Map<String, Object> params) {
        return monitoringMapper.selectAptApUserMonitoringList(params);
    }

    /**
     * 이도어 사용자모니터링 누적 가입자수
     */
    @Override
    public int selectAptApUserMonitoringTotalList(Map<String, Object> params) {
        return monitoringMapper.selectAptApUserMonitoringTotalCount(params);
    }

    /**
     * 일자별 가입자 및 앱다운로드 현황 리스트
     */
    @Override
    public List<Map<String, Object>> selectAppDownkloadUserList(Map<String, Object> params) {

        return monitoringMapper.selectAppDownkloadUserList(params);
    }

    @Override
    public List<Map<String, Object>> selectAptMembershipList(Map<String, Object> params) {
        return monitoringMapper.selectAptMembershipList(params);
    }

    @Override
    public int selectAptMembershipListCount(Map<String, Object> params) {
        return monitoringMapper.selectAptMembershipListCount(params);
    }

    /*
     * E도어 가입통계 리스트 DATA
     * 
     * @see com.jaha.web.emaul.service.MonitoringService#selectAptApEdoorMemberList(java.util.Map)
     */
    @SuppressWarnings("static-access")
    @Override
    public List<Map<String, Object>> selectAptMemberListByDate(Map<String, Object> params) {

        // try {
        // SimpleDateFormat formatter = new Sim
        //
        // st = pagingHelper.getStartNum();pleDateFormat("yyyy-MM-dd");
        // Calendar cal = Calendar.getInstance();
        // Date sDate2 = formatter.parse((String) params.get("sDate"));
        // Date eDate2 = formatter.parse((String) params.get("eDate"));
        // long diff = eDate2.getTime() - sDate2.getTime();
        // long
        // cal.setTime(eDate2);
        // cal.add(cal.DATE, -st);
        // } else {
        //
        // cal.setTime(eDate2);
        // pageSz = (int) diffDays + 1;
        // cal.add(cal.DATE, -st);
        // }
        // }
        // params.put("baseDateList", dateList);
        //
        // } catch (Exception e) {
        // // TODO: han
        //
        // for (int i = 0; i < pageSz; i++) {
        // dateList.add(formatter.format(cal.getTime()));
        // if (formatter.format(cal.getTime()).equals(formatter.format(sDate2))) {
        // break;
        // }
        // cal.add(cal.DATE, -1);dle exception
        // logger.error(" 일자별 사용자 가입자 모니터링 AJAX 날짜변환 오류 " + e.getMessadiffDays = diff / (24 * 60 * 60 * 1000);
        // PagingHelper pagingHelper = (PagingHelper) params.get("pagingHelper");
        // List<String> dateList = new ArrayList<>();
        //
        // int st = 0;
        // int pageSz = 0;
        // if (pagingHelper != null) {
        // pageSz = pagingHelper.getPageSize();ge());
        // e.printStackTrace();
        // }
        return monitoringMapper.selectAptMemberListByDate(params);
    }

    /*
     * E도어 가입통계 TOTAL DATA (paging)
     * 
     * @see com.jaha.web.emaul.service.MonitoringService#selectAptApEdoorMemberTotalList(java.util.Map)
     */
    @Override
    public int selectAptMemberListByDateCount(Map<String, Object> params) {
        return monitoringMapper.selectAptMemberListByDateCount(params);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaha.web.emaul.service.MonitoringService#selectAppDownkloadUserListCount(java.util.Map)
     */
    @Override
    public int selectAppDownkloadUserListCount(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return monitoringMapper.selectAppDownkloadUserListCnt(params);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaha.web.emaul.service.MonitoringService#selectExceptApt(java.util.Map)
     */
    @Override
    public List<String> selectExceptApt(Map<String, Object> params) {
        Map<String, Object> item = monitoringMapper.selectExceptApt(params);
        List<String> itemList = new ArrayList<String>();
        String tmp = (String) item.get("data_1");
        String[] tmpList = tmp.split(",");
        for (String trimStr : tmpList) {
            itemList.add(trimStr.trim());
        }
        return itemList;
    }

}
