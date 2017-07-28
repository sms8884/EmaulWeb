/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.controller.board;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

///////////////////////////////////////////////////////////////// 이전 버전의 클래스 /////////////////////////////////////////////////////////////////
import com.jaha.web.emaul.exception.EmaulWebException;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.util.TagUtils;
import com.jaha.web.emaul.util.Thumbnails;
///////////////////////////////////////////////////////////////// 이전 버전의 클래스 /////////////////////////////////////////////////////////////////
import com.jaha.web.emaul.v2.mapper.board.BoardPostMapper;
import com.jaha.web.emaul.v2.model.board.BoardCommentReplyVo;
import com.jaha.web.emaul.v2.model.board.BoardCommentVo;
import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardPostVo;
import com.jaha.web.emaul.v2.model.common.LoginSession;
import com.jaha.web.emaul.v2.util.PagingHelper;

/**
 * <pre>
 * Class Name : BoardControllerAdvice.java
 * Description : 요청IP/사용자아이디 세팅, 파일저장
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
@Aspect
@Component
public class BoardControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${file.path.board.image}")
    private String filePathBoardImage;

    @Value("${file.path.board.attach}")
    private String filePathBoardAttach;

    @Value("${board.attach.download.url}")
    private String boardAttachDownloadUrl;

    // @Autowired
    // private BoardCategoryMapper boardCategoryMapper;

    @Autowired
    private BoardPostMapper boardPostMapper;

    /**
     * BoardController의 public 메소드 수행 전 실행
     *
     * @param joinPoint
     */
    @Before("execution(public * com.jaha.web.emaul.v2.controller.board*.*Board*Controller.*(..))")
    public void executeBeforeMethod(JoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        logger.debug("<<클래스명: {}, 메소드명: {}>>", className, methodName);
        // logger.debug("<<joinPoint>> {}", joinPoint);

        Object params[] = joinPoint.getArgs();

        HttpServletRequest req = null;
        PagingHelper pagingHelper = null;
        BoardDto boardDto = null;
        BoardPostVo boardPost = null;
        BoardCommentVo boardComment = null;
        BoardCommentReplyVo boardCommentReply = null;

        for (Object param : params) {
            if (param instanceof BoardDto) {
                boardDto = (BoardDto) param;
                // logger.debug(ToStringBuilder.reflectionToString(param));
            } else if (param instanceof BoardPostVo) {
                boardPost = (BoardPostVo) param;
                // logger.debug(ToStringBuilder.reflectionToString(param));
            } else if (param instanceof BoardCommentVo) {
                boardComment = (BoardCommentVo) param;
                // logger.debug(ToStringBuilder.reflectionToString(param));
            } else if (param instanceof BoardCommentReplyVo) {
                boardCommentReply = (BoardCommentReplyVo) param;
                // logger.debug(ToStringBuilder.reflectionToString(param));
            } else if (param instanceof HttpServletRequest) {
                req = (HttpServletRequest) param;
            } else if (param instanceof PagingHelper) {
                pagingHelper = (PagingHelper) param;
            }
        }

        if (boardDto == null) {
            return;
        }

        if (req != null) {
            String reqIp = req.getHeader("X-FORWARDED-FOR");
            if (StringUtils.isEmpty(reqIp)) {
                reqIp = req.getRemoteAddr();
            }
            logger.debug("<<요청IP>> {}", reqIp);

            this.settingDto(req, pagingHelper, boardDto);

            if (boardDto.getLoginSession() == null) {
                return;
            }

            Long loginId = boardDto.getLoginSession().getUserId();
            logger.debug("<<로그인 사용자 아이디>> {}", loginId);

            if (methodName.startsWith("create")) {
                this.settingVoForCreateMothod(reqIp, loginId, boardPost, boardComment, boardCommentReply);
            } else if (methodName.startsWith("modify") || methodName.startsWith("remove")) {
                this.settingVoForModifyMothod(reqIp, loginId, boardPost, boardComment, boardCommentReply);
            }
        }
    }

    /**
     * 생성 시 Vo 세팅
     *
     * @param reqIp
     * @param loginId
     * @param boardDto
     * @param boardPost
     * @param boardComment
     * @param boardCommentReply
     */
    private void settingVoForCreateMothod(String reqIp, Long loginId, BoardPostVo boardPost, BoardCommentVo boardComment, BoardCommentReplyVo boardCommentReply) {
        if (boardPost != null) {
            boardPost.setReqIp(reqIp);
            boardPost.setUserId(loginId);
        }
        if (boardComment != null) {
            boardComment.setReqIp(reqIp);
            boardComment.setUserId(loginId);
        }
        if (boardCommentReply != null) {
            boardCommentReply.setReqIp(reqIp);
            boardCommentReply.setUserId(loginId);
        }
    }

    /**
     * 수정 시 Vo 세팅
     *
     * @param reqIp
     * @param loginId
     * @param boardDto
     * @param boardPost
     * @param boardComment
     * @param boardCommentReply
     */
    private void settingVoForModifyMothod(String reqIp, Long loginId, BoardPostVo boardPost, BoardCommentVo boardComment, BoardCommentReplyVo boardCommentReply) {
        if (boardPost != null) {
            boardPost.setReqIp(reqIp);
            boardPost.setModId(loginId);
        }
        if (boardComment != null) {
            boardComment.setReqIp(reqIp);
            boardComment.setModId(loginId);
        }
        if (boardCommentReply != null) {
            boardCommentReply.setReqIp(reqIp);
            boardCommentReply.setModId(loginId);
        }
    }

    /**
     * Dto 세팅
     *
     * @param req
     * @param pagingHelper
     * @param boardDto
     */
    private void settingDto(HttpServletRequest req, PagingHelper pagingHelper, BoardDto boardDto) {
        LoginSession loginSession = SessionAttrs.getLoginSession(req.getSession());
        boardDto.setPagingHelper(pagingHelper);
        boardDto.setLoginSession(loginSession);
    }

    /**
     * 게시판 글 생성/수정(에러없이 정상적으로 타켓 메소드가 실행된 이후에 실행됨)
     *
     * @param joinPoint
     */
    @AfterReturning(pointcut = "execution(public * com.jaha.web.emaul.v2.controller.board*.*Board*Controller.*(..))", returning = "returnObject")
    public void executeAfterReturnCreateOrModifyMethod(JoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        logger.debug("<<클래스명: {}, 메소드명: {}>>", className, methodName);
        // logger.debug("<<joinPoint>> {}", joinPoint);

        if (className.endsWith("CommonBoardController")) {
            return;
        }

        Object params[] = joinPoint.getArgs();

        BoardDto boardDto = null;
        BoardPostVo boardPost = null;

        for (Object param : params) {
            if (param instanceof BoardDto) {
                boardDto = (BoardDto) param;
                // logger.debug(ToStringBuilder.reflectionToString(param));
            } else if (param instanceof BoardPostVo) {
                boardPost = (BoardPostVo) param;
                // logger.debug(ToStringBuilder.reflectionToString(param));
            }
        }

        if (methodName.startsWith("create") || methodName.startsWith("modify")) {
            this.saveBoardFiles(boardDto, boardPost); // 예전 방식(이미지 파일 최대 3개, 첨부파일 최대 2개)의 파일 저장 처리
        }
    }

    /**
     * 게시판 파일을 저장한다.
     *
     * @param boardDto
     * @param boardPost
     * @throws EmaulWebException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    private void saveBoardFiles(BoardDto boardDto, BoardPostVo boardPost) throws EmaulWebException {
        if (boardDto == null || boardPost == null) {
            return;
        }
        if (boardDto.getImageFiles() == null || boardDto.getAttachFiles() == null) {
            return;
        }

        BoardPostVo param = new BoardPostVo();

        long postId = boardPost.getId();
        long postParentNum = postId / 1000L;

        param.setId(postId);

        String imageFilePath = String.format(this.filePathBoardImage, postParentNum, postId);
        // logger.debug("<<이미지파일 저장경로>> {}", imageFilePath);

        int imageCount = this.saveImageFile(boardDto.getImageFiles(), imageFilePath);
        if (boardPost.getImageCount() != null) { // 수정 시
            imageCount = boardPost.getImageCount() + imageCount;
        }
        param.setImageCount(imageCount);

        String attachFilePath = String.format(this.filePathBoardAttach, postParentNum, postId);
        // logger.debug("<<첨부파일 저장경로>> {}", attachFilePath);

        List<String> orgFileNameList = this.saveAttachFile(boardDto.getAttachFiles(), attachFilePath);

        if (orgFileNameList != null && !orgFileNameList.isEmpty()) {
            int size = orgFileNameList.size();

            for (int i = 0; i < size; i++) {
                String fileUri = String.format(this.boardAttachDownloadUrl, postId, orgFileNameList.get(i));

                if (i == 0) {
                    if (StringUtils.isNotEmpty(orgFileNameList.get(0))) {
                        param.setFile1(fileUri);
                    }
                } else if (i == 1) {
                    if (StringUtils.isNotEmpty(orgFileNameList.get(1))) {
                        param.setFile2(fileUri);
                    }
                }
            }
        }

        this.boardPostMapper.updateBoardPost(param);
    }

    /**
     * 이미지 파일을 저장한다.
     *
     * @param imageFiles
     * @param imageFilePath
     * @return
     * @throws EmaulWebException
     */
    private int saveImageFile(MultipartFile[] imageFiles, String imageFilePath) throws EmaulWebException {
        if (imageFiles == null || imageFiles.length == 0) {
            return 0;
        }

        final int imageCount = imageFiles.length;
        int finalImageCount = 0;

        for (int i = 0; i < imageCount; i++) {
            try {
                if (imageFiles[i] == null || imageFiles[i].isEmpty()) {
                    continue;
                }

                File dir = new File(imageFilePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File dest = new File(dir, String.format("%s.jpg", i));
                dest.createNewFile();
                imageFiles[i].transferTo(dest);

                // 썸네일 생성
                Thumbnails.create(dest);

                finalImageCount++;
            } catch (IOException ioe) {
                logger.error("<<이미지 파일 저장 중 오류>>", ioe);
            }
        }

        return finalImageCount;
    }

    /**
     * 첨부 파일을 저장한다.
     *
     * @param attachFiles
     * @param attachFilePath
     * @return
     * @throws EmaulWebException
     */
    private List<String> saveAttachFile(MultipartFile[] attachFiles, String attachFilePath) throws EmaulWebException {
        if (attachFiles == null || attachFiles.length == 0) {
            return null;
        }

        List<String> orgFileNameList = new ArrayList<String>();
        final int attachCount = attachFiles.length;

        for (int i = 0; i < attachCount; i++) {
            try {
                if (attachFiles[i] == null || attachFiles[i].isEmpty()) {
                    orgFileNameList.add(StringUtils.EMPTY);
                    continue;
                }

                String originalFileName = attachFiles[i].getOriginalFilename();
                if (originalFileName == null || originalFileName.isEmpty()) {
                    orgFileNameList.add(StringUtils.EMPTY);
                    continue;
                }

                File dir = new File(attachFilePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // logger.debug("<<originalFileName1>> {}", originalFileName);
                originalFileName = FilenameUtils.getName(TagUtils.removeTag(originalFileName.replaceAll(" ", "-").replaceAll("%", "")));
                // logger.debug("<<originalFileName2>> {}", originalFileName);

                File dest = new File(dir, originalFileName);
                dest.createNewFile();
                attachFiles[i].transferTo(dest);

                orgFileNameList.add(originalFileName);
            } catch (IOException ioe) {
                logger.error("<<첨부 파일 저장 중 오류>>", ioe);
            }
        }

        return orgFileNameList;
    }

}
