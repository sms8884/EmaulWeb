package com.jaha.web.emaul.service;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.jaha.web.emaul.constants.UserPrivacy;
import com.jaha.web.emaul.model.BoardCategory;
import com.jaha.web.emaul.model.BoardComment;
import com.jaha.web.emaul.model.BoardCommentReply;
import com.jaha.web.emaul.model.BoardPost;
import com.jaha.web.emaul.model.BoardPostAirWord;
import com.jaha.web.emaul.model.BoardTag;
import com.jaha.web.emaul.model.Hashtag;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.util.ScrollPage;
import com.jaha.web.emaul.v2.model.board.BoardCategoryVo;

/**
 * Created by doring on 15. 3. 9..
 */

public interface BoardService {
    BoardPost saveAndFlush(BoardPost post);

    BoardComment save(BoardComment comment);

    BoardTag save(BoardTag tag);

    BoardCommentReply save(BoardCommentReply reply);

    void updateTodayContent(Long postId, String content);

    BoardPost get(User user, long postId);

    BoardPost get(long postId);

    BoardCategory getCategory(Long id);

    BoardCategory getTodayCategory(Long aptId, String delYn);

    BoardComment getComment(Long commentId);

    List<BoardCategory> getCategories(Long userId, String type, String delYn);

    List<BoardCategory> getAllTodayCategories(User user, String delYn);

    Page<BoardPost> getPostByAdmin(Specification<BoardPost> spec, Pageable pageable);

    Page<BoardPost> getPosts(User user, Long categoryId, Pageable pageable);

    Page<BoardPost> getPostsLessThan(User user, Long categoryId, Date regDateForLess, Pageable pageable, int exceptCount);

    List<BoardPost> getFirst16Posts(Long userId, String delYn);

    Page<BoardPost> getAllTodayPosts(User user, Pageable pageable);

    List<BoardPost> getFirst4Todays(User user, Long categoryId);

    ScrollPage<BoardComment> getComments(Long userId, Long postId, Long lastCommentId);

    ScrollPage<BoardCommentReply> getCommentReplies(Long userId, Long commentId);

    Boolean deletePost(Long userId, Long postId);

    void deletePostImages(BoardPost post);

    Boolean deleteComment(Long userId, Long commentId);

    Boolean deleteCommentReply(Long userId, Long replyId);

    Long getPostSize(Long categoryId);

    List<BoardTag> getBoardTags();

    BoardTag getBoardTag(String name);

    Page<BoardPost> getCategoryTodayPosts(User user, Pageable pageable, String cateogry);

    List<Map<String, Object>> selectNewsList(Map<String, Object> params) throws SQLException;

    int selectNewsListCount(Map<String, Object> params) throws SQLException;

    Map<String, Object> getNews(Long postId);

    Long insertNews(Map<String, Object> params);

    Long insertNews(Map<String, Object> params, List<String> hashtags);

    void updateNews(Map<String, Object> params);

    void updateNews(Map<String, Object> params, List<String> hashtags);

    void deleteNews(Long postId);

    List<Map<String, Object>> selectUserNewsList(Map<String, Object> params);

    int selectUserNewsListCount(Map<String, Object> params);

    String makeUserNameForPost(User user, UserPrivacy userPrivacy);

    // void modifyTtsBoardPost(long postId, String voiceGubun, int voiceVolume,
    // String pushSendYn) throws Exception;
    void modifyAirSendDateOfTts(long postId);

    List<BoardPost> getTopFixBoardPost(Long categoryId, Boolean topFix);

    List<Hashtag> fetchHashTags(Long postId);

    int getTotalContentLength(Long categoryId);


    /**
     * @param user
     * @param categoryId
     * @param pageable
     * @return
     */
    Page<BoardPost> getPostsTTS(User user, Long categoryId, Pageable pageable);


    /**
     * 게시판 카테고리 등록
     *
     * @param boardCategoryVo
     * @return
     */
    int insertBoardCategory(BoardCategoryVo boardCategoryVo);

    /**
     * 게시판 카테고리 수정
     *
     * @param boardCategoryVo
     * @return
     */
    int updateBoardCategory(BoardCategoryVo boardCategoryVo);

    /**
     * 게시판 카테고리 조회
     *
     * @param id
     * @return
     */
    BoardCategoryVo getBoardCategory(Long id);

    /**
     * 등록/수정한 글자수 조회
     *
     * @param bpaw
     * @return
     */
    BoardPostAirWord saveAndFlush(BoardPostAirWord bpaw);

}
