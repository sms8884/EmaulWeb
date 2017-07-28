package com.jaha.web.emaul.repo;

import java.util.Date;
import java.util.List;

import javax.persistence.OrderBy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.BoardPost;

/**
 * Created by doring on 15. 3. 9..
 */
@Repository
public interface BoardPostRepository extends JpaRepository<BoardPost, Long>, JpaSpecificationExecutor<BoardPost> {


    @SuppressWarnings("rawtypes")
    @Override
    Page<BoardPost> findAll(Specification spec, Pageable pagealbe);

    @SuppressWarnings("rawtypes")
    @OrderBy("reg_date desc")
    Page<BoardPost> findAllOrderByRegDate(Specification spec, Pageable pagealbe);

    @SuppressWarnings("rawtypes")
    @OrderBy("top_fix desc, reg_date desc")
    Page<BoardPost> findAllOrderByTopFix(Specification spec, Pageable pagealbe);

    @Override
    Page<BoardPost> findAll(Pageable pagealbe);

    List<BoardPost> findFirst16ByCategoryIdIn(List<Long> categoryIds, Sort sort);

    List<BoardPost> findFirst16ByCategoryIdInAndDisplayYn(List<Long> categoryIds, Sort sort, String displayYn);

    List<BoardPost> findFirst4ByCategoryIdIn(List<Long> categoryIds, Sort sort);

    Page<BoardPost> findByCategoryIdIn(List<Long> categoryIds, Pageable pageable);

    Page<BoardPost> findByCategoryIdInAndDisplayYn(List<Long> categoryIds, Pageable pageable, String displayYn);

    Page<BoardPost> findByCategoryId(Long categoryId, Pageable pageable);

    Page<BoardPost> findByCategoryIdAndDisplayYn(Long categoryId, Pageable pageable, String displayYn);

    Page<BoardPost> findByCategoryIdAndRegDateLessThan(Long categoryId, Date less, Pageable pageable);

    Long countByCategoryId(Long categoryId);

    Long countByCategoryIdAndDisplayYn(Long categoryId, String DisplayYn);

    Page<BoardPost> findByNewsCategoryAndCategoryIdIn(List<Long> categoryIds, Pageable pageable, String newsCategory);

    Page<BoardPost> findByNewsCategoryAndCategoryIdInAndDisplayYn(List<Long> categoryIds, Pageable pageable, String newsCategory, String displayYn);


    List<BoardPost> findByCategoryIdAndTopFix(Long categoryId, Boolean topFix);

    List<BoardPost> findByCategoryIdAndTopFixAndDisplayYn(Long categoryId, Boolean topFix, String displayYn);

    // 게시판 검색조건 추가
    // @Query(value = "SELECT * FROM board_post WHERE category_id = :categoryId AND display_yn = 'Y' ", nativeQuery = true)
    // public Page<BoardPost> findByCategoryIdAndDisplayYnAndTitleLike(@Param("categoryId") Long categoryId, );
    //
    // @Query(value = "SELECT * FROM board_post WHERE category_id = :categoryId AND display_yn = 'Y' AND reg_date > CURDATE()", nativeQuery = true)
    // public List<BoardPost> findByCategoryIdAndDisplayYnAndContentLike(@Param("categoryId") Long categoryId);


    ///////////////////////////////////////////////// 방송 게시판 칼럼 수정 추가 /////////////////////////////////////////////////
    // @Modifying
    // @Query("UPDATE BoardPost SET voice_gubun = :voiceGubun, voice_volume = :voiceVolume, push_send_yn = :pushSendYn WHERE id = :postId")
    // int updateTtsBoardPost(@Param("postId") long postId
    // , @Param("voiceGubun") String voiceGubun
    // , @Param("voiceVolume") Integer voiceVolume
    // , @Param("pushSendYn") String pushSendYn
    // );
    @Modifying
    @Query("UPDATE BoardPost SET air_send_date = now() WHERE id = :postId")
    void updateBoardPostAirSendDate(@Param("postId") long postId);

    /*
     * SELECT * FROM emaul.board_post as a where a.category_id = '6848' AND a.display_yn = 'Y' and a.reg_date > curdate();
     */
    @Query(value = "SELECT * FROM board_post WHERE category_id = :categoryId AND display_yn = 'Y' AND reg_date >= CURDATE() ", nativeQuery = true)
    public List<BoardPost> findByCategoryIdAndDisplayYnAndRegDateLike(@Param("categoryId") long categoryId);

    @Query(value = "SELECT * FROM board_post WHERE category_id = :categoryId AND display_yn = 'Y' AND mod_date >= CURDATE() ", nativeQuery = true)
    public List<BoardPost> findByCategoryIdAndDisplayYnAndModDateLike(@Param("categoryId") long categoryId);

    @Query("SELECT b FROM BoardPost b WHERE b.category.id = :categoryId AND b.displayYn = 'Y' AND IFNULL(b.airReserveDate, b.regDate) <  DATE_FORMAT(NOW(), '%Y-%m-%d%p%h:%i')")
    public Page<BoardPost> findByCategoryIdAndDisplayYn(@Param("categoryId") long categoryId, Pageable page);

    ///////////////////////////////////////////////// 방송 게시판 칼럼 수정 추가 ///////////////////////////////////////////////////

    BoardPost findByIdAndDisplayYn(long postId, String displayYn);
}
