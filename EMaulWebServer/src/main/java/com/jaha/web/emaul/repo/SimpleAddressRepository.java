package com.jaha.web.emaul.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.SimpleAddress;

/**
 * <pre>
 * Class Name : SimpleAddressRepository.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 9. 12.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 9. 12.
 * @version 1.0
 */
@Repository
public interface SimpleAddressRepository extends JpaRepository<SimpleAddress, String> {

    @Query(value = "SELECT * FROM simple_address WHERE sido_nm LIKE CONCAT(:sido, '%')", nativeQuery = true)
    List<SimpleAddress> findBySido(@Param("sido") String sido);

    @Query(value = "SELECT * FROM simple_address WHERE sido_nm LIKE CONCAT(:sido, '%') AND sgg_nm LIKE CONCAT(:sigungu, '%')", nativeQuery = true)
    List<SimpleAddress> findBySigungu(@Param("sido") String sido, @Param("sigungu") String sigungu);

    @Query(value = "SELECT * FROM simple_address WHERE sido_nm LIKE CONCAT(:sido, '%') AND sgg_nm LIKE CONCAT(:sigungu, '%') AND emd_nm LIKE CONCAT(:dong, '%')", nativeQuery = true)
    List<SimpleAddress> findByDong(@Param("sido") String sido, @Param("sigungu") String sigungu, @Param("dong") String dong);

    @Query(value = "SELECT * FROM simple_address WHERE sido_nm = :sido AND emd_nm > ''", nativeQuery = true)
    List<SimpleAddress> findDongListBySido(@Param("sido") String sido);

    @Query(value = "SELECT * FROM simple_address WHERE sido_nm = :sido AND sgg_nm = :sigungu AND emd_nm > ''", nativeQuery = true)
    List<SimpleAddress> findDongListBySidoAndSigungu(@Param("sido") String sido, @Param("sigungu") String sigungu);

    @Query(value = "SELECT * FROM simple_address WHERE sido_nm = :sido AND sgg_nm > ''", nativeQuery = true)
    List<SimpleAddress> findSigunguListBySido(@Param("sido") String sido);

}
