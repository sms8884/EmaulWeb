package com.jaha.web.emaul.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.MetroNews;

/**
 * <pre>
 * Class Name : MetroNewsRepository.java
 * Description : 메트로신문사 뉴스 Repository
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 8. 27.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 8. 27.
 * @version 1.0
 */
@Repository
public interface MetroNewsRepository extends JpaRepository<MetroNews, String> {

    List<MetroNews> findByNewsActionIn(List<String> actions);

}
