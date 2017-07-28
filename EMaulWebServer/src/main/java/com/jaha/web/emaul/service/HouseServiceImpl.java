package com.jaha.web.emaul.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.jaha.web.emaul.constants.Constants;
import com.jaha.web.emaul.constants.UserPrivacy;
import com.jaha.web.emaul.mapper.AptApMapper;
import com.jaha.web.emaul.mapper.AptSchedulerMapper;
import com.jaha.web.emaul.mapper.HouseMapper;
import com.jaha.web.emaul.model.Address;
import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.AptContact;
import com.jaha.web.emaul.model.AptFee;
import com.jaha.web.emaul.model.AptFeeAvr;
import com.jaha.web.emaul.model.AptFeePush;
import com.jaha.web.emaul.model.AptInfo;
import com.jaha.web.emaul.model.AptMgtCorp;
import com.jaha.web.emaul.model.AptScheduler;
import com.jaha.web.emaul.model.BatchSchedule;
import com.jaha.web.emaul.model.BoardCategory;
import com.jaha.web.emaul.model.GasCheck;
import com.jaha.web.emaul.model.GcmSendForm;
import com.jaha.web.emaul.model.House;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.prop.UrlProperties;
import com.jaha.web.emaul.repo.AddressRepository;
import com.jaha.web.emaul.repo.AptApAccessAuthRepository;
import com.jaha.web.emaul.repo.AptApAccessDeviceRepository;
import com.jaha.web.emaul.repo.AptApRepository;
import com.jaha.web.emaul.repo.AptContactRepository;
import com.jaha.web.emaul.repo.AptFeeAvrRepository;
import com.jaha.web.emaul.repo.AptFeePushRepository;
import com.jaha.web.emaul.repo.AptFeeRepository;
import com.jaha.web.emaul.repo.AptInfoRepository;
import com.jaha.web.emaul.repo.AptMgtCorpRepository;
import com.jaha.web.emaul.repo.AptRepository;
import com.jaha.web.emaul.repo.AptSchedulerRepository;
import com.jaha.web.emaul.repo.BatchScheduleRepository;
import com.jaha.web.emaul.repo.BoardCategoryRepository;
import com.jaha.web.emaul.repo.GasCheckRepository;
import com.jaha.web.emaul.repo.HouseRepository;
import com.jaha.web.emaul.util.AddressConverter;
import com.jaha.web.emaul.util.Locations;
import com.jaha.web.emaul.util.Orms;
import com.jaha.web.emaul.util.StringUtil;

/**
 * Created by doring on 15. 3. 31..
 */
