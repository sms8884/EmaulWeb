package com.jaha.web.emaul.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.jaha.web.emaul.model.BoardCategory;
import com.jaha.web.emaul.model.GroupAdminTargetArea;
import com.jaha.web.emaul.model.SimpleAddress;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.BoardService;
import com.jaha.web.emaul.service.CommonService;
import com.jaha.web.emaul.service.GroupAdminService;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.AddressConverter;
import com.jaha.web.emaul.util.SessionAttrs;

public class LoginChecker extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginChecker.class);

    @Autowired
    private UserService userService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private GroupAdminService groupAdminService;

    @Autowired
    private CommonService commonService;

    // private static final List<String> filterUrlsStartWith = Lists.newArrayList("/api/public", "/health", "/error", "/favicon.ico", "/join", "/today/public", "/app", "/api/board/post/image");
    private static final List<String> filterUrlsStartWith = Lists.newArrayList("/api/public", "/health", "/error", "/favicon.ico", "/join", "/today/public", "/app", "/api/board/post/image",
            "/common/gcm", "/provision", "/app/download", "/v2/public");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        LOGGER.debug("<<login checker. req url>> {}", url);

        if (!isLoginChecked(request)) {
            boolean invalidSession = true;
            for (String filterUrl : filterUrlsStartWith) {
                if (url.startsWith(filterUrl)) {
                    invalidSession = false;
                    break;
                }
            }

            if (invalidSession) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/api/public/web/login");
                dispatcher.forward(request, response);

                return false;
            }
        } else {
            // [START] 단체관리자 기능 추가 by PNS 2016.09.19
            // user와 url의 null 체크하기 위해서 코드 수정
            Long userId = SessionAttrs.getUserId(request.getSession());
            User user = userService.getUser(userId);

            if (url == null || user == null || user.type == null) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/api/public/web/admin-required");
                dispatcher.forward(request, response);

                return false;
            }
            // [END]

            if (url.startsWith("/admin")) {
                if (!user.type.jaha && !user.type.admin) {
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/api/public/web/admin-required");
                    dispatcher.forward(request, response);

                    return false;
                }
            } else if (url.startsWith("/jaha")) {
                if (!user.type.jaha) {

                    // [START] 단체관리자 기능 추가 by PNS 2016.09.19
                    // 단체관리자의 경우 jaha 밑에 있는 news 접근을 허용해줌
                    if (!user.type.groupAdmin && !url.startsWith("/jaha/board/news/")) {
                        RequestDispatcher dispatcher = request.getRequestDispatcher("/api/public/web/admin-required");
                        dispatcher.forward(request, response);

                        return false;
                    }
                    // [END]
                }
            }
        }

        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            this.setUserAndBoardCategory(request, modelAndView);
        } else if ("POST".equalsIgnoreCase(request.getMethod())) {
            String uri = request.getRequestURI();

            LOGGER.debug("<<uri>> {}", uri);

            // 2.0버전의 게시판
            if (uri.startsWith("/v2/jaha/board") || uri.startsWith("/v2/admin/board") || uri.startsWith("/v2/group-admin/board") || uri.startsWith("/v2/user/board")
                    || uri.startsWith("/v2/board/common")) {
                this.setUserAndBoardCategory(request, modelAndView);
            }
        }
    }

    private boolean isLoginChecked(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long userId = SessionAttrs.getUserId(session);

        return userId != null && userId != 0l;
    }

    /**
     * 페이지 이동 시마다 로그인한 사용자 정보와 게시판 카테고리 정보를 세팅한다.
     *
     * @param request
     * @param modelAndView
     */
    private void setUserAndBoardCategory(HttpServletRequest request, ModelAndView modelAndView) {
        Long userId = SessionAttrs.getUserId(request.getSession());

        if (userId != null && modelAndView != null) {
            User user = userService.getUser(userId);
            user.house.apt.strAddress = AddressConverter.toStringAddress(user.house.apt.address);
            user.house.apt.strAddressOld = AddressConverter.toStringAddressOld(user.house.apt.address);

            // 상단 커뮤니티 클릭 시 좌측 게시판 카테고리 출력 처리
            // 게시판 카테고리 읽기 권한 추가
            List<BoardCategory> categories = new ArrayList<BoardCategory>();

            // List<BoardCategory> categories = boardService.getCategories(userId, "notice", "N");
            List<BoardCategory> noticeCategories = boardService.getCategories(userId, "notice", "N");
            for (BoardCategory bc : noticeCategories) {
                if (bc.isUserReadable(user)) {
                    categories.add(bc);
                }
            }

            // categories.addAll(boardService.getCategories(userId, "community", "N"));
            List<BoardCategory> communityCategories = boardService.getCategories(userId, "community", "N");
            for (BoardCategory bc : communityCategories) {
                if (bc.isUserReadable(user)) {
                    categories.add(bc);
                }
            }

            try {
                // 아직 민원게시판이 정식 오픈하지 않았으므로 임시로 아래 아파트만 보이게 처리
                if (1L == user.house.apt.id || 576L == user.house.apt.id || 581L == user.house.apt.id) {
                    // categories.addAll(boardService.getCategories(userId, "complaint", "N")); // 민원 게시판 추가
                    List<BoardCategory> complaintCategories = boardService.getCategories(userId, "complaint", "N");
                    for (BoardCategory bc : complaintCategories) {
                        if (bc.isUserReadable(user)) {
                            categories.add(bc);
                        }
                    }
                }

                // 아직 방송게시판이 정식 오픈하지 않았으므로 임시로 아래 아파트만 보이게 처리
                if (1L == user.house.apt.id || 191L == user.house.apt.id || 576L == user.house.apt.id || 224L == user.house.apt.id) {
                    // categories.addAll(boardService.getCategories(userId, "tts", "N")); // 방송 게시판은 라이선스 문제로 인하여 이번 운영반영에서 제외되므로 주석처리
                    List<BoardCategory> ttsCategories = boardService.getCategories(userId, "tts", "N");
                    for (BoardCategory bc : ttsCategories) {
                        if (bc.isUserReadable(user)) {
                            categories.add(bc);
                        }
                    }
                }

                if (user.type.groupAdmin) {
                    // categories.addAll(boardService.getCategories(userId, "group", "N")); // 단체 관리자일 경우, 단체 게시판 추가
                    List<BoardCategory> groupCategories = boardService.getCategories(userId, "group", "N");
                    for (BoardCategory bc : groupCategories) {
                        if (bc.isUserReadable(user)) {
                            categories.add(bc);
                        }
                    }

                    // 단체관리자의 노출범위 조회
                    GroupAdminTargetArea displayRange = this.groupAdminService.getTargetArea(userId, user.house.apt.id);

                    if (displayRange != null) {
                        modelAndView.addObject("displayRange", displayRange);

                        List<SimpleAddress> simpleAddrList = this.commonService.findDongListBySidoAndSigungu(displayRange.area1, displayRange.area2);
                        modelAndView.addObject("simpleAddrList", simpleAddrList);
                    }
                }
            } catch (Exception e) {
                LOGGER.info("<<민원게시판/방송게시판 커뮤니티 메뉴 출력 중 오류 발생>> {}", e.getMessage());
            }

            // 메인화면에서 사용
            modelAndView.addObject("noticeCategories", noticeCategories);
            modelAndView.addObject("communityCategories", communityCategories);

            Ordering<BoardCategory> byOrd = new Ordering<BoardCategory>() {
                @Override
                public int compare(BoardCategory left, BoardCategory right) {
                    return Ints.compare(left.ord, right.ord);
                }
            };

            Collections.sort(categories, byOrd);

            modelAndView.addObject("user", user);
            // 커뮤니티 메뉴에서 사용
            modelAndView.addObject("categories", categories);
        }
    }

}
