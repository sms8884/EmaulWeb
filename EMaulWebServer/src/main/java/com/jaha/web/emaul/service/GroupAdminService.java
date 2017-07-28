package com.jaha.web.emaul.service;

import java.util.HashMap;
import java.util.List;

import com.jaha.web.emaul.model.GroupAdminTargetArea;

/**
 * <pre>
 * Class Name : GroupAdminService.java
 * Description : 단체 관리자용 Service
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
public interface GroupAdminService {
    GroupAdminTargetArea getTargetArea(Long userId, Long aptId);

    List<String> getSidoNames(Long userId, Long aptId);

    List<String> getSigunguNames(String sidoName, Long userId, Long aptId);
    
    List<HashMap> getGugunList(String sidoName, Long userId, Long aptId);
}
