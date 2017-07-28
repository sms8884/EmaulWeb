package com.jaha.web.emaul.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.jaha.web.emaul.constants.Constants;
import com.jaha.web.emaul.mapper.SystemFaqMapper;
import com.jaha.web.emaul.mapper.SystemNoticeMapper;
import com.jaha.web.emaul.model.Provision;
import com.jaha.web.emaul.model.SystemFaq;
import com.jaha.web.emaul.model.SystemNotice;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.repo.SystemFaqRepository;
import com.jaha.web.emaul.repo.SystemNoticeRepository;
import com.jaha.web.emaul.repo.SystemProvisionRepository;
import com.jaha.web.emaul.util.StringUtil;

/**
 * Created by shavrani on 16. 06. 02..
 */
@Service
public class SystemServiceImpl implements SystemService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SystemNoticeMapper systemNoticeMapper;
    @Autowired
    private SystemNoticeRepository systemNoticeRepository;
    @Autowired
    private SystemFaqMapper systemFaqMapper;
    @Autowired
    private SystemFaqRepository systemFaqRepository;
    @Autowired
    private SystemProvisionRepository systemProvisionRepository;
    @Autowired
    private CommonService commonService;

    @Value("${file.path.editor.image}")
    private String editorImagePath;

    @Value("${file.path.root}")
    private String rootFilePath;

    /**
     * @author shavrani 2016.06.02
     */
    @Override
    public Map<String, Object> getSystemNoticeList(User user, Map<String, Object> params) {

        String pageNum = StringUtil.nvl(params.get("pageNum"), "1");
        String pageSize = StringUtil.nvl(params.get("pageSize"), String.valueOf(Constants.DEFAULT_PAGE_SIZE));

        int nPageNum = Integer.parseInt(pageNum);
        int nPageSize = Integer.parseInt(pageSize);

        int startNum = (nPageNum - 1) * nPageSize;

        params.put("startNum", startNum);
        params.put("pageSize", nPageSize);

        List<SystemNotice> dataList = systemNoticeMapper.selectSystemNoticeList(params);

        int totalCount = systemNoticeMapper.selectSystemNoticeListCount(params);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("totalCount", totalCount);
        result.put("dataList", dataList);

        return result;

    }

    /**
     * @author shavrani 2016.06.02
     */
    @Override
    public SystemNotice getSystemNotice(User user, Long id) {
        return systemNoticeRepository.findById(id);
    }

    /**
     * @author shavrani 2016.06.09
     */
    @Override
    public SystemNotice getSystemNotice(User user, Map<String, Object> params) {
        return systemNoticeMapper.selectSystemNotice(params);
    }

    /**
     * @author shavrani 2016.06.02
     */
    @Override
    public SystemNotice systemNoticeSave(SystemNotice systemNotice) {
        return systemNoticeRepository.save(systemNotice);
    }

    /**
     * @author shavrani 2016.06.02
     */
    @Override
    public void deleteSystemNotice(Long id) {

        systemNoticeRepository.delete(id);// db 삭제

        try {
            String editorImgDirPath = String.format(editorImagePath + "/%s/%s", Constants.EDITOR_IMAGE_MIDDLEPATH_SYSTEMNOTICE, String.valueOf(id));
            File editorImgDir = new File(editorImgDirPath);
            if (editorImgDir.exists()) {
                FileUtils.deleteDirectory(editorImgDir);// editor 이미지 삭제
            }
            commonService.deleteFileGroup(Constants.FILE_CATEGORY_NOTICE, id);// 파일과 db 삭제
            String savePath = rootFilePath + File.separator + Constants.ATTACH_FILE_MIDDLEPATH_NOTICE + File.separator + String.valueOf(id);
            File attachFileDir = new File(savePath);
            if (attachFileDir.exists()) {
                FileUtils.deleteDirectory(attachFileDir);// editor 이미지 삭제
            }
        } catch (Exception e) {
            logger.info(" # 시스템공지사항삭제 Exception : ", e);
        }
    }

    /**
     * @author shavrani 2016.06.02
     */
    @Override
    public Map<String, Object> getSystemFaqList(User user, Map<String, Object> params) {

        String pageNum = StringUtil.nvl(params.get("pageNum"), "1");
        String pageSize = StringUtil.nvl(params.get("pageSize"), String.valueOf(Constants.DEFAULT_PAGE_SIZE));

        int nPageNum = Integer.parseInt(pageNum);
        int nPageSize = Integer.parseInt(pageSize);

        int startNum = (nPageNum - 1) * nPageSize;

        params.put("startNum", startNum);
        params.put("pageSize", nPageSize);

        List<SystemFaq> dataList = systemFaqMapper.selectSystemFaqList(params);

        int totalCount = systemFaqMapper.selectSystemFaqListCount(params);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("totalCount", totalCount);
        result.put("dataList", dataList);

        return result;

    }

    /**
     * @author shavrani 2016.06.02
     */
    @Override
    public SystemFaq getSystemFaq(User user, Long id) {
        return systemFaqRepository.findById(id);
    }

    /**
     * @author shavrani 2016.06.09
     */
    @Override
    public SystemFaq getSystemFaq(User user, Map<String, Object> params) {
        return systemFaqMapper.selectSystemFaq(params);
    }

    /**
     * @author shavrani 2016.06.02
     */
    @Override
    public SystemFaq systemFaqSave(SystemFaq systemFaq) {
        return systemFaqRepository.save(systemFaq);
    }

    /**
     * @author shavrani 2016.06.02
     */
    @Override
    public void deleteSystemFaq(Long id) {

        systemFaqRepository.delete(id);

        try {
            String editorImgDirPath = String.format(editorImagePath + "/%s/%s", Constants.EDITOR_IMAGE_MIDDLEPATH_SYSTEMFAQ, String.valueOf(id));
            File editorImgDir = new File(editorImgDirPath);
            if (editorImgDir.exists()) {
                FileUtils.deleteDirectory(editorImgDir);// editor 이미지 삭제
            }
        } catch (Exception e) {
            logger.info(" # 시스템FAQ삭제 Exception : ", e);
        }
    }

    @Override
    public List<Provision> getSystemProvisionList() {
        return systemProvisionRepository.findAll(new Sort(Sort.Direction.DESC, "id"));
    }

    /**
     * @author shavrani 2016.06.02
     */
    @Override
    public Provision getSystemProvision(User user, Long id) {
        return systemProvisionRepository.findById(id);
    }

    @Override
    public Provision getSystemProvisionUseStatus(User user, Long id, String status) {
        return systemProvisionRepository.findByIdAndStatus(id, status);
    }

    @Override
    public Provision systemProvisionSave(Provision provision) {
        return systemProvisionRepository.save(provision);
    }

}
