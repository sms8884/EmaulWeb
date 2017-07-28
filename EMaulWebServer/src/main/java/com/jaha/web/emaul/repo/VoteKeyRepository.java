package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.Vote;
import com.jaha.web.emaul.model.VoteKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2015-06-28.
 */
@Repository
public interface VoteKeyRepository extends JpaRepository<VoteKey, Long> {
    Page<VoteKey> findByAptId(Long aptId, Pageable pageable);
    Page<VoteKey> findByAptIdAndKeyGrantYn(Long aptId, String keyGrandYn, Pageable pageable);
    List<VoteKey> findByVkIdIn(List<Long> vkIds);

}
