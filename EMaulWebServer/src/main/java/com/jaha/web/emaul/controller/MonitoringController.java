/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.controller;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jaha.web.emaul.constants.Constants;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.CommonService;
import com.jaha.web.emaul.service.HouseService;
import com.jaha.web.emaul.service.MonitoringService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.ExcelFileBuilder;
import com.jaha.web.emaul.util.PoiUtil;
import com.jaha.web.emaul.util.Responses;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.util.StringUtil;
import com.jaha.web.emaul.util.Util;
import com.jaha.web.emaul.v2.util.PagingHelper;

/**
 * <pre>
 * Class Name : MonitoringController.java
 * Description : 모니터링 서비스 관리
 * 
 * Modification Information
 * 
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * </pre>
 *
 * @version 1.0
 */
@Controller("MonitoringController")
public class MonitoringController {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private MonitoringService monitoringService;
    @Autowired
    private Environment env;

    /**
     * 일자별 가입자 및 앱다운로드 화면
     * 
     * @param model
     * @param req
     * @return
     */
    @RequestMapping(value = "/jaha/user/app/download/list")
    public String getUserAppDownloadList(Model model, HttpServletRequest req) {

        model.addAttribute("sidoList", houseService.getSidoNames());
        return "jaha/apt-user-app-download-list";
    }

    /**
     * 일자별 가입자 및 앱다운로드 현황 Ajax DATA
     * 
     * @param model
     * @param req
     * @return
     */
    @RequestMapping(value = "/jaha/user/app/download/list-data")
    @ResponseBody
    public Map<String, Object> getUserAppDownloadListData(Model model, HttpServletRequest req, PagingHelper pagingHelper, @RequestParam Map<String, Object> params,
            @RequestParam(value = "aptList", required = false) List<Long> aptList) {

        String sDate = StringUtil.nvl(params.get("sDate"));
        String eDate = StringUtil.nvl(params.get("eDate"));
        Long aptId = StringUtil.nvlLong(params.get("aptId"), null);

        int pageSize = StringUtil.nvlInt(params.get("pageSize"), 10);
        int pageNum = StringUtil.nvlInt(params.get("pageNum"), 1);

        params.put("sDate", sDate);
        params.put("eDate", eDate);
        params.put("aptId", aptId);
        params.put("testAptId", Constants.AP_TEST_APT_ID);
        if (aptList != null) {
            params.put("aptList", aptList);
        }
        params.put("type", "EXCEPT_MONITORING");
        List<String> exceptAptList = monitoringService.selectExceptApt(params);

        params.put("exceptAptList", exceptAptList);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        pagingHelper.setPageSize(pageSize);
        pagingHelper.setPageNum(pageNum);
        pagingHelper.setStartNum(pagingHelper.calcStartNum());
        pagingHelper.setEndNum(pagingHelper.calcEndNum());
        params.put("pagingHelper", pagingHelper);

        try {
            Date sDate2 = formatter.parse(sDate);
            Date eDate2 = formatter.parse(eDate);
            long diff = eDate2.getTime() - sDate2.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            pagingHelper.setTotalRecordCount(diffDays + 1);
        } catch (Exception e) {
            logger.error("<< method : aptApEdoorMemberListData parse exception >>", e);
        }

        // 실 데이터는 해당 페이징 번호에 해당하는 날짜만큼만 정확히 select하기위해 날짜계산을해서 parameter로 넘김.
        try {
            Calendar cal = Calendar.getInstance();
            Date eDate2 = formatter.parse(eDate);

            int st = 0;
            int pageSz = 0;

            st = pagingHelper.getStartNum();
            pageSz = pagingHelper.getPageSize();
            cal.setTime(eDate2);
            cal.add(cal.DATE, -st);

            params.put("eDate", formatter.format(cal.getTime()));

            cal.add(cal.DATE, -(pageSz - 1));

            String minDate = formatter.format(cal.getTime());

            int compare = minDate.compareTo(sDate);
            if (compare >= 0) {
                // compare 0보다 크거나 같으면 minDate 사용
                params.put("sDate", minDate);
            }

        } catch (Exception e) {
            logger.error(" << method : getUserAppDownloadListData ERROR >> " + e.getMessage());
            e.printStackTrace();
        }

        List<Map<String, Object>> resultList = monitoringService.selectAppDownkloadUserList(params);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("pagingHelper", pagingHelper);
        result.put("resultList", resultList);

        return result;
    }

