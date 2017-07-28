package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.AptFee;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by doring on 15. 3. 31..
 */
@Repository
public interface AptFeeRepository extends JpaRepository<AptFee, Long> {
    AptFee findOneByHouseIdAndDate(Long houseId, String date);

    AptFee findOneByHouseId(Long houseId, Sort sort);

    List<AptFee> findByHouseId(Long houseId, Sort sort);

    void deleteByHouseIdInAndDate(List<Long> houseIds, String date);
}
