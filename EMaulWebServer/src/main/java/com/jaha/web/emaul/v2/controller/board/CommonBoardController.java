/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 11. 8.
 */
package com.jaha.web.emaul.v2.controller.board;

import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
///////////////////////////////////////////////////////////////// 이전 버전의 클래스 /////////////////////////////////////////////////////////////////
import com.jaha.web.emaul.model.FileInfo;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.Responses;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.v2.constants.BoardConstants.BoardType;
import com.jaha.web.emaul.v2.model.board.BoardCategoryVo;
///////////////////////////////////////////////////////////////// 이전 버전의 클래스 /////////////////////////////////////////////////////////////////
import com.jaha.web.emaul.v2.model.board.BoardCommentReplyVo;
import com.jaha.web.emaul.v2.model.board.BoardCommentVo;
import com.jaha.web.emaul.v2.model.board.BoardDto;
import com.jaha.web.emaul.v2.model.board.BoardPostVo;
import com.jaha.web.emaul.v2.service.board.BoardCategoryService;
import com.jaha.web.emaul.v2.service.board.BoardService;
import com.jaha.web.emaul.v2.util.PagingHelper;

/**
 * <pre>
 * Class Name : CommonBoardController.java
 * Description : 게시판 댓글/답글, 이미지 뷰 등 공통 컨트롤러
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 11. 8.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 11. 8.
 * @version 1.0
 */
