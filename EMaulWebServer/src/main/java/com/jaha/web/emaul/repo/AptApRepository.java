package com.jaha.web.emaul.repo;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jaha.web.emaul.model.AptAp;

/**
 * Created by shavrani on 16-06-17
 */
@Repository
public interface AptApRepository extends JpaRepository<AptAp, Long> {
    
    AptAp findById(Long id);
    
}
