package com.jaha.web.emaul.service;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.jaha.web.emaul.constants.Constants;
import com.jaha.web.emaul.constants.UserPrivacy;
import com.jaha.web.emaul.mapper.BoardMapper;
import com.jaha.web.emaul.mapper.NewsMapper;
import com.jaha.web.emaul.mapper.UserMapper;
import com.jaha.web.emaul.model.BoardCategory;
import com.jaha.web.emaul.model.BoardComment;
import com.jaha.web.emaul.model.BoardCommentReply;
import com.jaha.web.emaul.model.BoardPost;
import com.jaha.web.emaul.model.BoardPostAirWord;
import com.jaha.web.emaul.model.BoardTag;
import com.jaha.web.emaul.model.Hashtag;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.model.UserNickname;
import com.jaha.web.emaul.repo.BoardCategoryRepository;
import com.jaha.web.emaul.repo.BoardCommentReplyRepository;
import com.jaha.web.emaul.repo.BoardCommentRepository;
import com.jaha.web.emaul.repo.BoardPostAirWordRepository;
import com.jaha.web.emaul.repo.BoardPostRepository;
import com.jaha.web.emaul.repo.BoardTagRepository;
import com.jaha.web.emaul.repo.HashtagRepository;
import com.jaha.web.emaul.repo.UserRepository;
import com.jaha.web.emaul.util.ScrollPage;
import com.jaha.web.emaul.util.TagUtils;
import com.jaha.web.emaul.v2.model.board.BoardCategoryVo;

/**
 * Created by doring on 15. 3. 9..
 */