@Controller
public class CommonBoardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonBoardController.class);

    @Value("${file.path.temp}")
    private String tempFilePath;

    @Value("${file.path.board.image}")
    private String filePathBoardImage;

    @Value("${file.path.board.attach}")
    private String filePathBoardAttach;

    @Autowired
    private BoardCategoryService boardCategoryService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private UserService userService;

    /**
     * 게시글 목록 조회
     *
     * @param req
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @param userAuthType admin / user / group-admin / jaha
     * @param categoryType notice / event / group / tts / community / complaint
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/list/{categoryId}")
    public String findBoardPostList(HttpServletRequest req, Model model, PagingHelper pagingHelper, BoardDto boardDto, @PathVariable(value = "userAuthType") String userAuthType,
            @PathVariable(value = "categoryType") String categoryType, @PathVariable(value = "categoryId") Long categoryId) {
        try {
            boardDto.setSearchCategoryId(categoryId);

            if ("user".equals(userAuthType)) {
                boardDto.setSearchDisplayYn("Y");
            }

            // 게시글 목록 조회
            List<?> boardPostList = this.boardService.findBoardPostList(boardDto);

            model.addAttribute("category", boardDto.getBoardCategory());
            model.addAttribute("boardPostList", boardPostList);
            model.addAttribute("leftSideMenu", "board");
        } catch (Exception e) {
            LOGGER.error("<<게시판 게시글 조회 중 오류>>", e);
        }

        String viewPath = "";
        if (!"jaha".equals(userAuthType) && !"group-admin".equals(userAuthType) && !"event".equals(userAuthType) && !"system-notice".equals(userAuthType)) {
            viewPath = "v2/" + userAuthType + "/board/common/list";
        } else {
            viewPath = "v2/" + userAuthType + "/board/" + categoryType + "/list";
        }

        return viewPath;
    }

    /**
     * 게시글 글 등록 페이지 이동
     *
     * @param req
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @param userAuthType admin / user / group-admin / jaha
     * @param categoryType notice / event / group / tts / community / complaint
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/create-form/{categoryId}", method = RequestMethod.GET)
    public String moveBoardPostForm(HttpServletRequest req, Model model, PagingHelper pagingHelper, BoardDto boardDto, @PathVariable(value = "userAuthType") String userAuthType,
            @PathVariable(value = "categoryType") String categoryType, @PathVariable(value = "categoryId") Long categoryId) {
        try {

            LOGGER.debug(">>> categoryType : " + categoryType + " / categoryId : " + categoryId);
            boardDto.setSearchCategoryId(categoryId);

            BoardCategoryVo boardCategory = this.boardCategoryService.findBoardCategory(categoryId);
            model.addAttribute("category", boardCategory);
            model.addAttribute("leftSideMenu", "board");
        } catch (Exception e) {
            LOGGER.error("<<게시판 게시글 등록 페이지 이동 중 오류>>", e);
        }

        String viewPath = "";
        if (!"jaha".equals(userAuthType) && !"group-admin".equals(userAuthType) && !"event".equals(userAuthType) && !"system-notice".equals(userAuthType)) {
            viewPath = "v2/" + userAuthType + "/board/common/create-form";
        } else {
            viewPath = "v2/" + userAuthType + "/board/" + categoryType + "/create-form";
        }

        LOGGER.debug("<<viewPath>> {}", viewPath);

        return viewPath;
    }

    /**
     * 게시글 글 삭제
     *
     * @param req
     * @param boardDto
     * @param boardPost
     * @param userAuthType admin / user / group-admin / jaha
     * @param categoryType notice / event / group / tts / community / complaint
     * @param categoryId
     * @param postId
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/remove/{categoryId}/{postId}")
    public String removeBoardPost(HttpServletRequest req, BoardDto boardDto, BoardPostVo boardPost, @PathVariable(value = "userAuthType") String userAuthType,
            @PathVariable(value = "categoryType") String categoryType, @PathVariable(value = "categoryId") Long categoryId, @PathVariable(value = "postId") Long postId) {
        try {
            boardPost.setId(postId);

            // 게시글 삭제
            this.boardService.removeBoardPost(boardDto, boardPost);
        } catch (Exception e) {
            LOGGER.error("<<게시판 게시글 삭제 중 오류>>", e);
        }

        if (BoardType.EVENT.getCode().equalsIgnoreCase(categoryType)) {
            return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/list";
        } else {
            return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/list/" + categoryId;
        }
    }

    /**
     * 댓글 등록
     *
     * @param req
     * @param model
     * @param redirectAttr
     * @param pagingHelper
     * @param boardDto
     * @param boardComment
     * @param userAuthType admin / user / group-admin / jaha
     * @param categoryType notice / event / group / tts / community / complaint
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/comment/create", method = RequestMethod.POST)
    public String createBoardComment(HttpServletRequest req, Model model, RedirectAttributes redirectAttr, PagingHelper pagingHelper, BoardDto boardDto, BoardCommentVo boardComment,
            @PathVariable(value = "userAuthType") String userAuthType, @PathVariable(value = "categoryType") String categoryType) {
        try {
            // 댓글 등록
            this.boardService.regBoardComment(boardDto, boardComment);

            // 해당 댓글로 이동
            redirectAttr.addFlashAttribute("commentIdForAnchor", boardComment.getId());

            if (BoardType.EVENT.getCode().equalsIgnoreCase(categoryType) || BoardType.FAQ.getCode().equalsIgnoreCase(categoryType)) {
                return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/read/" + boardDto.getSearchPostId();
            } else {
                return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/read/" + boardDto.getSearchCategoryId() + "/" + boardDto.getSearchPostId();
            }
        } catch (Exception e) {
            LOGGER.error("<<댓글 등록 중 오류>>", e);
        }

        return "redirect:/";
    }

    /**
     * 댓글 수정
     *
     * @param req
     * @param model
     * @param redirectAttr
     * @param pagingHelper
     * @param boardDto
     * @param boardComment
     * @param userAuthType admin / user / group-admin / jaha
     * @param categoryType notice / event / group / tts / community / complaint
     * @param commentId
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/comment/modify/{commentId}", method = RequestMethod.POST)
    public String modifyBoardComment(HttpServletRequest req, Model model, RedirectAttributes redirectAttr, PagingHelper pagingHelper, BoardDto boardDto, BoardCommentVo boardComment,
            @PathVariable(value = "userAuthType") String userAuthType, @PathVariable(value = "categoryType") String categoryType, @PathVariable(value = "commentId") Long commentId) {
        try {
            // 댓글 수정
            boardComment.setId(commentId);
            this.boardService.modifyBoardComment(boardDto, boardComment);

            // 해당 댓글로 이동
            redirectAttr.addFlashAttribute("commentIdForAnchor", commentId);

            if (BoardType.EVENT.getCode().equalsIgnoreCase(categoryType) || BoardType.FAQ.getCode().equalsIgnoreCase(categoryType)) {
                return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/read/" + boardDto.getSearchPostId();
            } else {
                return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/read/" + boardDto.getSearchCategoryId() + "/" + boardDto.getSearchPostId();
            }
        } catch (Exception e) {
            LOGGER.error("<<댓글 수정 중 오류>>", e);
        }

        return "redirect:/";
    }

    /**
     * 댓글 삭제
     *
     * @param req
     * @param model
     * @param redirectAttr
     * @param pagingHelper
     * @param boardDto
     * @param boardComment
     * @param userAuthType admin / user / group-admin / jaha
     * @param categoryType notice / event / group / tts / community / complaint
     * @param commentId
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/comment/remove/{commentId}", method = RequestMethod.POST)
    public String removeBoardComment(HttpServletRequest req, Model model, RedirectAttributes redirectAttr, PagingHelper pagingHelper, BoardDto boardDto, BoardCommentVo boardComment,
            @PathVariable(value = "userAuthType") String userAuthType, @PathVariable(value = "categoryType") String categoryType, @PathVariable(value = "commentId") Long commentId) {
        try {
            // 댓글 삭제
            boardComment.setId(commentId);
            this.boardService.removeBoardComment(boardDto, boardComment);

            // 해당 댓글로 이동
            redirectAttr.addFlashAttribute("commentIdForAnchor", commentId);

            if (BoardType.EVENT.getCode().equalsIgnoreCase(categoryType) || BoardType.FAQ.getCode().equalsIgnoreCase(categoryType)) {
                return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/read/" + boardDto.getSearchPostId();
            } else {
                return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/read/" + boardDto.getSearchCategoryId() + "/" + boardDto.getSearchPostId();
            }
        } catch (Exception e) {
            LOGGER.error("<<댓글 삭제 중 오류>>", e);
        }

        return "redirect:/";
    }

    /**
     * 답글 등록
     *
     * @param req
     * @param model
     * @param redirectAttr
     * @param pagingHelper
     * @param boardDto
     * @param boardCommentReply
     * @param userAuthType admin / user / group-admin / jaha
     * @param categoryType notice / event / group / tts / community / complaint
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/comment/reply/create", method = RequestMethod.POST)
    public String createBoardCommentReply(HttpServletRequest req, Model model, RedirectAttributes redirectAttr, PagingHelper pagingHelper, BoardDto boardDto, BoardCommentReplyVo boardCommentReply,
            @PathVariable(value = "userAuthType") String userAuthType, @PathVariable(value = "categoryType") String categoryType) {
        try {
            // 답글 등록
            this.boardService.regBoardCommentReply(boardDto, boardCommentReply);

            // 해당 답글로 이동
            redirectAttr.addFlashAttribute("commentIdForAnchor", boardDto.getSearchCommentId());
            redirectAttr.addFlashAttribute("replyIdForAnchor", boardCommentReply.getId());
            // 답글 노출 여부
            redirectAttr.addFlashAttribute("replyAreaDisplayYn", "Y");

            if (BoardType.EVENT.getCode().equalsIgnoreCase(categoryType) || BoardType.FAQ.getCode().equalsIgnoreCase(categoryType)) {
                return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/read/" + boardDto.getSearchPostId();
            } else {
                return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/read/" + boardDto.getSearchCategoryId() + "/" + boardDto.getSearchPostId();
            }
        } catch (Exception e) {
            LOGGER.error("<<답글 등록 중 오류>>", e);
        }

        return "redirect:/";
    }

    /**
     * 답글 삭제
     *
     * @param req
     * @param model
     * @param redirectAttr
     * @param pagingHelper
     * @param boardDto
     * @param boardCommentReply
     * @param userAuthType admin / user / group-admin / jaha
     * @param categoryType notice / event / group / tts / community / complaint
     * @param replyId
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/comment/reply/remove/{replyId}", method = RequestMethod.POST)
    public String removeBoardCommentReply(HttpServletRequest req, Model model, RedirectAttributes redirectAttr, PagingHelper pagingHelper, BoardDto boardDto, BoardCommentReplyVo boardCommentReply,
            @PathVariable(value = "userAuthType") String userAuthType, @PathVariable(value = "categoryType") String categoryType, @PathVariable(value = "replyId") Long replyId) {
        try {
            // 답글 삭제
            boardCommentReply.setId(replyId);
            this.boardService.removeBoardCommentReply(boardDto, boardCommentReply);

            // 해당 댓글로 이동
            redirectAttr.addFlashAttribute("commentIdForAnchor", boardDto.getSearchCommentId());
            // 답글 노출 여부
            redirectAttr.addFlashAttribute("replyAreaDisplayYn", "Y");

            if (BoardType.EVENT.getCode().equalsIgnoreCase(categoryType) || BoardType.FAQ.getCode().equalsIgnoreCase(categoryType)) {
                return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/read/" + boardDto.getSearchPostId();
            } else {
                return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/read/" + boardDto.getSearchCategoryId() + "/" + boardDto.getSearchPostId();
            }
        } catch (Exception e) {
            LOGGER.error("<<답글 삭제 중 오류>>", e);
        }

        return "redirect:/";
    }

    /**
     * 이미지 파일 뷰
     *
     * @param postId
     * @param fileName
     * @return
     */
    @RequestMapping(value = "/v2/board/common/post/image/{postId}/{fileName:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleImageRequest(@PathVariable("postId") Long postId, @PathVariable("fileName") String fileName) {
        File imageFile = new File(String.format(this.filePathBoardImage, postId / 1000L, postId), fileName);
        return Responses.getFileEntity(imageFile, postId + "-" + fileName);
    }

    /**
     * 첨부파일 다운로드
     *
     * @param postId
     * @param fileName
     * @return
     */
    @RequestMapping(value = "/v2/board/common/post/file/{postId}/{fileName:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleFileRequest(@PathVariable("postId") Long postId, @PathVariable("fileName") String fileName) {
        File attachFile = new File(String.format(this.filePathBoardAttach, postId / 1000L, postId), fileName);
        return Responses.getFileEntity(attachFile, fileName);
    }

    /**
     * 아파트별로 이미지와 파일을 리턴한다.<br />
     * 서수원자이 홈페이지에서 이관한 데이터, 이미지, 파일을 처리하기 위해서... (2016.07.04)
     *
     * @author PNS
     * @param aptId
     * @param type
     * @param fileName
     * @return
     */
    @RequestMapping(value = "/v2/api/public/files/{aptId}/{type}/{fileName:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> handleFileRequestByApt(@PathVariable("aptId") String aptId, @PathVariable("type") String type, @PathVariable("fileName") String fileName) {
        try {
            String decFilename = URLDecoder.decode(fileName, "utf-8");
            File file = new File("/nas/EMaul/files", String.format("/%s/%s/%s", aptId, type, decFilename));

            return Responses.getFileEntity(file, aptId + "-" + fileName);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("<<파일 및 이미지 다운로드 중 오류>>", e);
        }

        return null;
    }

    /**
     * 이미지 파일 삭제
     *
     * @param req
     * @param boardDto
     * @param boardPost
     * @param postId
     * @param fileName
     * @return
     */
    @RequestMapping(value = "/v2/board/common/post/image/remove/{postId}/{fileName:.+}")
    @ResponseBody
    public String removePostImageFile(HttpServletRequest req, BoardDto boardDto, BoardPostVo boardPost, @PathVariable("postId") Long postId, @PathVariable("fileName") String fileName) {
        try {
            File imageFile = new File(String.format(this.filePathBoardImage, postId / 1000L, postId), fileName); // 0, 1, 2
            LOGGER.debug("<<이미지파일 삭제>> {}", imageFile.getCanonicalPath());
            imageFile.delete();

            String[] temps = fileName.split("[.]");
            String thumbName = temps[0] + "-thumb.jpg";

            File thumbFile = new File(String.format(this.filePathBoardImage, postId / 1000L, postId), thumbName); // 0, 1, 2
            LOGGER.debug("<<썸네일파일 삭제>> {}", thumbFile.getCanonicalPath());
            thumbFile.delete();

            boardPost.setId(postId);
            this.boardService.removeBoardPostImage(boardPost);

            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * 첨부파일 삭제
     *
     * @param req
     * @param boardDto
     * @param boardPost
     * @param postId
     * @param fileName
     * @return
     */
    @RequestMapping(value = "/v2/board/common/post/file/remove/{postId}/{fileName:.+}")
    @ResponseBody
    public String removePostAttachFile(HttpServletRequest req, BoardDto boardDto, BoardPostVo boardPost, @PathVariable("postId") Long postId, @PathVariable("fileName") String fileName) {
        try {
            File attachFile = new File(String.format(this.filePathBoardAttach, postId / 1000L, postId), fileName);
            LOGGER.debug("<<첨부파일 삭제>> {}", attachFile.getCanonicalPath());
            attachFile.delete();

            boardPost.setId(postId);
            this.boardService.removeBoardPostAttach(boardPost, fileName);

            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * 첨부파일 및 파일정보 삭제
     *
     * @param req
     * @param boardDto
     * @param boardPost
     * @param postId
     * @param fileKey
     * @return
     */
    @RequestMapping(value = "/v2/board/common/post/file-info/remove/{postId}/{fileKey}")
    @ResponseBody
    public String removePostAttachFile(HttpServletRequest req, BoardDto boardDto, BoardPostVo boardPost, @PathVariable("postId") Long postId, @PathVariable("fileKey") Long fileKey) {
        try {
            this.boardService.removeBoardPostAttach(postId, fileKey);

            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * 파일을 임시 저장 폴더에 업로드한다.<br />
     *
     * @param img
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/v2/board/common/file/temp/upload", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public @ResponseBody String uploadBoardTempFile(@RequestParam(value = "attachFile", required = false) MultipartFile attachFile) throws JsonProcessingException {
        this.deleteOldTempFiles();

        String originalFileName = attachFile.getOriginalFilename();
        String ext = FilenameUtils.getExtension(originalFileName);

        ObjectMapper mapper = new ObjectMapper();
        FileInfo fileInfo = null;

        try {
            File dir = new File(this.tempFilePath);
            if (!dir.exists()) {
                dir.mkdirs();
                dir.setReadable(true, false);
                dir.setWritable(true, false);
            }

            // String changedFileName = String.format("%s.%s", RandomKeys.make(16), ext);
            // File dest = new File(dir, changedFileName);
            //
            // while (dest.exists()) {
            // dest = new File(dir, changedFileName);
            // }

            String onlyFileName = FilenameUtils.getName(originalFileName);
            String fileName = FilenameUtils.removeExtension(onlyFileName) + "-" + new SimpleDateFormat("HHmmss").format(new Date()) + "." + ext;
            File dest = new File(dir, fileName);

            while (dest.exists()) {
                dest = new File(dir, fileName);
            }

            dest.createNewFile();
            dest.setReadable(true, false);
            dest.setWritable(true, false);

            attachFile.transferTo(dest);

            fileInfo = new FileInfo();
            fileInfo.filePath = this.tempFilePath;
            fileInfo.fileName = dest.getName();
            fileInfo.fileOriginName = fileName; // FilenameUtils.getName(originalFileName);
            fileInfo.ext = ext;
            fileInfo.size = attachFile.getSize() / 1024; // kb 단위로 저장
        } catch (Exception e) {
            LOGGER.error("<<파일업로드 오류>> {}", e.getMessage());
        }

        return mapper.writeValueAsString(fileInfo);
    }

    /**
     * 임시 저장소의 오래된 파일 삭제
     */
    private void deleteOldTempFiles() {
        File dir = new File(this.tempFilePath);
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return System.currentTimeMillis() - pathname.lastModified() > 24 * 60 * 60 * 1000;
            }
        });
        if (files != null && files.length != 0) {
            for (File file : files) {
                file.delete();
            }
        }
    }



    /**
     * 단체관리자용 댓글 리스트 조회 <br/>
     * 게시판 카테고리별 댓글 목록 조회
     *
     * @param req
     * @param model
     * @param pagingHelper
     * @param boardDto
     * @param userAuthType
     * @param categoryType
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/comment/list/{categoryId}")
    public String findBoardCommentList(HttpServletRequest req, Model model, PagingHelper pagingHelper, BoardDto boardDto, @PathVariable(value = "userAuthType") String userAuthType,
            @PathVariable(value = "categoryType") String categoryType, @PathVariable(value = "categoryId") Long categoryId) {
        try {
            boardDto.setSearchCategoryId(categoryId);

            // 댓글 목록 조회 (공통 find 제외)
            List<BoardCommentVo> boardPostCommentList = this.boardService.selectGroupAdminBoardCommentList(boardDto);

            model.addAttribute("category", boardDto.getBoardCategory());
            model.addAttribute("boardPostCommentList", boardPostCommentList);
        } catch (Exception e) {
            LOGGER.error("<<게시판 댓글 조회 중 오류>>", e);
        }

        String viewPath = "v2/" + userAuthType + "/board/" + categoryType + "/comment-list";

        return viewPath;
    }



    /**
     * 예약된 게시글 푸시 발송
     */
    @RequestMapping(value = "/v2/public/batch/board/reserved-post/push/send")
    @ResponseBody
    public String sendPushReservedPostBatch() {
        try {
            this.boardService.sendPushReservedPostBatch();
            return "OK";
        } catch (Exception e) {
            LOGGER.error("<<예약된 게시글 푸시 발송 배치 실행 중 오류>>", e);
            return "NOK";
        }
    }


    /**
     * 게시글 댓글 표시여부 수정
     *
     * @param req
     * @param json
     * @return
     */
    @RequestMapping(value = "/v2/board/common/post/update/commentDisplayYn", method = RequestMethod.POST)
    @ResponseBody
    public String updatePostCommentDisplayYn(HttpServletRequest req, PagingHelper pagingHelper, BoardDto boardDto, @RequestBody String json) {
        try {

            User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
            if (!user.type.jaha && !user.type.admin) {
                LOGGER.error(">>> 게시물 댓글 표시여부 수정 접근권한 오류");
                return "NOK";
            }

            JSONObject obj = new JSONObject(json);

            LOGGER.debug(">>> postId : " + obj.getLong("postId"));
            LOGGER.debug(">>> commentDisplayYn : " + obj.getString("commentDisplayYn"));

            BoardPostVo post = new BoardPostVo();
            post.setId(obj.getLong("postId"));
            post.setCommentDisplayYn(obj.getString("commentDisplayYn"));
            this.boardService.updateCommentDisplayYn(post);

            return "OK";
        } catch (Exception e) {
            LOGGER.error(">>> 게시물 댓글 표시여부 수정 오류 : ", e);
            return "NOK";
        }
    }

    /**
     * 게시물 숨김 처리여부 수정
     *
     * @param req
     * @param pagingHelper
     * @param boardDto
     * @param json
     * @return
     */
    @RequestMapping(value = "/v2/board/common/post/update/blindYn", method = RequestMethod.POST)
    @ResponseBody
    public String updatePostBlindYn(HttpServletRequest req, PagingHelper pagingHelper, BoardDto boardDto, @RequestBody String json) {
        try {

            User user = userService.getUser(SessionAttrs.getUserId(req.getSession()));
            if (!user.type.jaha) {
                LOGGER.error(">>> 게시물 숨김 처리 여부 수정 접근권한 오류");
                return "NOK";
            }

            JSONObject obj = new JSONObject(json);

            LOGGER.debug(">>> postId : " + obj.getLong("postId"));
            LOGGER.debug(">>> blindYn : " + obj.getString("blindYn"));

            BoardPostVo post = new BoardPostVo();
            post.setId(obj.getLong("postId"));
            post.setBlindYn(obj.getString("blindYn"));
            this.boardService.updateBlindYn(post);

            return "OK";
        } catch (Exception e) {
            LOGGER.error(">>> 게시물 숨김 여부 수정 오류 : ", e);
            return "NOK";
        }
    }

    /**
     * 답글 수정
     *
     * @param req
     * @param model
     * @param redirectAttr
     * @param pagingHelper
     * @param boardDto
     * @param boardCommentReply
     * @param userAuthType admin / user / group-admin / jaha
     * @param categoryType notice / event / group / tts / community / complaint
     * @param replyId
     * @return
     */
    @RequestMapping(value = "/v2/{userAuthType}/board/{categoryType}/comment/reply/modify/{replyId}", method = RequestMethod.POST)
    public String modifyBoardCommentReply(HttpServletRequest req, Model model, RedirectAttributes redirectAttr, PagingHelper pagingHelper, BoardDto boardDto, BoardCommentReplyVo boardCommentReply,
            @PathVariable(value = "userAuthType") String userAuthType, @PathVariable(value = "categoryType") String categoryType, @PathVariable(value = "replyId") Long replyId) {
        try {
            // 답글 수정
            boardCommentReply.setId(replyId);
            this.boardService.modifyBoardCommentReply(boardDto, boardCommentReply);

            // 해당 답글로 이동
            redirectAttr.addFlashAttribute("commentIdForAnchor", boardDto.getSearchCommentId());
            redirectAttr.addFlashAttribute("replyIdForAnchor", replyId);
            // 답글 노출 여부
            redirectAttr.addFlashAttribute("replyAreaDisplayYn", "Y");

            if (BoardType.EVENT.getCode().equalsIgnoreCase(categoryType) || BoardType.FAQ.getCode().equalsIgnoreCase(categoryType)) {
                return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/read/" + boardDto.getSearchPostId();
            } else {
                return "redirect:/v2/" + userAuthType + "/board/" + categoryType + "/read/" + boardDto.getSearchCategoryId() + "/" + boardDto.getSearchPostId();
            }
        } catch (Exception e) {
            LOGGER.error("<<답글 수정 중 오류>>", e);
        }

        return "redirect:/";
    }

}
