package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.Vote;
import com.jaha.web.emaul.model.VoteKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by doring on 15. 2. 25..
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>, JpaSpecificationExecutor<Vote> {
    List<Vote> findFirst10ByIdLessThanAndVisibleIsTrue(Long lastVoteId, Sort sort);

    List<Vote> findFirst10ByIdLessThanAndTypeIdInAndVisibleIsTrueAndTargetAptAndTargetDong(
            Long lastVoteId, List<Long> voteTypeIds, Sort sort, Long targetApt, String targetDong);

    List<Vote> findFirst10ByIdLessThanAndTypeIdInAndVisibleIsTrueAndTargetApt(
            Long lastId, List<Long> typeIds, Long targetApt, Sort sort);

    List<Vote> findByVoteKey(VoteKey voteKey);

    List<Vote> findBySecurityCheckStateGreaterThanAndSecurityNoticeState(Integer securityCheckState, Boolean securityNoticeState);

    Page<Vote> findByTypeIdInAndTargetApt(List<Long> typeIds, Apt targetApt, Pageable pageable);

    Page<Vote> findByTypeIdIn(List<Long> ids, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Vote v SET v.file1 = ?2, v.file2 = ?3 WHERE v.id = ?1")
    int updateVoteFiles(Long voteId, String file1, String file2);

}
