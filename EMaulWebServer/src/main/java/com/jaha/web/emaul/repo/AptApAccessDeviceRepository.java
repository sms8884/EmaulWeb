package com.jaha.web.emaul.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.AptApAccessDevice;

/**
 * Created by shavrani on 16-08-09
 */
@Repository
public interface AptApAccessDeviceRepository extends JpaRepository<AptApAccessDevice, Long>, JpaSpecificationExecutor<AptApAccessDevice> {
}
