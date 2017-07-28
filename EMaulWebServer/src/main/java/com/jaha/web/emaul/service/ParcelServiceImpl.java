package com.jaha.web.emaul.service;

import static com.jaha.web.emaul.model.spec.ParcelLockerSpecification.aptIdEq;
import static com.jaha.web.emaul.model.spec.ParcelLockerSpecification.isDeletedEq;
import static com.jaha.web.emaul.model.spec.ParcelLockerSpecification.nameLike;
import static com.jaha.web.emaul.model.spec.ParcelLockerSpecification.uuidEq;
import static com.jaha.web.emaul.model.spec.ParcelLogSpecification.lockerIdEq;
import static org.springframework.data.jpa.domain.Specifications.where;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jaha.web.emaul.exception.EmaulWebException;
import com.jaha.web.emaul.mapper.ParcelMapper;
import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.ParcelLocker;
import com.jaha.web.emaul.model.ParcelLockerForm;
import com.jaha.web.emaul.model.ParcelLog;
import com.jaha.web.emaul.model.ParcelSmsPolicy;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.repo.ParcelLockerRepository;
import com.jaha.web.emaul.repo.ParcelLogRepository;

/**
 * @author 이현수(lhs@jahasmart.com), 2016. 7. 6.
 * @description 무인택배시스템
 */
