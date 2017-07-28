package com.jaha.web.emaul.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.SystemFaq;

/**
 * Created by shavrani on 16. 06. 01..
 */
@Repository
public interface SystemFaqRepository extends JpaRepository<SystemFaq, Long> {

    public SystemFaq findById(Long id);

    public int deleteById(Long id);

}
