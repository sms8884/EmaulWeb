/**
 *
 */
package com.jaha.web.emaul.v2.mapper.vote;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.v2.model.vote.VoteDto;
import com.jaha.web.emaul.v2.model.vote.VoteGroupVo;
import com.jaha.web.emaul.v2.model.vote.VoteItemVo;
import com.jaha.web.emaul.v2.model.vote.VoteKeyVo;
import com.jaha.web.emaul.v2.model.vote.VoteOfflineResultVo;
import com.jaha.web.emaul.v2.model.vote.VoteTypeVo;
import com.jaha.web.emaul.v2.model.vote.VoteVo;
import com.jaha.web.emaul.v2.model.vote.VoterOfflineVo;
import com.jaha.web.emaul.v2.model.vote.VoterSecurityVo;
import com.jaha.web.emaul.v2.model.vote.VoterVo;

/**
 * @author 조영태(cyt@jahasmart.com) <br />
 *         This Mapper class mapped db-table called vote (+ apt)
 */
/**
 * <pre>
 * Class Name : VoteAdminMapper.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 10. 19.     조영태      Generation
 * </pre>
 *
 * @author 조영태
 * @since 2016. 10. 19.
 * @version 1.0
 */
@Mapper
public interface VoteAdminMapper {

    /**
     * vote의 총 레코드 수를 검색조건에 맞게 조회한다.
     *
     * @param voteDto
     * @return
     */
    public int selectVoteListCount(VoteDto voteDto);

    /**
     * vote의 목록을 검색조건에 맞게 조회한다.
     *
     * @param voteDto
     * @return
     */
    public List<VoteVo> selectVoteList(VoteDto voteDto);

    /**
     * vote 의 종류를 조회한다. (투표유형)
     *
     * @param voteTypeVo
     * @return
     */
    public List<VoteTypeVo> selectVoteTypeList(VoteTypeVo voteTypeVo);


    /**
     * vote 상세 조회 (투표 상세 조회)
     *
     * @param voteDto
     * @return
     */
    public VoteVo getVote(VoteDto voteDto);

    /**
     * voter 리스트 조회
     *
     * @param voteDto
     * @return
     */
    public List<VoterVo> selectVoterList(VoteDto voteDto);

    /**
     * 전자투표 참여자 목록 갯수 조회
     *
     * @param voteDto
     * @return
     */
    public int selectVoterListCount(VoteDto voteDto);

    /**
     * 보안투표 투표자 리스트 조회
     *
     * @param voteDto
     * @return
     */
    public List<VoterSecurityVo> selectVoterSecurityList(VoteDto voteDto);

    /**
     * 보안투표자 아이템 복호화 처리
     * 
     * @param voterSecurityVo
     * @return
     */
    public Long updateVoterSecurityItem(VoterSecurityVo voterSecurityVo);

    /**
     * 오프라인 voter 리스트 조회
     *
     * @param voteDto
     * @return
     */
    public List<VoterOfflineVo> selectOfflineVoterList(VoteDto voteDto);

    /**
     * 오프라인 voter 리스트 갯수 조회
     *
     * @param voteDto
     * @return
     */
    public int selectOfflineVoterListCount(VoteDto voteDto);


    /**
     * 투표 삭제처리 : vote.use_yn = 'N'
     *
     * @param voteDto
     * @return
     */
    public Long deleteVote(VoteDto voteDto);


    /**
     * 투표정보 수정처리 : vote
     *
     * @param voteVo
     * @return
     */
    public Long updateVote(VoteVo voteVo);

    /**
     * 보안투표 개표여부 수정 : vote.dec_yn
     *
     * @param voteVo
     * @return
     */
    public Long updateVoteDecYn(VoteVo voteVo);


    /**
     * 투표 파일정보 수정 : file1, file2
     *
     * @param voteVo
     * @return
     */
    public Long updateVoteFile(VoteVo voteVo);


    /**
     * 투표 파일정보 수정 : photo1 ~ 5
     *
     * @param voteVo
     * @return
     */
    public Long updateVoteImageFile(VoteVo voteVo);


    /**
     * 투표 집계완료여부 수정 : vote_result_available
     *
     * @param voteVo
     * @return
     */
    public Long updateVoteResult(VoteVo voteVo);

    /**
     * 푸쉬 발송일자 수정 : vote.push_send_date
     *
     * @param voteVo
     * @return
     */
    public Long updateVotePushSendDate(VoteVo voteVo);

    /**
     * 투표 기간 수정 : start_date, end_date
     *
     * @param voteVo
     * @return
     */
    public Long updateVoteInfo(VoteVo voteVo);



    /**
     * 투표정보 입력 : vote
     *
     * @param voteVo
     * @return
     */
    public Long insertVote(VoteVo voteVo);