@Service
public class ParcelServiceImpl implements ParcelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParcelServiceImpl.class);

    @Autowired
    private ParcelLockerRepository parcelLockerRepository;

    @Autowired
    private ParcelLogRepository parcelLogRepository;

    @Autowired
    private ParcelMapper parcelMapper;

    @Override
    public Page<ParcelLocker> fetchParcelLockers(ParcelLockerForm.Search searchForm, Pageable pageable, Long aptId) {
        Page<ParcelLocker> page;

        if (searchForm.getSearchType() == ParcelLockerForm.SearchType.LOCKER_NAME)
            page = parcelLockerRepository.findAll(where(aptIdEq(aptId)).and(isDeletedEq(ParcelLocker.Deleted.N)).and(nameLike(searchForm.getSearchKeyword())), pageable);
        else
            page = parcelLockerRepository.findAll(where(aptIdEq(aptId)).and(isDeletedEq(ParcelLocker.Deleted.N)).and(uuidEq(searchForm.getSearchKeyword())), pageable);

        return page;
    }

    @Override
    public ParcelLocker getParcelLocker(Long id, Long aptId) {
        ParcelLocker parcelLocker = parcelLockerRepository.findOne(id);

        if (!parcelLocker.vertifyApt(aptId))
            throw new EmaulWebException("무인택배함 조회 권한이 없습니다");

        return parcelLocker;
    }

    @Override
    public Page<ParcelLog> fetchParcelLogs(Long parcelLockerId, Pageable pageable) {
        return parcelLogRepository.findAll(where(lockerIdEq(parcelLockerId)), pageable);
    }

    @Override
    public ParcelLocker updateParcelLockerAuthKey(Long id) {

        ParcelLocker parcelLocker = parcelLockerRepository.findOne(id);
        parcelLocker.updateAuthKey();

        return parcelLockerRepository.save(parcelLocker);
    }

    @Override
    public ParcelLocker updateParcelLocker(Long id, ParcelLockerForm.Update updateForm) {

        ParcelLocker parcelLocker = parcelLockerRepository.findOne(id);
        parcelLocker.setUuid(updateForm.getUuid());
        parcelLocker.setName(updateForm.getName());
        parcelLocker.setLocation(updateForm.getLocation());

        return parcelLockerRepository.save(parcelLocker);
    }

    @Override
    public ParcelLocker createParcelLocker(ParcelLockerForm.Create createForm, Apt apt) {
        ParcelLocker parcelLocker = new ParcelLocker();
        parcelLocker.setApt(apt);
        parcelLocker.updateAuthKey();
        parcelLocker.setName(createForm.getName());
        parcelLocker.setLocation(createForm.getLocation());

        return parcelLockerRepository.save(parcelLocker);
    }

    @Override
    @Transactional
    public void deleteParcelLocker(List<Long> parcelLockerIds) {
        List<ParcelLocker> parcelLockers = parcelLockerRepository.findAll(parcelLockerIds);
        for (ParcelLocker parcelLocker : parcelLockers)
            parcelLocker.setDeleted(ParcelLocker.Deleted.Y);
    }

    @Override
    public List<ParcelLog> selectParcelSmsList(Map<String, Object> params) {
        return parcelMapper.selectParcelSmsList(params);
    }

    @Override
    public int selectParcelSmsListCount(Map<String, Object> params) {
        return parcelMapper.selectParcelSmsListCount(params);
    }

    @Override
    public ParcelSmsPolicy selectParcelSmsPolicy(Map<String, Object> params) {
        return parcelMapper.selectParcelSmsPolicy(params);
    }

    @Override
    @Transactional
    public void saveParcelSmsPolicy(Map<String, Object> params) {
        parcelMapper.saveParcelSmsPolicy(params);

    }

    /**
     * 장기보관 택배 관리
     */
    @Override
    public Page<ParcelLog> getParcelLockerLongTerm(User user, int pageNum, String searchItem, String searchKeyWord) {

        Map<String, Object> resultMap = new HashMap<String, Object>();
        Page<ParcelLog> parcelLogPage = null;
        PageRequest pageRequest = new PageRequest(pageNum, 10, new Sort(Direction.DESC, "regDate")); // 현재페이지, 조회할 페이지수,
        Date sysDate = new Date();
        sysDate.setHours(0);
        if (searchItem.equals("") && searchKeyWord.equals("")) {
            parcelLogPage = parcelLogRepository.findByParcelLockerAptIdAndApiNumberAndRegDateBeforeAndDelYn(user.house.apt.id, 1, sysDate, "N", pageRequest);
        } else if (!searchItem.equals("") && !searchItem.isEmpty() && searchItem.equals("phone")) {
            searchKeyWord = searchKeyWord.replaceAll("-", "");
            searchKeyWord = searchKeyWord.trim();
            System.out.println("**************" + searchKeyWord);
            parcelLogPage = parcelLogRepository.findByParcelLockerAptIdAndApiNumberAndPhoneAndDelYn(user.house.apt.id, 1, searchKeyWord, "N", pageRequest);

        } else if (!searchItem.equals("") && !searchItem.isEmpty() && searchItem.equals("parcelLockerName")) {
            parcelLogPage = parcelLogRepository.findByParcelLockerAptIdAndApiNumberAndParcelLockerLocationAndDelYn(user.house.apt.id, 1, searchKeyWord, "N", pageRequest);


        } else if (!searchItem.equals("") && !searchItem.isEmpty() && searchItem.equals("dongHo")) {

            String tmpDong, tmpHo;
            int dong = 0;
            int ho = 0;
            if (searchKeyWord.indexOf("동") != -1 && searchKeyWord.indexOf("호") != -1) {
                int i = searchKeyWord.indexOf("동");
                int j = searchKeyWord.indexOf("호");
                String tmp = searchKeyWord;
                tmp = tmp.replace("동", "");
                tmp = tmp.replace("호", "");
                tmp = tmp.trim();

                if (tmp.matches("^[0-9]*$")) {
                    tmpDong = searchKeyWord.substring(0, i);
                    tmpHo = searchKeyWord.substring(i + 1, j);

                    dong = Integer.parseInt(tmpDong);
                    ho = Integer.parseInt(tmpHo);

                } else {
                    dong = 0;
                    ho = 0;
                }
            }

            parcelLogPage = parcelLogRepository.findByParcelLockerAptIdAndApiNumberAndDongAndHoAndDelYn(user.house.apt.id, 1, dong, ho, "N", pageRequest);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < parcelLogPage.getContent().size(); i++) {

            Date userDate = parcelLogPage.getContent().get(i).getRegDate();
            String start = formatter.format(userDate);
            String end = formatter.format(sysDate);

            try {
                Date beginDate = formatter.parse(start);
                Date endDate = formatter.parse(end);

                // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
                long diff = endDate.getTime() - beginDate.getTime();
                long diffDays = diff / (24 * 60 * 60 * 1000);


                parcelLogPage.getContent().get(i).setWarningDay((int) (diffDays + 1));

                System.out.println("날짜차이=" + diffDays);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        resultMap.put("result", parcelLogPage);

        return parcelLogPage;
    }

    /*
     * 택배 상태 업데이트
     */
    @Override
    @Transactional
    public void updateStatusParcel(User user, Long parcelLogId, String status) {
        // TODO Auto-generated method stub
        // office store -- mod_id // office keep date
        Date sysDate = new Date();
        ParcelLog parcelLog = parcelLogRepository.findOne(parcelLogId);

        String officeYn;

        if (status.equals("adminOffice")) {
            officeYn = "Y";
            parcelLog.setOfficeStoreYn(officeYn);
            parcelLog.setOfficeKeepDate(sysDate);
            parcelLog.setModId(user.id);

        } else if (status.equals("receiveComplete")) {
            officeYn = "N";
            parcelLog.setOfficeStoreYn(officeYn);
            parcelLog.setOfficeKeepDate(sysDate);
            parcelLog.setModId(user.id);
        }
        parcelLogRepository.saveAndFlush(parcelLog);
    }

    /*
     * 장기보관 중인 택배 총개수
     */
    @Override
    public Long parcelLogTotalCount(Long aptId, int apiNum, Date date) {
        // TODO Auto-generated method stub

        return parcelLogRepository.countByParcelLockerAptIdAndApiNumberAndRegDateBeforeAndDelYn(aptId, 1, date, "N");
    }

    /*
     *
     * 기준날짜로 배치를 돌면서 del_yn 을 삭제상태로 업데이트한다
     *
     * @see com.jaha.web.emaul.service.ParcelService#getBatchParcelLogList(int)
     */
    @Override
    public void updateParcelDelStatus() {
        List<ParcelLog> tmpList = parcelLogRepository.findByApiNumberAndFindDateNotNull(1);

        Date sysDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < tmpList.size(); i++) {
            Date userDate = new Date();

            if (tmpList.get(i).getOfficeKeepDate() == null) {
                userDate = tmpList.get(i).getFindDate();
                LOGGER.info("LogDate : " + userDate + "LogId = " + tmpList.get(i).getId());

            } else {
                userDate = tmpList.get(i).getOfficeKeepDate();
                LOGGER.info("LogDate : " + userDate + "LogId = " + tmpList.get(i).getId());
            }

            String start = formatter.format(userDate);
            String end = formatter.format(sysDate);

            try {
                Date beginDate = formatter.parse(start);
                Date endDate = formatter.parse(end);

                // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
                long diff = endDate.getTime() - beginDate.getTime();
                long diffDays = diff / (24 * 60 * 60 * 1000);

                if (tmpList.get(i).getOfficeKeepDate() == null) {
                    if (diffDays > 0) {
                        tmpList.get(i).setDelDate(sysDate);
                        tmpList.get(i).setDelYn("Y");
                        parcelLogRepository.saveAndFlush(tmpList.get(i));

                        continue;
                    }
                } else if (tmpList.get(i).getOfficeKeepDate() != null) {
                    if (diffDays > 2L) {
                        tmpList.get(i).setDelDate(sysDate);
                        tmpList.get(i).setDelYn("Y");
                        parcelLogRepository.saveAndFlush(tmpList.get(i));
                        continue;
                    }
                }
                // System.out.println("날짜차이=" + diffDays);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

}
