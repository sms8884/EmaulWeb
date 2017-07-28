package com.jaha.web.emaul.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.Provision;

/**
 * Created by shavrani on 16. 6. 02..
 */
@Repository
public interface SystemProvisionRepository extends JpaRepository<Provision, String> {
    
    Provision findById(Long id);
    
    Provision findByIdAndStatus(Long id, String status);
}
