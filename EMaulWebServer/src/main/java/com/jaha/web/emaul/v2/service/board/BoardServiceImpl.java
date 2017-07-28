/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.service.board;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
///////////////////////////////////////////////////////////////// 이전 버전의 클래스 /////////////////////////////////////////////////////////////////
import com.jaha.web.emaul.model.BaseSecuModel;
import com.jaha.web.emaul.model.FileInfo;
import com.jaha.web.emaul.model.SimpleUser;
import com.jaha.web.emaul.service.CommonService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.TagUtils;
import com.jaha.web.emaul.util.Thumbnails;
///////////////////////////////////////////////////////////////// 이전 버전의 클래스 /////////////////////////////////////////////////////////////////
import com.jaha.web.emaul.v2.constants.BoardConstants;
import com.jaha.web.emaul.v2.constants.BoardConstants.BoardType;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushAction;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushAlarmSetting;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushGubun;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushMessage;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushStatus;
import com.jaha.web.emaul.v2.constants.CommonConstants.PushTargetType;
import com.jaha.web.emaul.v2.constants.CommonConstants.UserDeviceType;
import com.jaha.web.emaul.v2.mapper.board.BoardCategoryMapper;
import com.jaha.web.emaul.v2.mapper.board.BoardCommentMapper;
import com.jaha.web.emaul.v2.mapper.board.BoardCommentReplyMapper;
import com.jaha.web.emaul.v2.mapper.board.BoardEmpathyMapper;
import com.jaha.web.emaul.v2.mapper.board.BoardPostAirMapper;
import com.jaha.web.emaul.v2.mapper.board.BoardPostEventMapper;
import com.jaha.web.emaul.v2.mapper.board.BoardPostHashtagMapper;
import com.jaha.web.emaul.v2.mapper.board.BoardPostMapper;
import com.jaha.web.emaul.v2.mapper.board.BoardPostMaulnewsMapper;
import com.jaha.web.emaul.v2.mapper.board.BoardPostRangeMapper;
import com.jaha.web.emaul.v2.mapper.board.StatSharerMapper;
import com.jaha.web.emaul.v2.model.board.BoardCategoryVo;
import com.jaha.web.emaul.v2.model.board.BoardCommentReplyVo;
import com.jaha.web.emaul.v2.model.board.BoardCommentVo;
import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardPostEventVo;
import com.jaha.web.emaul.v2.model.board.BoardPostRangeVo;
import com.jaha.web.emaul.v2.model.board.BoardPostVo;
import com.jaha.web.emaul.v2.model.board.BoardPostWrapper;
import com.jaha.web.emaul.v2.model.common.Sort;
import com.jaha.web.emaul.v2.util.PushUtils;

/**
 * <pre>
 * Class Name : BoardServiceImpl.java
 * Description : 게시판 서비스
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 9. 22.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 9. 22.
 * @version 1.0
 */
