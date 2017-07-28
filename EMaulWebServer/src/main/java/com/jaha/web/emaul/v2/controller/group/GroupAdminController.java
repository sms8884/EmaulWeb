/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.controller.group;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.BaseSecuModel;
import com.jaha.web.emaul.model.BoardCategory;
import com.jaha.web.emaul.model.House;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.BoardService;
import com.jaha.web.emaul.service.HouseService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.util.StringUtil;
import com.jaha.web.emaul.v2.model.apt.AptDto;
import com.jaha.web.emaul.v2.model.apt.AptVo;
import com.jaha.web.emaul.v2.model.group.CsDto;
import com.jaha.web.emaul.v2.model.group.CsVo;
import com.jaha.web.emaul.v2.model.group.GroupAdminVo;
import com.jaha.web.emaul.v2.service.group.GroupAdminService;
import com.jaha.web.emaul.v2.util.PagingHelper;

/**
 * <pre>
 * Class Name : GroupAdminController.java
 * Description : 그룹 관리자 컨트롤러
 * 
 * Modification Information
 * 
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 11. 18.     조영태      Generation
 * </pre>
 *
 * @author 조영태
 * @since 2016. 11. 18.
 * @version 1.0
 */
@Controller
public class GroupAdminController {

    private static final Logger logger = LoggerFactory.getLogger(GroupAdminController.class);

    @Autowired
    private GroupAdminService groupAdminService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private UserService userService;

    @Autowired
    public JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sendUser;

    @Value("${file.path.root}")
    private String rootFilePath;