    /**
     * e도어 사용자 모니터링 화면
     * 
     * @param model
     * @param req
     * @return
     */
    @RequestMapping(value = "/jaha/apt/ap/user/monitoring/list")
    public String getJahaAptApUserMonitoringList(Model model, HttpServletRequest req) {
        return "jaha/apt-ap-user-monitoring-list";
    }

    /**
     * e도어 사용자모니터링 data
     * 
     * @param req
     * @return
     */
    @RequestMapping(value = "/jaha/apt/ap/user/monitoring/list-data")
    @ResponseBody
    public List<Map<String, Object>> aptApUserMonitoringListData(HttpServletRequest req, @RequestParam Map<String, Object> params, PagingHelper pagingHelper,
            @RequestParam(value = "aptList", required = false) List<Long> aptList) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String sDate = StringUtil.nvl(params.get("sDate"));
        String eDate = StringUtil.nvl(params.get("eDate"));
        Long aptId = StringUtil.nvlLong(params.get("aptId"), null);

        int pageSize = StringUtil.nvlInt(params.get("pageSize"), 10);
        int pageNum = StringUtil.nvlInt(params.get("pageNum"), 1);

        // ////////////// tmp/////////////////////////////////
        if (aptList != null) {
            params.put("aptList", aptList);
        }
        params.put("type", "EXCEPT_MONITORING");
        List<String> exceptAptList = monitoringService.selectExceptApt(params);

        params.put("exceptAptList", exceptAptList);

        // ///////////////////////////////////////////////
        params.put("sDate", sDate);
        params.put("eDate", eDate);
        params.put("aptId", aptId);

        pagingHelper.setPageSize(pageSize);
        pagingHelper.setPageNum(pageNum);
        pagingHelper.setStartNum(pagingHelper.calcStartNum());
        pagingHelper.setEndNum(pagingHelper.calcEndNum());
        pagingHelper.setTotalRecordCount(monitoringService.selectAptApUserMonitoringTotalList(params));
        params.put("pagingHelper", pagingHelper);

        List<Map<String, Object>> result = monitoringService.selectAptApUserMonitoringList(params);
        result.add(params);

