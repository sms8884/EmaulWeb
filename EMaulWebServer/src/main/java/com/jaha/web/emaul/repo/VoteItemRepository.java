package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by doring on 15. 3. 5..
 */
@Repository
public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {
    public List<VoteItem> findByParentId(Long voteId);
}
