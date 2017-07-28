package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.AptScheduler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by shavrani on 16. 05. 11..
 */
@Repository
public interface AptSchedulerRepository extends JpaRepository<AptScheduler, Long> {

    public AptScheduler findById(Long id);
    
    public AptScheduler findByIdAndAptId(Long id, Long aptId);
    
    public int deleteById(Long id);
    
}
