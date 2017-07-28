/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.service.board;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

///////////////////////////////////////////////////////////////// 이전 버전의 클래스 /////////////////////////////////////////////////////////////////
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.TagUtils;
///////////////////////////////////////////////////////////////// 이전 버전의 클래스 /////////////////////////////////////////////////////////////////
import com.jaha.web.emaul.v2.constants.BoardConstants;
import com.jaha.web.emaul.v2.model.board.BoardCategoryVo;
import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardPostVo;
import com.jaha.web.emaul.v2.model.board.BoardPostWrapper;

/**
 * <pre>
 * Class Name : BoardServiceAdvice.java
 * Description : 게시판 서비스 실행 이후, 작성자명/제목명 등 공통 변경 처리
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
public class BoardServiceAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    /**
     * 에러없이 정상적으로 타켓 메소드가 실행된 이후에 실행됨
     *
     * @param joinPoint
     */
    @AfterReturning(pointcut = "execution(public * com.jaha.web.emaul.v2.service.board*.*Board*ServiceImpl.find*(..))", returning = "returnObject")
    public void executeAfterReturnFindMethod(JoinPoint joinPoint, Object returnObject) throws Throwable {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        logger.debug("<<클래스명: {}, 메소드명: {}>>", className, methodName);
        // logger.debug("<<joinPoint>> {}", joinPoint);

        Object params[] = joinPoint.getArgs();

        BoardCategoryVo boardCategory = null;

        for (Object param : params) {
            if (param instanceof BoardDto) {
                boardCategory = ((BoardDto) param).getBoardCategory();
                // logger.debug(ToStringBuilder.reflectionToString(param));
            }
        }

        if (returnObject != null) {
            if (returnObject instanceof List<?>) {

                @SuppressWarnings("unchecked")
                List<? extends BoardPostVo> boardPostList = (List<? extends BoardPostVo>) returnObject;

                for (BoardPostVo boardPost : boardPostList) {

                    User user = this.userService.getUser(boardPost.getUserId());

                    if (boardCategory == null || StringUtils.isEmpty(boardCategory.getUserPrivacy())) {
                        // 게시글 관리용
                        boardPost.setWriterName(this.makeUserNameForPost(user, "ALIAS"));
                    } else {
                        boardPost.setWriterName(this.makeUserNameForPost(user, boardCategory.getUserPrivacy()));
                    }


                    if (user.type.jaha) {
                        boardPost.setUserAptName("e마을");
                    } else {
                        boardPost.setUserAptName(user.house.apt.name);
                    }

                    this.convertBoardPostTitle(boardCategory, boardPost);

                    // 게시글 모니터링 관리자용
                    if (boardCategory == null || StringUtils.isEmpty(boardCategory.getUserPrivacy())) {
                        String tempContent = boardPost.getContent().replaceAll("(\r\n|\n)", StringUtils.EMPTY);
                        tempContent = TagUtils.extractBody(tempContent);
                        tempContent = tempContent.replaceAll("<.*?>", "");
                        // StringUtil.removeHTML
                        boardPost.setContentOnlyBody(tempContent);
                    }

                }

                returnObject = boardPostList;
                // logger.debug(returnObject.toString());
            } else if (returnObject instanceof BoardPostVo) {
                BoardPostVo boardPost = (BoardPostVo) returnObject;
                User user = this.userService.getUser(boardPost.getUserId());

                boardPost.setWriterName(this.makeUserNameForPost(user, boardCategory.getUserPrivacy()));
                boardPost.setUserAptName(user.house.apt.name);

                this.convertBoardPostTitle(boardCategory, boardPost);
                this.convertBoardPostBody(boardCategory, boardPost);
                // logger.debug(returnObject.toString());
            } else if (returnObject instanceof BoardPostWrapper<?>) {
                BoardPostWrapper<?> boardPost = (BoardPostWrapper<?>) returnObject;

                User user = this.userService.getUser(boardPost.get().getUserId());

                boardPost.get().setWriterName(this.makeUserNameForPost(user, boardCategory.getUserPrivacy()));
                boardPost.get().setUserAptName(user.house.apt.name);

                this.convertBoardPostTitle(boardCategory, boardPost.get());
                this.convertBoardPostBody(boardCategory, boardPost.get());
                // logger.debug(returnObject.toString());
            }
        }
    }

    /**
     * 게시판에 작성자 표시를 고객의 요구에 맞게 다양하게 처리함. 기본 규칙은 닉네임이 있으면 [닉네임 + 동]으로 표시하고 없는 경우는 [이름 + 동]으로 표시함
     *
     * @author namsuk.park
     * @param user
     * @return String 게시판용 작성자명
     */
    private String makeUserNameForPost(User user, String userPrivacyType) {
        String userName = HtmlUtils.htmlEscape(user.getFullName());

        if (BoardConstants.UserPrivacy.ALIAS.getCode().equals(userPrivacyType)) {
            // 2017.01.19 오석민 팀장 요청으로 위시티블루밍5단지아파트 하드코딩 제거
            // if (user.getNickname() != null && user.house.apt.id != 255) {
            if (user.getNickname() != null) {
                userName = HtmlUtils.htmlEscape(user.getNickname().name);
            }
        }

        userName += " (" + HtmlUtils.htmlEscape(user.house == null ? "" : user.house.dong) + "동)";

        return userName;
    }

    /**
     * 게시판 카테고리 모드가 html이고 게시판 title이 비어있는 경우에만 게시판 내용을 변환하여 타이틀로 수정
     *
     * @param boardCategory
     * @param boardPost
     */
    private void convertBoardPostTitle(BoardCategoryVo boardCategory, BoardPostVo boardPost) {
        // if (BoardConstants.ContentMode.HTML.getValue().equals(boardCategory.getContentMode()) && StringUtils.isBlank(boardPost.getTitle())) {
        if (StringUtils.isBlank(boardPost.getTitle())) {
            String tempContent = boardPost.getContent();
            String tempTitle = TagUtils.removeTag(tempContent).replaceAll("<!DOCTYPE html>", StringUtils.EMPTY);

            if (tempTitle.length() > 60) {
                boardPost.setTitle(StringUtils.abbreviate(tempTitle, 60));
            } else {
                boardPost.setTitle(tempTitle);
            }
        }
    }

    /**
     * 게시판 카테고리 모드가 html인 경우, <body> 태그 안의 내용 추출
     *
     * @param boardCategory
     * @param boardPost
     */
    private void convertBoardPostBody(BoardCategoryVo boardCategory, BoardPostVo boardPost) {
        String content = StringUtils.defaultString(boardPost.getContent());

        if (BoardConstants.ContentMode.HTML.getValue().equals(boardCategory.getContentMode())) {
            if (content.indexOf("<!DOCTYPE html>") > -1) {
                content = content.replaceAll("(\r\n|\n)", StringUtils.EMPTY);
            }

            String contentOnlyBody = TagUtils.extractBody(content);

            boardPost.setContentOnlyBody(contentOnlyBody);
            return;
        }

        boardPost.setContentOnlyBody(content);
    }

}
