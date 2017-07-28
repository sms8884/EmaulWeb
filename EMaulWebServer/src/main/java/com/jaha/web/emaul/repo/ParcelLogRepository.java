package com.jaha.web.emaul.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.ParcelLog;

/**
 * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 26.
 * @description 무인택배함사용기록 Repository
 */
@Repository
public interface ParcelLogRepository extends JpaRepository<ParcelLog, Long>, JpaSpecificationExecutor<ParcelLog> {

    Page<ParcelLog> findByParcelLockerIdAndApiNumber(Long lockerId, int apiNum, Pageable pageable);

    Page<ParcelLog> findByParcelLockerAptIdAndApiNumberAndRegDateBeforeAndDelYn(Long aptId, int apiNum, Date date, String delYn, Pageable pageable);

    // 핸드폰으로검색
    Page<ParcelLog> findByParcelLockerAptIdAndApiNumberAndPhoneAndDelYn(Long aptId, int apiNum, String phone, String delYn, Pageable pageable);

    // 택배함위치로검색
    Page<ParcelLog> findByParcelLockerAptIdAndApiNumberAndParcelLockerLocationAndDelYn(Long aptId, int apiNum, String location, String delYn, Pageable pageable);

    // 동-호 검색
    Page<ParcelLog> findByParcelLockerAptIdAndApiNumberAndDongAndHoAndDelYn(Long aptId, int apiNum, int dong, int ho, String delYn, Pageable pageable);

    List<ParcelLog> findByApiNumberAndFindDateNotNull(int apiNum);

    Long countByParcelLockerAptIdAndApiNumberAndRegDateBeforeAndDelYn(Long aptId, int apiNum, Date date, String delYn);


}
