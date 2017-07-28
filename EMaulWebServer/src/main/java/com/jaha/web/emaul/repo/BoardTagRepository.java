package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.BoardTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by doring on 15. 6. 16..
 */
@Repository
public interface BoardTagRepository extends JpaRepository<BoardTag, String> {
}
