package com.jaha.web.emaul.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jaha.web.emaul.model.ParcelLocker;
import com.jaha.web.emaul.model.ParcelLockerForm;
import com.jaha.web.emaul.model.ParcelLog;
import com.jaha.web.emaul.model.ParcelSmsPolicy;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.repo.ParcelLogRepository;
import com.jaha.web.emaul.service.ParcelService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.CommonUtil;
import com.jaha.web.emaul.util.PageWrapper;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.util.StringUtil;

/**
 * @author 이현수(lhs@jahasmart.com), 2016. 7. 6.
 * @description 무인택배함
 */

@Controller
@RequestMapping(value = "/admin")
public class ParcelController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParcelController.class);

    @Autowired
    private ParcelService parcelService;

    @Autowired
    private UserService userService;

    @Autowired
    private ParcelLogRepository parcelLogRepository;

    @RequestMapping(value = "/parcel-lockers", method = RequestMethod.GET)
    public String parcelLockers() {
        return "admin/parcel-lockers";
    }

    @RequestMapping(value = "/parcel-lockers-data", method = RequestMethod.GET)
    @ResponseBody
    public PageImpl<ParcelLocker> parcelLockersData(@ModelAttribute ParcelLockerForm.Search searchForm, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            HttpSession session) {

        User user = userService.getUser(SessionAttrs.getUserId(session));

        Page<ParcelLocker> page = parcelService.fetchParcelLockers(searchForm, pageable, user.house.apt.id);
        return new PageImpl<>(page.getContent(), pageable, page.getTotalElements());
    }

    @RequestMapping(value = "/parcel-lockers/{id}", method = RequestMethod.GET)
    public String parcelLockersDetail(@PathVariable Long id, Model model, HttpSession session) {

        User user = userService.getUser(SessionAttrs.getUserId(session));

        ParcelLocker parcelLocker = parcelService.getParcelLocker(id, user.house.apt.id);
        model.addAttribute("parcelLocker", parcelLocker);

        return "admin/parcel-lockers-detail";
    }

    @RequestMapping(value = "/parcel-logs-data/{parcelLockerId}", method = RequestMethod.GET)
    @ResponseBody
    public PageImpl<ParcelLog> parcelLogs(@PathVariable Long parcelLockerId, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ParcelLog> page = parcelService.fetchParcelLogs(parcelLockerId, pageable);
        return new PageImpl<>(page.getContent(), pageable, page.getTotalElements());
    }

    @RequestMapping(value = "/parcel-lockers/{id}/auth-key", method = RequestMethod.POST)
    public ResponseEntity<ParcelLocker> refreshParcelLockerAuthkey(@PathVariable Long id) {

        ParcelLocker parcelLocker = parcelService.updateParcelLockerAuthKey(id);
        return new ResponseEntity<>(parcelLocker, HttpStatus.OK);
    }

    @RequestMapping(value = "/parcel-lockers/{id}", method = RequestMethod.POST)
    public ResponseEntity<ParcelLocker> updateParcelLocker(@PathVariable Long id, @ModelAttribute @Valid ParcelLockerForm.Update updateForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ParcelLocker parcelLocker = parcelService.updateParcelLocker(id, updateForm);
        return new ResponseEntity<>(parcelLocker, HttpStatus.OK);
    }

    @RequestMapping(value = "/parcel-lockers/popup", method = RequestMethod.GET)
    public String createParcelLocker() {
        return "admin/parcel-lockers-popup";
    }

    @RequestMapping(value = "/parcel-lockers", method = RequestMethod.POST)
    public ResponseEntity<ParcelLocker> createParcelLocker(@ModelAttribute @Valid ParcelLockerForm.Create createForm, HttpSession session, BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        User user = userService.getUser(SessionAttrs.getUserId(session));
        ParcelLocker parcelLocker = parcelService.createParcelLocker(createForm, user.house.apt);

        return new ResponseEntity<>(parcelLocker, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/parcel-lockers-delete", method = RequestMethod.POST)
    public ResponseEntity parcelLockersDelete(@RequestParam(value = "parcelLockerId") List<Long> parcelLockerIds) {
        parcelService.deleteParcelLocker(parcelLockerIds);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // SMS 정책관리
    @RequestMapping(value = "/parcel-sms")
    public String parcelSms(HttpServletRequest req, @RequestParam Map<String, Object> params, Model model) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        model.addAttribute("user", user);
        params.put("userId", user.id); // 사용자 ID
        params.put("aptId", user.house.apt.id); // 아파트아이디
        params.put("pageSize", 10);

        // 화면에서 년도와 월조회
        Calendar cal = Calendar.getInstance();
        ArrayList<String> searchYear = new ArrayList<String>();
        for (int y = 2015; y <= cal.get(Calendar.YEAR); y++) {
            searchYear.add(y + "");
        }
        model.addAttribute("searchYear", searchYear);
        ArrayList<String> searchMonth = new ArrayList<String>();
        for (int m = 1; m <= 12; m++) {
            String str = String.format("%02d", m);
            searchMonth.add(str);
        }
        model.addAttribute("searchMonth", searchMonth);
        // end 화면에 년도와 월 정보

        ParcelSmsPolicy parcelSms = parcelService.selectParcelSmsPolicy(params); // SMS수신 여부 정보조회
        model.addAttribute("parcelSms", parcelSms);

        int totalcount = parcelService.selectParcelSmsListCount(params); // 총 카운트

        CommonUtil.setPagingParams(params);
        Pageable pageable = new PageRequest(StringUtil.nvlInt(params.get("pageNum"), 1) - 1, StringUtil.nvlInt(params.get("pageSize"), 10)); // 페이지넘버,페이지사이즈
        // // 페이지넘버
        List<ParcelLog> resultList = parcelService.selectParcelSmsList(params);
        Page<ParcelLog> pageImple = new PageImpl<ParcelLog>(resultList, pageable, totalcount);
        PageWrapper<ParcelLog> page = new PageWrapper<ParcelLog>(pageImple, "/admin/parcel-sms"); // 페이징완성

        model.addAttribute("resultList", resultList);
        model.addAttribute("page", page);

        params.put("searchColumn", params.get("searchColumn"));
        params.put("searchDong", params.get("searchDong"));
        params.put("searchHo", params.get("searchHo"));
        params.put("searchY", StringUtil.nvlInt(params.get("searchY"), cal.get(Calendar.YEAR)) + "");
        params.put("searchM", String.format("%02d", StringUtil.nvlInt(params.get("searchM"), cal.get(Calendar.MONTH) + 1)));
        model.addAttribute("params", params);
        return "admin/parcel-sms";
    }

    // SMS 정책관리 저장
    @RequestMapping(value = "/parcel-sms-save")
    @ResponseBody
    public String saveParcelSms(HttpServletRequest req, @RequestParam Map<String, Object> params, Model model) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        params.put("userId", user.id); // 사용자 ID
        params.put("aptId", user.house.apt.id); // 아파트아이디
        parcelService.saveParcelSmsPolicy(params);
        return "ok";
    }


    @RequestMapping(value = "/parcel-lockers-long-term", method = RequestMethod.GET)
    public String parcelLockersLongTerm(HttpSession session, HttpServletRequest req, Model model, @RequestParam(value = "page", required = false, defaultValue = "0") int pageNum,
            @RequestParam(value = "searchItem", required = false, defaultValue = "") String searchItem,
            @RequestParam(value = "searchKeyWord", required = false, defaultValue = "") String searchKeyWord) {

        User user = userService.getUser(SessionAttrs.getUserId(session));
        Page<ParcelLog> parcelLog = parcelService.getParcelLockerLongTerm(user, pageNum, searchItem, searchKeyWord);
        model.addAttribute("pages", parcelLog);

        return "admin/parcel-lockers-long-term";
    }


    @RequestMapping(value = "/parcel-lockers-long-term-save", method = RequestMethod.POST)
    @ResponseBody
    public Boolean parcelLockersLongTermUpdate(HttpSession session, HttpServletRequest req, Model model, @RequestParam(value = "status", required = false, defaultValue = "") String status,
            @RequestParam(value = "parcelLogId", required = false, defaultValue = "") Long parcelLogId) {
        User user = userService.getUser(SessionAttrs.getUserId(session));
        boolean result = false;
        try {
            parcelService.updateStatusParcel(user, parcelLogId, status);
            result = true;
        } catch (Exception e) {
        }
        return result;
    }

}
