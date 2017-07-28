package com.jaha.web.emaul.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.BoardPostAirWord;

/**
 * Created by doring on 15. 6. 16..
 */
@Repository
public interface BoardPostAirWordRepository extends JpaRepository<BoardPostAirWord, String> {

    BoardPostAirWord findByCategoryIdAndRegDate(Long categoryId, String reg_date);

}
