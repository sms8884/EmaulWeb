package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.GasCheck;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by doring on 15. 4. 6..
 */
@Repository
public interface GasCheckRepository extends JpaRepository<GasCheck, Long> {
    List<GasCheck> findByUserId(Long userId, Sort sort);

}
