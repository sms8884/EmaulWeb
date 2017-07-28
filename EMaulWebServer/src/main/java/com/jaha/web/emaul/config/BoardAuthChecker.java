package com.jaha.web.emaul.config;

import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.collect.Lists;
///////////////////////////////////////////////////////////////// 이전 버전의 클래스 /////////////////////////////////////////////////////////////////
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.v2.mapper.board.BoardCategoryMapper;
import com.jaha.web.emaul.v2.model.board.BoardCategoryVo;
import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.common.LoginSession;

public class BoardAuthChecker extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoardAuthChecker.class);

    @Autowired
    private BoardCategoryMapper boardCategoryMapper;
    @Autowired
    private UserService userService;

    /** 일단 관리자 게시판 권한만 체크 */
    static final List<String> BOARD_ACCESS_CHECK_URLS = Lists.newArrayList("/v2/admin/board", "/v2/jaha/board", "/v2/jaha/board-mgr");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String uri = request.getRequestURI();
            LOGGER.debug("<<auth checker. request.getRequestURI()>> {}", uri);
            LOGGER.debug("<<auth checker. request.getRemoteAddr()>> {}", request.getRemoteAddr());

            boolean needToCheck = false;

            for (String filterUrl : BOARD_ACCESS_CHECK_URLS) {
                if (uri.startsWith(filterUrl)) {
                    needToCheck = true;
                    break;
                }
            }

            if (needToCheck == false) {
                LOGGER.debug("<<체크할 필요가 없는 URL이므로 통과!>>");
                return super.preHandle(request, response, handler);
            }

            // URI의 마지막은 카테고리ID
            String[] checkers = uri.split("[/]", -1);
            String checkCategoryId = checkers[checkers.length - 1];
            LOGGER.debug("<<게시판 카테고리 ID>> {}", checkCategoryId);

            if (NumberUtils.isNumber(checkCategoryId) == false) {
                needToCheck = false;
            }

            if (needToCheck == false) {
                LOGGER.debug("<<비정상적인 카테고리 아이디이므로 통과!>>");
                return super.preHandle(request, response, handler);
            }

            LoginSession loginSession = SessionAttrs.getLoginSession(request.getSession());
            if (loginSession == null) {
                LOGGER.debug("<<로그인 세션이 없습니다. 로그인 페이지로 이동합니다!>>");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/api/public/web/login");
                dispatcher.forward(request, response);
                return false;
            }

            boolean hasBoardAccessAuth = this.checkBoardAccessAuth(loginSession, checkCategoryId, uri);

            if (hasBoardAccessAuth) {
                return super.preHandle(request, response, handler);
            } else {
                LOGGER.info("<<게시판 접근 권한이 없습니다!>>");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/api/public/web/login"); // TODO: 권한 오류 페이지로 이동
                dispatcher.forward(request, response);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("<<게시판 권한체크 오류>>", e);
            return false;
        }
    }

    /**
     * 게시판 접근 권한을 체크한다.
     *
     * @param loginSession
     * @param checkCategoryId
     * @return
     */
    private boolean checkBoardAccessAuth(LoginSession loginSession, String checkCategoryId, String uri) throws Exception {
        User user = this.userService.getUser(loginSession.getUserId());
        if (user.type.jaha) { // 자하 권한자는 권한 체크를 하지 않는다.
            LOGGER.debug("<<자하 권한자는 권한 체크를 하지 않는다.>>");
            return true;
        }

        boolean hasBoardAccessAuth = false;
        Long categoryId = Long.valueOf(checkCategoryId);

        BoardDto param = new BoardDto();
        param.setSearchAptId(loginSession.getAptId());
        // 사용자 아파트ID로 카테고리 목록 조회
        List<BoardCategoryVo> boardCategoryList = this.boardCategoryMapper.selectBoardCategoryList(param);

        BoardCategoryVo checkCategory = null;

        if (boardCategoryList != null && !boardCategoryList.isEmpty()) {
            if (uri.contains("/read") || uri.contains("/modify") || uri.contains("/modify-form") || uri.contains("/remove")) { // TODO : 읽기 및 수정화면의 권한은 다시 확인.
                return true;
            }
            for (BoardCategoryVo bc : boardCategoryList) {
                if (categoryId.equals(bc.getId())) {
                    hasBoardAccessAuth = true;
                    checkCategory = bc;
                    break;
                }
            }
        }

        if (checkCategory != null) {
            if (uri.contains("/list")) {
                hasBoardAccessAuth = this.compareUserTypeToBoardType(user.type.getTrueTypes(), checkCategory.getJsonArrayReadableUserType());
            } else if (uri.contains("/read")) {
                hasBoardAccessAuth = this.compareUserTypeToBoardType(user.type.getTrueTypes(), checkCategory.getJsonArrayReadableUserType());
            } else if (uri.contains("/create") || uri.contains("/modify") || uri.contains("/remove") || uri.contains("/delete")) {
                hasBoardAccessAuth = this.compareUserTypeToBoardType(user.type.getTrueTypes(), checkCategory.getJsonArrayWritableUserType());
            }
        }

        return hasBoardAccessAuth;
    }

    /**
     * 사용자권한타입과 게시판사용자권한타입(["jaha","admin","user","gasChecker"])을 비교한다.
     *
     * @param user
     * @param userType
     * @return
     */
    private boolean compareUserTypeToBoardType(List<String> userTypeList, String boardAuthType) {
        boardAuthType = StringUtils.remove(boardAuthType, '[');
        boardAuthType = StringUtils.remove(boardAuthType, ']');
        boardAuthType = StringUtils.remove(boardAuthType, '"');
        String[] boardTypes = boardAuthType.split("[,]", -1);

        LOGGER.debug("<<게시판 읽기/쓰기 권한 체크>> 사용자권한 {}, 게시판사용자권한 {}", userTypeList, boardAuthType);

        boolean checkResult = false;
        for (String ut : userTypeList) {
            if (ArrayUtils.contains(boardTypes, ut)) {
                checkResult = true;
                break;
            }
        }

        return checkResult;
    }

}
