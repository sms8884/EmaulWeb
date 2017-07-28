package com.jaha.web.emaul.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.ParcelLocker;
import com.jaha.web.emaul.model.ParcelLockerForm;
import com.jaha.web.emaul.model.ParcelLog;
import com.jaha.web.emaul.model.ParcelSmsPolicy;
import com.jaha.web.emaul.model.User;

/**
 * @author 이현수(lhs@jahasmart.com), 2016. 7. 6.
 * @description 무인택배시스템
 */
public interface ParcelService {

    Page<ParcelLocker> fetchParcelLockers(ParcelLockerForm.Search searchForm, Pageable pageable, Long aptId);

    ParcelLocker getParcelLocker(Long id, Long aLong);

    Page<ParcelLog> fetchParcelLogs(Long parcelLockerId, Pageable pageable);

    ParcelLocker updateParcelLockerAuthKey(Long id);

    ParcelLocker updateParcelLocker(Long id, ParcelLockerForm.Update updateForm);

    ParcelLocker createParcelLocker(ParcelLockerForm.Create createForm, Apt apt);

    void deleteParcelLocker(List<Long> parcelLockerIds);

    public List<ParcelLog> selectParcelSmsList(Map<String, Object> params);

    public int selectParcelSmsListCount(Map<String, Object> params);

    public ParcelSmsPolicy selectParcelSmsPolicy(Map<String, Object> params);

    public void saveParcelSmsPolicy(Map<String, Object> params);

    public Page<ParcelLog> getParcelLockerLongTerm(User user, int pageNum, String searchItem, String searchKeyWord);

    void updateStatusParcel(User user, Long parcelLogId, String status);

    Long parcelLogTotalCount(Long aptId, int apiNum, Date date);

    void updateParcelDelStatus();



}