    /**
     * 투표 항목 정보 조회
     *
     * @param voteDto
     * @return
     */
    public List<VoteItemVo> selectVoteItemList(VoteDto voteDto);

    /**
     * 투표 항목정보 입력 : vote_item
     *
     * @param voteItemList
     * @return
     */
    public Long insertVoteItemList(List<VoteItemVo> voteItemList);

    /**
     * 투표 항목 삭제 : vote_item <br />
     * 투표 수정 시 UPDATE 방식을 DELTE - INSERT 로 수행한다.
     *
     * @param voteVo
     * @return
     */
    public Long deleteVoteItem(VoteVo voteVo);


    /**
     * 보안키 상세 조회
     *
     * @param voteDto
     * @return
     */
    public VoteKeyVo getVoteKey(VoteDto voteDto);


    /**
     * 투표 보안키 목록 조회 <br/>
     * SELECT BOX용 조건 (오늘날자 이후등...)
     *
     * @param voteDto
     * @return
     */
    public List<VoteKeyVo> getVoteKeyList(VoteDto voteDto);


    /**
     * 개표 목록 조회 (투표 보안키 목록)
     *
     * @param voteDto
     * @return
     */
    public List<VoteKeyVo> selectVoteKeyList(VoteDto voteDto);


    /**
     * 개표목록 갯수 조회
     *
     * @param voteDto
     * @return
     */
    public int selectVoteKeyListCount(VoteDto voteDto);

    /**
     * 투표 보안키 수정
     *
     * @param voteKeyVo
     * @return
     */
    public Long updateVoteKey(VoteKeyVo voteKeyVo);


    /**
     * 투표 시작/종료일자 수정 <br />
     * 동일한 보안키사용 투표 일괄 적용
     *
     * @param voteVo
     * @return
     */
    public Long updateVoteDateByVoteKey(VoteVo voteVo);

    /**
     * 투표 오프라인 집계처리
     *
     * @param voteOfflineResultVo
     * @return
     */
    public Long insertVoteOfflineResult(VoteOfflineResultVo voteOfflineResultVo);

    /**
     * 투표 오프라인 집계 취소 처리
     *
     * @param voteId
     * @return
     */
    public Long deleteVoteOfflineResult(Long voteId);


    /**
     * 투표 오프라인 집계처리 조회
     *
     * @param voteId
     * @return
     */
    public VoteOfflineResultVo getVoteOfflineResult(Long voteId);


    /**
     * 아파트별 동 조회
     *
     * @param aptId
     * @return
     * @throws Exception
     */
    public List<String> getAptDong(Long aptId);


    /**
     * 아파트 동별 호 조회 (숫자형 데이터만 조회)
     *
     * @param voteDto
     * @return
     */
    public List<String> getAptHo(VoteDto voteDto);

    /**
     * 아파트 층수 조회 (숫자형 호 데이터만 조회)
     *
     * @param voteDto
     * @return
     */
    public String getAptHoRow(VoteDto voteDto);

    /**
     * 아파트 호 조회 (숫자형 호 데이터만 조회)
     *
     * @param voteDto
     * @return
     */
    public String getAptHoColumn(VoteDto voteDto);


    /**
     * 선거구 목록 갯수
     *
     * @param voteDto
     * @return
     */
    public int selectVoteGroupListCount(VoteDto voteDto);

    /**
     * 선거구 목록 조회
     *
     * @param voteDto
     * @return
     */
    public List<VoteGroupVo> selectVoteGroupList(VoteDto voteDto);


    /**
     * 선거구 등록
     *
     * @param voteGroupVo
     * @return
     */
    public Long insertVoteGroup(VoteGroupVo voteGroupVo);


    /**
     * 선거구 상세조회
     *
     * @param voteDto
     * @return
     */
    public VoteGroupVo getVoteGroup(VoteDto voteDto);

    /**
     * 선거구 삭제처리 : vote.use_yn = 'N'
     *
     * @param voteDto
     * @return
     */
    public Long deleteVoteGroup(VoteDto voteDto);

    /**
     * 선거구 수정
     *
     * @param voteGroupVo
     * @return
     */
    public Long updateVoteGroup(VoteGroupVo voteGroupVo);


    /**
     * 투표 미참여 리스트 조회
     *
     * @param voteVo
     * @return
     */
    public List<Long> getYetVoteList(VoteVo voteVo);


    /**
     * 오프라인 투표자 명부 삭제
     *
     * @param voterOfflineVo
     * @return
     */
    public Long deleteVoterOffline(VoterOfflineVo voterOfflineVo);


    /**
     * 오프라인 투표 명부 입력 (리스트)
     *
     * @param list
     * @return
     */
    public Long insertVoterOfflineList(List<VoterOfflineVo> list);

}
