package com.jaha.web.emaul.repo;


import com.jaha.web.emaul.model.VoterOffline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by doring on 15. 5. 25..
 */
public interface VoterOfflineRepository extends JpaRepository<VoterOffline, Long> {
    VoterOffline findOneByVoteIdAndAptIdAndDongAndHo(Long voteId, Long aptId, String dong, String ho);
    List<VoterOffline> findByVoteId(Long voteId);
}