@Service
public class HouseServiceImpl implements HouseService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AptRepository aptRepository;
    @Autowired
    private AptFeeRepository aptFeeRepository;
    @Autowired
    private HouseRepository houseRepository;
    @Autowired
    private GasCheckRepository gasCheckRepository;
    @Autowired
    private AptFeeAvrRepository aptFeeAvrRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private BoardCategoryRepository boardCategoryRepository;
    @Autowired
    private AptInfoRepository aptInfoRepository;
    @Autowired
    private AptContactRepository aptContactRepository;
    @Autowired
    private AptFeePushRepository aptFeePushRepository;
    @Autowired
    private AptSchedulerRepository aptSchedulerRepository;
    @Autowired
    private AptSchedulerMapper aptSchedulerMapper;
    @Autowired
    private BatchScheduleRepository batchScheduleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private GcmService gcmService;
    @Autowired
    private AptApMapper aptApMapper;
    @Autowired
    private AptApRepository aptApRepository;
    @Autowired
    private AptApAccessAuthRepository aptApAccessAuthRepository;
    @Autowired
    private AptApAccessDeviceRepository aptApAccessDeviceRepository;
    @Autowired
    private AptMgtCorpRepository aptMgtCorpRepository;
    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UrlProperties urlProperties;

    private Gson gson = new Gson();

    @Override
    public List<Apt> searchApt(String name) {
        List<Apt> list = aptRepository.findByNameContainingAndRegisteredAptIsTrueAndVirtualFalse(name);

        for (Apt apt : list) {
            apt.strAddress = AddressConverter.toStringAddress(apt.address);
            apt.strAddressOld = AddressConverter.toStringAddressOld(apt.address);
        }

        return list;
    }

    @Override
    public Apt getApt(Long aptId) {
        return aptRepository.findOne(aptId);
    }

    @Override
    public House saveAndFlush(House house) {
        return houseRepository.saveAndFlush(house);
    }

    @Override
    public House save(House house) {
        return houseRepository.save(house);
    }

    @Override
    public House getHouse(Long aptId, String dong, String ho) {
        return houseRepository.findOneByAptIdAndDongAndHo(aptId, dong, ho);
    }

    @Override
    public AptFee saveAndFlush(AptFee aptFee) {
        return aptFeeRepository.saveAndFlush(aptFee);
    }

    @Override
    public AptFee save(AptFee aptFee) {
        return aptFeeRepository.save(aptFee);
    }

    @Override
    public AptFeeAvr save(AptFeeAvr aptFeeAvr) {
        return aptFeeAvrRepository.save(aptFeeAvr);
    }

    @Override
    public AptFeeAvr getAptFeeAvr(Long aptId, String date, String houseSize) {
        return aptFeeAvrRepository.findOneByAptIdAndDateAndHouseSize(aptId, date, houseSize);
    }

    @Override
    public List<AptFeeAvr> getAptFeeAvrList(Long aptId, String houseSize) {
        return aptFeeAvrRepository.findByAptIdAndHouseSize(aptId, houseSize);
    }

    @Override
    @Transactional
    public void deleteAptFee(Long aptId, String date) {
        List<House> houseList = houseRepository.findByAptId(aptId);
        List<Long> ids = Lists.transform(houseList, input -> input.id);
        aptFeeRepository.deleteByHouseIdInAndDate(ids, date);
    }

    @Override
    public AptFee getAptFee(Long houseId, String yyyyMM) {
        return aptFeeRepository.findOneByHouseIdAndDate(houseId, yyyyMM);
    }

    @Override
    public AptFee getLastAptFee(Long houseId) {
        return aptFeeRepository.findOneByHouseId(houseId, new Sort(Sort.Direction.DESC, "date"));
    }

    @Override
    public List<AptFee> getAptFeeList(Long houseId) {
        return aptFeeRepository.findByHouseId(houseId, new Sort(Sort.Direction.DESC, "date"));
    }

    @Override
    public List<String> getAptFeeRegisteredDate(Long aptId) {
        String sql = "SELECT f.date FROM apt_fee f, house h WHERE f.house_id=h.id and h.apt_id=? GROUP BY f.date ORDER BY f.date DESC;";
        return jdbcTemplate.query(sql, new Object[] {aptId}, (rs, rowNum) -> {
            return rs.getString("date");
        });
    }

    @Override
    public GasCheck saveAndFlush(GasCheck gas) {
        return gasCheckRepository.saveAndFlush(gas);
    }

    @Override
    public List<GasCheck> getGasCheckList(Long userId) {
        return gasCheckRepository.findByUserId(userId, new Sort(Sort.Direction.DESC, "date"));
    }

    @Override
    public List<String> getSidoNames() {
        String sql = "SELECT DISTINCT 시도명 FROM address;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return rs.getString("시도명");
        });
    }

    @Override
    public List<String> getSigunguNames(String sidoName) {
        String sql = "SELECT DISTINCT 시군구명 FROM address WHERE 시도명=?;";

        return jdbcTemplate.query(sql, new Object[] {sidoName}, (rs, rowNum) -> {
            return rs.getString("시군구명");
        });
    }

    @Override
    public List<String> getEupMyunDongNames(String sigunguName) {
        String sql = "SELECT DISTINCT 법정읍면동명 FROM address WHERE 시군구명=?;";

        return jdbcTemplate.query(sql, new Object[] {sigunguName}, (rs, rowNum) -> {
            return rs.getString("법정읍면동명");
        });
    }

    @Override
    public List<String> getEupMyunDongNames(String sidoName, String sigunguName) {
        String sql = "SELECT DISTINCT 법정읍면동명 FROM address WHERE 시도명=? AND 시군구명=?;";

        return jdbcTemplate.query(sql, new Object[] {sidoName, sigunguName}, (rs, rowNum) -> {
            return rs.getString("법정읍면동명");
        });
    }

    @Override
    public List<Address> searchBuilding(String sidoName, String sigunguName, String eupmyundongName, String buildingName) {
        String sql;
        if (eupmyundongName != null && !eupmyundongName.isEmpty()) {
            sql = "SELECT * FROM address WHERE 시도명=? AND 시군구명=? AND 법정읍면동명=? AND 시군구용건물명 like ? AND 비고1 != 'virtual' GROUP BY 건물본번, 건물부번";
            return jdbcTemplate.query(sql, new Object[] {sidoName, sigunguName, eupmyundongName, "%" + buildingName + "%"}, (rs, rowNum) -> {
                try {
                    return Orms.mapping(rs, Address.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
        } else {
            sql = "SELECT * FROM address WHERE 시도명=? AND 시군구명=? AND 시군구용건물명 like ? AND 비고1 != 'virtual' GROUP BY 건물본번, 건물부번";
            return jdbcTemplate.query(sql, new Object[] {sidoName, sigunguName, "%" + buildingName + "%"}, (rs, rowNum) -> {
                try {
                    return Orms.mapping(rs, Address.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
        }
    }

    @Override
    public List<String> getDongs(Long aptId) {
        String sql = "SELECT dong FROM house WHERE apt_id=? GROUP BY dong";
        List<String> ret = jdbcTemplate.query(sql, new Object[] {aptId}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("dong");
            }
        });
        return ret;
    }

    @Override
    public List<Apt> jahaGetAptList(String registered) {
        List<Apt> list = null;

        if ("all".equals(registered)) {
            registered = StringUtils.EMPTY;
        }

        // ///////////////////// 추가 result true 이면 등록아파트 false 면 미등록아파트 목록만 가져옵니다
        if (StringUtil.isBlank(registered)) {
            // list = aptRepository.findByVirtualFalse();
            list = aptRepository.findAll();
        } else {
            boolean result = "0".equals(registered) ? true : false;
            list = aptRepository.findByRegisteredAptAndVirtualFalse(result);
        }

        // List<Apt> list = aptRepository.findAll(); 기존에 모든 아파트 목록을가져오는것 주석처리

        for (Apt apt : list) {
            apt.strAddress = AddressConverter.toStringAddress(apt.address);
            apt.strAddressOld = AddressConverter.toStringAddressOld(apt.address);
        }
        return list;
    }

    @Override
    public Apt save(Apt apt) {
        return aptRepository.save(apt);
    }

    @Override
    public AptInfo saveAndFlush(AptInfo aptInfo) {
        return aptInfoRepository.saveAndFlush(aptInfo);
    }

    @Override
    public Boolean aptExist(String addressCode) {
        Address address = addressRepository.findOne(addressCode);
        return aptRepository.findByAddress(address) != null;

        // String name = address.시군구용건물명;
        // return aptRepository.findByName(name) != null;
    }

    @Override
    public void jahaNewApt(String addressCode) {
        Address address = addressRepository.findOne(addressCode);
        Apt apt = new Apt();
        apt.name = address.시군구용건물명;
        apt.address = address;
        apt.registeredApt = true;

        Locations.LatLng latLng = Locations.getLocationFromAddress(AddressConverter.toStringAddressOld(address));
        if (latLng != null) {
            apt.latitude = latLng.lat;
            apt.longitude = latLng.lng;
        }

        apt.virtual = false; // 가상여부 추가
        apt.regDate = new Date(); // 등록일자 추가
        apt = aptRepository.saveAndFlush(apt);

        saveBoardCategory(apt, "오늘", 1, "today", Lists.newArrayList("jaha", "admin", "user", "gasChecker", "anonymous"), Lists.newArrayList("jaha", "admin"));
        saveBoardCategory(apt, "공지사항", 2, "notice", Lists.newArrayList("jaha", "admin", "user", "gasChecker"), Lists.newArrayList("jaha", "admin"));
        // saveBoardCategory(apt, "방송 게시판", 3, "tts", Lists.newArrayList("jaha", "admin", "user"), Lists.newArrayList("jaha", "admin"));
        // saveBoardCategory(apt, "민원", 4, "complaint", Lists.newArrayList("jaha", "admin", "user"), Lists.newArrayList("jaha", "admin", "user"));
        saveBoardCategory(apt, "주민 게시판", 5, "community", Lists.newArrayList("jaha", "admin", "user", "gasChecker"), Lists.newArrayList("jaha", "admin", "user", "gasChecker"));
    }

    private void saveBoardCategory(Apt apt, String name, int ord, String type, List<String> readable, List<String> writable) {
        BoardCategory boardCategory = new BoardCategory();
        boardCategory.apt = apt;
        boardCategory.name = name;
        boardCategory.ord = ord;
        boardCategory.type = type;
        boardCategory.contentMode = ("today".equals(type) || "notice".equals(type)) ? "html" : "text";
        boardCategory.pushAfterWrite = "N";
        boardCategory.jsonArrayReadableUserType = gson.toJson(readable);
        boardCategory.jsonArrayWritableUserType = gson.toJson(writable);
        boardCategory.setUserPrivacy(UserPrivacy.ALIAS);
        boardCategoryRepository.save(boardCategory);
    }

    @Override
    public List<String> getHos(Long aptId, String dong) {
        String sql = "SELECT ho FROM house WHERE apt_id=? AND dong=? GROUP BY ho";
        List<String> ret = jdbcTemplate.query(sql, new Object[] {aptId, dong}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("ho");
            }
        });
        return ret;
    }

    @Override
    public void deleteAptInfoContacts(List<AptContact> list) {
        if (list == null) {
            return;
        }

        for (AptContact contact : list) {
            aptContactRepository.delete(contact.id);
        }
    }

    @Override
    public AptInfo updateAptLogo(Long aptId, String url) {
        Apt apt = aptRepository.findOne(aptId);
        if (apt.aptInfo == null) {
            return null;
        }
        jdbcTemplate.update("UPDATE apt_info SET url_logo=? WHERE id=?", new Object[] {url, apt.aptInfo.id});

        return aptRepository.findOne(aptId).aptInfo;
    }

    @Override
    public AptInfo updateAptPhoto(Long aptId, String url) {
        Apt apt = aptRepository.findOne(aptId);
        if (apt.aptInfo == null) {
            return null;
        }
        jdbcTemplate.update("UPDATE apt_info SET url_apt_photo=? WHERE id=?", new Object[] {url, apt.aptInfo.id});

        return aptRepository.findOne(aptId).aptInfo;
    }

    @Override
    public List<House> getHouses(Long aptId) {
        return houseRepository.findByAptId(aptId);
    }



    @Override
    @Transactional
    public AptFeePush addAptFeePush(AptFeePush aptFeePush) {
        AptFeePush afp = null;

        try {
            if ("Y".equals(aptFeePush.getBookYn())) { // 예약 발송
                aptFeePush.setSendYn("N");
                afp = this.aptFeePushRepository.saveAndFlush(aptFeePush);

                String batchTaskReqUrl = String.format(urlProperties.getCommonGcmSendUrl(), "users", aptFeePush.getApt().id);

                /**
                 * var json = {}; json.type = "action"; json.action = "emaul://aptfee"; json.title = "관리비 푸시알림"; json.message = jsonData.contents; json.isTargetAll = false;
                 */
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"").append("type").append("\"").append(":").append("\"").append("action").append("\"");
                json.append(",").append("\"").append("title").append("\"").append(":").append("\"").append("관리비 푸시알림").append("\"");
                json.append(",").append("\"").append("message").append("\"").append(":").append("\"").append(aptFeePush.getContents()).append("\"");
                json.append(",").append("\"").append("action").append("\"").append(":").append("\"").append("emaul://aptfee").append("\"");
                json.append("}");

                String batchTaskReqParams = String.format(urlProperties.getCommonGcmSendParam(), json, "notiFeePush");

                BatchSchedule batchSchedule = new BatchSchedule();
                batchSchedule.batchTaskName = "관리비 예약 푸시알림";
                batchSchedule.batchGroupKey = Constants.BATCH_GROUP_PREFIX.APT_FEE_PUSH_.name() + afp.getId();
                batchSchedule.httpMethod = "POST";
                batchSchedule.batchTaskReqUrl = batchTaskReqUrl;
                batchSchedule.nextRuntime = aptFeePush.getSendDate() + "00";
                batchSchedule.scheduleInterval = -1; // 인터벌이 -1일 경우 한번만 발송하고 종료됨
                batchSchedule.regId = String.valueOf(aptFeePush.getUser().id);
                batchSchedule.params = batchTaskReqParams;
                this.batchScheduleRepository.saveAndFlush(batchSchedule);
            } else { // 즉시 발송
                aptFeePush.setSendYn("Y");
                afp = this.aptFeePushRepository.saveAndFlush(aptFeePush);
            }

            return afp;
        } catch (Exception e) {
            logger.error("<<아파트 관리비 푸시 추가 중 오류 발생>>", e);
            return null;
        }
    }

    @Override
    @Transactional
    public AptFeePush modifyAptFeePush(AptFeePush aptFeePush) {
        try {
            AptFeePush afp = this.aptFeePushRepository.findOne(aptFeePush.getId());

            if (afp != null && "N".equals(afp.getSendYn())) { // 발송전에만 수정 가능
                this.aptFeePushRepository
                        .updateAptFeePush(aptFeePush.getId(), aptFeePush.getGubun(), aptFeePush.getBookYn(), aptFeePush.getSendDate(), aptFeePush.getContents(), aptFeePush.getModId());

                // 배치스케줄 삭제
                this.batchScheduleRepository.deleteByBatchGroupKey(Constants.BATCH_GROUP_PREFIX.APT_FEE_PUSH_.name() + aptFeePush.getId());

                String batchTaskReqUrl = String.format(urlProperties.getCommonGcmSendUrl(), "users", aptFeePush.getApt().id);

                /**
                 * var json = {}; json.type = "action"; json.action = "emaul://aptfee"; json.title = "관리비 푸시알림"; json.message = jsonData.contents; json.isTargetAll = false;
                 */
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"").append("type").append("\"").append(":").append("\"").append("action").append("\"");
                json.append(",").append("\"").append("title").append("\"").append(":").append("\"").append("관리비 푸시알림").append("\"");
                json.append(",").append("\"").append("message").append("\"").append(":").append("\"").append(aptFeePush.getContents()).append("\"");
                json.append(",").append("\"").append("action").append("\"").append(":").append("\"").append("emaul://aptfee").append("\"");
                json.append("}");

                String batchTaskReqParams = String.format(urlProperties.getCommonGcmSendParam(), json, "notiFeePush");

                BatchSchedule batchSchedule = new BatchSchedule();
                batchSchedule.batchTaskName = "관리비 예약 푸시알림";
                batchSchedule.batchGroupKey = Constants.BATCH_GROUP_PREFIX.APT_FEE_PUSH_.name() + aptFeePush.getId();
                batchSchedule.httpMethod = "POST";
                batchSchedule.batchTaskReqUrl = batchTaskReqUrl;
                batchSchedule.nextRuntime = aptFeePush.getSendDate() + "00";
                batchSchedule.scheduleInterval = -1; // 인터벌이 -1일 경우 한번만 발송하고 종료됨
                batchSchedule.regId = String.valueOf(aptFeePush.getUser().id);
                batchSchedule.params = batchTaskReqParams;
                this.batchScheduleRepository.saveAndFlush(batchSchedule);
            }

            return aptFeePush;
        } catch (Exception e) {
            logger.error("<<아파트 관리비 푸시 수정 중 오류 발생>>", e);
            return null;
        }
    }

    @Override
    @Transactional
    public AptFeePush removeAptFeePush(long id) {
        try {
            AptFeePush afp = this.aptFeePushRepository.findOne(id);

            if (afp != null && "N".equals(afp.getSendYn())) { // 발송전에만 삭제 가능
                this.aptFeePushRepository.delete(id);
            }

            return afp;
        } catch (Exception e) {
            logger.error("<<아파트 관리비 푸시 삭제 중 오류 발생>>", e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AptFeePush> getAptFeePushList(long aptId, Pageable pageable) {
        Page<AptFeePush> aptFeePushPage = this.aptFeePushRepository.findByAptIdOrderByIdDesc(aptId, pageable);

        return aptFeePushPage;
    }

    @Override
    @Transactional(readOnly = true)
    public AptFeePush getAptFeePush(long id) {
        return this.aptFeePushRepository.findOne(id);
    }

    @Override
    public Map<String, Object> getAptScheduler(User user, String pageNum, String startDate, String endDate, String searchItem, String searchKeyWord) {

        int nPageNum = Integer.parseInt(pageNum);
        int nPageSize = Constants.DEFAULT_PAGE_SIZE;

        int startNum = (nPageNum - 1) * nPageSize;

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("aptId", user.house.apt.id);
        params.put("startNum", startNum);
        params.put("pageSize", nPageSize);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("searchItem", searchItem);
        params.put("searchKeyWord", searchKeyWord);

        List<AptScheduler> dataList = aptSchedulerMapper.selectAptSchedulerList(params);

        int totalCount = aptSchedulerMapper.selectAptSchedulerListCount(params);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("totalCount", totalCount);
        result.put("dataList", dataList);

        return result;
    }

    @Override
    public AptScheduler getAptScheduler(Long id) {
        return aptSchedulerRepository.findById(id);
    }

    @Override
    public AptScheduler getAptScheduler(Long id, Long aptId) {
        return aptSchedulerRepository.findByIdAndAptId(id, aptId);
    }

    @Override
    @Transactional
    public AptScheduler saveAptScheduler(HttpServletRequest req, User user, AptScheduler params) {
        AptScheduler aptScheduler = null;
        if (params.id == null) {
            aptScheduler = new AptScheduler();
            aptScheduler.regId = user.id;
        } else {
            aptScheduler = aptSchedulerRepository.findById(params.id);
            aptScheduler.modId = user.id;
        }

        aptScheduler.aptId = user.house.apt.id;
        aptScheduler.noticeTarget = params.noticeTarget;
        aptScheduler.noticeTargetDong = params.noticeTargetDong;
        aptScheduler.noticeTargetHo = params.noticeTargetHo;
        aptScheduler.startDate = params.startDate;
        aptScheduler.startTime = params.startTime;
        aptScheduler.endDate = params.endDate;
        aptScheduler.endTime = params.endTime;
        aptScheduler.title = params.title;
        aptScheduler.content = params.content;
        aptScheduler.pushTiming = params.pushTiming;
        aptScheduler.pushDate = params.pushDate;
        aptScheduler.pushTime = params.pushTime;
        aptScheduler.exposureTiming = params.exposureTiming;
        aptScheduler.exposureDate = params.exposureDate;
        aptScheduler.exposureTime = params.exposureTime;

        // 노출타이밍이 즉시( 0 )면 active로 바로 조회되게하고 예약이면 unactive로 조회되지 않게한다.
        aptScheduler.status = "0".equals(aptScheduler.exposureTiming) ? "active" : "unactive";

        AptScheduler result = aptSchedulerRepository.save(aptScheduler);

        String url = req.getRequestURL().toString();
        url = url.replace("save", "");
        url = url.replace("/admin/", "/api/public/");

        // batchGroup은 모두삭제후 수정된 항목으로 등록 ( 즉시로 변경했을경우도 batch는 모두 삭제 )
        batchScheduleRepository.deleteByBatchGroupKey(result.getAptSchedulerPushBatchGroupKey());
        if ("1".equals(aptScheduler.pushTiming)) {
            for (int i = 0; i < aptScheduler.pushDate.length; i++) {
                BatchSchedule batchSchedule = new BatchSchedule();
                String pushDate = aptScheduler.pushDate[i].replaceAll("-", "");
                String pushTime = aptScheduler.pushTime[i].replaceAll(":", "");
                if (!StringUtils.isEmpty(pushDate)) {
                    batchSchedule.batchTaskName = "아파트 일정 예약 푸시발송";
                    batchSchedule.batchGroupKey = result.getAptSchedulerPushBatchGroupKey();
                    batchSchedule.httpMethod = "POST";
                    batchSchedule.batchTaskReqUrl = url + "pushBatch";
                    batchSchedule.nextRuntime = pushDate + pushTime;
                    batchSchedule.scheduleInterval = -1;
                    batchSchedule.regId = String.valueOf(user.id);
                    batchSchedule.params = "id=" + result.id;
                    // batchScheduleRepository.save(batchSchedule); // 예약 push는 보류
                }
            }
        } else if ("0".equals(aptScheduler.pushTiming)) {
            // 0이면 즉시 push
            // sendGcmAptScheduler(aptScheduler); // push는 보류
        }

        // batchGroup은 모두삭제후 수정된 항목으로 등록 ( 즉시로 변경했을경우도 batch는 모두 삭제 )
        batchScheduleRepository.deleteByBatchGroupKey(result.getAptSchedulerExposureBatchGroupKey());
        if ("1".equals(aptScheduler.exposureTiming)) {
            BatchSchedule batchSchedule = new BatchSchedule();
            String exposureDate = aptScheduler.exposureDate.replaceAll("-", "");
            String exposureTime = aptScheduler.exposureTime.replaceAll(":", "");
            if (!StringUtils.isEmpty(exposureDate)) {
                batchSchedule.batchTaskName = "아파트 일정 예약 조회노출";
                batchSchedule.batchGroupKey = result.getAptSchedulerExposureBatchGroupKey();
                batchSchedule.httpMethod = "POST";
                batchSchedule.batchTaskReqUrl = url + "exposureBatch";
                batchSchedule.nextRuntime = exposureDate + exposureTime;
                batchSchedule.scheduleInterval = -1;
                batchSchedule.regId = String.valueOf(user.id);
                batchSchedule.params = "id=" + result.id;

                batchScheduleRepository.save(batchSchedule);
            }
        }
        return result;
    }

    @Override
    public void sendGcmAptScheduler(AptScheduler aptScheduler) {
        Long aptId = aptScheduler.aptId;
        List<User> userList = null;
        if ("1".equals(aptScheduler.noticeTarget)) {
            userList = userService.getAllAptUsers(aptId);
        } else if ("2".equals(aptScheduler.noticeTarget)) {
            // 2번이 관리자라는 타겟인데 후에 아파트관련 종사자분들의 타입이 결정되면 getUsersByAdmin가 수정되던지 이부분이 수정되던지 해야함.
            userList = userService.getUsersByAdmin(aptId);
        } else if ("3".equals(aptScheduler.noticeTarget)) {
            userList = userService.getUsersByDongAndHoIn(aptId, aptScheduler.noticeTargetDong, null);
        } else if ("4".equals(aptScheduler.noticeTarget)) {
            List<String> hos = new ArrayList<String>();
            hos.add(aptScheduler.noticeTargetHo);
            userList = userService.getUsersByDongAndHoIn(aptId, aptScheduler.noticeTargetDong, hos);
        }

        List<Long> userIds = Lists.newArrayList();
        for (User users : userList) {
            // 알람설정을 on해놓았고 탈퇴및 차단되지않은 user만 필터링.
            if (users.setting.notiAlarm && !users.type.blocked && !users.type.deactivated) {
                userIds.add(users.id);
            }
        }

        GcmSendForm form = new GcmSendForm();
        Map<String, String> msg = Maps.newHashMap();
        msg.put("type", "action");
        // 스케줄러 화면으로 이동 : aptschedule
        // 스케줄러 디테일 : aptschedule-detail?id=1
        msg.put("action", "emaul://aptschedule-detail?id=" + aptScheduler.id);
        msg.put("titleResId", "scheduler_title");
        msg.put("value", aptScheduler.title + "의 일정을 확인해주세요.");
        form.setUserIds(Lists.newArrayList(userIds));
        form.setMessage(msg);

        gcmService.sendGcm(form);
    }

    @Override
    @Transactional
    public int deleteAptScheduler(AptScheduler params) {
        int result = aptSchedulerRepository.deleteById(params.id);
        if (result > 0) {
            result += batchScheduleRepository.deleteByBatchGroupKey(params.getAptSchedulerPushBatchGroupKey());
            result += batchScheduleRepository.deleteByBatchGroupKey(params.getAptSchedulerExposureBatchGroupKey());
        }
        return result;
    }

    @Override
    public int updateAptScheduleStatus(Long id) {
        return jdbcTemplate.update("UPDATE apt_scheduler SET status=? WHERE id=?", new Object[] {"active", id});
    }

    @Override
    public Apt getAptByAddressCode(String addressCode) {
        Address address = addressRepository.findOne(addressCode);
        return aptRepository.findByAddress(address);
    }


    /**
     * 아파트업체 한개를 가져온다
     */
    @Override
    public AptMgtCorp getAptMgtCorp(Long aptId) {
        return aptMgtCorpRepository.findByAptId(aptId);
    }

    /**
     * 아파트 관리업체 업데이트
     */
    @Override
    public AptMgtCorp saveAndFlush(AptMgtCorp aptMgtCorp) {

        return aptMgtCorpRepository.saveAndFlush(aptMgtCorp);

    }

    @Override
    public List<Map<String, Object>> searchAptAll(Map<String, Object> params) {
        return houseMapper.selectAddressAptList(params);
    }

    @Override
    public List<House> getHouseList(Long aptId, String dong, String ho) {
        return houseRepository.findByAptIdAndDongAndHo(aptId, dong, ho);
    }

    @Override
    public int insertHouse(House house) {
        return houseMapper.insertHouse(house);
    }

}
