/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.service.vote;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.jaha.web.emaul.v2.model.vote.VoteDto;
import com.jaha.web.emaul.v2.model.vote.VoteGroupVo;
import com.jaha.web.emaul.v2.model.vote.VoteKeyVo;
import com.jaha.web.emaul.v2.model.vote.VoteOfflineResultVo;
import com.jaha.web.emaul.v2.model.vote.VoteTypeVo;
import com.jaha.web.emaul.v2.model.vote.VoteVo;
import com.jaha.web.emaul.v2.model.vote.VoterOfflineVo;
import com.jaha.web.emaul.v2.model.vote.VoterVo;

/**
 * <pre>
 * Class Name : VoteAdminService.java
 * Description : 어드민 투표 서비스
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 12.     조영태      Generation
 * </pre>
 *
 * @author 조영태
 * @since 2016. 10. 12.
 * @version 1.0
 */
public interface VoteAdminService {

    /**
     * 투표 목록 조회 (vote)
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    List<VoteVo> selectVoteList(VoteDto voteDto) throws Exception;

    /**
     * 투표 목록 조회 (자하권한)
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    List<VoteVo> selectJahaVoteList(VoteDto voteDto) throws Exception;

    /**
     * 투표 종류 조회 (일반형, 찬반형, 단일후보형...)
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    List<VoteTypeVo> selectVoteTypeList(VoteDto voteDto) throws Exception;

    /**
     * 투표 상세
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    VoteVo getVote(VoteDto voteDto) throws Exception;

    /**
     * 투표 정보 조회 <br />
     * 투표 상세 + 투표 항목
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    Map<String, Object> getVoteInfo(VoteDto voteDto) throws Exception;

    /**
     * 투표 결과 정보 조회
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    Map<String, Object> getVoteResultInfo(VoteDto voteDto) throws Exception;

    /**
     * 투표 미 참여자 리스트 조회 (이미 투표에 참여한 회원 제외) AS-IS 로직 유지<br/>
     * TODO : 분석 후 차후에 로직 변경 (2016.10.17 : cyt)
     *
     * @param voteVo
     * @return
     * @throws Exception
     */
    List<Long> getYetVoteList(VoteVo voteVo) throws Exception;


    /**
     * 투표 온라인 참여목록 조회 <br />
     * 현장투표 등 멘트처리는 Query상에서 처리
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    List<VoterVo> selectVoterList(VoteDto voteDto) throws Exception;

    /**
     * 투표 오프라인 참여목록 조회 <br/>
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    List<VoterOfflineVo> selectOfflineVoterList(VoteDto voteDto) throws Exception;

    /**
     * 투표 삭제 : 사용여부 use_yn = 'N' 업데이트 처리
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    Long deleteVote(VoteDto voteDto) throws Exception;

    /**
     * 투표 수정 : vote
     *
     * @param voteVo
     * @return
     * @throws Exception
     */
    Long updateVote(VoteVo voteVo) throws Exception;

    /**
     * 투표 집계결과 수정
     *
     * @param voteVo
     * @return
     * @throws Exception
     */
    Long updateVoteResult(VoteVo voteVo) throws Exception;


    /**
     * 푸쉬 발송일자 수정
     * 
     * @param voteVo
     * @return
     * @throws Exception
     */
    Long updateVotePushSendDate(VoteVo voteVo) throws Exception;



    /**
     * 투표 기간 수정
     *
     * @param voteVo
     * @return
     * @throws Exception
     */
    Long updateVoteDate(VoteVo voteVo) throws Exception;


    /**
     * 투표 보안키 상세 조회
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    VoteKeyVo getVoteKey(VoteDto voteDto) throws Exception;

    /**
     * 투표 보안키 목록 조회 <br />
     * [SELECT BOX용 오늘날자 이후]
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    List<VoteKeyVo> getVoteKeyList(VoteDto voteDto) throws Exception;


    /**
     * 투표 보안키 리스트 조회 [개표 리스트]
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    List<VoteKeyVo> selectVoteKeyList(VoteDto voteDto) throws Exception;



    /**
     * 투표 등록 / 수정 처리<br />
     * isNew true : 등록 / false : 수정<br />
     * v1 : VoteController.fillVoteData()
     *
     * @param isNew
     * @param request
     * @param voteVo
     * @return
     * @throws Exception
     */
    VoteVo setVote(boolean isNew, MultipartHttpServletRequest request, VoteVo voteVo) throws Exception;


    /**
     * 투표 오프라인 집계 처리
     *
     * @param voteOfflineResultVo
     * @return
     * @throws Exception
     */
    Long insertVoteOfflineResult(VoteOfflineResultVo voteOfflineResultVo) throws Exception;

    /**
     * 투표 오프라인 집계 취소 처리
     *
     * @param voteId
     * @return
     * @throws Exception
     */
    Long deleteVoteOfflineResult(Long voteId) throws Exception;


    /**
     * 투표 오프라인 결과 조회
     *
     * @param voteId
     * @return
     * @throws Exception
     */
    VoteOfflineResultVo getVoteOfflineResult(Long voteId) throws Exception;


    /**
     * 보안투표 개표처리
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    Map<String, Object> openVoteKey(VoteDto voteDto) throws Exception;


    /**
     * 아파트별 동 조회
     *
     * @param aptId
     * @return
     * @throws Exception
     */
    List<String> getAptDong(Long aptId) throws Exception;


    /**
     * 아파트 동별 호 조회
     *
     * @param aptId
     * @param dong
     * @return
     * @throws Exception
     */
    List<String> getAptHo(Long aptId, String dong) throws Exception;

    /**
     * 아파트의 층수를 구한다. (숫자형 호만 대상)
     *
     * @param aptId
     * @param dong
     * @return
     * @throws Exception
     */
    String getAptHoRow(Long aptId, String dong) throws Exception;

    /**
     * 아파트 최대 호수를 구한다 (숫자형 호만 대상)
     *
     * @param aptId
     * @param dong
     * @return
     * @throws Exception
     */
    String getAptHoColumn(Long aptId, String dong) throws Exception;


    /**
     * 선거구 목록 조회
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    List<VoteGroupVo> selectVoteGroupList(VoteDto voteDto) throws Exception;

    /**
     * 선거구 등록
     *
     * @param voteGroupVo
     * @return
     * @throws Exception
     */
    Long insertVoteGroup(VoteGroupVo voteGroupVo) throws Exception;


    /**
     * 선거구 상세 조회
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    VoteGroupVo getVoteGroup(VoteDto voteDto) throws Exception;

    /**
     * 선거구 삭제 : 사용여부 use_yn = 'N' 업데이트 처리
     *
     * @param voteDto
     * @return
     * @throws Exception
     */
    Long deleteVoteGroup(VoteDto voteDto) throws Exception;

    /**
     * 선거구 수정
     *
     * @param voteGroupVo
     * @return
     * @throws Exception
     */
    Long updateVoteGroup(VoteGroupVo voteGroupVo) throws Exception;


    /**
     * 투표 오프라인 투표자 명부 삭제 (DELETE / INSERT용)
     *
     * @param VoterOfflineVo
     * @return
     * @throws Exception
     */
    Long deleteVoterOffline(VoterOfflineVo voterOfflineVo) throws Exception;

    /**
     * 투표 오프라인 명부 입력
     *
     * @param list
     * @return
     * @throws Exception
     */
    Long insertVoterOfflineList(List<VoterOfflineVo> list) throws Exception;

}