@Service
public class BoardServiceImpl implements BoardService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String DISP_Y = "Y";

    @Autowired
    private BoardCategoryRepository boardCategoryRepository;
    @Autowired
    private BoardPostRepository boardPostRepository;
    @Autowired
    private BoardCommentRepository boardCommentRepository;
    @Autowired
    private BoardCommentReplyRepository boardCommentReplyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardTagRepository boardTagRepository;
    @Autowired
    private BoardPostAirWordRepository boardPostAirWordRepository;

    @Autowired
    private NewsMapper newsMapper;
    @Autowired
    private HashtagRepository hashtagRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private GcmService gcmService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public BoardPost saveAndFlush(BoardPost post) {
        return boardPostRepository.saveAndFlush(post);
    }

    @Override
    public BoardComment save(BoardComment comment) {
        return boardCommentRepository.saveAndFlush(comment);
    }

    @Override
    public BoardTag save(BoardTag tag) {
        return boardTagRepository.save(tag);
    }

    @Override
    public BoardCommentReply save(BoardCommentReply reply) {
        return boardCommentReplyRepository.save(reply);
    }

    @Override
    public void updateTodayContent(Long postId, String content) {
        jdbcTemplate.update("UPDATE board_post SET content=? WHERE id=?", new Object[] {content, postId});
    }

    @Override
    @Transactional
    public BoardPost get(User user, long postId) {
        BoardPost post = boardPostRepository.findByIdAndDisplayYn(postId, DISP_Y);
        post.increaseViewCount();

        if (user != null)
            post.isDeletable = user.type.jaha || post.user.id.equals(user.id);

        return post;
    }

    @Override
    public BoardPost get(long postId) {
        return get(null, postId);
    }

    @Override
    public BoardCategory getCategory(Long id) {
        return boardCategoryRepository.findOne(id);
    }

    @Override
    public BoardCategory getTodayCategory(Long aptId, String delYn) {
        if (StringUtils.isEmpty(delYn)) {
            delYn = "N";
        }
        List<BoardCategory> bcList = boardCategoryRepository.findByTypeAndAptIdAndDelYn("today", aptId, delYn);
        if (bcList != null && !bcList.isEmpty()) {
            return bcList.get(0);
        }
        return null;
    }

    @Override
    public BoardComment getComment(Long commentId) {
        return boardCommentRepository.findOne(commentId);
    }

    @Override
    public List<BoardCategory> getCategories(Long userId, String type, String delYn) {
        final User user = userRepository.findOne(userId);

        if (StringUtils.isEmpty(delYn)) {
            delYn = "N";
        }

        List<BoardCategory> list = boardCategoryRepository.findByTypeAndAptIdAndDelYn(type, user.house.apt.id, delYn, new Sort(Sort.Direction.ASC, "ord"));

        for (BoardCategory boardCategory : list) {
            boardCategory.isWritable = boardCategory.isUserWritable(user);
        }

        return Lists.newArrayList(Collections2.filter(list, new Predicate<BoardCategory>() {
            @Override
            public boolean apply(BoardCategory input) {
                return input.isUserReadable(user);
            }
        }));
    }

    @Override
    public List<BoardCategory> getAllTodayCategories(User user, String delYn) {
        if (!user.type.jaha) {
            return null;
        }

        if (StringUtils.isEmpty(delYn)) {
            delYn = "N";
        }

        return boardCategoryRepository.findByTypeAndDelYn("today", delYn, new Sort(Sort.Direction.DESC, "id"));
    }

    private List<BoardPost> getPosts(Long userId, Long categoryId, String sido, String sigungu, Long lastPostId, int from, int count, Date lessThan) {
        if (lastPostId == null || lastPostId == 0l) {
            lastPostId = Long.MAX_VALUE;
        }

        logger.debug("<<사용자아이디: {}, 카테고리아이디: {}, 시도: {}, 시군구: {}, 마지막게시글번호: {}, from: {}, count: {}, lessThan: {}>>", userId, categoryId, sido, sigungu, lastPostId, from, count, lessThan);

        String dateLessThan = "";
        if (lessThan != null) {
            dateLessThan = " AND b.reg_date < '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lessThan) + "' ";
        }

        User user = userRepository.findOne(userId);

        List<BoardPost> list = jdbcTemplate.query("SELECT b.*, u.id as 'u_user_id', u.nickname, u.has_profile_image, c.name AS 'category_name' "
                + "FROM board_post b, user u, board_category c WHERE b.display_yn = 'Y' AND (b.category_id=? OR \n"
                + "(b.range_sido=IF(b.range_sigungu='', ?, null) OR b.range_sigungu=? OR b.range_all=1)) " + "AND b.id < ? AND b.user_id = u.id AND b.category_id=c.id " + dateLessThan
                + " AND b.display_yn = 'Y' ORDER BY b.reg_date DESC LIMIT ?, ?", new Object[] {categoryId, sido, sigungu, lastPostId, from, count}, new RowMapper<BoardPost>() {
                    @Override
                    public BoardPost mapRow(ResultSet rs, int rowNum) throws SQLException {
                        BoardPost post = new BoardPost();
                        post.id = rs.getLong("id");
                        post.content = rs.getString("content");
                        post.imageCount = rs.getInt("image_count");
                        post.rangeSido = rs.getString("range_sido");
                        post.rangeSigungu = rs.getString("range_sigungu");
                        post.regDate = new Date(rs.getTimestamp("reg_date").getTime());
                        // post.title = rs.getString("title");

                        User user = new User();
                        UserNickname nickname = new UserNickname();
                        nickname.name = rs.getString("nickname");
                        user.id = rs.getLong("u_user_id");
                        user.setNickname(nickname.name == null ? null : nickname);
                        user.hasProfileImage = rs.getBoolean("has_profile_image");
                        post.user = user;

                        BoardCategory category = new BoardCategory();
                        category.name = rs.getString("category_name");
                        post.category = category;

                        // 게시판 카테고리 모드가 html이고 게시판 title이 비어있는 경우에만 게시판 내용을 변환하여 타이틀로 수정
                        if ("html".equals(post.category.contentMode) && StringUtils.isBlank(rs.getString("title"))) {
                            String tempContent = post.content;
                            String tempTitle = TagUtils.removeTag(tempContent);

                            if (tempTitle.length() > 300) {
                                post.title = tempTitle.substring(0, 300);
                            } else {
                                post.title = tempTitle;
                            }
                        } else {
                            post.title = rs.getString("title");
                        }

                        return post;
                    }
                });
        if (user != null) {
            for (BoardPost post : list) {
                post.isDeletable = user.type.jaha || user.id.equals(post.user == null ? 0l : post.user.id);
            }
        }
        return list;
    }

    // [START] 단체관리자 기능 추가 by PNS 2016.09.30
    // 장기적으로는 게시판과 뉴스를 분리하는 방향으로 하기 위해서 일단은 getPosts()를 getNewsPosts()로 하나 더 만듬 by PNS 20160930
    // 현재는 메인화면에서 뉴스게시물 4개 가져오는곳에서만 사용하는 듯함
    private List<BoardPost> getNewsPosts(Long userId, Long categoryId, String sido, String sigungu, Long lastPostId, int from, int count, Date lessThan) {
        if (lastPostId == null || lastPostId == 0l) {
            lastPostId = Long.MAX_VALUE;
        }

        logger.debug("[getNewsPosts] <<사용자아이디: {}, 카테고리아이디: {}, 시도: {}, 시군구: {}, 마지막게시글번호: {}, from: {}, count: {}, lessThan: {}>>", userId, categoryId, sido, sigungu, lastPostId, from, count,
                lessThan);

        String dateLessThan = "";
        if (lessThan != null) {
            dateLessThan = " AND b.reg_date < '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lessThan) + "' ";
        }

        User user = userRepository.findOne(userId);


        List<BoardPost> list = jdbcTemplate.query("SELECT b.*, u.id as 'u_user_id', u.nickname, u.has_profile_image, c.name AS 'category_name' "
                + "FROM board_post b, user u, board_category c WHERE b.display_yn = 'Y' AND (b.category_id=? OR \n"
                // [START] 단체관리자 기능 추가 : 마을 뉴스 리스트 by PNS 2016.09.26
                // 마을뉴스 타겟주소 구조가 '경기도'의 경우 '수원시 권선구' 시/구가 같이 있는 구조라서 '수원시' 전체에 권한을 주기가 힘듬 ==> 문자열에 들어있는지만 체크하는 것으로 수정하고 range_all은 제일 먼제 체크하게 수정함
                + "(b.range_all=1 OR b.range_sido=IF(b.range_sigungu='', ?, null) OR (b.range_sigungu!='' AND LOCATE(b.range_sigungu, ?) > 0))) "
                + "AND b.id < ? AND b.user_id = u.id AND b.category_id=c.id " + dateLessThan
                // [END]
                + " AND b.display_yn = 'Y' ORDER BY b.reg_date DESC LIMIT ?, ?", new Object[] {categoryId, sido, sigungu, lastPostId, from, count}, new RowMapper<BoardPost>() {
                    @Override
                    public BoardPost mapRow(ResultSet rs, int rowNum) throws SQLException {
                        BoardPost post = new BoardPost();
                        post.id = rs.getLong("id");
                        post.content = rs.getString("content");
                        post.imageCount = rs.getInt("image_count");
                        post.rangeSido = rs.getString("range_sido");
                        post.rangeSigungu = rs.getString("range_sigungu");
                        post.regDate = new Date(rs.getTimestamp("reg_date").getTime());
                        // post.title = rs.getString("title");

                        User user = new User();
                        UserNickname nickname = new UserNickname();
                        nickname.name = rs.getString("nickname");
                        user.id = rs.getLong("u_user_id");
                        user.setNickname(nickname.name == null ? null : nickname);
                        user.hasProfileImage = rs.getBoolean("has_profile_image");
                        post.user = user;

                        BoardCategory category = new BoardCategory();
                        category.name = rs.getString("category_name");
                        post.category = category;

                        // 게시판 카테고리 모드가 html이고 게시판 title이 비어있는 경우에만 게시판 내용을 변환하여 타이틀로 수정
                        if ("html".equals(post.category.contentMode) && StringUtils.isBlank(rs.getString("title"))) {
                            String tempContent = post.content;
                            String tempTitle = TagUtils.removeTag(tempContent);

                            if (tempTitle.length() > 300) {
                                post.title = tempTitle.substring(0, 300);
                            } else {
                                post.title = tempTitle;
                            }
                        } else {
                            post.title = rs.getString("title");
                        }

                        return post;
                    }
                });
        if (user != null) {
            for (BoardPost post : list) {
                post.isDeletable = user.type.jaha || user.id.equals(post.user == null ? 0l : post.user.id);
            }
        }
        return list;
    }

    // [END]

    private int getAllPostsCount(Long userId, Long categoryId, String sido, String sigungu) {
        Integer count =
                jdbcTemplate.queryForObject(
                        "SELECT count(b.id) " + "FROM board_post b, board_category c WHERE b.display_yn = 'Y' AND (b.category_id=? OR  \n"
                                + "(b.range_sido=IF(b.range_sigungu='', ?, null) OR b.range_sigungu=? OR b.range_all=1)) " + "AND b.category_id=c.id",
                        new Object[] {categoryId, sido, sigungu}, Integer.class);
        return count;
    }

    @Override
    public Page<BoardPost> getPosts(User user, Long categoryId, Pageable pageable) {
        return boardPostRepository.findByCategoryIdAndDisplayYn(categoryId, pageable, DISP_Y);
    }

    @Override
    public Page<BoardPost> getPostsTTS(User user, Long categoryId, Pageable pageable) {
        return boardPostRepository.findByCategoryIdAndDisplayYn(categoryId, pageable);
    }

    @Override
    public Page<BoardPost> getPostByAdmin(Specification<BoardPost> spec, Pageable pageable) {

        return boardPostRepository.findAll(spec, pageable);
    }


    @Override
    public Page<BoardPost> getPostsLessThan(User user, Long categoryId, Date regDateForLess, Pageable pageable, int exceptCount) {
        logger.debug("<<메인화면 마을뉴스 하단 전체(마을뉴스가 4개이상일 경우에만) 조회>>");
        List<BoardPost> list = this.getPosts4Main(user.id, categoryId, user.house.apt.address.시도명, user.house.apt.address.시군구명, null, pageable.getPageNumber() * pageable.getPageSize(),
                pageable.getPageSize(), regDateForLess);
        Page<BoardPost> page = new PageImpl<BoardPost>(list, pageable, getAllPostsCount(user.id, categoryId, user.house.apt.address.시도명, user.house.apt.address.시군구명) - exceptCount);
        return page;
    }

    @Override
    public List<BoardPost> getFirst16Posts(Long userId, String delYn) {
        User user = userRepository.findOne(userId);

        if (StringUtils.isEmpty(delYn)) {
            delYn = "N";
        }

        List<BoardCategory> categories = boardCategoryRepository.findByTypeAndAptIdAndDelYn("community", user.house.apt.id, delYn);
        List<Long> categoryIds = Lists.transform(categories, input -> input.id);

        List<BoardPost> posts = boardPostRepository.findFirst16ByCategoryIdInAndDisplayYn(categoryIds, new Sort(Sort.Direction.DESC, "regDate"), DISP_Y);
        for (BoardPost post : posts) {
            post.isDeletable = user.type.jaha || user.id.equals(post.user == null ? 0l : post.user.id);
        }

        return posts;
    }

    @Override
    public Page<BoardPost> getAllTodayPosts(User user, Pageable pageable) {
        if (!user.type.jaha) {
            return null;
        }

        List<BoardCategory> categories = getAllTodayCategories(user, "N");

        if (categories == null) {
            return null;
        }

        List<Long> categoryIds = Lists.transform(categories, new Function<BoardCategory, Long>() {
            @Override
            public Long apply(BoardCategory input) {
                return input.id;
            }
        });
        return boardPostRepository.findByCategoryIdInAndDisplayYn(categoryIds, pageable, DISP_Y);
    }

    @Override
    public List<BoardPost> getFirst4Todays(User user, Long categoryId) {
        logger.debug("<<메인화면 마을뉴스 조회>>");
        return getNewsPosts(user.id, categoryId, user.house.apt.address.시도명, user.house.apt.address.시군구명, null, 0, 4, null);
    }

    @Override
    public ScrollPage<BoardComment> getComments(Long userId, Long postId, Long lastCommentId) {
        if (lastCommentId == null || lastCommentId == 0l) {
            lastCommentId = Long.MAX_VALUE;
        }
        User user = userRepository.findOne(userId);

        ScrollPage<BoardComment> ret = new ScrollPage<>();
        List<BoardComment> list = boardCommentRepository.findFirst20ByPostIdAndIdLessThan(postId, lastCommentId, new Sort(Sort.Direction.DESC, "regDate"));
        ret.setContent(list);
        final int size = list.size();
        if (size >= 20) {
            ret.setNextPageToken(String.valueOf(list.get(size - 1).id));
        }
        if (user != null) {
            for (BoardComment comment : list) {
                comment.isDeletable = user.type.jaha || user.id.equals(comment.user == null ? 0l : comment.user.id);
            }
        }
        return ret;
    }

    @Override
    public ScrollPage<BoardCommentReply> getCommentReplies(Long userId, Long commentId) {
        User user = userRepository.findOne(userId);

        ScrollPage<BoardCommentReply> ret = new ScrollPage<>();
        List<BoardCommentReply> list = boardCommentReplyRepository.findByCommentId(commentId, new Sort(Sort.Direction.DESC, "regDate"));
        ret.setContent(list);
        if (user != null) {
            for (BoardCommentReply reply : list) {
                reply.isDeletable = user.type.jaha || user.id.equals(reply.user == null ? 0l : reply.user.id);
            }
        }
        return ret;

    }

    @Override
    @Transactional
    public Boolean deletePost(Long userId, Long postId) {
        User user = userRepository.findOne(userId);
        BoardPost post = boardPostRepository.findOne(postId);

        if (user != null && post != null && post.user != null) {
            if (user.type.jaha || user.type.admin || user.id.equals(post.user.id)) {
                deletePostImages(post);
                // boardPostRepository.delete(postId);
                post.delete(userId);
                return true;
            }
        }
        return false;
    }

    @Override
    public void deletePostImages(BoardPost post) {
        long postId = post.id;
        long postParentNum = post.id / 1000l;

        File dir = new File(String.format("/nas/EMaul/board/post/image/%s/%s", postParentNum, postId));
        if (!dir.exists()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                file.delete();
            }
        }
        dir.delete();
    }

    @Override
    public Boolean deleteComment(Long userId, Long commentId) {
        User user = userRepository.findOne(userId);
        BoardComment comment = boardCommentRepository.findOne(commentId);

        if (user != null && comment != null && comment.user != null) {
            if (user.type.jaha || user.type.admin || user.id.equals(comment.user.id)) {
                boardCommentRepository.delete(commentId);
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean deleteCommentReply(Long userId, Long replyId) {
        User user = userRepository.findOne(userId);
        BoardCommentReply reply = boardCommentReplyRepository.findOne(replyId);

        if (user != null && reply != null && reply.user != null) {
            if (user.type.jaha || user.type.admin || user.id.equals(reply.user.id)) {
                boardCommentReplyRepository.delete(replyId);
                return true;
            }
        }
        return false;
    }

    @Override
    public Long getPostSize(Long categoryId) {
        return boardPostRepository.countByCategoryIdAndDisplayYn(categoryId, DISP_Y);
    }

    @Override
    public List<BoardTag> getBoardTags() {
        return boardTagRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
    }

    @Override
    public BoardTag getBoardTag(String name) {
        return boardTagRepository.findOne(name);
    }

    @Override
    public Page<BoardPost> getCategoryTodayPosts(User user, Pageable pageable, String category) {
        if (!user.type.jaha) {
            return null;
        }

        List<BoardCategory> categories = getAllTodayCategories(user, "N");

        if (categories == null) {
            return null;
        }

        List<Long> categoryIds = Lists.transform(categories, new Function<BoardCategory, Long>() {
            @Override
            public Long apply(BoardCategory input) {
                return input.id;
            }
        });

        return boardPostRepository.findByNewsCategoryAndCategoryIdInAndDisplayYn(categoryIds, pageable, category, DISP_Y);
    }

    @Override
    public List<Map<String, Object>> selectNewsList(Map<String, Object> params) throws SQLException {
        return newsMapper.selectNewsList(params);
    }

    @Override
    public int selectNewsListCount(Map<String, Object> params) throws SQLException {
        return newsMapper.selectNewsListCount(params);
    }


    @Override
    public Map<String, Object> getNews(Long postId) {
        return newsMapper.getNews(postId);
    }

    @Override
    public Long insertNews(Map<String, Object> params) {
        return newsMapper.insertNews(params);

    }

    @Override
    @Transactional
    public Long insertNews(Map<String, Object> params, List<String> hashtags) {
        Long postId = newsMapper.insertNews(params);

        if (hashtags != null && !hashtags.isEmpty()) {
            for (String hashtag : hashtags) {
                hashtagRepository.save(new Hashtag(Long.parseLong(params.get("id").toString()), hashtag));
                hashtagRepository.flush();
            }
        }

        String pushSendYn = (String) params.get("pushSendYn");

        // <-- 마을뉴스 push 추가, 20161019, 전강욱
        if ("Y".equals(pushSendYn)) {
            int rangeAll = (Integer) params.get("range_all");
            String sido = (String) params.get("range_sido");
            String sigungu = (String) params.get("range_sigungu");
            String newsCategory = (String) params.get("news_category");
            String title = (String) params.get("title");
            String gender = (String) params.get("gender");
            String age = (String) params.get("age");

            this.sendPushForMaulnews(newsCategory, title, Long.parseLong(params.get("id").toString()), rangeAll, sido, sigungu, gender, age);
        }
        // --> 마을뉴스 push 추가, 20161019, 전강욱

        return postId;
    }

    /**
     * 마을뉴스 등록 시 푸시를 발송한다.
     *
     * @param newsCategory
     * @param value
     * @param postId
     * @param rangeAll
     * @param sido
     * @param sigungu
     * @param gender
     * @param age
     */
    private void sendPushForMaulnews(String newsCategory, String value, long postId, int rangeAll, String sido, String sigungu, String gender, String age) {
        try {
            if (rangeAll == 0) { // 지역별 대상자 검색 후 발송
                if (StringUtils.isBlank(sido) && StringUtils.isBlank(sigungu) || StringUtils.isBlank(sido) && StringUtils.isNotBlank(sigungu)) {
                    logger.info("<<전체 발송이 아니나 지역구분이 없어 마을뉴스 푸시 발송을 중단합니다!>>");
                    return;
                }
            }

            Map<String, Object> searchMap = new HashMap<String, Object>();
            searchMap.put("searchAlarm", "noti_alarm");

            if ("M".equalsIgnoreCase(gender) || "F".equalsIgnoreCase(gender)) {
                searchMap.put("searchGender", gender);
            }
            if (age != null && !"ALL".equalsIgnoreCase(age)) {
                searchMap.put("searchAges", age.split("[|]", -1));
            }

            if (rangeAll == 0) {
                searchMap.put("searchSido", sido);
                searchMap.put("searchSigungu", sigungu);
                logger.info("<<지역별 푸시발송대상자 조회, {} {}>>", sido, sigungu);
            }

            List<SimpleUser> targetUserList = this.userMapper.selectTargetUserListForPush(searchMap);

            if (targetUserList == null || targetUserList.isEmpty()) {
                logger.info("<<발송대상자가 없어 마을뉴스 푸시 발송을 중단합니다!>>");
                return;
            }

            List<Long> targetUerIdList = Lists.transform(targetUserList, input -> input.id);
            String action = String.format(Constants.APP_URI_MAUL_NEWS, postId, newsCategory);

            this.gcmService.sendPush(value, action, targetUerIdList);
            logger.info("<<마을뉴스 푸시 발송 로그>> 게시글번호: {}, 시도: {}, 시군구: {}, 성별: {}, 연령대: {}, 발송대상자수: {}, 발송대상자: {}", postId, sido, sigungu, gender, age, targetUerIdList.size(), targetUerIdList);
        } catch (Exception e) {
            logger.error("<<마을뉴스 푸시 발송 중 오류>>", e);
        }
    }

    @Override
    public void updateNews(Map<String, Object> params) {
        newsMapper.updateNews(params);
    }

    @Override
    @Transactional
    public void updateNews(Map<String, Object> params, List<String> hashtags) {
        newsMapper.updateNews(params);

        List<Hashtag> _hashtags = hashtagRepository.findByPostId(Long.parseLong(params.get("id").toString()));
        hashtagRepository.delete(_hashtags);
        hashtagRepository.flush();

        if (hashtags != null && !hashtags.isEmpty()) {
            for (String hashtag : hashtags) {
                hashtagRepository.save(new Hashtag(Long.parseLong(params.get("id").toString()), hashtag));
                hashtagRepository.flush();
            }
        }
    }

    @Override
    @Transactional
    public void deleteNews(Long postId) {
        newsMapper.deleteNews(postId);
        List<Hashtag> hashtags = hashtagRepository.findByPostId(postId);
        hashtagRepository.delete(hashtags);
        hashtagRepository.flush();
    }

    @Override
    public List<Map<String, Object>> selectUserNewsList(Map<String, Object> params) {
        return newsMapper.selectUserNewsList(params);
    }

    @Override
    public int selectUserNewsListCount(Map<String, Object> params) {
        return newsMapper.selectUserNewsListCount(params);
    }

    /**
     * 게시판에 작성자 표시를 고객의 요구에 맞게 다양하게 처리함.
     *
     * 기본 규칙은 닉네임이 있으면 [닉네임 + 동]으로 표시하고 없는 경우는 [이름 + 동]으로 표시함
     *
     * @author namsuk.park
     * @param user
     * @return String 게시판용 작성자명
     */
    @Override
    public String makeUserNameForPost(User user, UserPrivacy userPrivacy) {
        String userName = HtmlUtils.htmlEscape(user.getFullName());

        if (userPrivacy == UserPrivacy.ALIAS) {
            // 2017.01.19 오석민 팀장 요청으로 위시티블루밍5단지아파트 하드코딩 제거
            // if (user.getNickname() != null && user.house.apt.id != 255) {
            if (user.getNickname() != null) {
                userName = HtmlUtils.htmlEscape(user.getNickname().name);
            }
        }

        userName += " (" + HtmlUtils.htmlEscape(user.house == null ? "" : user.house.dong) + "동)";

        return userName;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void modifyAirSendDateOfTts(long postId) {
        this.boardPostRepository.updateBoardPostAirSendDate(postId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.service.BoardService#getTopFixBoardPost(java.lang.Long)
     */
    @Override
    public List<BoardPost> getTopFixBoardPost(Long categoryId, Boolean topFix) {
        return boardPostRepository.findByCategoryIdAndTopFixAndDisplayYn(categoryId, topFix, DISP_Y);
    }

    // @Override
    // @Transactional(propagation=Propagation.REQUIRED, rollbackFor={ Exception.class })
    // public void modifyTtsBoardPost(long postId, String voiceGubun, int voiceVolume, String
    // pushSendYn) throws Exception {
    // this.boardPostRepository.updateTtsBoardPost(postId, voiceGubun, voiceVolume, pushSendYn);
    // }

    @Override
    public List<Hashtag> fetchHashTags(Long postId) {
        return hashtagRepository.findByPostId(postId);
    }

    // 메인화면 마을뉴스
    private List<BoardPost> getPosts4Main(Long userId, Long categoryId, String sido, String sigungu, Long lastPostId, int from, int count, Date lessThan) {
        if (lastPostId == null || lastPostId == 0l) {
            lastPostId = Long.MAX_VALUE;
        }

        logger.debug("<<사용자아이디: {}, 카테고리아이디: {}, 시도: {}, 시군구: {}, 마지막게시글번호: {}, from: {}, count: {}, lessThan: {}>>", userId, categoryId, sido, sigungu, lastPostId, from, count, lessThan);

        String dateLessThan = "";
        if (lessThan != null) {
            dateLessThan = " AND b.reg_date < '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lessThan) + "' ";
        }

        User user = userRepository.findOne(userId);

        List<BoardPost> list = jdbcTemplate.query("SELECT b.*, u.id as 'u_user_id', u.nickname, u.has_profile_image, c.name AS 'category_name' "
                + "FROM board_post b, user u, board_category c WHERE (b.news_type IS NOT NULL AND b.news_category IS NOT NULL) AND b.display_yn = 'Y' AND (b.category_id=? OR \n"
                + "(b.range_sido=IF(b.range_sigungu='', ?, null) OR b.range_sigungu=? OR b.range_all=1)) " + "AND b.id < ? AND b.user_id = u.id AND b.category_id=c.id " + dateLessThan
                + " AND b.display_yn = 'Y' ORDER BY b.reg_date DESC LIMIT ?, ?", new Object[] {categoryId, sido, sigungu, lastPostId, from, count}, new RowMapper<BoardPost>() {
                    @Override
                    public BoardPost mapRow(ResultSet rs, int rowNum) throws SQLException {
                        BoardPost post = new BoardPost();
                        post.id = rs.getLong("id");
                        post.content = rs.getString("content");
                        post.imageCount = rs.getInt("image_count");
                        post.rangeSido = rs.getString("range_sido");
                        post.rangeSigungu = rs.getString("range_sigungu");
                        post.regDate = new Date(rs.getTimestamp("reg_date").getTime());
                        // post.title = rs.getString("title");

                        User user = new User();
                        UserNickname nickname = new UserNickname();
                        nickname.name = rs.getString("nickname");
                        user.id = rs.getLong("u_user_id");
                        user.setNickname(nickname.name == null ? null : nickname);
                        user.hasProfileImage = rs.getBoolean("has_profile_image");
                        post.user = user;

                        BoardCategory category = new BoardCategory();
                        category.name = rs.getString("category_name");
                        post.category = category;

                        // 게시판 카테고리 모드가 html이고 게시판 title이 비어있는 경우에만 게시판 내용을 변환하여 타이틀로 수정
                        if ("html".equals(post.category.contentMode) && StringUtils.isBlank(rs.getString("title"))) {
                            String tempContent = post.content;
                            String tempTitle = TagUtils.removeTag(tempContent);

                            if (tempTitle.length() > 300) {
                                post.title = tempTitle.substring(0, 300);
                            } else {
                                post.title = tempTitle;
                            }
                        } else {
                            post.title = rs.getString("title");
                        }

                        return post;
                    }
                });

        if (user != null) {
            for (BoardPost post : list) {
                post.isDeletable = user.type.jaha || user.id.equals(post.user == null ? 0l : post.user.id);
            }
        }

        return list;
    }


    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.service.BoardService#insertBoardCategory(com.jaha.web.emaul.v2.model.board.BoardCategoryVo)
     */
    @Override
    @Transactional
    public int insertBoardCategory(BoardCategoryVo boardCategoryVo) {
        return boardMapper.insertBoardCategoryVo(boardCategoryVo);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.service.BoardService#updateBoardCategory(com.jaha.web.emaul.v2.model.board.BoardCategoryVo)
     */
    @Override
    @Transactional
    public int updateBoardCategory(BoardCategoryVo boardCategoryVo) {
        return boardMapper.updateBoardCategoryVo(boardCategoryVo);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.service.BoardService#getBoardCategory(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public BoardCategoryVo getBoardCategory(Long id) {
        return boardMapper.selectBoardCategory(id);
    }

    /**
     * 오늘작성한 글자수 가져오기
     */
    @Override
    public int getTotalContentLength(Long categoryId) {
        int result = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date today = new Date();
        String todayStr = sdf.format(today);
        BoardPostAirWord bpaw = boardPostAirWordRepository.findByCategoryIdAndRegDate(categoryId, todayStr);
        if (bpaw != null) {
            result = bpaw.getAccWordCount();
        }
        return result;
    }

    /**
     * 방송게시판 > 등록 /수정 시 글자수 저장
     */
    @Override
    public BoardPostAirWord saveAndFlush(BoardPostAirWord bpaw) {
        // TODO Auto-generated method stub
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date today = new Date();
        String todayStr = sdf.format(today);
        bpaw.setRegDate(todayStr);
        BoardPostAirWord tmpBpaw = boardPostAirWordRepository.findByCategoryIdAndRegDate(bpaw.getCategoryId(), todayStr);
        if (tmpBpaw != null) {
            bpaw.setAccWordCount(bpaw.getAccWordCount() + tmpBpaw.getAccWordCount());
        }

        return boardPostAirWordRepository.saveAndFlush(bpaw);
    }



}
