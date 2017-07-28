package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.BoardComment;
import com.jaha.web.emaul.model.BoardCommentReply;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by doring on 15. 3. 9..
 */
@Repository
public interface BoardCommentReplyRepository extends JpaRepository<BoardCommentReply, Long> {
    List<BoardCommentReply> findFirst20ByCommentIdAndIdLessThan(Long commentId, Long lastReplyId, Sort sort);
    List<BoardCommentReply> findByCommentId(Long commentId, Sort sort);
}