        return result;
    }

    /**
     * e도어 가입자 통계data
     * 
     * @param req
     * @return
     */
    @RequestMapping(value = "/jaha/apt/ap/user/edoorMember/list-data")
    @ResponseBody
    public Map<String, Object> aptApEdoorMemberListData(HttpServletRequest req, @RequestParam Map<String, Object> params, PagingHelper pagingHelper,
            @RequestParam(value = "aptList", required = false) List<Long> aptList) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String sDate = StringUtil.nvl(params.get("sDate"));
        String eDate = StringUtil.nvl(params.get("eDate"));
        Long aptId = StringUtil.nvlLong(params.get("aptId"), null);
        int pageSize = StringUtil.nvlInt(params.get("pageSize"), 10);
        int pageNum = StringUtil.nvlInt(params.get("pageNum"), 1);
        if (aptList != null) {
            params.put("aptList", aptList);
        }
        params.put("sDate", sDate);
        params.put("eDate", eDate);
        params.put("aptId", aptId);
        params.put("type", "EXCEPT_MONITORING");
        List<String> exceptAptList = monitoringService.selectExceptApt(params);

        params.put("exceptAptList", exceptAptList);


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        pagingHelper.setPageSize(pageSize);
        pagingHelper.setPageNum(pageNum);
        pagingHelper.setStartNum(pagingHelper.calcStartNum());
        pagingHelper.setEndNum(pagingHelper.calcEndNum());
        params.put("pagingHelper", pagingHelper);

        try {
            Date sDate2 = formatter.parse(sDate);
            Date eDate2 = formatter.parse(eDate);
            long diff = eDate2.getTime() - sDate2.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            pagingHelper.setTotalRecordCount(diffDays + 1);
        } catch (Exception e) {
            logger.error("<< method : aptApEdoorMemberListData parse exception >>", e);
        }


        // 실 데이터는 해당 페이징 번호에 해당하는 날짜만큼만 정확히 select하기위해 날짜계산을해서 parameter로 넘김.
        try {
            Calendar cal = Calendar.getInstance();
            Date eDate2 = formatter.parse(eDate);

            int st = 0;
            int pageSz = 0;

            st = pagingHelper.getStartNum();
            pageSz = pagingHelper.getPageSize();
            cal.setTime(eDate2);
            cal.add(cal.DATE, -st);

            params.put("eDate", formatter.format(cal.getTime()));

            cal.add(cal.DATE, -(pageSz - 1));

            String minDate = formatter.format(cal.getTime());

            int compare = minDate.compareTo(sDate);
            if (compare >= 0) {
                // compare 0보다 크거나 같으면 minDate 사용
                params.put("sDate", minDate);
            }

        } catch (Exception e) {
            logger.error(" << method : aptApEdoorMemberListData ERROR >> " + e.getMessage());
            e.printStackTrace();
        }

        List<Map<String, Object>> resultList = monitoringService.selectAptMemberListByDate(params);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("resultList", resultList);
        result.put("pagingHelper", pagingHelper);

        return result;
    }

    /**
     * 자하권한 E도어 사용자 모니터링 엑셀 다운로드
     * 
     * @param req
     * @param res
     * @param model
     * @param params
     * @param pagingHelper
     * @throws Exception
     */
    @RequestMapping(value = "/jaha/apt/ap/user/monitoring/list/excel-download")
    public void downloadExcelJahaEdoorUserListData(HttpServletRequest req, HttpServletResponse res, Model model, @RequestParam Map<String, Object> params, PagingHelper pagingHelper,
            @RequestParam(value = "aptList", required = false) List<Long> aptList) throws Exception {


        String sDate = StringUtil.nvl(params.get("sDate"));
        String eDate = StringUtil.nvl(params.get("eDate"));
        Long aptId = StringUtil.nvlLong(params.get("aptId"), null);
        int pageSize = StringUtil.nvlInt(params.get("pageSize"), 10);
        int pageNum = StringUtil.nvlInt(params.get("pageNum"), 1);
        params.put("sDate", sDate);
        params.put("eDate", eDate);
        params.put("aptId", aptId);
        if (aptList != null) {
            params.put("aptList", aptList);
        }
        params.put("type", "EXCEPT_MONITORING");
        List<String> exceptAptList = monitoringService.selectExceptApt(params);

        params.put("exceptAptList", exceptAptList);

        pagingHelper.setPageSize(pageSize);
        pagingHelper.setPageNum(pageNum);
        pagingHelper.setStartNum(pagingHelper.calcStartNum());
        pagingHelper.setEndNum(pagingHelper.calcEndNum());
        pagingHelper.setTotalRecordCount(monitoringService.selectAptApUserMonitoringTotalList(params));


        try {
            List<Map<String, Object>> userLogList = monitoringService.selectAptApUserMonitoringList(params);

            final String[] headers = {"일련번호", "시/도", "구/군", "아파트명", "출입수(건)", "유니크사용자수(명)", "신규가입자(명)", "탈퇴자(명)", "누적가입자(명)"};
            final ExcelFileBuilder excelFileBuilder = new ExcelFileBuilder("Sheet1");
            excelFileBuilder.setHeaders(headers);


            if (userLogList != null && userLogList.size() > 0) {
                int accessLogSize = userLogList.size();

                if (accessLogSize < 65536) {
                    for (int i = 0; i < userLogList.size(); i++) {
                        int col = 0;
                        excelFileBuilder.setDataValue(col++, accessLogSize--);
                        excelFileBuilder.setDataValue(col++, StringUtil.nvl(userLogList.get(i).get("sido_nm")));
                        excelFileBuilder.setDataValue(col++, StringUtil.nvl(userLogList.get(i).get("sgg_nm")));
                        excelFileBuilder.setDataValue(col++, StringUtil.nvl(userLogList.get(i).get("apt_name")));
                        excelFileBuilder.setDataValue(col++, StringUtil.nvl(userLogList.get(i).get("access_count"), "0"));
                        excelFileBuilder.setDataValue(col++, StringUtil.nvl(userLogList.get(i).get("usique_user"), "0"));
                        excelFileBuilder.setDataValue(col++, StringUtil.nvl(userLogList.get(i).get("new_user"), "0"));
                        excelFileBuilder.setDataValue(col++, StringUtil.nvl(userLogList.get(i).get("deactive_user"), "0"));
                        excelFileBuilder.setDataValue(col++, StringUtil.nvl(userLogList.get(i).get("total_user"), "0"));
                        excelFileBuilder.nextRow();
                    }
                } else {
                    int col = 0;
                    excelFileBuilder.setDataValue(col++, "검색결과 " + accessLogSize + "건으로 엑셀 최대 행사이즈(65535) 를 초과하였습니다. 다시 검색하여 주십시오.");
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.setDataValue(col++, StringUtils.EMPTY);
                    excelFileBuilder.nextRow();
                }
            }

            excelFileBuilder.autoSizeColumns();
            Responses.downloadEexcel("EdoorUserListData-" + Util.getNowDate() + ".xls", excelFileBuilder, req, res);
        } catch (Exception e) {
            logger.error("<<E도어 출입 로그 엑셀 다운로드 중 오류>>", e);
            throw e;
        }
    }


    /**
     * 아파트별 가입자 현황 화면
     * 
     * @param model
     * @param req
     * @return
     */
    @RequestMapping(value = "/jaha/apt/membership/list")
    public String getAptMembershipList(Model model, HttpServletRequest req) {
        return "jaha/apt-membership-list";
    }

    /**
     * 아파트별 가입자 현황 Ajax Data
     * 
     * @param req
     * @param model
     * @param params
     * @param pagingHelper
     * @return
     */
    @RequestMapping(value = "/jaha/apt/membership/list-data")
    @ResponseBody
    public Map<String, Object> getAptMembershipListData(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params, PagingHelper pagingHelper,
            @RequestParam(value = "aptList", required = false) List<Long> aptList) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        String sidoNm = StringUtil.nvl(params.get("sidoNm"));
        String sggNm = StringUtil.nvl(params.get("sggNm"));
        String aptNm = StringUtil.nvl(params.get("aptNm"));
        String sDate = StringUtil.nvl(params.get("sDate"));
        String eDate = StringUtil.nvl(params.get("eDate"));

        int pageSize = StringUtil.nvlInt(params.get("pageSize"), 10);
        pagingHelper.setPageSize(pageSize);
        pagingHelper.setStartNum(pagingHelper.calcStartNum());
        pagingHelper.setEndNum(pageSize);

        params.put("pagingHelper", pagingHelper);
        if (aptList != null) {
            params.put("aptList", aptList);
        }
        params.put("type", "EXCEPT_MONITORING");
        List<String> exceptAptList = monitoringService.selectExceptApt(params);

        params.put("exceptAptList", exceptAptList);


        List<Map<String, Object>> aptMembershipList = monitoringService.selectAptMembershipList(params);
        int totalRecordCount = monitoringService.selectAptMembershipListCount(params);

        pagingHelper.setTotalRecordCount(totalRecordCount);

        Map<String, Object> result = Maps.newHashMap();
        result.put("dataList", aptMembershipList);
        result.put("pagingHelper", pagingHelper);

        return result;
    }

    /**
     * 일자별가입자현황 아파트별 엑셀다운로드
     * 
     * @param req
     * @param res
     * @param model
     * @param params
     * @param pagingHelper
     * @throws Exception
     */
    @RequestMapping(value = "/jaha/apt/membership/list/excel-download")
    public void downloadExcelAptMembershipListData(HttpServletRequest req, HttpServletResponse res, Model model, @RequestParam Map<String, Object> params, PagingHelper pagingHelper,
            @RequestParam(value = "aptList", required = false) List<Long> aptList) throws Exception {

        String sidoNm = StringUtil.nvl(params.get("sidoNm"));
        String sggNm = StringUtil.nvl(params.get("sggNm"));
        String aptNm = StringUtil.nvl(params.get("aptNm"));
        String sDate = StringUtil.nvl(params.get("sDate"));
        String eDate = StringUtil.nvl(params.get("eDate"));


        if (aptList != null) {
            params.put("aptList", aptList);
        }
        params.put("type", "EXCEPT_MONITORING");
        List<String> exceptAptList = monitoringService.selectExceptApt(params);

        params.put("exceptAptList", exceptAptList);

        try {
            List<Map<String, Object>> aptMembershipList = monitoringService.selectAptMembershipList(params);


            // /// Excel 생성/////////
            Workbook wb = new HSSFWorkbook();
            int currRow = 0;
            int currCell = 0;

            Sheet sheet = wb.createSheet("DataListByApt");
            Sheet sheet2 = wb.createSheet("DataListByDate");

            Row row = null;
            Cell cell = null;
            Map<String, CellStyle> cellStyleMap = PoiUtil.getCellStyle(wb);// 공통정의한 cell style
            row = sheet.createRow((short) 0);
            cell = row.createCell((short) 0);
            // /// Excel 생성 ENd/////////

            List<String> titles = Lists.newArrayList();
            titles.add("일련번호");
            titles.add("시/도");
            titles.add("구/군");
            titles.add("아파트명");
            titles.add("총세대(세대)");
            titles.add("신규가입자(명)");
            titles.add("탈퇴자(명)");
            titles.add("총누적가입자(명)");

            row.setHeightInPoints(17);
            cell.setCellValue("");
            for (int i = 0; i < titles.size(); i++) {
                sheet.setColumnWidth(currCell, 6000);
                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleHeader"));
                cell.setCellValue(titles.get(i));
            }

            currCell = 0;
            currRow = 1;

            int size = aptMembershipList.size();

            for (int i = 0; i < aptMembershipList.size(); i++) {
                Map<String, Object> item = aptMembershipList.get(i);
                row = sheet.createRow(currRow++);
                row.setHeightInPoints(17);
                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(size--);

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvl(item.get("sidoNm")));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvl(item.get("sggNm")));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvl(item.get("aptName")));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvl(item.get("hoCnt")));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvl(StringUtil.nvlInt(item.get("aptUserCntPeriod"))));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvl(StringUtil.nvlInt(item.get("deactiveUserCnt"))));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvl(StringUtil.nvlInt(item.get("aptUserCnt"))));
                currCell = 0;
            }

            // ///// 일자별 시트/////
            aptMembershipList = monitoringService.selectAptMemberListByDate(params);
            currRow = 0;
            currCell = 0;
            row = null;
            cell = null;
            sheet2.setColumnWidth(currCell, 6000);
            row = sheet2.createRow((short) 0);
            cell = row.createCell(0, Cell.CELL_TYPE_STRING);
            cell.setCellStyle(cellStyleMap.get("styleHeader"));
            cell.setCellValue("일자");


            cell = row.createCell(1, Cell.CELL_TYPE_STRING);
            cell.setCellStyle(cellStyleMap.get("styleHeader"));
            cell.setCellValue("e마을");

            cell = row.createCell(4, Cell.CELL_TYPE_STRING);
            cell.setCellStyle(cellStyleMap.get("styleHeader"));
            cell.setCellValue("e도어");


            List<String> titles2 = Lists.newArrayList();
            titles2.add("신규 가입자수");
            titles2.add("탈퇴자수");
            titles2.add("누적가입자수");
            titles2.add("신규가입자수");
            titles2.add("탈퇴자수");
            titles2.add("누적가입자수");

            row.setHeightInPoints(17);
            currCell = 1;
            row = sheet2.createRow((short) 1);
            for (int i = 0; i < titles2.size(); i++) {
                sheet2.setColumnWidth(currCell, 6000);
                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);

                cell.setCellStyle(cellStyleMap.get("styleHeader"));
                cell.setCellValue(titles2.get(i));
            }
            sheet2.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
            sheet2.addMergedRegion(new CellRangeAddress(0, 0, 1, 3));
            sheet2.addMergedRegion(new CellRangeAddress(0, 0, 4, 6));


            currCell = 0;
            currRow = 2;

            size = aptMembershipList.size();

            for (int i = 0; i < size; i++) {
                Map<String, Object> item = aptMembershipList.get(i);
                row = sheet2.createRow(currRow++);
                row.setHeightInPoints(17);
                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvl(item.get("reg_date")));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvlInt(item.get("emaulNewUser_cnt")));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvlInt(item.get("emaulDeactive_cnt")));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvlInt(item.get("emaulTotalCnt")));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvlInt(item.get("edoorNewUser_cnt")));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvlInt(StringUtil.nvlInt(item.get("edoorDeactive_cnt"))));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvlInt(StringUtil.nvlInt(item.get("edoorTotalCnt"))));

                currCell = 0;
            }
            // / file 다운로드/////

            OutputStream os = null;

            String userAgent = req.getHeader("User-Agent");
            String fileName = null;

            if (userAgent.indexOf("MSIE") > -1 || userAgent.indexOf("Trident") > -1) {
                fileName = URLEncoder.encode("memeberShipExcel.xls", Constants.DEFAULT_ENCODING);
            } else {
                fileName = new String("memeberShipExcel.xls".getBytes(Constants.DEFAULT_ENCODING), "ISO-8859-1");
            }

            res.setHeader("Cache-Control", "no-cache"); // http 1.1
            res.setHeader("Pragma", "no-cache"); // http 1.0
            res.setDateHeader("Expires", -1); // proxy server에 cache방지

            res.setContentType("application/vnd.ms-excel; charset=" + Constants.DEFAULT_ENCODING);
            res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // 파일명 지정
            res.setHeader("Content-Transfer-Encoding", "binary");
            // / file 다운로드/////

            try {
                os = res.getOutputStream();
                wb.write(os);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    os.close();
                }
            }

        } catch (

        Exception e) {
            logger.error("<< 일자별가입자현황 아파트별 엑셀다운로드중 오류>>", e);
            throw e;
        }
    }

    /**
     * 일자별앱다운로드
     * 
     * @param req
     * @param res
     * @param model
     * @param params
     * @param pagingHelper
     * @throws Exception
     */
    @RequestMapping(value = "/jaha/apt/AppDownload/list/excel-download")
    public void getUserAppDownloadListData(HttpServletRequest req, HttpServletResponse res, Model model, @RequestParam Map<String, Object> params, PagingHelper pagingHelper,
            @RequestParam(value = "aptList", required = false) List<Long> aptList) throws Exception {

        String sidoNm = StringUtil.nvl(params.get("sidoNm"));
        String sggNm = StringUtil.nvl(params.get("sggNm"));
        String aptNm = StringUtil.nvl(params.get("aptNm"));
        String sDate = StringUtil.nvl(params.get("sDate"));
        String eDate = StringUtil.nvl(params.get("eDate"));

        if (aptList != null) {
            params.put("aptList", aptList);
        }
        params.put("type", "EXCEPT_MONITORING");
        List<String> exceptAptList = monitoringService.selectExceptApt(params);

        params.put("exceptAptList", exceptAptList);


        try {
            List<Map<String, Object>> appdownList = monitoringService.selectAppDownkloadUserList(params);


            // /// Excel 생성/////////
            Workbook wb = new HSSFWorkbook();
            int currRow = 0;
            int currCell = 0;

            Sheet sheet2 = wb.createSheet("DataListByDate");

            Row row = null;
            Cell cell = null;
            Map<String, CellStyle> cellStyleMap = PoiUtil.getCellStyle(wb);// 공통정의한 cell style

            // ///// 일자별 시트/////
            currRow = 0;
            currCell = 0;
            row = null;
            cell = null;
            sheet2.setColumnWidth(currCell, 6000);
            row = sheet2.createRow((short) 0);
            cell = row.createCell(0, Cell.CELL_TYPE_STRING);
            cell.setCellStyle(cellStyleMap.get("styleHeader"));
            cell.setCellValue("일자");


            sheet2.setColumnWidth(currCell++, 6000);
            cell = row.createCell(1, Cell.CELL_TYPE_STRING);
            cell.setCellStyle(cellStyleMap.get("styleHeader"));
            cell.setCellValue("신규가입자(명)");

            sheet2.setColumnWidth(currCell++, 6000);
            cell = row.createCell(2, Cell.CELL_TYPE_STRING);
            cell.setCellStyle(cellStyleMap.get("styleHeader"));
            cell.setCellValue("탈퇴자");


            cell = row.createCell(3, Cell.CELL_TYPE_STRING);
            cell.setCellStyle(cellStyleMap.get("styleHeader"));
            cell.setCellValue("신규다운로드");


            cell = row.createCell(5, Cell.CELL_TYPE_STRING);
            cell.setCellStyle(cellStyleMap.get("styleHeader"));
            cell.setCellValue("누적다운로드");


            List<String> titles2 = Lists.newArrayList();
            titles2.add("안드로이드");
            titles2.add("ios");
            titles2.add("안드로이드");
            titles2.add("ios");

            row.setHeightInPoints(17);
            currCell = 3;
            row = sheet2.createRow((short) 1);
            for (int i = 0; i < titles2.size(); i++) {
                sheet2.setColumnWidth(currCell, 6000);
                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);

                cell.setCellStyle(cellStyleMap.get("styleHeader"));
                cell.setCellValue(titles2.get(i));
            }
            sheet2.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
            sheet2.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
            sheet2.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));

            sheet2.addMergedRegion(new CellRangeAddress(0, 0, 3, 4));
            sheet2.addMergedRegion(new CellRangeAddress(0, 0, 5, 6));


            currCell = 0;
            currRow = 2;

            int size = appdownList.size();

            for (int i = 0; i < size; i++) {
                Map<String, Object> item = appdownList.get(i);
                row = sheet2.createRow(currRow++);
                row.setHeightInPoints(17);
                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvl(item.get("regDate")));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvlInt(item.get("newUser")));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvlInt(item.get("deactiveUser")));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvlInt(item.get("newAndroidCnt")));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvlInt(item.get("newIosCnt")));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvlInt(StringUtil.nvlInt(item.get("androidTotal"))));

                cell = row.createCell(currCell++, Cell.CELL_TYPE_STRING);
                cell.setCellStyle(cellStyleMap.get("styleStringValue"));
                cell.setCellValue(StringUtil.nvlInt(StringUtil.nvlInt(item.get("iosTotal"))));

                currCell = 0;
            }
            // / file 다운로드/////

            OutputStream os = null;

            String userAgent = req.getHeader("User-Agent");
            String fileName = null;

            if (userAgent.indexOf("MSIE") > -1 || userAgent.indexOf("Trident") > -1) {
                fileName = URLEncoder.encode("AppDownloadExcel.xls", Constants.DEFAULT_ENCODING);
            } else {
                fileName = new String("AppDownloadExcel.xls".getBytes(Constants.DEFAULT_ENCODING), "ISO-8859-1");
            }

            res.setHeader("Cache-Control", "no-cache"); // http 1.1
            res.setHeader("Pragma", "no-cache"); // http 1.0
            res.setDateHeader("Expires", -1); // proxy server에 cache방지

            res.setContentType("application/vnd.ms-excel; charset=" + Constants.DEFAULT_ENCODING);
            res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // 파일명 지정
            res.setHeader("Content-Transfer-Encoding", "binary");
            // / file 다운로드/////

            try {
                os = res.getOutputStream();
                wb.write(os);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    os.close();
                }
            }

        } catch (Exception e) {
            logger.error("<<일자별 앱다운로드엑셀다운로드중 오류>>", e);
            throw e;
        }
    }


}
