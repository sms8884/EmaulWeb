package com.jaha.web.emaul.repo;

import java.util.List;

import com.jaha.web.emaul.model.Hashtag;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    @Query(value = "SELECT id, post_id, name FROM board_post_hashtag WHERE post_id = :postId", nativeQuery = true)
    List<Hashtag> findByPostId(@Param("postId") Long postId) throws DataAccessException;
}
