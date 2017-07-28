package com.jaha.web.emaul.service;

import static com.jaha.web.emaul.model.spec.AptApAccessDeviceSpecification.accessKeyEq;
import static com.jaha.web.emaul.model.spec.AptApAccessDeviceSpecification.aptIdEq;
import static com.jaha.web.emaul.model.spec.AptApAccessDeviceSpecification.deactive;
import static com.jaha.web.emaul.model.spec.AptApAccessDeviceSpecification.idEq;
import static com.jaha.web.emaul.model.spec.AptApAccessDeviceSpecification.typeEq;
import static com.jaha.web.emaul.model.spec.AptApAccessDeviceSpecification.userNmLike;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jaha.web.emaul.constants.Constants;
import com.jaha.web.emaul.mapper.AptApMapper;
import com.jaha.web.emaul.model.AptAp;
import com.jaha.web.emaul.model.AptApAccessAuth;
import com.jaha.web.emaul.model.AptApAccessDevice;
import com.jaha.web.emaul.model.AptApAccessLog;
import com.jaha.web.emaul.model.AptApFirmwareLog;
import com.jaha.web.emaul.model.AptApMonitoringAlive;
import com.jaha.web.emaul.model.AptApMonitoringNoti;
import com.jaha.web.emaul.model.CommonCode;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.prop.UrlProperties;
import com.jaha.web.emaul.repo.AptApAccessAuthRepository;
import com.jaha.web.emaul.repo.AptApAccessDeviceRepository;
import com.jaha.web.emaul.repo.AptApFirmwareLogRepository;
import com.jaha.web.emaul.repo.AptApRepository;
import com.jaha.web.emaul.util.HttpUtils;
import com.jaha.web.emaul.util.PoiUtil;
import com.jaha.web.emaul.util.StringUtil;

/**
 * @author shavrani
 * @since 2016. 9. 2.
 * @version 1.0
 */
