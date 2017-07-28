package com.jaha.web.emaul.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jaha.web.emaul.constants.Constants;
import com.jaha.web.emaul.model.FileInfo;
import com.jaha.web.emaul.model.Provision;
import com.jaha.web.emaul.model.SystemFaq;
import com.jaha.web.emaul.model.SystemNotice;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.CommonService;
import com.jaha.web.emaul.service.SystemService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.Responses;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.util.StringUtil;
import com.jaha.web.emaul.util.TagUtils;

/**
 * Created by shavrani on 16. 06. 02..
 */
@Controller
public class SystemController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SystemService systemService;

    @Value("${file.path.root}")
    private String rootFilePath;

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/notice/list")
    public String systemNoticeList(HttpServletRequest req, Model model) {
        model.addAttribute("types", commonService.findByCodeGroup("SYSNT_TYPE"));// 공지사항 구분 항목
        return "jaha/notice-list";
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/notice/list-data")
    @ResponseBody
    public Map<String, Object> systemNoticeListData(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        Map<String, Object> result = systemService.getSystemNoticeList(user, params);

        return result;
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/notice/form")
    public String systemNoticeForm(HttpServletRequest req, Model model, @RequestParam(value = "id", required = false) Long id) {
        model.addAttribute("types", commonService.findByCodeGroup("SYSNT_TYPE"));// 공지사항 구분 항목
        model.addAttribute("viewServices", commonService.findByCodeGroup("VIEW_SERV"));// 서비스 노출 항목
        model.addAttribute("id", id);
        return "jaha/notice-form";
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/notice/form-data")
    @ResponseBody
    public Map<String, Object> systemNoticeFormData(HttpServletRequest req, @RequestParam(value = "id") Long id) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        SystemNotice systemNotice = systemService.getSystemNotice(user, id);
        Map<String, Object> result = new HashMap<String, Object>();

        if (systemNotice != null) {
            systemNotice.content = TagUtils.extractBody(systemNotice.content);
            List<FileInfo> fileInfoList = commonService.getFileGroup(Constants.FILE_CATEGORY_NOTICE, systemNotice.id);
            if (fileInfoList != null && fileInfoList.size() > 0) {
                result.put("fileList", fileInfoList);
            }
        }

        result.put("data", systemNotice);

        return result;
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/notice/save")
    public String systemNoticeSave(HttpServletRequest req, SystemNotice params, @RequestParam(value = "deleteFileKey", required = false) Long[] deleteFileKey,
            @RequestParam(value = "noticeFileupload", required = false) MultipartFile[] noticeFileupload) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        SystemNotice systemNotice = new SystemNotice();
        if (params.id != null) {
            systemNotice = systemService.getSystemNotice(user, params.id);
            systemNotice.modId = user.id;
        } else {
            systemNotice.regId = user.id;
        }

        systemNotice.type = params.type;
        systemNotice.viewService = params.viewService;
        systemNotice.title = params.title;
        systemNotice.content = String.format(Constants.ANDROID_HTML_WRAP, params.content);
        systemNotice.status = params.status;

        if (deleteFileKey != null) {
            for (int i = 0; i < deleteFileKey.length; i++) {
                commonService.deleteFileInfo(deleteFileKey[i]);
            }
        }

        SystemNotice savedNotice = systemService.systemNoticeSave(systemNotice);

        if (noticeFileupload != null) {

            String middlePath = Constants.ATTACH_FILE_MIDDLEPATH_NOTICE + File.separator + StringUtil.nvl(savedNotice.id);

            for (MultipartFile file : noticeFileupload) {
                // if (!file.isEmpty()) { // file은 존재하는데 내용이 0 byte면 isEmpty가 true로 나와서 업로드안됨. 빈파일도 업로드되게 파일명이존재하는지로 처리. ( 테스트때 빈파일로하다가 착각하기도해서 .. )
                if (!"".equals(file.getOriginalFilename())) {

                    FileInfo fileInfo = new FileInfo();
                    fileInfo.category = Constants.FILE_CATEGORY_NOTICE;
                    fileInfo.fileGroupKey = String.valueOf(savedNotice.id);
                    fileInfo.regId = user.id;
                    commonService.saveFileInfo(file, fileInfo, middlePath);
                }
            }
        }

        // editor내의 이미지저장시 id별로 저장하기위해 내용저장후 파일 위치이동
        savedNotice.content = commonService.saveEditorImageFiles(Constants.EDITOR_IMAGE_MIDDLEPATH_SYSTEMNOTICE, String.valueOf(savedNotice.id), savedNotice.content);
        systemService.systemNoticeSave(savedNotice);

        return "redirect:/jaha/notice/form?id=" + savedNotice.id;
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/notice/delete")
    @ResponseBody
    public int deleteSystemNotice(HttpServletRequest req, @RequestParam(value = "id") Long id) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        int result = -1;
        if (id != null) {
            SystemNotice systemNotice = systemService.getSystemNotice(user, id);
            if (systemNotice != null) {
                systemService.deleteSystemNotice(id);
                result = 1;
            }
        }
        return result;
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/faq/list")
    public String systemFaqList(HttpServletRequest req, Model model) {
        model.addAttribute("types", commonService.findByCodeGroup("FAQ_TYPE"));// 자주하는질문 항목
        return "jaha/faq-list";
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/faq/list-data")
    @ResponseBody
    public Map<String, Object> systemFaqListData(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        Map<String, Object> result = systemService.getSystemFaqList(user, params);

        return result;
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/faq/form")
    public String systemFaqForm(HttpServletRequest req, Model model, @RequestParam(value = "id", required = false) Long id) {
        model.addAttribute("types", commonService.findByCodeGroup("FAQ_TYPE"));// 자주하는질문 항목
        model.addAttribute("viewServices", commonService.findByCodeGroup("VIEW_SERV"));// 서비스 노출 항목
        model.addAttribute("id", id);
        return "jaha/faq-form";
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/faq/form-data")
    @ResponseBody
    public SystemFaq systemFaqFormData(HttpServletRequest req, @RequestParam(value = "id") Long id) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        SystemFaq systemFaq = systemService.getSystemFaq(user, id);
        if (systemFaq != null) {
            systemFaq.content = TagUtils.extractBody(systemFaq.content);
        }
        return systemFaq;
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/faq/save")
    public String systemFaqSave(HttpServletRequest req, SystemFaq params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        SystemFaq systemFaq = new SystemFaq();
        if (params.id != null) {
            systemFaq = systemService.getSystemFaq(user, params.id);
            systemFaq.modId = user.id;
        } else {
            systemFaq.regId = user.id;
        }

        systemFaq.type = params.type;
        systemFaq.viewService = params.viewService;
        systemFaq.title = params.title;
        systemFaq.content = String.format(Constants.ANDROID_HTML_WRAP, params.content);
        systemFaq.status = params.status;

        SystemFaq savedFaq = systemService.systemFaqSave(systemFaq);

        // editor내의 이미지저장시 id별로 저장하기위해 내용저장후 파일 위치이동
        savedFaq.content = commonService.saveEditorImageFiles(Constants.EDITOR_IMAGE_MIDDLEPATH_SYSTEMFAQ, String.valueOf(savedFaq.id), savedFaq.content);
        systemService.systemFaqSave(savedFaq);

        return "redirect:/jaha/faq/list";
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/faq/delete")
    @ResponseBody
    public int deleteSystemFaq(HttpServletRequest req, @RequestParam(value = "id") Long id) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        int result = -1;
        if (id != null) {
            SystemFaq systemFaq = systemService.getSystemFaq(user, id);
            if (systemFaq != null) {
                systemService.deleteSystemFaq(id);
                result = 1;
            }
        }
        return result;
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/provision/list")
    public String systemProvisionList(HttpServletRequest req, Model model) {
        return "jaha/provision-list";
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/provision/list-data")
    @ResponseBody
    public List<Provision> systemProvisionListData(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {
        // User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        List<Provision> dataList = systemService.getSystemProvisionList();
        return dataList;
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/provision/form")
    public String systemProvisionForm(HttpServletRequest req, Model model, @RequestParam(value = "id", required = false) Long id) {
        model.addAttribute("id", id);
        return "jaha/provision-form";
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/provision/form-data")
    @ResponseBody
    public Provision systemProvisionFormData(HttpServletRequest req, @RequestParam(value = "id") Long id) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        Provision data = systemService.getSystemProvision(user, id);
        if (data != null) {
            data.content = TagUtils.extractBody(data.content);
        }
        return data;
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/jaha/provision/save")
    public String systemProvisionSave(HttpServletRequest req, SystemFaq params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        Provision provision = new Provision();
        if (params.id != null) {
            provision = systemService.getSystemProvision(user, params.id);
            provision.modUser = user;
        } else {
            provision.regUser = user;
        }

        provision.title = params.title;
        provision.content = String.format(Constants.ANDROID_HTML_WRAP, params.content);
        provision.status = params.status;

        Provision savedProvision = systemService.systemProvisionSave(provision);

        // editor내의 이미지저장시 id별로 저장하기위해 내용저장후 파일 위치이동
        savedProvision.content = commonService.saveEditorImageFiles(Constants.EDITOR_IMAGE_MIDDLEPATH_PROVISION, String.valueOf(savedProvision.id), savedProvision.content);
        systemService.systemProvisionSave(savedProvision);

        return "redirect:/jaha/provision/list";
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/provision/view")
    public String provisionView(HttpServletRequest req, Model model, @RequestParam(value = "id", required = false) Long id) {
        model.addAttribute("id", id);
        return "user/policy";
    }

    /**
     * 게시상태의 약관만 조회
     *
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/provision/view-data")
    @ResponseBody
    public Provision provisionViewData(HttpServletRequest req, @RequestParam(value = "id") Long id) {
        User user = null;
        // User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        Provision data = systemService.getSystemProvisionUseStatus(user, id, "1");

        if (data != null) {
            data.content = TagUtils.extractBody(data.content);
        }
        return data;
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/user/system-notice/list")
    public String userSystemNoticeList(HttpServletRequest req, Model model) {
        model.addAttribute("leftSideMenu", "system");
        return "user/system-notice-list";
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/user/system-notice/list-data")
    @ResponseBody
    public Map<String, Object> userSystemNoticeListData(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        params.put("status", "1");// 게시상태의 데이터만 조회
        List<String> viewServices = new ArrayList<String>();// 전체이거나 Web이 포함된 항목만 조회
        viewServices.add("1");// 전체
        viewServices.add("4");// web
        params.put("viewServices", viewServices);
        Map<String, Object> result = systemService.getSystemNoticeList(user, params);

        return result;
    }

    /**
     * @author shavrani 2016.06.03
     */
    @RequestMapping(value = "/user/system-notice/form")
    public String userSystemNoticeForm(HttpServletRequest req, Model model, @RequestParam(value = "id") Long id) {
        model.addAttribute("id", id);
        model.addAttribute("leftSideMenu", "system");
        return "user/system-notice-form";
    }

    /**
     * @author shavrani 2016.06.03
     */
    @RequestMapping(value = "/user/system-notice/form-data")
    @ResponseBody
    public Map<String, Object> userSystemNoticeFormData(HttpServletRequest req, @RequestParam Map<String, Object> params) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        Map<String, Object> result = new HashMap<String, Object>();

        if (params.get("id") != null) {
            params.put("status", "1");// 게시상태의 데이터만 조회
            List<String> viewServices = new ArrayList<String>();// 전체이거나 Web이 포함된 항목만 조회
            viewServices.add("1");// 전체
            viewServices.add("4");// web
            params.put("viewServices", viewServices);

            SystemNotice systemNotice = systemService.getSystemNotice(user, params);

            if (systemNotice != null) {
                systemNotice.content = TagUtils.extractBody(systemNotice.content);
                List<FileInfo> fileInfoList = commonService.getFileGroup(Constants.FILE_CATEGORY_NOTICE, systemNotice.id);
                if (fileInfoList != null && fileInfoList.size() > 0) {
                    result.put("fileList", fileInfoList);
                }
            }

            result.put("data", systemNotice);
        }

        return result;
    }

    /**
     * @author shavrani 2016.06.03
     */
    @RequestMapping(value = "/user/system-notice/file-down")
    public ResponseEntity<byte[]> userSystemNoticeFileDown(HttpServletRequest req, @RequestParam Map<String, Object> params) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));

        if (params.get("id") != null) {
            params.put("status", "1");// 게시상태의 데이터만 조회
            List<String> viewServices = new ArrayList<String>();// 전체이거나 Web이 포함된 항목만 조회
            viewServices.add("1");// 전체
            viewServices.add("4");// web
            params.put("viewServices", viewServices);

            SystemNotice systemNotice = systemService.getSystemNotice(user, params);
            if (systemNotice != null) {
                /** 해당글의 fileGroupKey와 fileKey조합이 맞아야만 다운로드 되게 처리. */
                Long fileKey = -1L;
                if (params.get("fileKey") != null) {
                    try {
                        fileKey = StringUtil.nvlLong(params.get("fileKey"));
                        FileInfo fileInfo = commonService.getFileInfo(Constants.FILE_CATEGORY_NOTICE, systemNotice.id, fileKey);
                        return Responses.getFileEntity(fileInfo.getFile(), fileInfo.fileOriginName);
                    } catch (Exception e) {
                    }
                }
            }
        }
        return null;
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/user/system-faq/list")
    public String userSystemFaqList(HttpServletRequest req, Model model) {
        model.addAttribute("leftSideMenu", "system");
        return "user/system-faq-list";
    }

    /**
     * @author shavrani 2016.06.02
     */
    @RequestMapping(value = "/user/system-faq/list-data")
    @ResponseBody
    public Map<String, Object> userSystemFaqListData(HttpServletRequest req, Model model, @RequestParam Map<String, Object> params) {

        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        params.put("status", "1");// 게시상태의 데이터만 조회
        List<String> viewServices = new ArrayList<String>();// 전체이거나 Web이 포함된 항목만 조회
        viewServices.add("1");// 전체
        viewServices.add("4");// web
        params.put("viewServices", viewServices);

        Map<String, Object> result = systemService.getSystemFaqList(user, params);

        return result;
    }

    /**
     * @author shavrani 2016.06.07
     */
    @RequestMapping(value = "/user/system-faq/form")
    public String userSystemFaqForm(HttpServletRequest req, Model model, @RequestParam(value = "id") Long id) {
        model.addAttribute("id", id);
        model.addAttribute("leftSideMenu", "system");
        return "user/system-faq-form";
    }

    /**
     * @author shavrani 2016.06.07
     */
    @RequestMapping(value = "/user/system-faq/form-data")
    @ResponseBody
    public SystemFaq userSystemFaqFormData(HttpServletRequest req, @RequestParam Map<String, Object> params) {
        User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
        SystemFaq systemFaq = null;
        if (params.get("id") != null) {
            params.put("status", "1");// 게시상태의 데이터만 조회
            List<String> viewServices = new ArrayList<String>();// 전체이거나 Web이 포함된 항목만 조회
            viewServices.add("1");// 전체
            viewServices.add("4");// web
            params.put("viewServices", viewServices);

            systemFaq = systemService.getSystemFaq(user, params);
            if (systemFaq != null) {
                systemFaq.content = TagUtils.extractBody(systemFaq.content);
            }
        }

        return systemFaq;
    }

    /**
     * @author shavrani 2016.10.11
     */
    @RequestMapping(value = "/admin/manual/form")
    public String manualForm(HttpServletRequest req, Model model) {

        String defaultPath = rootFilePath + File.separator + Constants.FILE_MIDDLEPATH_MANUAL + File.separator;

        String adminPath = defaultPath + "admin" + File.separator;
        String adminPathPdf = adminPath + "pdf" + File.separator;
        String adminPathImg = adminPath + "img" + File.separator;

        String userPath = defaultPath + "user" + File.separator;
        String userPathPdf = userPath + "pdf" + File.separator;
        String userPathImg = userPath + "img" + File.separator;

        // 관리자 관련 pdf와 image url 설정
        File dirPdf = new File(adminPathPdf);
        if (dirPdf.exists()) {
            String[] arrPdf = dirPdf.list();
            if (arrPdf != null && arrPdf.length > 0) {
                model.addAttribute("adminPdfFile", arrPdf[0]);
                String downUrl = "/admin/manual/file-down/" + FilenameUtils.getBaseName(arrPdf[0]);
                model.addAttribute("adminDownUrl", downUrl);
            }
        }

        File dirImg = new File(adminPathImg);
        if (dirImg.exists()) {
            String[] imgNames = dirImg.list();
            if (imgNames != null && imgNames.length > 0) {
                List<String> imgSrcList = new ArrayList<String>();
                for (int i = 0; i < imgNames.length; i++) {
                    String src = "/admin/manual/img-view/" + FilenameUtils.getBaseName(imgNames[i]);
                    imgSrcList.add(src);
                }
                Collections.sort(imgSrcList);
                model.addAttribute("adminImgSrcList", imgSrcList);
            }
        }

        // 사용자 관련 pdf와 image url 설정
        dirPdf = new File(userPathPdf);
        if (dirPdf.exists()) {
            String[] arrPdf = dirPdf.list();
            if (arrPdf != null && arrPdf.length > 0) {
                model.addAttribute("userPdfFile", arrPdf[0]);
                String downUrl = "/user/manual/file-down/" + FilenameUtils.getBaseName(arrPdf[0]);
                model.addAttribute("userDownUrl", downUrl);
            }
        }

        dirImg = new File(userPathImg);
        if (dirImg.exists()) {
            String[] imgNames = dirImg.list();
            if (imgNames != null && imgNames.length > 0) {
                List<String> imgSrcList = new ArrayList<String>();
                for (int i = 0; i < imgNames.length; i++) {
                    String src = "/user/manual/img-view/" + FilenameUtils.getBaseName(imgNames[i]);
                    imgSrcList.add(src);
                }
                Collections.sort(imgSrcList);
                model.addAttribute("userImgSrcList", imgSrcList);
            }
        }

        return "admin/manual-form";
    }

    /**
     * @author shavrani 2016.10.11
     */
    @RequestMapping(value = "/jaha/manual/save")
    public String manualSave(HttpServletRequest req, RedirectAttributes rda, SystemNotice params, @RequestParam(value = "tabIdx") String tabIdx,
            @RequestParam(value = "manualFile") MultipartFile manualFile) {

        try {
            if (manualFile != null && !StringUtil.isBlank(manualFile.getOriginalFilename())) {

                String ext = FilenameUtils.getExtension(manualFile.getOriginalFilename());
                String fileBaseName = FilenameUtils.getBaseName(manualFile.getOriginalFilename());

                if ("pdf".equals(StringUtil.lowerCase(ext))) {
                    String defaultPath = rootFilePath + File.separator + Constants.FILE_MIDDLEPATH_MANUAL + File.separator;
                    String authPath = defaultPath + ("1".equals(tabIdx) ? "admin" : "user") + File.separator;// tabIdex 1은 관리자 파일, 2는 유저 파일
                    String typePathPdf = authPath + "pdf" + File.separator;
                    String typePathImg = authPath + "img" + File.separator;

                    File dirPdf = new File(typePathPdf);
                    if (!dirPdf.exists()) {
                        dirPdf.mkdirs();
                        dirPdf.setReadable(true, false);
                        dirPdf.setWritable(true, false);
                    }
                    FileUtils.cleanDirectory(dirPdf);

                    File dirImg = new File(typePathImg);
                    if (!dirImg.exists()) {
                        dirImg.mkdirs();
                        dirImg.setReadable(true, false);
                        dirImg.setWritable(true, false);
                    }
                    FileUtils.cleanDirectory(dirImg);

                    // pdf파일 저장
                    File dest = new File(dirPdf, fileBaseName + "." + StringUtil.lowerCase(ext));
                    dest.createNewFile();
                    dest.setReadable(true, false);
                    dest.setWritable(true, false);

                    manualFile.transferTo(dest);// 물리파일 저장

                    PDDocument pdd = PDDocument.load(dest);
                    PDFRenderer pr = new PDFRenderer(pdd);
                    int size = pdd.getNumberOfPages();// default value 0
                    for (int i = 0; i < size; i++) {
                        BufferedImage bImg = pr.renderImageWithDPI(i, 200, ImageType.RGB);
                        String fileFullPath = typePathImg + "img-" + StringUtil.leftPad(StringUtil.nvl(i + 1), 3, "0") + ".png";
                        ImageIOUtil.writeImage(bImg, fileFullPath, 200);// 해당경로의 디렉토리가 생성되어있어야 이미지가 저장된다.
                    }
                    pdd.close();
                }
            }

        } catch (Exception e) {
            logger.info(" # 사용설명서 저장 에러", e);
        }

        rda.addFlashAttribute("tabIdx", tabIdx);

        return "redirect:/admin/manual/form";

    }

    /**
     * @author shavrani 2016.10.12
     */
    @RequestMapping(value = "/{type}/manual/file-down/{name}")
    public ResponseEntity<byte[]> manualFileDown(HttpServletRequest req, @PathVariable(value = "type") String type, @PathVariable(value = "name") String name) {

        String defaultPath = rootFilePath + File.separator + Constants.FILE_MIDDLEPATH_MANUAL + File.separator;
        String authPath = defaultPath + type + File.separator;
        String typePathPdf = authPath + "pdf" + File.separator;

        File file = new File(typePathPdf + name + ".pdf");
        if (file.exists()) {
            return Responses.getFileEntity(file, file.getName());
        } else {
            return null;
        }
    }

    /**
     * @author shavrani 2016.10.12
     */
    @RequestMapping(value = "/{type}/manual/img-view/{img}")
    public ResponseEntity<byte[]> manualImageView(HttpServletRequest req, @PathVariable(value = "type") String type, @PathVariable(value = "img") String img) {

        String defaultPath = rootFilePath + File.separator + Constants.FILE_MIDDLEPATH_MANUAL + File.separator;
        String authPath = defaultPath + type + File.separator;
        String typePathImg = authPath + "img" + File.separator;

        File file = new File(typePathImg + img + ".png");
        if (file.exists()) {
            return Responses.getFileEntity(file, file.getName());
        } else {
            return null;
        }
    }

    /**
     * @author shavrani 2016.10.11
     */
    @RequestMapping(value = "/user/manual/view")
    public String manualView(HttpServletRequest req, Model model) {

        String defaultPath = rootFilePath + File.separator + Constants.FILE_MIDDLEPATH_MANUAL + File.separator;
        String userPath = defaultPath + "user" + File.separator;
        String userPathPdf = userPath + "pdf" + File.separator;
        String userPathImg = userPath + "img" + File.separator;

        // 사용자 관련 pdf와 image url 설정
        File dirPdf = new File(userPathPdf);
        if (dirPdf.exists()) {
            String[] arrPdf = dirPdf.list();
            if (arrPdf != null && arrPdf.length > 0) {
                model.addAttribute("userPdfFile", arrPdf[0]);
                String downUrl = "/user/manual/file-down/" + FilenameUtils.getBaseName(arrPdf[0]);
                model.addAttribute("userDownUrl", downUrl);
            }
        }

        File dirImg = new File(userPathImg);
        if (dirImg.exists()) {
            String[] imgNames = dirImg.list();
            if (imgNames != null && imgNames.length > 0) {
                List<String> imgSrcList = new ArrayList<String>();
                for (int i = 0; i < imgNames.length; i++) {
                    String src = "/user/manual/img-view/" + FilenameUtils.getBaseName(imgNames[i]);
                    imgSrcList.add(src);
                }
                Collections.sort(imgSrcList);
                model.addAttribute("userImgSrcList", imgSrcList);
            }
        }

        return "user/manual-view";
    }

}
