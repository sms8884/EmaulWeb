package com.jaha.web.emaul.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.AptApFirmwareLog;

/**
 * Created by shavrani on 16-06-17
 */
@Repository
public interface AptApFirmwareLogRepository extends JpaRepository<AptApFirmwareLog, Long> {

    AptApFirmwareLog findByApId(Long id);

}
