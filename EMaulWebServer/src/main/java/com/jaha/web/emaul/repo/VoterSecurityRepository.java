package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.VoterSecurity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by imac1 on 2016. 3. 7..
 */
@Repository
public interface VoterSecurityRepository extends JpaRepository<VoterSecurity, String> {
    List<VoterSecurity> findByVoteIdAndItemId(Long voteId, Long itemId, Sort sort);
    List<VoterSecurity> findByVoteId(Long voteId, Sort sort);
}
