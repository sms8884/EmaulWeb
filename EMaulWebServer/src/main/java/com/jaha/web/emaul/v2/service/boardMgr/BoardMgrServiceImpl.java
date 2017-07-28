/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.service.boardMgr;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jaha.web.emaul.model.BaseSecuModel;
import com.jaha.web.emaul.model.CommonCode;
import com.jaha.web.emaul.service.CommonService;
import com.jaha.web.emaul.v2.constants.BoardConstants;
import com.jaha.web.emaul.v2.constants.BoardConstants.BoardMgrType;
import com.jaha.web.emaul.v2.mapper.board.BoardCategoryMapper;
import com.jaha.web.emaul.v2.mapper.board.BoardCommentMapper;
import com.jaha.web.emaul.v2.mapper.board.BoardCommentReplyMapper;
import com.jaha.web.emaul.v2.mapper.board.BoardPostMapper;
import com.jaha.web.emaul.v2.mapper.boardMgr.BoardMgrMapper;
import com.jaha.web.emaul.v2.model.board.BoardCommentReplyVo;
import com.jaha.web.emaul.v2.model.board.BoardCommentVo;
import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardMgrPostVo;
import com.jaha.web.emaul.v2.model.board.BoardPostReadVo;
import com.jaha.web.emaul.v2.model.board.BoardPostVo;
import com.jaha.web.emaul.v2.model.board.BoardPostWrapper;
import com.jaha.web.emaul.v2.model.common.FileInfoVo;
import com.jaha.web.emaul.v2.model.common.Sort;

/**
 * <pre>
 * Class Name : BoardMgrServiceImpl.java
 * Description : 게시판 모니터링 서비스
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * </pre>
 *
 * @version 1.0
 */
