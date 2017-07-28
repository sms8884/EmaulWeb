package com.jaha.web.emaul.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.ParcelLocker;

/**
 * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 26.
 * @description 무인택배함 Repository
 */
@Repository
public interface ParcelLockerRepository extends JpaRepository<ParcelLocker, Long>, JpaSpecificationExecutor<ParcelLocker> {
    List<ParcelLocker> findByAptId(Long aptId);
}
