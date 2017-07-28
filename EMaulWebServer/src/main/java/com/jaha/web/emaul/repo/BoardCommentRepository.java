package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.BoardComment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by doring on 15. 3. 9..
 */
@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    List<BoardComment> findFirst20ByPostIdAndIdLessThan(Long postId, Long lastCommentId, Sort sort);
}