@Service("BoardMgrServiceImpl")
public class BoardMgrServiceImpl implements BoardMgrService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BoardCategoryMapper boardCategoryMapper;

    @Autowired
    private BoardMgrMapper boardMgrMapper;

    @Autowired
    private BoardPostMapper boardPostMapper;

    @Autowired
    private BoardCommentMapper boardCommentMapper;

    @Autowired
    private BoardCommentReplyMapper boardCommentReplyMapper;

    @Autowired
    private CommonService commonService;

    private final BaseSecuModel baseSecuModel = new BaseSecuModel();

    @Value("${file.path.board.attach}")
    private String filePathBoardAttach;

    @Value("${file.path.editor.image}")
    private String filePathEditorImage;

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.boardMgr.BoardMgrService#findBoardMgrPostList(com.jaha.web.emaul.v2.model.board.BoardDto)
     */
    @Override
    @Transactional(readOnly = true)
    public List<? extends BoardMgrPostVo> findBoardMgrPostList(BoardDto param) throws Exception {

        String type = param.getType();
        if (BoardMgrType.SPAM.getCode().equals(type)) {
            param.setCodeList(commonService.findByCodeGroup("POST_WORD"));
        }

        // 목록 레코드 수 조회
        param.getPagingHelper().setTotalRecordCount(this.getTotalRecordCount(param));
        this.putSortList(param);
        logger.debug(">>> pagingHelper : " + param.getPagingHelper());
        // 목록 조회
        return this.getBoardMgrPostList(param);
    }

    /**
     * 게시글 관리 총 레코드 수 반환<br />
     *
     * @param param
     * @return
     */
    private int getTotalRecordCount(BoardDto param) throws Exception {
        String type = param.getType();
        int totalCount = 0;

        if (BoardMgrType.BLIND.getCode().equals(type)) {
            // 숨김글
            totalCount = this.boardMgrMapper.selectBoardBlindPostListCount(param);
        } else if (BoardMgrType.POST.getCode().equals(type)) {
            totalCount = this.boardMgrMapper.selectBoardPostListCount(param);
        } else if (BoardMgrType.SPAM.getCode().equals(type)) {
            totalCount = this.boardMgrMapper.selectBoardSpamPostListCount(param);
        } else if (BoardMgrType.COMMENT.getCode().equals(type)) {
            totalCount = this.boardCommentMapper.selectBoardMgrCommentListCount(param);
        } else {
            // 사용자 작성글 조회
            totalCount = this.boardMgrMapper.selectBoardMgrPostListCount(param);
        }

        logger.debug(">>> totalCount : " + totalCount);
        return totalCount;
    }

    /**
     * 정렬 세팅(DB Query에 전달할 ORDER BY 데이터)<br />
     *
     * @param param
     * @return
     */
    private void putSortList(BoardDto param) throws Exception {
        String type = param.getType();
        List<Sort> sortList = new ArrayList<Sort>();

        if (BoardMgrType.BLIND.getCode().equals(type) || BoardMgrType.POST.getCode().equals(type)) {
            Sort sort = new Sort();
            sort = new Sort();
            sort.setColumn("id");
            sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
            sortList.add(sort);
            param.getPagingHelper().setSortList(sortList);

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
     * 게시글 관리 목록 반환<br />
     *
     * @param param
     * @return
     * @throws Exception
     */
    private List<? extends BoardMgrPostVo> getBoardMgrPostList(BoardDto param) throws Exception {
        String type = param.getType();

        if (BoardMgrType.BLIND.getCode().equals(type)) {
            return this.boardMgrMapper.selectBoardBlindPostList(param);
        } else if (BoardMgrType.POST.getCode().equals(type)) {
            return this.boardMgrMapper.selectBoardPostList(param);
        } else if (BoardMgrType.SPAM.getCode().equals(type)) {
            return this.boardMgrMapper.selectBoardSpamPostList(param);
        } else {
            return this.boardMgrMapper.selectBoardMgrPostList(param);
        }
    }

    @Override
    public List<BoardCommentVo> getPostCommentList(BoardDto param) throws Exception {
        param.getPagingHelper().setTotalRecordCount(this.getTotalRecordCount(param));
        this.putSortList(param);
        logger.debug(">>> pagingHelper : " + param.getPagingHelper());
        // 목록 조회
        return this.boardCommentMapper.selectBoardMgrCommentList(param);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.boardMgr.BoardMgrService#boardPostCountByUserId(java.lang.Long)
     */
    @Override
    public int boardPostCountByUserId(Long userId) throws Exception {
        return boardMgrMapper.selectBoardPostCountByUserId(userId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.boardMgr.BoardMgrService#boardCommentCountByUserId(java.lang.Long)
     */
    @Override
    public int boardCommentCountByUserId(Long userId) throws Exception {
        return boardMgrMapper.selectBoardCommentCountByUserId(userId);
    }



    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.boardMgr.BoardMgrService#findBoardMgrPost(com.jaha.web.emaul.v2.model.board.BoardDto, java.lang.Long)
     */
    @Override
    public BoardPostWrapper<?> findBoardMgrPost(BoardDto param, Long postId) throws Exception {

        BoardPostVo boardPost = this.boardPostMapper.selectBoardPost(postId);
        if (boardPost != null && StringUtils.isNotEmpty(boardPost.getFileGroupKey())) {
            List<FileInfoVo> fileInfoList = this.boardMgrMapper.getFileGroup(boardPost.getFileGroupKey());
            boardPost.setFileInfoListV2(fileInfoList);
        }

        // 카테고리 정보 세팅
        param.setBoardCategory(boardCategoryMapper.selectBoardCategory(boardPost.getCategoryId()));

        // 댓글 목록 조회할 게시글 아이디
        param.setSearchPostId(postId);

        // 댓글 목록 레코드 수 조회
        int totalRecordCount = this.boardMgrMapper.selectBoardCommentListCount(param);
        param.getPagingHelper().setTotalRecordCount(totalRecordCount);

        List<Sort> sortList = new ArrayList<Sort>();
        Sort sort = new Sort();
        sort.setColumn("BC.id");
        sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
        sortList.add(sort);

        param.getPagingHelper().setSortList(sortList);

        List<BoardCommentVo> boardCommentList = this.boardMgrMapper.selectBoardCommentList(param);

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

        boardPost.setBoardCommentList(boardCommentList);

        return new BoardPostWrapper<BoardPostVo>(boardPost);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.boardMgr.BoardMgrService#boardCommentDisplayUpdate(com.jaha.web.emaul.v2.model.board.BoardCommentVo)
     */
    @Override
    public int boardCommentDisplayUpdate(BoardCommentVo boardCommentVo) throws Exception {
        return boardMgrMapper.boardCommentDisplayUpdate(boardCommentVo);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.boardMgr.BoardMgrService#boardPostBlindYnUpdate(com.jaha.web.emaul.v2.model.board.BoardPostVo)
     */
    @Override
    public int boardPostBlindYnUpdate(BoardPostVo boardPostVo) throws Exception {
        return boardMgrMapper.boardPostBlindYnUpdate(boardPostVo);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.boardMgr.BoardMgrService#setPostRead(com.jaha.web.emaul.v2.model.board.BoardPostReadVo)
     */
    @Override
    public int setPostRead(BoardPostReadVo boardPostReadVo) throws Exception {

        BoardPostReadVo asisRead = boardMgrMapper.getPostRead(boardPostReadVo);
        if (asisRead != null) {
            return 0;
        } else {
            return boardMgrMapper.setPostRead(boardPostReadVo);
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.boardMgr.BoardMgrService#boardWordUseYnUpdate(com.jaha.web.emaul.model.CommonCode)
     */
    @Override
    public int boardWordUseYnUpdate(CommonCode commonCode) throws Exception {
        return boardMgrMapper.boardWordUseYnUpdate(commonCode.getCdIds());
    }
}