@Service
public class EdoorServiceImpl implements EdoorService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UrlProperties urlProperties;
    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private GcmService gcmService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private HouseService houseService;
    @Autowired
    private AptApMapper aptApMapper;
    @Autowired
    private AptApRepository aptApRepository;
    @Autowired
    private AptApAccessAuthRepository aptApAccessAuthRepository;
    @Autowired
    private AptApAccessDeviceRepository aptApAccessDeviceRepository;
    @Autowired
    private AptApFirmwareLogRepository aptApFirmwareLogRepository;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    PlatformTransactionManager txManager;


    /** @author shavrani 2016.06.17 */
    @Override
    public Map<String, Object> getAptApList(Map<String, Object> params) {

        String pageNum = StringUtil.nvl(params.get("pageNum"), "1");
        String pageSize = StringUtil.nvl(params.get("pageSize"), String.valueOf(Constants.DEFAULT_PAGE_SIZE));

        int nPageNum = Integer.parseInt(pageNum);
        int nPageSize = Integer.parseInt(pageSize);

        int startNum = (nPageNum - 1) * nPageSize;

        params.put("startNum", startNum);
        params.put("pageSize", nPageSize);

        String usePaging = StringUtil.nvl(params.get("usePaging"), "Y");
        List<AptAp> dataList = null;
        if ("N".equals(usePaging)) {
            // 페이징 없이 모두
            dataList = aptApMapper.selectAptApAllList(params);
        } else {
            // 페이징 처리
            dataList = aptApMapper.selectAptApList(params);
        }

        int totalCount = aptApMapper.selectAptApListCount(params);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("totalCount", totalCount);
        result.put("dataList", dataList);

        return result;
    }

    @Override
    public List<AptAp> getAptApAllList(Map<String, Object> params) {
        return aptApMapper.selectAptApAllList(params);
    }

    @Override
    public AptAp saveAptAp(AptAp aptAp) {
        return aptApRepository.save(aptAp);
    }

    @Override
    public AptAp getAptAp(Map<String, Object> params) {
        return aptApMapper.selectAptAp(params);
    }

    @Override
    public List<AptApAccessAuth> getAptApAccessAuthList(Long aptApId, String type) {

        List<AptApAccessAuth> resultList = new ArrayList<AptApAccessAuth>();

        AptAp aptAp = aptApRepository.findById(aptApId);
        if (aptAp.deactiveDate == null) {
            resultList = aptApAccessAuthRepository.findByAptApIdAndType(aptApId, type);
        }

        return resultList;
    }

    @Override
    public AptApAccessAuth getAptApAccessAuth(Long aptApId, String type, String dong, String ho, String hoType) {
        return aptApAccessAuthRepository.findByAptApIdAndTypeAndDongAndHoAndHoType(aptApId, type, dong, ho, hoType);
    }

    @Override
    public AptApAccessAuth getAptApAccessAuth(Long aptApId, String type, Long userId) {
        return aptApAccessAuthRepository.findByAptApIdAndTypeAndUserId(aptApId, type, userId);
    }

    @Override
    public AptApAccessAuth saveAptApAccessAuth(AptApAccessAuth aaaa) {
        return aptApAccessAuthRepository.save(aaaa);
    }

    @Override
    public void deleteAptApAccessAuth(Long id) {
        aptApAccessAuthRepository.delete(id);
    }

    @Override
    public void deleteAptApAccessAuthType(Long aptApId, String type) {
        aptApAccessAuthRepository.deleteByAptApIdAndType(aptApId, type);
    }

    @Override
    public void deleteAptApAccessAuthTypeDong(Long aptApId, String type, String dong) {
        aptApAccessAuthRepository.deleteByAptApIdAndTypeAndDong(aptApId, type, dong);
    }

    @Override
    public void deleteAptApAccessAuthTypeDongHoLine(Long aptApId, String type, String dong, String ho) {
        aptApAccessAuthRepository.deleteByAptApIdAndTypeAndDongAndHoEndingWith(aptApId, type, dong, ho);
    }

    @Override
    public void deleteAptAp(Long id) {
        aptApRepository.delete(id);
        aptApAccessAuthRepository.deleteByAptApId(id);
    }

    @Override
    public void deactiveAptAp(Long id) {
        AptAp aptAp = aptApRepository.findById(id);
        if (aptAp != null) {
            aptAp.deactiveDate = new Date();
            aptApRepository.save(aptAp);
        }
    }

    /** @author shavrani 2016.08.09 */
    @Override
    public Page<AptApAccessDevice> getAptApAccessDeviceList(Map<String, Object> params, Pageable pageable) {

        Page<AptApAccessDevice> page = null;

        String type = StringUtil.nvl(params.get("type"));
        String accessKey = StringUtil.nvl(params.get("accessKey"));
        String userNm = StringUtil.nvl(params.get("userNm"));
        Long aptId = StringUtil.nvlLong(params.get("aptId"));
        String deactive = StringUtil.nvl(params.get("_deactive"));

        Specifications<AptApAccessDevice> spec = Specifications.where(deactive(deactive)).and(aptIdEq(aptId));

        if (!"".equals(type)) {
            spec = spec.and(typeEq(type));
        }
        if (!"".equals(accessKey)) {
            spec = spec.and(accessKeyEq(accessKey));
        }
        if (!"".equals(userNm)) {
            spec = spec.and(userNmLike(userNm));
        }

        page = aptApAccessDeviceRepository.findAll(spec, pageable);

        return page;
    }

    @Override
    public AptApAccessDevice getAptApAccessDevice(Map<String, Object> params) {

        Long aptId = StringUtil.nvlLong(params.get("aptId"));
        String deactive = StringUtil.nvl(params.get("_deactive"));
        Long id = StringUtil.nvlLong(params.get("id"));

        Specifications<AptApAccessDevice> spec = Specifications.where(deactive(deactive)).and(aptIdEq(aptId)).and(idEq(id));

        return aptApAccessDeviceRepository.findOne(spec);
    }

    @Override
    public AptApAccessDevice existAptApAccessDevice(Map<String, Object> params) {
        Long id = StringUtil.nvlLong(params.get("id"));
        String accessKey = StringUtil.nvl(params.get("accessKey"));
        String deactive = "N";

        Specifications<AptApAccessDevice> spec = Specifications.where(deactive(deactive));

        if (id > 0) {
            spec = spec.and(idEq(id));
        }
        if (!"".equals(accessKey)) {
            spec = spec.and(accessKeyEq(accessKey));
        }

        return aptApAccessDeviceRepository.findOne(spec);
    }

    @Override
    public AptApAccessDevice saveAptApAccessDevice(AptApAccessDevice aptApAccessDevice) {
        return aptApAccessDeviceRepository.save(aptApAccessDevice);
    }

    @Override
    public int deleteAptApAccessDevice(Long id) {

        int result = 0;
        AptApAccessDevice aaad = aptApAccessDeviceRepository.findOne(id);
        if (aaad != null) {
            aaad.deactiveDate = new Date();
            result = 1;
            // transactional anotation 설정으로 jpa 자동 commit
        }
        return result;
    }

    @Override
    public Map<String, Object> aptApFirmwareUpdate(User user, List<String> apList, String[] firmwareList) {

        Map<String, Object> resultMap = Maps.newHashMap();
        List<Map<String, Object>> successList = Lists.newArrayList();
        List<Map<String, Object>> failList = Lists.newArrayList();

        // 파일명으로 파일 찾아서 매칭되는 파일만 업데이트 처리
        File dir = new File("/nas/EMaul/aptAp/firmware");
        if (dir.exists()) {
            List<File> fileList = new ArrayList<File>();
            File[] fileArray = dir.listFiles();
            if (fileArray != null) {
                for (File file : fileArray) {
                    if (file.isFile()) {
                        // firmwareList : 펌웨어 파일명
                        for (String str : firmwareList) {
                            if (StringUtil.nvl(str).equals(file.getName())) {
                                fileList.add(file);
                            }
                        }
                    }
                }
            }

            if (fileList.size() > 0) {

                Map<String, Object> params = Maps.newHashMap();
                params.put("_active", true);
                params.put("inApBeaconUuids", apList);

                List<AptAp> aptApList = aptApMapper.selectAptApAllList(params);

                // apList : 아파트 AP apBeaconUuid
                for (AptAp aptAp : aptApList) {

                    apList.remove(aptAp.apBeaconUuid);// 조회된 id를 parameter에서 지워서 조회되지 않은항목은 apList에 남김

                    if (StringUtil.isBlank(aptAp.expIp)) {
                        logger.info(" # AP id :  {}, apBeaconUuid : {} ] 의 ip 미설정", aptAp.id, aptAp.apBeaconUuid);
                        failList.add(setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "ip 미설정"));
                    } else {
                        Map<String, Object> uploadFiles = new HashMap<String, Object>();
                        uploadFiles.put("files", fileList);
                        String url = "http://" + aptAp.expIp + "/firmware/upload";

                        // url = "http://localhost:8888/api/public/ap/ip/update/multipart/file/test"; // 테스트 url

                        try {

                            String successYn = "N";

                            String responseJson = httpUtils.multipart(url, null, null, uploadFiles, 180);
                            if (!StringUtil.isBlank(responseJson)) {

                                if ("fail".equals(responseJson)) {
                                    logger.info(" # ap response is empty, update fail, id : {}, apBeaconUuid : {}", aptAp.id, aptAp.apBeaconUuid);
                                    failList.add(setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "AP와 통신실패"));
                                } else if (responseJson.startsWith("error")) {
                                    logger.info(" # ap response is http status error, update fail, id : {}, apBeaconUuid : {}", aptAp.id, aptAp.apBeaconUuid);
                                    failList.add(setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "http status error : " + responseJson));
                                } else {
                                    JSONObject jo = new JSONObject(responseJson);
                                    successYn = jo.getString("result");// 실패시에는 'N[실패이유]'형식으로 AP에서 리턴함.
                                    if ("Y".equals(successYn)) {
                                        successList.add(setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, ""));// 성공
                                    } else {
                                        logger.info(" # ap response is empty, update fail, id : {}, apBeaconUuid : {}", aptAp.id, aptAp.apBeaconUuid);
                                        failList.add(setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, successYn));
                                    }
                                }

                            }

                            // 펌웨어 로그 ( 파일별 )
                            for (int i = 0; i < fileList.size(); i++) {
                                File _file = fileList.get(i);
                                AptApFirmwareLog aafl = new AptApFirmwareLog();
                                aafl.apId = aptAp.id;
                                aafl.fileName = _file.getName();
                                aafl.firmwareVersion = "";
                                aafl.fileLastModified = new Date(_file.lastModified());
                                aafl.successYn = successYn.equals("Y") ? "Y" : "N";
                                aafl.memo = "";
                                aafl.regUser = user.id;
                                aafl.regDate = new Date();

                                aptApFirmwareLogRepository.save(aafl);
                            }

                        } catch (Exception e) {
                            logger.info(" # ap firmware file upload fail", e);
                            failList.add(setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "update fail, exception"));
                        }
                    }
                }

                for (int i = 0; i < apList.size(); i++) {
                    failList.add(setAptApUpdateResult(null, apList.get(i), "조회되지 않은 AP정보"));
                }

                resultMap.put("successList", successList);
                resultMap.put("failList", failList);

            }
        }

        return resultMap;
    }

    /**
     * aptApSystemDataUpdate, aptApFirmwareUpdate의 결과정리
     */
    private Map<String, Object> setAptApUpdateResult(Long id, String apBeaconUuid, String message) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("id", id);
        result.put("apBeaconUuid", apBeaconUuid);
        result.put("message", message);
        return result;
    }

    @Override
    public Map<String, Object> aptApSystemDataUpdate(User user, List<String> apList, Integer rssi, Integer keepon, Integer gpiodelay) {

        Map<String, Object> resultMap = Maps.newHashMap();
        List<Map<String, Object>> successList = Lists.newArrayList();
        List<Map<String, Object>> failList = Lists.newArrayList();

        Map<String, Object> params = Maps.newHashMap();
        params.put("_active", true);
        params.put("inApBeaconUuids", apList);

        List<AptAp> aptApList = aptApMapper.selectAptApAllList(params);

        // apList : 아파트 AP apBeaconUuid
        for (AptAp aptAp : aptApList) {

            apList.remove(aptAp.apBeaconUuid);// 조회된 id를 parameter에서 지워서 조회되지 않은항목은 apList에 남김

            // ap 별로 transaction 잡아 개별 처리.
            Map<String, Object> apResultMap = aptApSystemDataUpdateTransaction(aptAp, user, rssi, keepon, gpiodelay);

            if ("Y".equals(StringUtil.nvl(apResultMap.get("successYn")))) {
                successList.add(apResultMap);
            } else {
                failList.add(apResultMap);
            }

        }

        for (int i = 0; i < apList.size(); i++) {
            failList.add(setAptApUpdateResult(null, apList.get(i), "조회되지 않은 AP정보"));
        }

        resultMap.put("successList", successList);
        resultMap.put("failList", failList);

        return resultMap;
    }

    /**
     * aptApSystemDataUpdate의 AP별 transaction method
     */
    public Map<String, Object> aptApSystemDataUpdateTransaction(AptAp aptAp, User user, Integer rssi, Integer keepon, Integer gpiodelay) {

        Map<String, Object> resultMap = Maps.newHashMap();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(15000);
        factory.setConnectTimeout(15000);
        RestTemplate restTemplate = new RestTemplate(factory);

        if (StringUtil.isBlank(aptAp.expIp)) {
            logger.info(" # AP id :  {}, apBeaconUuid : {} ] 의 ip 미설정", aptAp.id, aptAp.apBeaconUuid);
            resultMap = setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "ip 미설정");
        } else {
            String url = "";
            int urlType = 0;

            int rssi_value = StringUtil.nvlInt(rssi);
            int keepon_value = StringUtil.nvlInt(keepon);

            // rssi( minus 값 ), keepon ( 밀리세컨 ) 둘다 존재
            if (rssi_value < 0 && keepon_value > 0) {
                if (!rssi.equals(aptAp.rssi) && !keepon.equals(aptAp.keepon)) {
                    // 둘다 기존정보와 다를경우 둘다 변경하는 url로 호출
                    url = "http://" + aptAp.expIp + "/firmware/set/" + rssi + "/" + keepon;
                    aptAp.rssi = rssi;
                    aptAp.keepon = keepon;
                    urlType = 1;
                } else if (!rssi.equals(aptAp.rssi)) {
                    // rssi만 기존정보와 다를경우 rssi만 url로 호출
                    url = "http://" + aptAp.expIp + "/door/action/beacon/rssi/" + rssi;
                    aptAp.rssi = rssi;
                    urlType = 2;
                } else if (!keepon.equals(aptAp.keepon)) {
                    // keepon만 기존정보와 다를경우 keepon만 url로 호출
                    url = "http://" + aptAp.expIp + "/door/action/doorkeepon/" + keepon;
                    aptAp.keepon = keepon;
                    urlType = 3;
                }
            } else if (rssi_value < 0) {
                if (!rssi.equals(aptAp.rssi)) {
                    // rssi만 기존정보와 다를경우 rssi만 url로 호출
                    url = "http://" + aptAp.expIp + "/door/action/beacon/rssi/" + rssi;
                    aptAp.rssi = rssi;
                    urlType = 2;
                }
            } else if (keepon_value > 0) {
                if (!keepon.equals(aptAp.keepon)) {
                    // keepon만 기존정보와 다를경우 keepon만 url로 호출
                    url = "http://" + aptAp.expIp + "/door/action/doorkeepon/" + keepon;
                    aptAp.keepon = keepon;
                    urlType = 3;
                }
            }

            if (gpiodelay != null) {
                if (!gpiodelay.equals(aptAp.gpiodelay)) {
                    // ap와 통신은 일단 보류
                    // url = "http://" + aptAp.expIp + "/door/action/doorgpiodelay/" + gpiodelay;
                    aptAp.gpiodelay = gpiodelay;
                    // urlType = 3;
                }
            }

            // url = "http://localhost:8888/api/public/ap/ip/update/test/" + rssi + "/" + keepon;
            // urlType = 1;

            if (StringUtil.isBlank(url)) {
                resultMap = setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "설정하려는값이 이미설정되어있거나 설정할수없는 값이어서 skip 처리");
            } else {

                TransactionStatus txStatus = txManager.getTransaction(new DefaultTransactionDefinition());

                try {

                    aptAp.modId = user.id;
                    int dbUpdateResult = aptApMapper.updateAptAp(aptAp);

                    if (dbUpdateResult > 0) {
                        logger.info(" # " + url + " connection start");
                        Map responseJson = restTemplate.getForObject(url, Map.class);

                        if (responseJson == null) {
                            logger.info(" # ap system data response is empty, update fail, id : {}, apBeaconUuid : {}, expIp : {}", aptAp.id, aptAp.apBeaconUuid, aptAp.expIp);
                            resultMap = setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "AP와 통신결과 응답없음");
                            txManager.rollback(txStatus);
                        } else {

                            String successYn = "N";

                            if (urlType == 1) {
                                if ("Y".equals(StringUtil.nvl(responseJson.get("result")))) {
                                    successYn = "Y";
                                }
                            } else if (urlType == 2) {
                                if ("success".equals(StringUtil.nvl(responseJson.get("data")))) {
                                    successYn = "Y";
                                }
                            } else if (urlType == 3) {
                                if ("success".equals(StringUtil.nvl(responseJson.get("data")))) {
                                    successYn = "Y";
                                }
                            }

                            if ("Y".equals(successYn)) {
                                logger.info(" # ap system data update success, id : {}, apBeaconUuid : {}, expIp : {}", aptAp.id, aptAp.apBeaconUuid, aptAp.expIp);
                                resultMap = setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "");// 성공
                                resultMap.put("successYn", successYn);// 성공 flag
                                txManager.commit(txStatus);
                            } else {
                                logger.info(" # ap system data update response is fail, update fail, id : {}, apBeaconUuid : {}, expIp : {}", aptAp.id, aptAp.apBeaconUuid, aptAp.expIp);
                                resultMap = setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "AP 통신결과 저장실패응답받음");// 실패
                                txManager.rollback(txStatus);
                            }
                        }
                    } else {
                        logger.info(" # ap system data database update query fail, id : {}, apBeaconUuid : {}", aptAp.id, aptAp.apBeaconUuid);
                        resultMap = setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "database update query fail");
                        txManager.rollback(txStatus);
                    }

                } catch (Exception e) {
                    txManager.rollback(txStatus);
                    logger.info(" # ap ip connection fail, update rollback !!", e);
                    resultMap = setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "ap ip connection fail, exception");
                }
            }
        }

        return resultMap;
    }

    /**
     * 아파트 변경 ( 기존AP를 비활성화 시키고 동일한 정보에 아파트만 새아파트로 지정후 생성. ( 권한은 복사하지않음. ) )
     */
    @Override
    public Long changeApt(User user, Long id, Long aptId) {
        Long result = 0L;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("_active", true);
        AptAp aptAp = getAptAp(params);
        if (aptAp == null) {
            return result;
        }

        params.clear();
        params.put("_aptId", aptId);
        List<Map<String, Object>> aptList = commonService.selectAddressAptList(params); // apt가 존재하는지 체크
        if (aptList.size() == 0) {
            return result;
        }

        // 기존 데이터는 비활성화처리.
        aptAp.modId = user.id;
        aptAp.deactiveDate = new Date();
        AptAp deactiveAptAp = saveAptAp(aptAp);

        if (deactiveAptAp != null) {

            // 아파트만 변경하고 기존과 동일한 데이터로 저장
            AptAp tempAptAp = new AptAp();

            try {
                tempAptAp = aptAp.clone();
                tempAptAp.id = null;
                tempAptAp.deactiveDate = null;
                tempAptAp.modId = null;
                tempAptAp.regId = user.id;
                tempAptAp.aptId = aptId;

                AptAp saveAptAp = saveAptAp(tempAptAp);
                if (saveAptAp != null) {
                    result = saveAptAp.id;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> aptApSystemRemoteControl(User user, List<String> apList, String fwVersion, List<String> fwTypeList, List<String> cmdList, List<String> pathList,
            List<MultipartFile> fileList) {

        Map<String, Object> resultMap = Maps.newHashMap();
        List<Map<String, Object>> successList = Lists.newArrayList();
        List<Map<String, Object>> failList = Lists.newArrayList();

        // cmd는 빈값들은 제거한다.
        if (cmdList != null && !cmdList.isEmpty()) {
            cmdList = cmdList.stream().filter(a -> !StringUtil.isEmpty(a)).collect(Collectors.toList());
            cmdList.forEach(cmd -> {
                try {
                    cmd = URLEncoder.encode(cmd, "utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put("_active", true);
        params.put("inApBeaconUuids", apList);

        List<AptAp> aptApList = aptApMapper.selectAptApAllList(params);

        for (AptAp aptAp : aptApList) {

            apList.remove(aptAp.apBeaconUuid);// 조회된 id를 parameter에서 지워서 조회되지 않은항목은 apList에 남김

            if (StringUtil.isBlank(aptAp.expIp)) {
                logger.info(" # AP id :  {}, apBeaconUuid : {} ] 의 ip 미설정", aptAp.id, aptAp.apBeaconUuid);
                failList.add(setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "ip 미설정"));
            } else {

                String url = "http://" + aptAp.expIp + "/system/fwbatch";

                // url = "http://localhost:8888/api/public/ap/ip/update/multipart/file/test2";// 테스트 URL

                Map<String, Object> multiParams = Maps.newHashMap();
                multiParams.put("fw_version", fwVersion);
                multiParams.put("fw_type", fwTypeList);
                multiParams.put("cmd", cmdList);
                multiParams.put("path", pathList);

                Map<String, Object> fileParams = Maps.newHashMap();
                fileParams.put("files", fileList);

                try {
                    String responseJson = httpUtils.multipart(url, null, multiParams, fileParams, 180);
                    if (!StringUtil.isBlank(responseJson)) {

                        if ("fail".equals(responseJson)) {
                            logger.info(" # ap system remote control fail, id : {}, apBeaconUuid : {}", aptAp.id, aptAp.apBeaconUuid);
                            failList.add(setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "AP와 통신실패"));
                        } else if (responseJson.startsWith("error")) {
                            logger.info(" # ap response is http status error, update fail, id : {}, apBeaconUuid : {}", aptAp.id, aptAp.apBeaconUuid);
                            failList.add(setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "http status error : " + responseJson));
                        } else {
                            JSONObject jo = new JSONObject(responseJson);
                            String result = jo.getString("result");// 실패시에는 'N[실패이유]'형식으로 AP에서 리턴함.
                            if ("Y".equals(result)) {
                                logger.info("<< ap system remote control success, id : {}, apBeaconUuid : {} >>", aptAp.id, aptAp.apBeaconUuid);// 성공

                                // 원격설정 정상처리후 db의 firmware를 업데이트처리함.
                                aptAp.modId = user.id;
                                aptAp.firmwareVersion = fwVersion;
                                aptApMapper.updateAptApFirmwareVersion(aptAp);

                                successList.add(setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, ""));// 성공

                            } else {
                                logger.info(" # ap system remote control response fail, id : {}, apBeaconUuid : {}", aptAp.id, aptAp.apBeaconUuid);
                                failList.add(setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, result));
                            }
                        }

                    }
                } catch (Exception e) {
                    logger.info("<< apt ap system remote control exception >>", e);
                    failList.add(setAptApUpdateResult(aptAp.id, aptAp.apBeaconUuid, "apt ap system remote control fail, exception"));
                }
            }
        }

        for (int i = 0; i < apList.size(); i++) {
            failList.add(setAptApUpdateResult(null, apList.get(i), "조회되지 않은 AP정보"));
        }

        resultMap.put("successList", successList);
        resultMap.put("failList", failList);

        return resultMap;
    }


    @Override
    public List<AptApAccessLog> selectAptApAccessLogList(Map<String, Object> params) {
        return aptApMapper.selectAptApAccessLogList(params);
    }

    @Override
    public int selectAptApAccessLogListCount(Map<String, Object> params) {
        return aptApMapper.selectAptApAccessLogListCount(params);
    }

    @Override
    public List<AptApAccessLog> selectAptApAccessLogInspectionDetailList(Map<String, Object> params) {

        List<Long> qcUserList = Lists.newArrayList(0L);// qc test user id
        List<Long> fieldUserList = Lists.newArrayList(0L);// field test user id

        List<SimpleUser> qcUserCodeList = selectAptApInspAccountList("QC");// qc 테스트 계정 list
        List<SimpleUser> fieldUserCodeList = selectAptApInspAccountList("FD");// field 테스트 계정 list

        if (qcUserCodeList != null && !qcUserCodeList.isEmpty()) {
            qcUserList.clear();
            for (SimpleUser simpleUser : qcUserCodeList) {
                Long id = StringUtil.nvlLong(simpleUser.id, null);
                if (id != null) {
                    qcUserList.add(id);
                }
            }
        }

        if (fieldUserCodeList != null && !fieldUserCodeList.isEmpty()) {
            fieldUserList.clear();
            for (SimpleUser simpleUser : fieldUserCodeList) {
                Long id = StringUtil.nvlLong(simpleUser.id, null);
                if (id != null) {
                    fieldUserList.add(id);
                }
            }
        }

        params.put("testAptId", Constants.AP_TEST_APT_ID);// 테스트하는 아파트 : 서울시 창업지원센터 apt id
        params.put("qcUserList", qcUserList);
        params.put("fieldUserList", fieldUserList);

        return aptApMapper.selectAptApAccessLogInspectionDetailList(params);
    }

    @Override
    public List<Map<String, Object>> selectAptApAccessLogInspectionList(Map<String, Object> params) {

        List<Long> qcUserList = Lists.newArrayList(0L);// qc test user id
        List<Long> fieldUserList = Lists.newArrayList(0L);// field test user id

        List<SimpleUser> qcUserCodeList = selectAptApInspAccountList("QC");// qc 테스트 계정 list
        List<SimpleUser> fieldUserCodeList = selectAptApInspAccountList("FD");// field 테스트 계정 list

        if (qcUserCodeList != null && !qcUserCodeList.isEmpty()) {
            qcUserList.clear();
            for (SimpleUser simpleUser : qcUserCodeList) {
                Long id = StringUtil.nvlLong(simpleUser.id, null);
                if (id != null) {
                    qcUserList.add(id);
                }
            }
        }

        if (fieldUserCodeList != null && !fieldUserCodeList.isEmpty()) {
            fieldUserList.clear();
            for (SimpleUser simpleUser : fieldUserCodeList) {
                Long id = StringUtil.nvlLong(simpleUser.id, null);
                if (id != null) {
                    fieldUserList.add(id);
                }
            }
        }

        params.put("testAptId", Constants.AP_TEST_APT_ID);// 테스트하는 아파트 : 서울시 창업지원센터 apt id
        params.put("qcUserList", qcUserList);
        params.put("fieldUserList", fieldUserList);

        return aptApMapper.selectAptApAccessLogInspectionList(params);
    }

    @Override
    public List<SimpleUser> selectAptApInspAccountList(String type) {
        return aptApMapper.selectAptApInspAccountList(type);
    }

    @Override
    @Transactional
    public int saveInspAccount(List<Map<String, Object>> inspList) {
        // 추가없이 전부 삭제만 할수도있기때문에 result는 삭제 포함 count 한다.
        int result = aptApMapper.deleteInspAccount("QC");
        result += aptApMapper.deleteInspAccount("FD");
        for (Map<String, Object> item : inspList) {
            result += aptApMapper.saveInspAccount(item);
        }
        return result;
    }

    @Override
    @Transactional
    public int saveInspMailAccount(List<Map<String, Object>> inspList) {
        // 추가없이 전부 삭제만 할수도있기때문에 result는 삭제 포함 count 한다.
        int result = aptApMapper.deleteInspAccount("MAIL");
        for (Map<String, Object> item : inspList) {
            result += aptApMapper.saveInspAccount(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> selectAptApInspDailyList(Map<String, Object> params) {
        return aptApMapper.selectAptApInspDailyList(params);
    }

    @Override
    public int selectAptApInspDailyListCount(Map<String, Object> params) {
        return aptApMapper.selectAptApInspDailyListCount(params);
    }

    @Override
    public List<Map<String, Object>> selectAptApInspDataLimitList(Map<String, Object> params) {
        return aptApMapper.selectAptApInspDataLimitList(params);
    }

    @Override
    public int selectAptApInspDataLimitListCount(Map<String, Object> params) {
        return aptApMapper.selectAptApInspDataLimitListCount(params);
    }

    @Override
    public List<Map<String, Object>> selectAptApInspNotiList(Map<String, Object> params) {
        return aptApMapper.selectAptApInspNotiList(params);
    }

    @Override
    public int selectAptApInspNotiListCount(Map<String, Object> params) {
        return aptApMapper.selectAptApInspNotiListCount(params);
    }

    @Override
    public List<Map<String, Object>> selectAptApInspWarningAptList(Map<String, Object> params) {
        return aptApMapper.selectAptApInspWarningAptList(params);
    }

    @Override
    public int selectAptApInspWarningAptListCount(Map<String, Object> params) {
        return aptApMapper.selectAptApInspWarningAptListCount(params);
    }

    @Override
    public List<Map<String, Object>> selectAptApInspNotiWarningAptList(Map<String, Object> params) {
        return aptApMapper.selectAptApInspNotiWarningAptList(params);
    }

    @Override
    public int selectAptApInspNotiWarningAptListCount(Map<String, Object> params) {
        return aptApMapper.selectAptApInspNotiWarningAptListCount(params);
    }

    @Override
    public List<AptApMonitoringNoti> selectAptApMonitoringNotiList(Map<String, Object> params) {
        return aptApMapper.selectAptApMonitoringNotiList(params);
    }

    @Override
    public int saveSettingDataLimit(String[] code, String[] dataLimit, String[] warningPercent) {

        int result = 0;

        if (code == null || code.length == 0) {
            return 0;
        }

        String codeGroup = "DATA_LIMIT";

        Map<String, Object> params = Maps.newHashMap();
        params.put("codeGroup", codeGroup);
        commonService.deleteCode(params);

        for (int i = 0; i < code.length; i++) {
            CommonCode ccd = new CommonCode();
            ccd.codeGroup = codeGroup;
            ccd.code = code[i];
            ccd.name = code[i];
            ccd.data1 = dataLimit[i];
            ccd.data2 = warningPercent[i];
            ccd.sort_order = 1;
            ccd.depth = 1;
            ccd.use_yn = "Y";
            result += commonService.insertCode(ccd);
        }

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> saveAptApAccessAuthFile(User user, MultipartFile file, Long authSaveAptId) {

        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> failList = Lists.newArrayList();
        result.put("failList", failList);
        result.put("resultYn", "Y");
        result.put("resultMsg", "success");

        if (authSaveAptId == null) {
            logger.info("<< edoorService.saveAptApAccessAuthFile apt id is null  >>");
            result.put("resultYn", "N");
            result.put("resultMsg", "missing apt id");
        } else if (file == null) {
            logger.info("<< edoorService.saveAptApAccessAuthFile File is null  >>");
            result.put("resultYn", "N");
            result.put("resultMsg", "missing file");
        } else {
            if (file.isEmpty()) {
                logger.info("<< edoorService.saveAptApAccessAuthFile file이 비어있음 >>");
                result.put("resultYn", "N");
                result.put("resultMsg", "empty file");
            } else {
                String ext = StringUtil.nvl(FilenameUtils.getExtension(file.getOriginalFilename())).toLowerCase();
                if ("xls".equals(ext) || "xlsx".equals(ext)) {

                    try {
                        logger.info("<< ----- apt ap access auth excel upload start ----- >>");
                        Workbook wb = WorkbookFactory.create(file.getInputStream());

                        int sheetCnt = wb.getNumberOfSheets();

                        logger.info("<< excel sheet count : {} >>", sheetCnt);
                        for (int i = 0; i < sheetCnt; i++) {

                            Sheet sheet = wb.getSheetAt(i);

                            logger.info("<< sheet read start, sheet name : {} >>", sheet.getSheetName());

                            // 숨김시트는 skip 처리
                            if (wb.isSheetHidden(i) == true) {
                                logger.info("<< sheet is hidden. skip sheet, sheet name : {} >>", sheet.getSheetName());
                                continue;
                            }

                            int sDataRow = 4;
                            int sDataCell = 1;
                            String apId = "";// while문 시작시 진행잘수있도록 공백.

                            while (true) {
                                sDataCell = 1;

                                Row row = sheet.getRow(sDataRow++);

                                if (row == null) {
                                    logger.info("<< row is null, row가 없어 시트 reading 종료>>", sheet.getSheetName());
                                    break;
                                } else {

                                    Cell cell = row.getCell(sDataCell++);
                                    apId = StringUtil.nvl(PoiUtil.getCellValue(cell), null);// empty 일경우 while문 조건에 false되도록 null처리.

                                    cell = row.getCell(sDataCell++);
                                    String dong = PoiUtil.getCellValue(cell);
                                    cell = row.getCell(sDataCell++);
                                    String floor = PoiUtil.getCellValue(cell);
                                    cell = row.getCell(sDataCell++);
                                    String line = PoiUtil.getCellValue(cell);

                                    logger.info("<< [ excel row data ] excel row no : {}, apId : {}, dong : {}, floor : {}, line : {} >>", row.getRowNum(), apId, dong, floor, line);

                                    if (StringUtil.isBlank(apId)) {
                                        logger.info("<< sheet name : {}, apId empty : apId가 없어 시트 reading 종료>>", sheet.getSheetName());// apid가 없는것은 종료된것으로 간주
                                        break;
                                    }
                                    if (StringUtil.isBlank(dong)) {
                                        logger.info("<< dong empty >>");
                                        Map<String, Object> failItem = Maps.newHashMap();
                                        failItem.put("apId", apId);
                                        failItem.put("msg", "dong empty");
                                        failItem.put("sheetName", sheet.getSheetName());
                                        failItem.put("rowNo", row.getRowNum() + 1);
                                        failList.add(failItem);
                                        continue;
                                    }
                                    if (StringUtil.isBlank(floor)) {
                                        logger.info("<< floor empty >>");
                                        Map<String, Object> failItem = Maps.newHashMap();
                                        failItem.put("apId", apId);
                                        failItem.put("msg", "floor empty");
                                        failItem.put("sheetName", sheet.getSheetName());
                                        failItem.put("rowNo", row.getRowNum() + 1);
                                        failList.add(failItem);
                                        continue;
                                    }
                                    if (StringUtil.isBlank(line)) {
                                        logger.info("<< line empty >>");
                                        Map<String, Object> failItem = Maps.newHashMap();
                                        failItem.put("apId", apId);
                                        failItem.put("msg", "line empty");
                                        failItem.put("sheetName", sheet.getSheetName());
                                        failItem.put("rowNo", row.getRowNum() + 1);
                                        failList.add(failItem);
                                        continue;
                                    }

                                    List<SimpleUser> gcmUserList = Lists.newArrayList();

                                    Map<String, Object> params = Maps.newHashMap();
                                    params.put("aptId", authSaveAptId);
                                    params.put("apId", apId);
                                    params.put("_active", true);
                                    AptAp aptAp = getAptAp(params);

                                    if (aptAp == null) {
                                        logger.info("<< AP가 존재하지않음. ap id : {}  >>", apId);
                                        Map<String, Object> failItem = Maps.newHashMap();
                                        failItem.put("apId", apId);
                                        failItem.put("msg", "AP가 존재하지않음");
                                        failItem.put("sheetName", sheet.getSheetName());
                                        failItem.put("rowNo", row.getRowNum() + 1);
                                        failList.add(failItem);
                                    } else {

                                        // 해당 AP의 아파트에 입력된 동이 존재하는지 확인 ( 확인체크 하지않음. )
                                        // List<String> dongList = houseService.getDongs(aptAp.aptId);
                                        // if (!dongList.contains(dong)) {
                                        // logger.info("<< AP의 아파트에 해당 동이 존재하지않음. ap id : {}, apt id : {}, dong : {} >>", aptAp.id, aptAp.aptId, dong);
                                        // }

                                        int nIdx = line.indexOf("(");
                                        String hoLines = nIdx == -1 ? line : line.substring(0, nIdx);

                                        String[] arrHoLine = hoLines.split("~");
                                        List<String> hoLineList = Lists.newArrayList();
                                        if (arrHoLine != null) {
                                            int size = arrHoLine.length;
                                            // 호라인은 1개와 2개 까지만 처리 ( 한개는 단일, 두개는 범위 )
                                            if (size == 1) {
                                                String ho = StringUtil.nvl(arrHoLine[0]).trim();

                                                if ("ALL".equals(ho.toUpperCase())) {
                                                    hoLineList.add("All");// 권한저장시 ho 전체는 'All'으로 입력
                                                } else {
                                                    Integer sHo = StringUtil.nvlInt(ho, null);
                                                    if (sHo == null) {
                                                        Map<String, Object> failItem = Maps.newHashMap();
                                                        failItem.put("apId", apId);
                                                        failItem.put("msg", "line 데이터오류");
                                                        failItem.put("sheetName", sheet.getSheetName());
                                                        failItem.put("rowNo", row.getRowNum() + 1);
                                                        failList.add(failItem);
                                                    } else {
                                                        hoLineList.add(StringUtil.nvl(sHo));
                                                    }
                                                }
                                            } else if (size == 2) {
                                                Integer sHo = StringUtil.nvlInt(StringUtil.nvl(arrHoLine[0]).trim(), null);
                                                Integer eHo = StringUtil.nvlInt(StringUtil.nvl(arrHoLine[1]).trim(), null);
                                                if (sHo == null || eHo == null) {
                                                    Map<String, Object> failItem = Maps.newHashMap();
                                                    failItem.put("apId", apId);
                                                    failItem.put("msg", "line 데이터오류");
                                                    failItem.put("sheetName", sheet.getSheetName());
                                                    failItem.put("rowNo", row.getRowNum() + 1);
                                                    failList.add(failItem);
                                                } else {
                                                    // 순서가 앞뒤가 바뀌었어도 고저 체크하여 진행될수있게 처리
                                                    int start = Math.min(sHo, eHo);
                                                    int end = Math.max(sHo, eHo);
                                                    for (int ii = start; ii <= end; ii++) {
                                                        hoLineList.add(StringUtil.nvl(ii));
                                                    }
                                                }
                                            } else {
                                                Map<String, Object> failItem = Maps.newHashMap();
                                                failItem.put("apId", apId);
                                                failItem.put("msg", "line 데이터오류");
                                                failItem.put("sheetName", sheet.getSheetName());
                                                failItem.put("rowNo", row.getRowNum() + 1);
                                                failList.add(failItem);
                                            }
                                        }

                                        for (int ii = 0; ii < hoLineList.size(); ii++) {
                                            AptApAccessAuth aaaa = new AptApAccessAuth();
                                            aaaa.aptApId = aptAp.id;
                                            aaaa.type = "1";// 1:아파트동호단위 출입권한, 2:사용자단위 출입권한
                                            aaaa.dong = dong;
                                            aaaa.ho = hoLineList.get(ii);
                                            aaaa.hoType = "1";// 1: 라인단위 ho, 2:단일 호
                                            if ("1".equals(aaaa.hoType) && !"All".equals(aaaa.dong)) {
                                                aaaa.hoLineDigit = "1";// 1:한자리, 2:두자리
                                            }
                                            aaaa.regId = user.id;

                                            AptApAccessAuth existAaaa = getAptApAccessAuth(aptAp.id, aaaa.type, dong, aaaa.ho, aaaa.hoType);
                                            if (existAaaa == null) {
                                                List<AptApAccessAuth> aptApAccessAuthList = getAptApAccessAuthList(aptAp.id, aaaa.hoType);

                                                if (aptApAccessAuthList.size() == 1) {
                                                    AptApAccessAuth existCheckData = aptApAccessAuthList.get(0);
                                                    if ("All".equals(existCheckData.dong)) {
                                                        // db data에 이미 전체가 저장되어있으면 다른 데이터는 입력받을 수 없음. continue하여 skip
                                                        continue;
                                                    }
                                                }

                                                if ("All".equals(dong)) {
                                                    // 동이 전체이면 all 하나만 저장하게 모두삭제 처리
                                                    deleteAptApAccessAuthType(aptAp.id, aaaa.type);

                                                    // gcm 보낼 사람 목록
                                                    params.clear();
                                                    params.put("aptId", aptAp.aptId);
                                                    params.put("_active", true);
                                                    params.put("_notiEdoor", true);
                                                    gcmUserList.addAll(userService.selectUserList(params));
                                                }

                                                if (aptApAccessAuthList.size() > 0) {
                                                    boolean existAll = false;
                                                    for (int j = 0; j < aptApAccessAuthList.size(); j++) {
                                                        AptApAccessAuth existCheckData = aptApAccessAuthList.get(j);

                                                        // 동의 호가 전체인항목이 이미있으면 해당동의 호는 입력불가.
                                                        if (existCheckData.dong.equals(aaaa.dong) && "All".equals(existCheckData.ho)) {
                                                            existAll = true;
                                                            break;
                                                        }

                                                        // 입력된 데이터 호가 조회한 데이터의 호라인에 포함되면 입력불가.
                                                        if ("2".equals(aaaa.hoType)) {
                                                            if (existCheckData.dong.equals(aaaa.dong) && "1".equals(existCheckData.hoType)) {
                                                                int hoLineDigit = Integer.valueOf(existCheckData.hoLineDigit);// hoType이 1이면 hoLineDigit는 숫자값이 필수로 존재함.
                                                                String hoStr = aaaa.ho.substring(aaaa.ho.length() - hoLineDigit, aaaa.ho.length());
                                                                if (existCheckData.ho.equals(hoStr)) {
                                                                    existAll = true;
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (existAll == true) {
                                                        // ho만 전체. 다른 데이터는 진행하도록 continue
                                                        // result = "3";
                                                        continue;
                                                    }
                                                }

                                                /** 호 라인으로 설정한 데이터면 해당 호라인이 적용되는 데이터는 삭제 */
                                                if (!"All".equals(aaaa.dong) && "1".equals(aaaa.hoType)) {
                                                    deleteAptApAccessAuthTypeDongHoLine(aptAp.id, aaaa.type, aaaa.dong, aaaa.ho);

                                                    // gcm 보낼 사람 목록
                                                    params.clear();
                                                    params.put("aptId", aptAp.aptId);
                                                    params.put("dong", aaaa.dong);
                                                    params.put("hoLine", aaaa.ho);
                                                    params.put("_active", true);
                                                    params.put("_notiEdoor", true);
                                                    gcmUserList.addAll(userService.selectUserList(params));
                                                }

                                                if (!"All".equals(aaaa.dong) && "2".equals(aaaa.hoType)) {
                                                    // gcm 보낼 사람 목록
                                                    params.clear();
                                                    params.put("aptId", aptAp.aptId);
                                                    params.put("dong", aaaa.dong);
                                                    params.put("ho", aaaa.ho);
                                                    params.put("_active", true);
                                                    params.put("_notiEdoor", true);
                                                    gcmUserList.addAll(userService.selectUserList(params));
                                                }

                                                /** 호가 all로 설정한 데이터면 해당 동의 호설정은 모두삭제 */
                                                if (!"All".equals(aaaa.dong) && "All".equals(aaaa.ho)) {
                                                    deleteAptApAccessAuthTypeDong(aptAp.id, aaaa.type, aaaa.dong);

                                                    // gcm 보낼 사람 목록
                                                    params.clear();
                                                    params.put("aptId", aptAp.aptId);
                                                    params.put("dong", aaaa.dong);
                                                    params.put("_active", true);
                                                    params.put("_notiEdoor", true);
                                                    gcmUserList.addAll(userService.selectUserList(params));
                                                }

                                            } else {
                                                // 중복데이터면 skip
                                                continue;
                                            }

                                            saveAptApAccessAuth(aaaa);

                                        }

                                        if (hoLineList != null && hoLineList.size() > 0) {
                                            // ap 이름 업데이트
                                            aptAp.apName = dong + " " + floor + " " + line;
                                            saveAptAp(aptAp);
                                        }

                                    }

                                }

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        result.put("resultYn", "N");
                        result.put("resultMsg", "exception");
                    }
                } else {
                    logger.info("<< edoorService.saveAptApAccessAuthFile Excel 파일이 아니어서 종료 >>");
                    result.put("resultYn", "N");
                    result.put("resultMsg", "not excel file");
                }
            }
        }

        logger.info("<< ----- apt ap access auth excel upload end ----- >>");

        return result;
    }

    @Override
    public int aptApApplyApt(User user, Long[] apList, Long aptId) {

        // transaction 설정하면 안됨. 설정하면 jpa, mybatis 혼용하고있어 데이터 저장시점이 달라 gcm 보낼목록을 처리할수없음.

        int result = -1;

        if (apList == null || apList.length == 0) {
            return result;
        }

        List<SimpleUser> gcmUserList = Lists.newArrayList();

        Map<String, Object> params = Maps.newHashMap();
        for (int i = 0; i < apList.length; i++) {
            Long apId = apList[i];

            params.clear();
            params.put("id", apId);
            params.put("aptSearchType", "emptyNtestApt");// 아파트가 미지정된or 테스트아파트 AP인지 체크
            params.put("testAptId", Constants.AP_TEST_APT_ID);
            params.put("_active", true);
            AptAp aptAp = getAptAp(params);
            if (aptAp == null) {
                return result;
            }

            params.clear();
            params.put("_aptId", aptId);
            List<Map<String, Object>> aptList = commonService.selectAddressAptList(params); // apt가 존재하는지 체크
            if (aptList.size() == 0) {
                return result;
            }


            AptAp saveAptAp = null;
            if (Constants.AP_TEST_APT_ID.equals(aptAp.aptId)) {
                // 테스트아파트를 변경하는것이면 기존 아파트는 비활성화처리후 동일한 정보로 새로생성
                Long newId = changeApt(user, aptAp.id, aptId);
                params.clear();
                params.put("id", newId);
                params.put("_active", true);
                saveAptAp = getAptAp(params);
                System.out.println(" ### 테스트아파트 AP 저장 : " + newId);
            } else {
                // 미지정의 아파트를 변경하는것이면 아파트 id만 입력
                aptAp.aptId = aptId;
                aptAp.modId = user.id;
                saveAptAp = saveAptAp(aptAp);
                System.out.println(" ### 미지정 AP 저장 : " + aptAp.id);
            }


            if (saveAptAp != null) {
                result++;
                // gcm 보낼사람 목록
                String skipAuth = StringUtil.nvl(aptAp.skipAuth);
                List<String> aptApList = Splitter.on(",").splitToList(skipAuth);

                params.clear();
                params.put("aptId", aptId);
                params.put("_active", true);
                params.put("_notiEdoor", true);
                if (aptApList.contains("admin")) {
                    params.put("_admin", true);
                    gcmUserList.addAll(userService.selectUserList(params));
                }
                if (aptApList.contains("gasChecker")) {
                    params.remove("_admin");
                    params.put("_gasChecker", true);
                    gcmUserList.addAll(userService.selectUserList(params));
                }
            }
        }

        // String androidValue = "e-door 기기의 정보가 변경되었습니다.\ne-door 정보 갱신을 위해\n재로그인 또는 앱을 종료후 다시 시작해주세요.";
        // String iosValue = "e-door 기기의 정보가 변경되었습니다.\n여기를 터치하여 e-door 정보를 갱신해주세요.";
        // gcmService.sendGcmFunction("", androidValue, iosValue, Lists.newArrayList("updateApList"), gcmUserList, false);

        return result;
    }

    private Map<String, Object> setAptApResultData(AptAp aptAp, String successYn, String message) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("aptAp", aptAp);
        result.put("successYn", successYn);
        result.put("message", message);
        return result;
    }

    /**
     *
     * @param user
     * @param apList 선택된 ap 목록
     * @param storagePeriod 삭제시 제외될 일수
     * @param type schedule & user
     * @return
     */
    private Map<String, Object> aptApHealthCheck(User user, List<AptAp> apList, Integer storagePeriod, String type) {

        Map<String, Object> resultMap = Maps.newHashMap();
        List<Map<String, Object>> successList = Lists.newArrayList();
        List<Map<String, Object>> failList = Lists.newArrayList();

        long s = System.currentTimeMillis();
        logger.info("<< aptApMonitoringHealthCheck start : {} >>" + s);

        int targetCnt = 0;
        if (apList != null) {
            targetCnt = apList.size();
        }

        int timeout = 10;
        RequestConfig httpConfig = RequestConfig.custom().setConnectTimeout(timeout * 1000).setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();
        CloseableHttpAsyncClient httpclient = HttpAsyncClientBuilder.create().setDefaultRequestConfig(httpConfig).setMaxConnTotal(targetCnt).setMaxConnPerRoute(targetCnt).build();

        try {
            httpclient.start();

            CountDownLatch latch = new CountDownLatch(targetCnt);
            String protocol = "http://%s/door/action/healthchk";

            for (int i = 0; i < targetCnt; i++) {

                AptAp aptAp = apList.get(i);

                if (StringUtil.isBlank(aptAp.expIp)) {
                    failList.add(setAptApResultData(aptAp, "N", "ap ip 미설정"));
                    latch.countDown();
                    continue;
                }

                HttpGet httpGet = new HttpGet(String.format(protocol, aptAp.expIp));
                httpclient.execute(httpGet, new FutureCallback<HttpResponse>() {

                    @Override
                    public void completed(final HttpResponse response) {
                        try {

                            int httpStatus = response.getStatusLine().getStatusCode();
                            // logger.info("<<HTTP 응답({})>>", httpStatus);

                            if (HttpStatus.SC_OK == httpStatus) {
                                HttpEntity httpEntity = response.getEntity();
                                String responseJson = EntityUtils.toString(httpEntity);

                                if (!StringUtil.isBlank(responseJson)) {
                                    JSONObject jo = new JSONObject(responseJson);
                                    String successYn = jo.getString("result");
                                    if ("Y".equals(successYn)) {
                                        successList.add(setAptApResultData(aptAp, "Y", "success"));// 성공
                                        // logger.info("<< ap response is success, health check success, id : {}, apBeaconUuid : {} >>", aptAp.id, aptAp.apBeaconUuid);
                                    } else {
                                        // logger.info("<< ap response is empty, health check fail, id : {}, apBeaconUuid : {} >>", aptAp.id, aptAp.apBeaconUuid);
                                        failList.add(setAptApResultData(aptAp, "N", successYn));
                                    }
                                }
                            } else {
                                // logger.error("<<HTTP response 에러>> status : {} ", httpStatus);
                                failList.add(setAptApResultData(aptAp, "N", "error status " + httpStatus));
                            }

                            latch.countDown();
                        } catch (Exception e) {
                            failed(e);
                        }
                    }

                    @Override
                    public void failed(final Exception ex) {
                        latch.countDown();
                        failList.add(setAptApResultData(aptAp, "N", "http client execute failed : " + ex));
                        // logger.error("<< http client execute failed : >> {} ", ex);
                    }

                    @Override
                    public void cancelled() {
                        latch.countDown();
                        failList.add(setAptApResultData(aptAp, "N", "http client execute cancelled"));
                        // logger.error("<< http client execute cancelled >>");
                    }

                });
            }
            logger.info("<< aptApMonitoringHealthCheck async send data. waiting... >>");
            latch.await();
            logger.info("<< aptApMonitoringHealthCheck Receive all responses. >>");


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        long e = System.currentTimeMillis();
        logger.info("<< aptApMonitoringHealthCheck end : {} >>" + e);
        logger.info("<< aptApMonitoringHealthCheck total time : {} >>" + (e - s));

        // check 결과 저장
        Date dbDate = commonService.selectDate();
        List<Map<String, Object>> healthCheckList = Lists.newArrayList();
        healthCheckList.addAll(successList);
        healthCheckList.addAll(failList);
        int resultSize = insertApMonitoringAliveBatch(user, healthCheckList, dbDate, type);

        if (storagePeriod != null) {
            int size = healthCheckList.size();
            if (size > 0) {
                aptApMapper.deleteApMonitoringAlive(storagePeriod);
            }
        }

        resultMap.put("successList", successList);
        resultMap.put("failList", failList);

        return resultMap;
    }

    /**
     * health check monitoring batch insert
     */
    private int insertApMonitoringAliveBatch(User user, List<Map<String, Object>> healthCheckList, Date dbDate, String type) {

        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);

        int result = 0;
        int size = healthCheckList.size();

        try {

            for (int i = 0; i < size; i++) {
                Map<String, Object> item = healthCheckList.get(i);

                AptAp ap = (AptAp) item.get("aptAp");
                String successYn = StringUtil.nvl(item.get("successYn"));
                String message = StringUtil.nvl(item.get("message"));

                AptApMonitoringAlive aptApMonitoringAlive = new AptApMonitoringAlive();
                aptApMonitoringAlive.type = type;
                aptApMonitoringAlive.apId = ap.id;
                aptApMonitoringAlive.expIp = ap.expIp;
                aptApMonitoringAlive.success = successYn;
                aptApMonitoringAlive.regId = user.id;
                aptApMonitoringAlive.regDate = dbDate;
                aptApMonitoringAlive.memo = message;

                // aptApMonitoringAlive.apBeaconUuid = ap.apBeaconUuid;
                // aptApMonitoringAlive.aptApId = ap.apId;
                // aptApMonitoringAlive.apName = ap.apName;

                result += sqlSession.insert("com.jaha.web.emaul.mapper.AptApMapper.insertApMonitoringAlive", aptApMonitoringAlive);

                // result += aptApMapper.insertApMonitoringAlive(aptApMonitoringAlive);
            }

            sqlSession.commit();

        } catch (Exception e) {
            sqlSession.rollback();
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }

        return result;
    }

    @Override
    public Map<String, Object> aptApRealTimeHealthCheck(User user, List<AptAp> apList) {
        return aptApHealthCheck(user, apList, null, "user");
    }

    @Override
    public Map<String, Object> aptApMonitoringHealthCheck(User user, List<AptAp> apList, Integer storagePeriod) {
        return aptApHealthCheck(user, apList, storagePeriod, "schedule");
    }

    @Override
    public List<Map<String, Object>> selectAptApHealthCheckList(Map<String, Object> params) {
        return aptApMapper.selectAptApHealthCheckList(params);
    }

    @Override
    public int selectAptApHealthCheckListCount(Map<String, Object> params) {
        return aptApMapper.selectAptApHealthCheckListCount(params);
    }

    @Override
    public List<SimpleUser> selectPeriodApAccessLogUserNApAccessAuthUserList(Map<String, Object> params) {
        return aptApMapper.selectPeriodApAccessLogUserNApAccessAuthUserList(params);
    }


}
