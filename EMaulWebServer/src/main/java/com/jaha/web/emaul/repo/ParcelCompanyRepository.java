package com.jaha.web.emaul.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.ParcelCompany;

/**
 * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 26.
 * @description 무인택배함 회사 Repository
 */
@Repository
public interface ParcelCompanyRepository extends JpaRepository<ParcelCompany, Long> {

}
