package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.AptFeePush;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author 전강욱(realsnake@jahasmart.com)
 */
@Repository
public interface AptFeePushRepository extends JpaRepository<AptFeePush, Long> {

	// http://docs.spring.io/spring-data/jpa/docs/1.7.0.RELEASE/reference/html/#jpa.query-methods
    Page<AptFeePush> findByAptId(Long aptId, Pageable pageable);

    Page<AptFeePush> findByAptIdOrderByIdDesc(Long aptId, Pageable pageable);
    
    @Modifying
    @Query("UPDATE AptFeePush SET gubun = :gubun, book_yn = :bookYn, send_date = :sendDate, contents = :contents, mod_id = :modId, mod_date = now() WHERE id = :id AND send_yn = 'N'")
    int updateAptFeePush(@Param("id") long id
    		, @Param("gubun") String gubun
    		, @Param("bookYn") String bookYn
    		, @Param("sendDate") String sendDate
    		, @Param("contents") String contents
    		, @Param("modId") long modId
    );
    
}