@Service("v2BoardService")
public class BoardServiceImpl implements BoardService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${file.path.board.attach}")
    private String filePathBoardAttach;

    @Value("${file.path.editor.image}")
    private String filePathEditorImage;

    @Autowired
    private BoardCategoryMapper boardCategoryMapper;

    @Autowired
    private BoardCommentMapper boardCommentMapper;

    @Autowired
    private BoardCommentReplyMapper boardCommentReplyMapper;

    @Autowired
    private BoardEmpathyMapper boardEmpathyMapper;

    @Autowired
    private BoardPostAirMapper boardPostAirMapper;

    @Autowired
    private BoardPostEventMapper boardPostEventMapper;

    @Autowired
    private BoardPostHashtagMapper boardPostHashtagMapper;

    @Autowired
    private BoardPostMapper boardPostMapper;

    @Autowired
    private BoardPostMaulnewsMapper boardPostMaulnewsMapper;

    @Autowired
    private BoardPostRangeMapper boardPostRangeMapper;

    @Autowired
    private StatSharerMapper statSharerMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private PushUtils pushUtils;

    private final BaseSecuModel baseSecuModel = new BaseSecuModel();

    @Override
    @Transactional(readOnly = true)
    public List<? extends BoardPostVo> findBoardPostList(BoardDto param) throws Exception {
        // 게시판 카테고리 조회
        BoardCategoryVo boardCategory = this.boardCategoryMapper.selectBoardCategory(param.getSearchCategoryId());
        param.setBoardCategory(boardCategory);

        // 목록 레코드 수 조회
        param.getPagingHelper().setTotalRecordCount(this.getTotalRecordCount(param));
        this.putSortList(param);

        // 목록 조회
        return this.getBoardPostList(param);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void regBoardPost(BoardDto param, BoardPostWrapper<?> boardPost) throws Exception {
        // 게시판 카테고리 조회
        BoardCategoryVo boardCategory = this.boardCategoryMapper.selectBoardCategory(param.getSearchCategoryId());
        param.setBoardCategory(boardCategory);

        String categoryType = boardCategory.getType();

        // 공지사항 상단고정(최대 3개)
        if (BoardType.NOTICE.getCode().equals(categoryType)) { // 공지시항인 경우 상단 고정여부 처리
            this.modifyTopFix(param, boardPost.get());
        }

        if (BoardConstants.ContentMode.HTML.getValue().equals(boardCategory.getContentMode())) {
            boardPost.get().setContent(String.format(BoardConstants.APP_HTML_FORMAT, boardPost.get().getContent()));
        }

        // 게시글 등록
        this.regPost(param, boardPost);

        BoardPostVo post = new BoardPostVo();
        Long postId = boardPost.get().getId();

        // 에디터로 게시글을 등록한 경우, 임시 저장 폴더에서 실 저장 폴더로 이동 후 content 수정
        if (BoardConstants.ContentMode.HTML.getValue().equals(param.getBoardCategory().getContentMode())) {
            String content = this.commonService.saveEditorImageFiles(categoryType, String.valueOf(postId), boardPost.get().getContent());
            post.setContent(content);

            String firstEditorImageThumbUrl = this.makeFirstEditorImageThumb(categoryType, postId, content);
            post.setFirstEditorImageThumbUrl(firstEditorImageThumbUrl);
        }

        // 신규 방식의 파일 저장 처리
        String fileGroupKey = this.moveUploadedFile(param.getUploadedFiles(), categoryType, postId, boardPost.get().getUserId());

        if (StringUtils.isNotEmpty(post.getContent()) || StringUtils.isNotEmpty(post.getFirstEditorImageThumbUrl()) || StringUtils.isNotEmpty(fileGroupKey)) {
            post.setFileGroupKey(fileGroupKey);
            post.setId(postId);

            this.boardPostMapper.updateBoardPost(post);
        }

        // PUSH 발송(자하 공지사항인 경우, 관리자 공지사항인 경우, 민원 게시판인 경우, 커뮤니티 게시판이면서 글등록 후 push 발송여부가 Y인 경우)
        this.sendPushToAptJumin(param, boardPost);
    }

    @Override
    public BoardPostWrapper<?> findBoardPost(BoardDto param, Long postId, boolean isModifying) throws Exception {
        // 게시판 카테고리 조회
        BoardCategoryVo boardCategory = this.boardCategoryMapper.selectBoardCategory(param.getSearchCategoryId());
        param.setBoardCategory(boardCategory);

        // 게시글 조회
        BoardPostWrapper<?> boardPost = this.getBoardPost(param, postId);

        // 수정화면으로 이동 시에는 조회 수 증가 및 댓글 목록 수 조회를 하지 않는다.
        if (isModifying) {
            return boardPost;
        }

        this.boardPostMapper.updateViewCount(postId);

        // 댓글을 노출하지 않는 게시판은 댓글 목록 수 조회를 하지 않는다.
        // 관리자 화면에서는 댓글이 노출되어야 하므로, 해당 컨트롤은 Controller단에서 수행하도록 수정 : cyt
        // if (!"Y".equals(boardCategory.getCommentDisplayYn())) {
        // return boardPost;
        // }

        // 댓글 목록 조회할 게시글 아이디
        param.setSearchPostId(postId);

        // 댓글 목록 레코드 수 조회
        int totalRecordCount = this.boardCommentMapper.selectBoardCommentListCount(param);
        param.getPagingHelper().setTotalRecordCount(totalRecordCount);

        if (totalRecordCount == 0) {
            return boardPost;
        }

        List<Sort> sortList = new ArrayList<Sort>();
        Sort sort = new Sort();
        sort.setColumn("BC.id");
        sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
        sortList.add(sort);

        param.getPagingHelper().setSortList(sortList);

        List<BoardCommentVo> boardCommentList = this.boardCommentMapper.selectBoardCommentList(param);

        if (boardCommentList != null && !boardCommentList.isEmpty()) {
            for (BoardCommentVo boardComment : boardCommentList) {
                // 댓글 등록자명 복호화
                boardComment.setFullName(baseSecuModel.descString(boardComment.getFullName()));

                // 댓글의 답글 목록 조회
                param.setSearchCommentId(boardComment.getId());
                List<BoardCommentReplyVo> boardCommentReplyList = this.boardCommentReplyMapper.selectBoardCommentReplyList(param);

                if (boardCommentReplyList != null && !boardCommentReplyList.isEmpty()) {
                    for (BoardCommentReplyVo boardCommentReply : boardCommentReplyList) {
                        // 답글 등록자명 복호화
                        boardCommentReply.setFullName(baseSecuModel.descString(boardCommentReply.getFullName()));
                    }
                }

                boardComment.setBoardCommentReplyList(boardCommentReplyList);
            }
        }

        boardPost.get().setBoardCommentList(boardCommentList);

        return boardPost;
    }

    @Override
    public void modifyBoardPost(BoardDto param, BoardPostWrapper<?> boardPost) throws Exception {
        // 게시판 카테고리 조회
        BoardCategoryVo boardCategory = this.boardCategoryMapper.selectBoardCategory(param.getSearchCategoryId());
        param.setBoardCategory(boardCategory);

        String categoryType = boardCategory.getType();

        // 공지사항 상단고정(최대 3개)
        if (BoardType.NOTICE.getCode().equals(categoryType)) {
            this.modifyTopFix(param, boardPost.get());
        }

        if (BoardConstants.ContentMode.HTML.getValue().equals(boardCategory.getContentMode())) {
            boardPost.get().setContent(String.format(BoardConstants.APP_HTML_FORMAT, boardPost.get().getContent()));
        }

        this.modifyPost(param, boardPost);

        BoardPostVo post = new BoardPostVo();
        Long postId = boardPost.get().getId();

        // 에디터로 게시글을 등록한 경우, 임시 저장 폴더에서 실 저장 폴더로 이동 후 content 수정
        if (BoardConstants.ContentMode.HTML.getValue().equals(param.getBoardCategory().getContentMode())) {
            String content = this.commonService.saveEditorImageFiles(categoryType, String.valueOf(postId), boardPost.get().getContent());
            post.setContent(content);

            String firstEditorImageThumbUrl = this.makeFirstEditorImageThumb(categoryType, postId, content);
            post.setFirstEditorImageThumbUrl(firstEditorImageThumbUrl);
        }

        // 신규 방식의 파일 저장 처리
        String fileGroupKey = this.moveUploadedFile(param.getUploadedFiles(), categoryType, postId, boardPost.get().getUserId());

        if (StringUtils.isNotEmpty(post.getContent()) || StringUtils.isNotEmpty(post.getFirstEditorImageThumbUrl()) || StringUtils.isNotEmpty(fileGroupKey)) {
            post.setFileGroupKey(fileGroupKey);
            post.setId(postId);

            this.boardPostMapper.updateBoardPost(post);
        }

        // PUSH 발송(자하 공지사항인 경우, 관리자 공지사항인 경우, 민원 게시판인 경우, 커뮤니티 게시판이면서 글등록 후 push 발송여부가 Y인 경우)
        this.sendPushToAptJumin(param, boardPost);
    }

    @Override
    public void removeBoardPost(BoardDto param, BoardPostVo boardPost) throws Exception {
        // 게시판 카테고리 조회
        BoardCategoryVo boardCategory = this.boardCategoryMapper.selectBoardCategory(param.getSearchCategoryId());
        param.setBoardCategory(boardCategory);

        boardPost.setDisplayYn("N");
        // 게시글 노출 여부
        this.boardPostMapper.updateDisplayYn(boardPost);
    }

    ////////////////////////////////////////////////////////////////////////////////// 공통 //////////////////////////////////////////////////////////////////////////////////
    /**
     * 게시글 총 레코드 수 반환<br />
     *
     * @param param
     * @return
     */
    private int getTotalRecordCount(BoardDto param) throws Exception {
        String categoryType = param.getBoardCategory().getType();
        int totalCount = 0;

        if (BoardType.NOTICE.getCode().equals(categoryType) || BoardType.COMMUNITY.getCode().equals(categoryType)) {
            totalCount = this.boardPostMapper.selectBoardPostListCount(param);
        } else if (BoardType.GROUP.getCode().equals(categoryType)) {
            totalCount = this.boardPostRangeMapper.selectBoardPostRangeListCount(param);
        } else if (BoardType.EVENT.getCode().equals(categoryType)) {
            totalCount = this.boardPostEventMapper.selectBoardPostEventListCount(param);
        } else {
            totalCount = this.boardPostMapper.selectBoardPostListCount(param);
        }

        return totalCount;
    }

    /**
     * 정렬 세팅(DB Query에 전달할 ORDER BY 데이터)<br />
     *
     * @param param
     * @return
     */
    private void putSortList(BoardDto param) throws Exception {
        String categoryType = param.getBoardCategory().getType();
        List<Sort> sortList = new ArrayList<Sort>();

        if (BoardType.NOTICE.getCode().equals(categoryType)) {
            Sort sort = new Sort();
            sort.setColumn("top_fix");
            sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
            sortList.add(sort);

            sort = new Sort();
            sort.setColumn("id");
            sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
            sortList.add(sort);

            param.getPagingHelper().setSortList(sortList);
        } else if (BoardType.GROUP.getCode().equals(categoryType) || BoardType.EVENT.getCode().equals(categoryType)) {
            if (param.getPagingHelper().getSortList() == null || param.getPagingHelper().getSortList().isEmpty()) {
                Sort sort = new Sort();
                sort.setColumn("BP.id");
                sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
                sortList.add(sort);

                param.getPagingHelper().setSortList(sortList);
            }

            sortList = param.getPagingHelper().getSortList();

            // 단체 게시판은 단수이기 때문에 일단 복수(배열)에 경우는 생략
            for (Sort sort : sortList) {
                param.setSortColumns(sort.getColumn());
            }

            // 게시글 등록순, 조회순, 댓글순(화면에서 전송한 파라미터는 PagingArgumentResolver에서 자동 처리(예: &sort=BP.reg_date&sort=BP.view_count,ASC)
        } else {
            if (param.getPagingHelper().getSortList() == null || param.getPagingHelper().getSortList().isEmpty()) {
                Sort sort = new Sort();
                sort.setColumn("id");
                sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
                sortList.add(sort);
            }

            param.getPagingHelper().setSortList(sortList);
        }
    }

    /**
     * 게시글 목록 반환<br />
     *
     * @param param
     * @return
     * @throws Exception
     */
    private List<? extends BoardPostVo> getBoardPostList(BoardDto param) throws Exception {
        String categoryType = param.getBoardCategory().getType();

        if (BoardType.NOTICE.getCode().equals(categoryType) || BoardType.COMMUNITY.getCode().equals(categoryType)) {
            return this.boardPostMapper.selectBoardPostList(param);
        } else if (BoardType.GROUP.getCode().equals(categoryType)) {
            return this.boardPostRangeMapper.selectBoardPostRangeList(param);
        } else if (BoardType.EVENT.getCode().equals(categoryType)) {
            return this.boardPostEventMapper.selectBoardPostEventList(param);
        } else {
            return this.boardPostMapper.selectBoardPostList(param);
        }
    }

    /**
     * 게시글 상세 반환<br />
     *
     * @param param
     * @return
     * @throws Exception
     */
    private BoardPostWrapper<?> getBoardPost(BoardDto param, Long postId) throws Exception {
        String categoryType = param.getBoardCategory().getType();

        if (BoardType.NOTICE.getCode().equals(categoryType) || BoardType.COMMUNITY.getCode().equals(categoryType)) {
            BoardPostVo boardPost = this.boardPostMapper.selectBoardPost(postId);
            return new BoardPostWrapper<BoardPostVo>(boardPost);
        } else if (BoardType.GROUP.getCode().equals(categoryType)) {
            BoardPostRangeVo boardPost = this.boardPostRangeMapper.selectBoardPostRange(postId);

            if (boardPost != null) {
                List<FileInfo> fileInfoList = this.commonService.getFileGroup(categoryType, boardPost.getFileGroupKey());
                boardPost.setFileInfoList(fileInfoList);
            }

            return new BoardPostWrapper<BoardPostRangeVo>(boardPost);
        } else if (BoardType.EVENT.getCode().equals(categoryType)) {
            BoardPostEventVo boardPost = this.boardPostEventMapper.selectBoardPostEvent(postId);

            if (boardPost != null) {
                List<FileInfo> fileInfoList = this.commonService.getFileGroup(categoryType, boardPost.getFileGroupKey());
                boardPost.setFileInfoList(fileInfoList);
            }

            return new BoardPostWrapper<BoardPostEventVo>(boardPost);
        } else if (BoardType.FAQ.getCode().equals(categoryType) || BoardType.SYSTEM_NOTICE.getCode().equals(categoryType)) {
            BoardPostVo boardPost = this.boardPostMapper.selectBoardPost(postId);

            if (boardPost != null) {
                List<FileInfo> fileInfoList = this.commonService.getFileGroup(categoryType, boardPost.getFileGroupKey());
                boardPost.setFileInfoList(fileInfoList);
            }

            return new BoardPostWrapper<BoardPostVo>(boardPost);
        } else {
            BoardPostVo boardPost = this.boardPostMapper.selectBoardPost(postId);
            return new BoardPostWrapper<BoardPostVo>(boardPost);
        }
    }

    /**
     * 게시글 등록
     *
     * @param boardPost
     */
    private void regPost(BoardDto param, BoardPostWrapper<?> boardPost) throws Exception {
        String categoryType = param.getBoardCategory().getType();

        if (BoardType.GROUP.getCode().equals(categoryType)) {
            BoardPostRangeVo boardPostRange = (BoardPostRangeVo) boardPost.get();
            boardPostRange.setImageCount(0);
            boardPostRange.setRangeAll(false);
            boardPostRange.setTopFix(false);

            if (PushStatus.INSTANT.getValue().equalsIgnoreCase(boardPostRange.getPushStatus())) {
                boardPostRange.setPushSendYn("Y");
            } else {
                boardPostRange.setPushSendYn("N");
            }

            this.boardPostMapper.insertBoardPost(boardPostRange);
            boardPostRange.setPostId(boardPostRange.getId());

            this.boardPostRangeMapper.insertBoardPostRange(boardPostRange);
        } else if (BoardType.EVENT.getCode().equals(categoryType)) {
            BoardPostEventVo boardPostEvent = (BoardPostEventVo) boardPost.get();
            boardPostEvent.setImageCount(0);
            boardPostEvent.setRangeAll(false); // 현재 이벤트 게시판은 전체 공개이나 일단 false
            boardPostEvent.setTopFix(false);

            if (PushStatus.INSTANT.getValue().equalsIgnoreCase(boardPostEvent.getPushStatus())) {
                boardPostEvent.setPushSendYn("Y");
            } else {
                boardPostEvent.setPushSendYn("N");
            }

            if (PushStatus.RESERV.getValue().equalsIgnoreCase(boardPostEvent.getPushStatus())) {
                boardPostEvent.setReservYn("Y");
                boardPostEvent.setOpenDate(boardPostEvent.getStartDate());
            }

            this.boardPostMapper.insertBoardPost(boardPostEvent);
            boardPostEvent.setPostId(boardPostEvent.getId());

            this.boardPostEventMapper.insertBoardPostEvent(boardPostEvent);
            this.boardPostRangeMapper.insertBoardPostRange(boardPostEvent);
        } else {
            this.boardPostMapper.insertBoardPost(boardPost.get());
        }
    }

    /**
     * 게시글 수정
     *
     * @param boardPost
     */
    private void modifyPost(BoardDto param, BoardPostWrapper<?> boardPost) throws Exception {
        String categoryType = param.getBoardCategory().getType();

        this.boardPostMapper.updateBoardPost(boardPost.get());

        if (BoardType.GROUP.getCode().equals(categoryType)) {
            this.boardPostRangeMapper.updateBoardPostRange((BoardPostRangeVo) boardPost.get());
        } else if (BoardType.EVENT.getCode().equals(categoryType)) {
            BoardPostEventVo boardPostEvent = (BoardPostEventVo) boardPost.get();

            if (PushStatus.RESERV.getValue().equalsIgnoreCase(boardPostEvent.getPushStatus())) {
                boardPostEvent.setReservYn("Y");
                boardPostEvent.setOpenDate(boardPostEvent.getStartDate());
            }

            this.boardPostEventMapper.updateBoardPostEvent(boardPostEvent);
            this.boardPostRangeMapper.updateBoardPostRange(boardPostEvent);
        }
    }

    /**
     * 임시 폴더의 파일을 옮긴 후 파일 그룹키를 반환한다.
     *
     * @param uploadedFiles
     * @param categoryType
     * @param postId
     * @param userId
     * @return
     * @throws Exception
     */
    private String moveUploadedFile(String[] uploadedFiles, String categoryType, Long postId, Long userId) throws Exception {
        if (uploadedFiles == null || uploadedFiles.length == 0) {
            return null;
        }

        String dir = String.format(this.filePathBoardAttach, postId / 1000L, postId);

        File destFolder = new File(dir);
        if (!destFolder.exists()) {
            destFolder.mkdirs();
            destFolder.setReadable(true, false);
            destFolder.setWritable(true, false);
        }

        String fileGroupKey = String.valueOf(postId); // StringUtils.left(RandomKeys.make(20), 20);

        for (String tempFilePath : uploadedFiles) {
            File tempUploadedFile = new File(tempFilePath);
            String fileName = tempUploadedFile.getName();

            if (tempUploadedFile.exists()) {
                File destFile = new File(destFolder, fileName);
                // while (destFile.exists()) {
                // destFile = new File(destFolder, fileName);
                // FileUtils.moveFile(tempUploadedFile, destFile);
                // }

                FileUtils.moveFile(tempUploadedFile, destFile);
                logger.debug("임시파일 {}, 저장파일 {}", tempUploadedFile.getCanonicalPath(), destFile.getCanonicalPath());

                if (destFile.exists()) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.category = categoryType;
                    fileInfo.fileGroupKey = fileGroupKey;
                    fileInfo.filePath = dir + File.separator;
                    fileInfo.fileName = fileName;
                    fileInfo.fileOriginName = fileName;
                    fileInfo.ext = FilenameUtils.getExtension(fileName);
                    fileInfo.size = destFile.length() / 1024; // kb 단위로 저장
                    fileInfo.regId = userId;

                    this.commonService.saveFileInfo(fileInfo);
                }
            }
        }

        return fileGroupKey;
    }

    /**
     * 에디터에서 등록한 첫번째 이미지를 썸네일로 만든다.
     *
     * @param categoryType
     * @param postId
     * @param content
     */
    private String makeFirstEditorImageThumb(String categoryType, Long postId, String content) throws Exception {
        String firstEditorImageThumbUrl = null;

        File dir = new File(String.format(this.filePathEditorImage + "/%s/%s", categoryType, postId));

        List<String> imgSrcList = TagUtils.getImgSrc(content);
        if (imgSrcList.size() > 0) {
            String imageFileName = FilenameUtils.getName(imgSrcList.get(0));
            File firstImageFile = new File(dir, imageFileName);

            String thumbFileName = FilenameUtils.removeExtension(firstImageFile.getCanonicalPath()) + "-thumb.jpg";
            File thumbFile = new File(thumbFileName);

            if (firstImageFile.exists() && !thumbFile.exists()) {
                Thumbnails.create(firstImageFile);
                firstEditorImageThumbUrl = "/" + FilenameUtils.getPath(imgSrcList.get(0)) + thumbFile.getName();
            }
        }

        return firstEditorImageThumbUrl;
    }

    /**
     * 공지사항 상단고정(최대 3개)
     *
     * @param param
     * @param boardPost
     * @throws Exception
     */
    private void modifyTopFix(BoardDto param, BoardPostVo boardPost) throws Exception {
        if (boardPost.getTopFix()) { // 공지사항 상단고정(최대3개)
            param.setSearchTopFix(true);
            List<BoardPostVo> topFixedBoardPostList = this.boardPostMapper.selectBoardPostList(param);

            if (topFixedBoardPostList != null && !topFixedBoardPostList.isEmpty()) {
                if (topFixedBoardPostList.size() == 3) {
                    BoardPostVo bpv = topFixedBoardPostList.get(0);
                    bpv.setTopFix(false);

                    this.boardPostMapper.updateTopFix(bpv);
                }
            }
        }
    }

    /**
     * 게시판 분류 별로 (즉시) 푸시 발송(공지사항 등)
     *
     * @param param
     * @param boardPost
     * @throws Exception
     */
    private void sendPushToAptJumin(BoardDto param, BoardPostWrapper<?> boardPost) throws Exception {
        String categoryType = param.getBoardCategory().getType();

        if (BoardType.NOTICE.getCode().equals(categoryType)) {
            if ("Y".equals(boardPost.get().getPushSendYn())) {
                Long aptId = param.getLoginSession().getAptId();
                List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(PushTargetType.APT, PushAlarmSetting.BOARD, Lists.newArrayList(aptId));
                String value = TagUtils.removeTag(boardPost.get().getContent()).replaceAll("<!DOCTYPE html>", StringUtils.EMPTY);
                value = value.replaceAll("&nbsp;", StringUtils.EMPTY);
                String action = String.format(PushAction.BOARD.getValue(), boardPost.get().getId());

                // 푸시 발송
                this.pushUtils.sendPush(PushGubun.BOARD_NOTICE, boardPost.get().getTitle(), value, action, String.valueOf(boardPost.get().getId()), false, targetUserList);
            }
        } else if (BoardType.GROUP.getCode().equals(categoryType)) { // 단체 게시판(단체관리자) 푸시 발송
            BoardPostRangeVo boardPostRange = (BoardPostRangeVo) boardPost.get();

            if (StringUtils.isEmpty(boardPostRange.getPushStatus()) || "Y".equalsIgnoreCase(boardPostRange.getReservYn())
                    || PushStatus.RESERV.getValue().equalsIgnoreCase(boardPostRange.getPushStatus())) {
                return;
            }

            List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(boardPostRange.getRangeSido(), boardPostRange.getRangeSigungu(), boardPostRange.getRangeDong());
            String value = TagUtils.removeTag(boardPostRange.getContent()).replaceAll("<!DOCTYPE html>", StringUtils.EMPTY);
            value = value.replaceAll("&nbsp;", StringUtils.EMPTY);
            String action = String.format(PushAction.BOARD_GROUP.getValue(), boardPostRange.getId());

            // 푸시 발송
            this.pushUtils.sendPush(PushGubun.BOARD_GROUP, boardPostRange.getTitle(), value, action, String.valueOf(boardPostRange.getId()), false, targetUserList);

            // 푸시 발송 여부 업데이트
            BoardPostVo post = new BoardPostVo();
            post.setId(boardPostRange.getId());
            post.setPushSendYn("Y");
            this.boardPostMapper.updateBoardPost(post);

            // 푸시 발송 시간 업데이트
            this.boardPostRangeMapper.updatePushSendDate(boardPostRange.getRangeId());
        } else if (BoardType.EVENT.getCode().equals(categoryType)) { // 이벤트 게시판(자하권한) 푸시 발송
            BoardPostEventVo boardPostEvent = (BoardPostEventVo) boardPost.get();

            if ("Y".equalsIgnoreCase(boardPostEvent.getReservYn()) || PushStatus.RESERV.getValue().equalsIgnoreCase(boardPostEvent.getPushStatus())) {
                return;
            }

            // 현재 이벤트 게시판은 성별 / 연령별 / 지역별 구분이 없다.
            // List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(boardPostEvent.getRangeSido(), boardPostEvent.getRangeSigungu(), boardPostEvent.getRangeDong());
            List<SimpleUser> targetUserList = null;
            if (UserDeviceType.ANDROID.getValue().equals(boardPostEvent.getDisplayPlatform())) {
                targetUserList = this.pushUtils.findPushTargetUserList(UserDeviceType.ANDROID);
            } else if (UserDeviceType.IOS.getValue().equals(boardPostEvent.getDisplayPlatform())) {
                targetUserList = this.pushUtils.findPushTargetUserList(UserDeviceType.IOS);
            } else {
                targetUserList = this.pushUtils.findPushTargetUserList();
            }
            String value = TagUtils.removeTag(boardPostEvent.getContent()).replaceAll("<!DOCTYPE html>", StringUtils.EMPTY);
            value = value.replaceAll("&nbsp;", StringUtils.EMPTY);
            String action = String.format(PushAction.BOARD_EVENT.getValue(), boardPostEvent.getId());

            // 푸시 발송
            this.pushUtils.sendPush(PushGubun.BOARD_EVENT, boardPostEvent.getTitle(), value, action, String.valueOf(boardPostEvent.getId()), false, targetUserList);

            // 푸시 발송 여부 업데이트
            BoardPostVo post = new BoardPostVo();
            post.setId(boardPostEvent.getId());
            post.setPushSendYn("Y");
            this.boardPostMapper.updateBoardPost(post);

            // 푸시 발송 시간 업데이트
            this.boardPostRangeMapper.updatePushSendDate(boardPostEvent.getRangeId());
        } else {

        }
    }

    @Override
    public void regBoardComment(BoardDto param, BoardCommentVo boardComment) throws Exception {
        // 게시판 카테고리 조회
        BoardCategoryVo boardCategory = this.boardCategoryMapper.selectBoardCategory(param.getSearchCategoryId());
        param.setBoardCategory(boardCategory);

        // 댓글 등록
        boardComment.setPostId(param.getSearchPostId());
        this.boardCommentMapper.insertBoardComment(boardComment);

        // 게시글 등록자에게 푸시발송
        Long userId = boardComment.getUserId();
        // 게시글 조회
        BoardPostVo boardPost = this.boardPostMapper.selectBoardPost(boardComment.getPostId());

        if (!userId.equals(boardPost.getUserId())) {
            Long postId = boardPost.getId();
            String categoryType = boardCategory.getType();

            List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(PushTargetType.USER, PushAlarmSetting.BOARD, Lists.newArrayList(boardPost.getUserId()));

            PushGubun pushGubun = null;
            String title = null;
            String value = boardComment.getContent();
            String action = String.format(PushAction.BOARD.getValue(), postId);
            boolean titleResIdYn = false;

            if (BoardType.COMPLAINT.getCode().equals(categoryType)) {
                title = PushMessage.BOARD_COMPLAINT_COMMENT_TITLE.getValue();
                pushGubun = PushGubun.BOARD_COMPLAINT;
            } else {
                title = PushMessage.BOARD_COMMENT_REG.getValue();
                pushGubun = PushGubun.BOARD_COMMENT;
                titleResIdYn = true;
            }

            this.pushUtils.sendPush(pushGubun, title, value, action, String.valueOf(postId), titleResIdYn, targetUserList);
        }
    }

    @Override
    public void modifyBoardComment(BoardDto param, BoardCommentVo boardComment) throws Exception {
        // 게시판 카테고리 조회
        BoardCategoryVo boardCategory = this.boardCategoryMapper.selectBoardCategory(param.getSearchCategoryId());
        param.setBoardCategory(boardCategory);

        // 댓글 수정
        // boardComment.setPostId(param.getSearchPostId());
        this.boardCommentMapper.updateBoardComment(boardComment);
    }

    @Override
    public void removeBoardComment(BoardDto param, BoardCommentVo boardComment) throws Exception {
        // 게시판 카테고리 조회
        BoardCategoryVo boardCategory = this.boardCategoryMapper.selectBoardCategory(param.getSearchCategoryId());
        param.setBoardCategory(boardCategory);

        // 댓글 삭제
        // boardComment.setPostId(param.getSearchPostId());
        boardComment.setDisplayYn("N");
        this.boardCommentMapper.updateDisplayYn(boardComment);

        // 댓글수 감소
        this.boardPostMapper.updateCommentCount(param.getSearchPostId());
    }

    @Override
    public void regBoardCommentReply(BoardDto param, BoardCommentReplyVo boardCommentReply) throws Exception {
        // 게시판 카테고리 조회
        BoardCategoryVo boardCategory = this.boardCategoryMapper.selectBoardCategory(param.getSearchCategoryId());
        param.setBoardCategory(boardCategory);

        // 답글 등록
        boardCommentReply.setCommentId(param.getSearchCommentId());
        this.boardCommentReplyMapper.insertBoardCommentReply(boardCommentReply);

        // 댓글 등록자에게 푸시발송
        Long userId = boardCommentReply.getUserId();
        Long commentId = boardCommentReply.getCommentId();

        // 댓글 조회
        BoardCommentVo boardComment = this.boardCommentMapper.selectBoardComment(commentId);

        if (!userId.equals(boardComment.getUserId())) {
            Long postId = boardComment.getPostId();

            List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(PushTargetType.USER, PushAlarmSetting.BOARD, Lists.newArrayList(boardComment.getUserId()));
            String title = PushMessage.BOARD_REPLY_REG.getValue();
            String value = boardCommentReply.getContent();
            String action = String.format(PushAction.BOARD_COMMENT.getValue(), commentId, postId);

            this.pushUtils.sendPush(PushGubun.BOARD_REPLY, title, value, action, String.valueOf(postId), true, targetUserList);
        }
    }

    @Override
    public void modifyBoardCommentReply(BoardDto param, BoardCommentReplyVo boardCommentReply) throws Exception {
        // 게시판 카테고리 조회
        BoardCategoryVo boardCategory = this.boardCategoryMapper.selectBoardCategory(param.getSearchCategoryId());
        param.setBoardCategory(boardCategory);

        // 답글 수정
        this.boardCommentReplyMapper.updateBoardCommentReply(boardCommentReply);
    }

    @Override
    public void removeBoardCommentReply(BoardDto param, BoardCommentReplyVo boardCommentReply) throws Exception {
        // 게시판 카테고리 조회
        BoardCategoryVo boardCategory = this.boardCategoryMapper.selectBoardCategory(param.getSearchCategoryId());
        param.setBoardCategory(boardCategory);

        // 답글 삭제
        boardCommentReply.setDisplayYn("N");
        this.boardCommentReplyMapper.updateDisplayYn(boardCommentReply);

        // 답글수 감소
        this.boardCommentMapper.updateReplyCount(param.getSearchCommentId());
    }

    @Override
    public void removeBoardPostImage(BoardPostVo boardPost) throws Exception {
        this.boardPostMapper.updateImageCount(boardPost);
    }

    @Override
    public void removeBoardPostAttach(BoardPostVo boardPost, String fileName) throws Exception {
        BoardPostVo post = this.boardPostMapper.selectBoardPost(boardPost.getId());

        if (StringUtils.isNotEmpty(post.getFile1()) && post.getFile1().endsWith(fileName)) {
            boardPost.setFile1("delete");
        }
        if (StringUtils.isNotEmpty(post.getFile2()) && post.getFile2().endsWith(fileName)) {
            boardPost.setFile2("delete");
        }

        this.boardPostMapper.updateAttachFileNull(boardPost);
    }

    @Override
    public void removeBoardPostAttach(Long postId, Long fileKey) throws Exception {
        FileInfo fileInfo = this.commonService.getFileInfo(fileKey);

        if (this.commonService.deleteFileInfo(fileKey)) {
            File attachFile = new File(String.format(this.filePathBoardAttach, postId / 1000L, postId), fileInfo.fileName);
            attachFile.delete();
            logger.debug("<<첨부파일 삭제>> {}", attachFile.getCanonicalPath());
        }
    }
    ////////////////////////////////////////////////////////////////////////////////// 공통 //////////////////////////////////////////////////////////////////////////////////



    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.board.BoardService#selectGroupAdminBoardCommentList(com.jaha.web.emaul.v2.model.board.BoardDto)
     */
    @Override
    public List<BoardCommentVo> selectGroupAdminBoardCommentList(BoardDto param) throws Exception {
        // 게시판 카테고리 조회
        BoardCategoryVo boardCategory = this.boardCategoryMapper.selectBoardCategory(param.getSearchCategoryId());
        param.setBoardCategory(boardCategory);

        // 댓글 목록 레코드 수 조회
        int totalRecordCount = this.boardCommentMapper.selectGroupAdminBoardCommentListCount(param);
        param.getPagingHelper().setTotalRecordCount(totalRecordCount);

        List<Sort> sortList = new ArrayList<Sort>();
        Sort sort = new Sort();

        logger.debug(">>> sort : " + param.getSortColumns());

        if (StringUtils.isEmpty(param.getSortColumns())) {
            param.setSortColumns("BC.reg_date");
        }
        sort.setColumn(param.getSortColumns());
        sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
        sortList.add(sort);


        param.getPagingHelper().setSortList(sortList);

        List<BoardCommentVo> boardCommentList = this.boardCommentMapper.selectGroupAdminBoardCommentList(param);

        if (boardCommentList != null && !boardCommentList.isEmpty()) {
            for (BoardCommentVo boardComment : boardCommentList) {
                // 댓글 등록자명 복호화
                boardComment.setFullName(baseSecuModel.descString(boardComment.getFullName()));

                // 댓글의 답글 목록 조회
                param.setSearchCommentId(boardComment.getId());
                List<BoardCommentReplyVo> boardCommentReplyList = this.boardCommentReplyMapper.selectBoardCommentReplyList(param);

                if (boardCommentReplyList != null && !boardCommentReplyList.isEmpty()) {
                    for (BoardCommentReplyVo boardCommentReply : boardCommentReplyList) {
                        // 답글 등록자명 복호화
                        boardCommentReply.setFullName(baseSecuModel.descString(boardCommentReply.getFullName()));
                    }
                }

                boardComment.setBoardCommentReplyList(boardCommentReplyList);
            }
        }

        return boardCommentList;
    }

    final SimpleDateFormat sdf4Batch = new SimpleDateFormat("yyyyMMddHHmm");

    @Override
    public void sendPushReservedPostBatch() throws Exception {
        String searchOpenDate = sdf4Batch.format(new Date());

        // 단체 게시판 예약 푸시 목록 조회
        List<BoardPostRangeVo> postRangeBatchList = this.boardPostRangeMapper.selectBoardPostRangeBatchList(searchOpenDate);
        // 이벤트 게시판 예약 푸시 목록 조회
        List<BoardPostEventVo> postEventBatchList = this.boardPostEventMapper.selectBoardPostEventBatchList(searchOpenDate);

        if (postRangeBatchList == null || postRangeBatchList.isEmpty()) {
            logger.debug("<<단체 게시판>> 예약 푸시 게시글이 없어 배치를 종료합니다.");
        } else {
            for (BoardPostRangeVo boardPostRange : postRangeBatchList) {
                if (StringUtils.isEmpty(boardPostRange.getRangeSido()) || StringUtils.isEmpty(boardPostRange.getRangeSigungu())) {
                    logger.debug("<<단체 게시판>> 시도 및 시군구 데이터가 없어 배치를 종료합니다.");
                    continue;
                }

                List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(boardPostRange.getRangeSido(), boardPostRange.getRangeSigungu(), boardPostRange.getRangeDong());
                String value = TagUtils.removeTag(boardPostRange.getContent()).replaceAll("<!DOCTYPE html>", StringUtils.EMPTY);
                // value = value.replaceAll("&nbsp;", StringUtils.EMPTY);
                String action = String.format(PushAction.BOARD_GROUP.getValue(), boardPostRange.getId());

                // 푸시 발송
                this.pushUtils.sendPush(PushGubun.BOARD_GROUP, boardPostRange.getTitle(), value, action, String.valueOf(boardPostRange.getId()), false, targetUserList);

                // 푸시 발송 여부 업데이트
                BoardPostVo post = new BoardPostVo();
                post.setId(boardPostRange.getId());
                post.setPushSendYn("Y");
                post.setDisplayYn("Y");
                this.boardPostMapper.updateBoardPost(post);

                // 푸시 발송 시간 업데이트
                this.boardPostRangeMapper.updatePushSendDate(boardPostRange.getRangeId());
            }
        }

        if (postEventBatchList == null || postEventBatchList.isEmpty()) {
            logger.debug("<<이벤트 게시판>> 예약 푸시 게시글이 없어 배치를 종료합니다.");
        } else {
            for (BoardPostEventVo boardPostEvent : postEventBatchList) {
                // 현재 이벤트 게시판은 성별 / 연령별 / 지역별 구분이 없다.
                // List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList(boardPostEvent.getRangeSido(), boardPostEvent.getRangeSigungu(), boardPostEvent.getRangeDong());
                List<SimpleUser> targetUserList = this.pushUtils.findPushTargetUserList();
                String value = TagUtils.removeTag(boardPostEvent.getContent()).replaceAll("<!DOCTYPE html>", StringUtils.EMPTY);
                // value = value.replaceAll("&nbsp;", StringUtils.EMPTY);
                String action = String.format(PushAction.BOARD_EVENT.getValue(), boardPostEvent.getId());

                // 푸시 발송
                this.pushUtils.sendPush(PushGubun.BOARD_EVENT, boardPostEvent.getTitle(), value, action, String.valueOf(boardPostEvent.getId()), false, targetUserList);

                // 푸시 발송 여부 업데이트
                BoardPostVo post = new BoardPostVo();
                post.setId(boardPostEvent.getId());
                post.setPushSendYn("Y");
                this.boardPostMapper.updateBoardPost(post);

                // 푸시 발송 시간 업데이트
                this.boardPostRangeMapper.updatePushSendDate(boardPostEvent.getRangeId());
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.board.BoardService#updateCommentDisplayYn(com.jaha.web.emaul.v2.model.board.BoardPostVo)
     */
    @Override
    @Transactional
    public void updateCommentDisplayYn(BoardPostVo param) {
        this.boardPostMapper.updateCommentDisplayYn(param);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.board.BoardService#updateBlindYn(com.jaha.web.emaul.v2.model.board.BoardPostVo)
     */
    @Override
    @Transactional
    public void updateBlindYn(BoardPostVo param) {
        this.boardPostMapper.updateBlindYn(param);
    }

}
