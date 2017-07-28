package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.AptFeeAvr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by doring on 15. 5. 4..
 */
@Repository
public interface AptFeeAvrRepository extends JpaRepository<AptFeeAvr, Long> {
    AptFeeAvr findOneByAptIdAndDateAndHouseSize(Long aptId, String date, String houseSize);
    List<AptFeeAvr> findByAptIdAndHouseSize(Long aptId, String houseSize);
}
