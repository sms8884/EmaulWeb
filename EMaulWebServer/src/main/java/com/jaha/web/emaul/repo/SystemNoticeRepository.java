package com.jaha.web.emaul.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.SystemNotice;

/**
 * Created by shavrani on 16. 05. 26..
 */
@Repository
public interface SystemNoticeRepository extends JpaRepository<SystemNotice, Long> {

    public SystemNotice findById(Long id);

    public int deleteById(Long id);

}
