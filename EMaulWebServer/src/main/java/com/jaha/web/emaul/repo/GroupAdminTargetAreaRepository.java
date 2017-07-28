package com.jaha.web.emaul.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.GroupAdminTargetArea;

/**
 * <pre>
 * Class Name : GroupAdminTargetAreaRepository.java
 * Description : 단체 관리자용 권한 지역 설정 Repository
 * 
 * Modification Information
 * 
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 8. 31.     박남석         Generation
 * </pre>
 *
 * @author 박남석
 * @since 2016. 8. 31.
 * @version 1.0
 */
@Repository
public interface GroupAdminTargetAreaRepository extends JpaRepository<GroupAdminTargetArea, Long> {

    GroupAdminTargetArea findById(Long id);

    GroupAdminTargetArea findByAptIdAndUserId(Long aptId, Long userId);
}
