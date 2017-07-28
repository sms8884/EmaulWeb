package com.jaha.web.emaul.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jaha.web.emaul.model.AptMgtCorp;

/**
 * Created by doring on 15. 6. 8..
 */
public interface AptMgtCorpRepository extends JpaRepository<AptMgtCorp, Long> {

    AptMgtCorp findByAptId(Long aptId);
}