    /**
     * 고객센터 1:1 문의내역 조회
     *
     * @param req
     * @param model
     * @param csVo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/group-admin/cs/cs-list", method = RequestMethod.GET)
    public String adminGroupUserCsList(HttpServletRequest req, PagingHelper pagingHelper, Model model, CsDto csDto) throws Exception {

        logger.debug(">>> pagingHelper : " + pagingHelper.toString());
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        if (!user.type.jaha) {
            logger.error("<<< 자하관리자 계정정보 접근권한 오류 >>>");
            throw new Exception("<<< 자하관리자 계정정보 접근권한 오류 >>>");
        }

        csDto.setPagingHelper(pagingHelper);
        List<CsVo> csList = this.groupAdminService.selectCsList(csDto);
        model.addAttribute("csList", csList);

        return "v2/group-admin/cs/cs-list";
    }


    /**
     * 단체관리자 계정 정보 조회
     *
     * @param req
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/group-admin/{pageType}", method = RequestMethod.GET)
    public String adminGroupUserInfo(HttpServletRequest req, Model model, GroupAdminVo groupAdminVo, @PathVariable("pageType") String pageType) throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        if (!user.type.groupAdmin) {
            logger.error("<<< 그룹관리자 계정정보 접근권한 오류 >>>");
            throw new Exception("<<< 그룹관리자 계정정보 접근권한 오류 >>>");
        }

        groupAdminVo.setAptId(user.house.apt.id);
        groupAdminVo.setUserId(user.id);

        GroupAdminVo groupAdmin = groupAdminService.getGroupAdmin(groupAdminVo);

        if (groupAdmin == null) {
            logger.error("<<< 그룹관리자 계정정보 오류 >>>");
            throw new Exception("<<< 그룹관리자 계정정보 오류 >>>");
        }

        BaseSecuModel bsm = new BaseSecuModel();
        groupAdmin.setGroupPhone1(bsm.descString(groupAdmin.getGroupPhone1()));
        groupAdmin.setGroupPhone2(bsm.descString(groupAdmin.getGroupPhone2()));
        groupAdmin.setGroupPhone3(bsm.descString(groupAdmin.getGroupPhone3()));
        groupAdmin.setName(bsm.descString(groupAdmin.getName()));
        groupAdmin.setPhone1(bsm.descString(groupAdmin.getPhone1()));
        groupAdmin.setPhone2(bsm.descString(groupAdmin.getPhone2()));
        groupAdmin.setPhone3(bsm.descString(groupAdmin.getPhone3()));
        groupAdmin.setEmail(bsm.descString(groupAdmin.getEmail()));

        model.addAttribute("groupAdmin", groupAdmin);

        if ("user-update".equals(pageType)) {
            // 계정정보 수정
            return "v2/group-admin/user/user-info-update";
        } else if ("cs".equals(pageType)) {
            // 고객센터 1:1 문의
            model.addAttribute("result", StringUtil.nvl(req.getParameter("result"), ""));
            return "v2/group-admin/cs/cs";
        } else {
            // 계정정보 조회
            return "v2/group-admin/user/user-info";
        }
    }



    /**
     * 단체관리자 정보 수정 처리
     *
     * @param request
     * @param model
     * @param groupAdminVo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v2/group-admin/user-update-proc")
    public @ResponseBody Map<String, Object> adminGroupUserInfoUpdateProc(HttpServletRequest request, Model model, GroupAdminVo groupAdminVo) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));
        groupAdminVo.setAptId(user.house.apt.id);
        groupAdminVo.setUserId(user.id);

        if (!user.type.groupAdmin) {
            logger.error("<<< 그룹관리자 계정정보 접근권한 오류 >>>");
            map.put("result", false);
            map.put("message", "그룹관리자 계정정보 접근권한 오류");
            return map;
        }

        try {
            // 비밀번호 수정여부
            groupAdminVo.setUserEmail(user.getEmail());

            BaseSecuModel bsm = new BaseSecuModel();
            groupAdminVo.setGroupPhone1(bsm.encString(groupAdminVo.getGroupPhone1()));
            groupAdminVo.setGroupPhone2(bsm.encString(groupAdminVo.getGroupPhone2()));
            groupAdminVo.setGroupPhone3(bsm.encString(groupAdminVo.getGroupPhone3()));
            groupAdminVo.setName(bsm.encString(groupAdminVo.getName()));
            groupAdminVo.setPhone1(bsm.encString(groupAdminVo.getPhone1()));
            groupAdminVo.setPhone2(bsm.encString(groupAdminVo.getPhone2()));
            groupAdminVo.setPhone3(bsm.encString(groupAdminVo.getPhone3()));
            groupAdminVo.setEmail(bsm.encString(groupAdminVo.getEmail()));
            groupAdminService.updateGroupAdmin(groupAdminVo);

            map.put("result", true);
            map.put("message", "그룹관리자 계정정보 수정");
        } catch (Exception e) {
            logger.error("<<< 그룹관리자 계정정보 수정 오류 >>>" + e.getMessage());
            map.put("result", false);
            map.put("message", "그룹관리자 계정정보 수정 오류");
            return map;
        }

        return map;
    }


    /**
     * 고객센터 1:1 문의 등록
     *
     * @param req
     * @param model
     * @param groupAdminVo
     * @return
     */
    @RequestMapping(value = "/v2/group-admin/cs/sendProc", method = RequestMethod.POST)
    public String groupAdminCsSendProc(MultipartHttpServletRequest request, Model model, GroupAdminVo groupAdminVo) {

        String result = "N";
        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));

        try {

            if ("Y".equals(groupAdminVo.getAgree())) {

                try {

                    logger.debug(">>> 제목 : " + groupAdminVo.getTitle());
                    logger.debug(">>> 받는사람 : " + groupAdminVo.getEmail());

                    CsVo cs = new CsVo();
                    cs.setUserId(user.id);
                    BaseSecuModel bsm = new BaseSecuModel();
                    cs.setEmail(bsm.encString(groupAdminVo.getEmail()));
                    // cs.setEmail(groupAdminVo.getEmail());
                    cs.setTitle(groupAdminVo.getTitle());
                    cs.setContent(groupAdminVo.getContent());
                    cs.setRegDate(new Date());

                    this.groupAdminService.insertGroupAdminCs(cs);
                    logger.debug(">>> cs_history : " + cs.getId());

                    // 기본메일
                    // SimpleMailMessage message = new SimpleMailMessage();
                    // message.setSubject(groupAdminVo.getTitle());
                    // message.setText(groupAdminVo.getContent());
                    // message.setFrom(sendUser);
                    // message.setTo(groupAdminVo.getEmail());
                    // mailSender.send(message);

                    MimeMessage message = mailSender.createMimeMessage();
                    message.setFrom(new InternetAddress(sendUser, "자하스마트 고객센터 1;1 문의", "euc-kr"));
                    message.setSubject(groupAdminVo.getTitle() + "  : 접수자 (" + user.getFullName() + " / " + groupAdminVo.getEmail() + ")");
                    message.setRecipient(RecipientType.TO, new InternetAddress(sendUser, "고객센터 담당자", "euc-kr"));
                    message.setSentDate(new Date());

                    MimeBodyPart bodypart = new MimeBodyPart();
                    bodypart.setContent(groupAdminVo.getContent(), "text/html;charset=euc-kr");

                    Multipart multipart = new MimeMultipart();
                    multipart.addBodyPart(bodypart);

                    List<MultipartFile> csfiles = request.getFiles("csfiles");

                    logger.debug(">>> 첨부파일 갯수 : " + csfiles.size());

                    if (!csfiles.isEmpty()) {

                        File docDir = new File(String.format(rootFilePath + File.separator + "cs" + File.separator + "%s", cs.getId()));
                        logger.debug(">>> docDir : " + docDir);
                        if (!docDir.exists()) {
                            docDir.mkdirs();
                        }


                        for (int i = 0; i < csfiles.size(); i++) {

                            String originalFileName = csfiles.get(i).getOriginalFilename();

                            if (StringUtils.isNotEmpty(originalFileName)) {

                                try {
                                    File dest = new File(docDir, originalFileName);
                                    dest.createNewFile();
                                    csfiles.get(i).transferTo(dest);

                                    logger.debug(">>> path : " + dest.getPath());
                                    logger.debug(">>> name : " + dest.getName());

                                    MimeBodyPart attachPart = new MimeBodyPart();
                                    attachPart.setDataHandler(new DataHandler(new FileDataSource(new File(dest.getPath()))));
                                    attachPart.setFileName(MimeUtility.encodeText(dest.getName()));
                                    multipart.addBodyPart(attachPart);

                                    if (i == 0) {
                                        cs.setFile1(docDir + "/" + originalFileName);
                                    } else if (i == 1) {
                                        cs.setFile2(docDir + "/" + originalFileName);
                                    } else {
                                        cs.setFile3(docDir + "/" + originalFileName);
                                    }

                                } catch (IOException e) {
                                    logger.error("\n\n투표 첨부파일 등록중 오류가 발생 : ", e);
                                }
                            }
                        }
                    }

                    message.setContent(multipart);
                    mailSender.send(message);

                    // 메일발송시 Exception 이 발생하면 cs_history.mail_send_date 가 null
                    cs.setMailSendDate(new Date());
                    this.groupAdminService.updateGroupAdminCs(cs);

                    result = "Y";
                } catch (Exception e) {
                    logger.error(">>> sendEmail exception : " + e.getMessage());
                }

            }
        } catch (Exception e) {
            logger.error(">>> 고객센터 1:1 문의 접수 중 오류가 발생", e);
        }
        return "redirect:/v2/group-admin/cs?result=" + result;
    }

    /**
     * @author shavrani 2016-12-26
     * @desc 아파트 관리
     */
    @RequestMapping(value = "/v2/group-admin/apt/list")
    public String getAptList(HttpServletRequest req, PagingHelper pagingHelper, Model model, AptDto aptDto) throws Exception {

        logger.debug(">>> pagingHelper : " + pagingHelper.toString());
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        if (!user.type.groupAdmin) {
            logger.error("<<< 그룹관리자 계정정보 접근권한 오류 >>>");
            throw new Exception("<<< 그룹관리자 계정정보 접근권한 오류 >>>");
        }

        GroupAdminVo groupAdminVo = new GroupAdminVo();
        groupAdminVo.setAptId(user.house.apt.id);
        groupAdminVo.setUserId(user.id);

        GroupAdminVo groupAdmin = groupAdminService.getGroupAdmin(groupAdminVo);
        aptDto.setGroupAdminVo(groupAdmin);
        aptDto.setPagingHelper(pagingHelper);

        List<AptVo> aptList = groupAdminService.selectAptList(aptDto);

        model.addAttribute("aptList", aptList);

        return "v2/group-admin/apt/apt-list";
    }

    @RequestMapping(value = "/v2/group-admin/apt/switch")
    public String switchApt(HttpSession session, @RequestParam(value = "aptId", required = false) Long aptId, @RequestParam(value = "comeback", required = false) String comeback) throws Exception {
        Long userId = SessionAttrs.getUserId(session);
        User user = userService.getUser(userId);

        if (!user.type.groupAdmin) {
            logger.error("<<< 그룹관리자 계정정보 접근권한 오류 >>>");
            throw new Exception("<<< 그룹관리자 계정정보 접근권한 오류 >>>");
        }

        String redirectUrl = "redirect:/user";

        Long _aptId = null;
        if ("Y".equals(comeback)) {

            // comeback이 Y이면 단체관리자 아파트로 복귀

            GroupAdminVo groupAdminVo = new GroupAdminVo();
            groupAdminVo.setUserId(user.id);

            GroupAdminVo groupAdmin = groupAdminService.getGroupAdmin(groupAdminVo);
            _aptId = groupAdmin.getAptId();

            redirectUrl = "redirect:/v2/group-admin/apt/list";
        } else {

            // aptId가 있으면 선택한 아파트로 전환

            if (aptId == null) {
                logger.error("<<< 아파트전환중 아파트정보 오류 >>>");
                throw new Exception("<<< 아파트전환중 아파트정보 오류 >>>");
            }
            _aptId = aptId;
        }

        logger.info(">>> before house : " + user.house.id + "/ dong : " + user.house.dong + "/ ho : " + user.house.dong);
        Apt apt = houseService.getApt(_aptId);
        House house = userService.selectOrCreateHouse(apt.address.건물관리번호, "0", "0");

        user.house = house;
        logger.info(">>> after house : " + user.house.id + "/ dong : " + user.house.dong + "/ ho : " + user.house.dong);
        userService.saveAndFlush(user);

        if ("지자체".equals(apt.address.비고1)) { // TODO: 기준정보인 address 테이블보다는 apt 테이블에 지자체 아파트 여부 추가
            // 단체 관리자인 경우, 단체 게시판 카테고리 조회
            List<BoardCategory> categoryList = boardService.getCategories(userId, "group", "N");

            if (categoryList == null || categoryList.isEmpty()) {
                logger.info("<<단체 게시판을 생성해주세요!>> 사용자아이디: {}, 아파트아이디: {}", user.id, apt.id);
            } else {
                return "redirect:/v2/group-admin/board/group/create-form/" + categoryList.get(0).id;
            }
        }

        return redirectUrl;
    }
}
