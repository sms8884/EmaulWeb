package com.jaha.web.emaul.repo;


import com.jaha.web.emaul.model.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by doring on 15. 3. 19..
 */
@Repository
public interface VoteTypeRepository extends JpaRepository<VoteType, Long> {
    VoteType findOneByMainAndSub(String main, String sub);

    List<VoteType> findByMain(String main);
}
