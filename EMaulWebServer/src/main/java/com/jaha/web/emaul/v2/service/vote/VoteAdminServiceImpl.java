/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 9. 22.
 */
package com.jaha.web.emaul.v2.service.vote;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jaha.web.emaul.model.BaseSecuModel;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.service.UserService;
import com.jaha.web.emaul.util.SessionAttrs;
import com.jaha.web.emaul.util.Thumbnails;
import com.jaha.web.emaul.util.VoteCrypto;
import com.jaha.web.emaul.v2.constants.BoardConstants;
import com.jaha.web.emaul.v2.mapper.vote.VoteAdminMapper;
import com.jaha.web.emaul.v2.model.common.Search;
import com.jaha.web.emaul.v2.model.common.Sort;
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
import com.jaha.web.emaul.v2.util.PagingHelper;

/**
 * <pre>
 * Class Name : VoteAdminServiceImpl.java
 * Description : 어드민 투표 서비스 구현
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
@Service
public class VoteAdminServiceImpl implements VoteAdminService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private VoteAdminMapper voteAdminMapper;

    // @Autowired
    // private UserService userService;

    @Value("${file.path.root}")
    private String rootFilePath;

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#selectVoteList(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional(readOnly = true)
    public List<VoteVo> selectVoteList(VoteDto voteDto) throws Exception {

        logger.debug(">>> voteDto : " + voteDto.toString());

        // 투표 목록 레코드 수 조회
        int totalRecordCount = this.voteAdminMapper.selectVoteListCount(voteDto);
        voteDto.getPagingHelper().setTotalRecordCount(totalRecordCount);

        List<Sort> sortList = new ArrayList<Sort>();
        Sort sort = new Sort();
        sort.setColumn("reg_date");
        sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
        sortList.add(sort);
        voteDto.getPagingHelper().setSortList(sortList);

        logger.debug(">>> totalRecordCount : " + totalRecordCount);
        logger.debug(">>> sort : " + sort.getColumn());
        logger.debug(">>> sortList : " + sortList.size());

        // 투표 목록 조회
        return this.voteAdminMapper.selectVoteList(voteDto);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#selectJahaVoteList(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional(readOnly = true)
    public List<VoteVo> selectJahaVoteList(VoteDto voteDto) throws Exception {

        logger.debug(">>> voteDto : " + voteDto.toString());

        // 투표 목록 레코드 수 조회
        int totalRecordCount = this.voteAdminMapper.selectVoteListCount(voteDto);
        voteDto.getPagingHelper().setTotalRecordCount(totalRecordCount);

        List<Sort> sortList = new ArrayList<Sort>();
        Sort sort = new Sort();
        sort.setColumn("reg_date");
        sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
        sortList.add(sort);
        voteDto.getPagingHelper().setSortList(sortList);

        logger.debug(">>> totalRecordCount : " + totalRecordCount);
        logger.debug(">>> sort : " + sort.getColumn());
        logger.debug(">>> sortList : " + sortList.size());

        // 투표 목록 조회
        return this.voteAdminMapper.selectVoteList(voteDto);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#selectVoteTypeList(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional(readOnly = true)
    public List<VoteTypeVo> selectVoteTypeList(VoteDto voteDto) throws Exception {

        VoteTypeVo voteTypeVo = new VoteTypeVo();
        voteTypeVo.setMain("vote");

        // 투표 목록 조회
        return this.voteAdminMapper.selectVoteTypeList(voteTypeVo);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#getVote(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional(readOnly = true)
    public VoteVo getVote(VoteDto voteDto) throws Exception {

        // 투표 상세 조회
        return this.voteAdminMapper.getVote(voteDto);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#getVoteInfo(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getVoteInfo(VoteDto voteDto) throws Exception {

        Map<String, Object> voteMap = new HashMap<>();
        voteMap.put("vote", this.voteAdminMapper.getVote(voteDto)); // vote
        voteMap.put("voteItems", this.voteAdminMapper.selectVoteItemList(voteDto)); // vote_item

        return voteMap;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#getVoteResultInfo(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getVoteResultInfo(VoteDto voteDto) throws Exception {

        Map<String, Object> voteMap = new HashMap<>();
        Long onlineVoterCount = 0l;
        Long offlineVoterCount = 0l;
        Long totalVoterCount = 0l;

        VoteVo vote = this.voteAdminMapper.getVote(voteDto);

        List<VoterVo> voterList = this.voteAdminMapper.selectVoterList(voteDto);
        List<VoteItemVo> voteItemList = this.voteAdminMapper.selectVoteItemList(voteDto); // vote_item
        // List<VoterSecurityVo> voteSecuList = this.voteAdminMapper.selectVoterSecurityList(voteDto);

        totalVoterCount = (long) voterList.size(); // 온라인 투표자수
        onlineVoterCount = (long) voterList.size(); // 온라인 투표자수

        // if ("Y".equalsIgnoreCase(vote.getEnableSecurity())) {
        //
        // if (voteSecuList != null && !voteSecuList.isEmpty()) {
        // totalVoterCount += voteSecuList.size();
        // onlineVoterCount += voteSecuList.size();
        // }
        // }

        logger.debug(">>> 1. 전체 투표(온라인만) : " + totalVoterCount + "/, 일반 온라인 투표자 : " + voterList.size());// );
        logger.debug(">>> 2. 보안여부 : " + vote.getEnableSecurity() + "/ 투표유형 : " + vote.getTypeId() + " [1 : 기본, 2 : 복수후보 3 : 찬반, 5 : 단일후보, 6 : 동의]");

        // 무효 항목 추가
        VoteItemVo noVoteItemVo = new VoteItemVo();
        noVoteItemVo.setId((long) 0);
        noVoteItemVo.setTitle("무효");
        noVoteItemVo.setParentId(vote.getId());
        noVoteItemVo.setOfflineCount(0);
        noVoteItemVo.setOnlineCount(0);
        voteItemList.add(noVoteItemVo);

        logger.debug(">>> 3. 투표항목수 (무효 강제 생성) : " + voteItemList.size());
        for (VoteItemVo a : voteItemList) {
            a.setOnlineCount(0);
            a.setOfflineCount(0);
            logger.debug("=== [" + a.getId() + "]" + a.getTitle() + "/ online : " + a.getOnlineCount() + "/ offline : " + a.getOfflineCount());
        }

        // 오프라인 투표여부 및 오프라인 집계처리 여부
        logger.debug(">>> 4 오프라인 투표 여부 : " + vote.getOfflineAvailable() + "/ 오프라인 집계 완료 여부 : " + vote.getVoteResultAvailable());
        /**
         * 기존 "실시간" 팝업에서 입력한 수치를 기준으로 오프라인 투표항목 투표숫자를 추출한다. <br />
         * Ref : /{auth}/vote/realtime/{voteId}
         */
        // # v1 유지
        if (vote.getOfflineAvailable()) { // 오프라인 투표여부
            if (vote.getVoteResultAvailable()) { // 오프라인 집계완료 여부
                VoteOfflineResultVo voteOfflineResultVo = this.voteAdminMapper.getVoteOfflineResult(vote.getId());
                Map<Long, Long> offlineMap = new Gson().fromJson(voteOfflineResultVo.getJsonMapVoteItemResult(), new TypeToken<Map<Long, Long>>() {}.getType());
                logger.debug(">>> 오프라인 집계값 : " + offlineMap != null ? offlineMap.toString() : "집계되지 않음");

                // 0 은 무효표
                if (offlineMap != null && !offlineMap.isEmpty()) {
                    for (Map.Entry<Long, Long> entry : offlineMap.entrySet()) {
                        logger.debug(">>> 오프라인 투표항목 : Key = " + entry.getKey() + ", Value = " + entry.getValue());
                        totalVoterCount += entry.getValue(); // 오프라인 투표자수를 추가한다.
                        offlineVoterCount += entry.getValue();

                        // vote_item.offlineCount 세팅
                        for (VoteItemVo a : voteItemList) {
                            if (a.getId().longValue() == entry.getKey().longValue()) {
                                a.setOfflineCount(entry.getValue().intValue());
                                break;
                            }
                        }

                    }
                }

            }
        }

        logger.debug(">>> 오프라인 처리 후 투표 항목 리스트 ");
        for (VoteItemVo a : voteItemList) {
            logger.debug(">>> " + a.getTitle() + "/ online : " + a.getOnlineCount() + "/ offline : " + a.getOfflineCount());
        }

        // -------------------- 투표 항목 별 Loop 수행 및 계산로직 처리 -----------------------------
        // 투표 항목 별 투표자 수 (비율) 조회
        int winnerId = 0;
        int winnerCount = 0;

        for (int i = 0; i < voteItemList.size(); i++) {
            // for (VoteItemVo voteItem : voteItemList) {
            VoteItemVo voteItem = voteItemList.get(i);

            // 투표 항목 아이디 세팅
            voteDto.setItemId(voteItem.getId());

            if ("Y".equalsIgnoreCase(vote.getEnableSecurity())) {
                // 보안투표
                List<VoterSecurityVo> vsList = this.voteAdminMapper.selectVoterSecurityList(voteDto);
                voteItem.setOnlineCount(voteItem.getOnlineCount() + (vsList == null || vsList.isEmpty() ? 0 : vsList.size()));
            } else {
                // 일반투표 (이마을 투표자)
                List<VoterVo> vList = this.voteAdminMapper.selectVoterList(voteDto);
                voteItem.setOnlineCount(voteItem.getOnlineCount() + (vList == null || vList.isEmpty() ? 0 : vList.size()));
            }

            float rate = 0;
            try {
                if (voteItem.getOnlineCount() != 0 || voteItem.getOfflineCount() != 0) {
                    rate = (voteItem.getOnlineCount() + voteItem.getOfflineCount()) / (float) totalVoterCount * 100;
                } else {
                    rate = 0;
                }
            } catch (Exception e) {
                rate = 0;
                logger.debug(">>> rate error : " + e.getMessage());
            }

            voteItem.setVoteRate(String.format("%.2f", rate) + "%");

            // 당선자 추출용
            if (i == 0) {
                winnerId = i;
                winnerCount = voteItem.getOnlineCount() + voteItem.getOfflineCount();
            } else {
                if (winnerCount < voteItem.getOnlineCount() + voteItem.getOfflineCount()) {
                    winnerId = i;
                    winnerCount = voteItem.getOnlineCount() + voteItem.getOfflineCount();
                }
            }
        }

        // 당선자 마킹
        voteItemList.get(winnerId).setIsWinner(true);

        logger.debug(">>> 온라인 처리 후 투표 항목 리스트 ");
        for (VoteItemVo a : voteItemList) {
            logger.debug(">>> " + a.getTitle() + "/ online : " + a.getOnlineCount() + "/ offline : " + a.getOfflineCount() + " / rate : " + a.getVoteRate() + " / winner : " + a.getIsWinner());
        }

        boolean resultState = true;
        if ("done".equals(vote.getStatus())) {
            if ("Y".equals(vote.getEnableSecurity()) && "N".equals(vote.getDecYn())) {
                resultState = false;
            }
        } else {
            resultState = false;
        }
        voteMap.put("resultState", resultState);
        voteMap.put("vote", vote);
        voteMap.put("voterList", voterList);
        voteMap.put("voteItemList", voteItemList);

        // 투표 참여율
        float a = ((float) totalVoterCount / vote.getVotersCount() * 100);;
        if (a > 100) {
            voteMap.put("voteRate", "100% 이상"); // 투표율
        } else {
            voteMap.put("voteRate", String.format("%.2f", a) + "%"); // 투표율
        }

        voteMap.put("totalVoterCount", totalVoterCount); // 총 투표자수
        voteMap.put("onlineVoterCount", onlineVoterCount); // 온라인 투표자수
        voteMap.put("offlineVoterCount", offlineVoterCount); // 오프라인 투표자수

        logger.debug(">>> 오프라인 투표여부 : " + vote.getOfflineAvailable() + "/" + vote.getVoteResultAvailable());
        logger.debug(">>> 총 투표자 : " + totalVoterCount);
        logger.debug(">>> 온라인 투표자 : " + onlineVoterCount);
        logger.debug(">>> 오프라인 투표자 : " + offlineVoterCount);
        logger.debug(">>> 투표항목 수치");

        return voteMap;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#getYetVoteList(com.jaha.web.emaul.v2.model.vote.VoteVo)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Long> getYetVoteList(VoteVo vote) throws Exception {

        /**
         * AS-IS 로직에서 Query로 전체 수정한다.<br/>
         * AS-IS는 해당 아파트의 동/호를 추출한 후 ,<br/>
         * 기 투표자 온라인 사용자 아이디와, <br/>
         * 오프라인 투표자의 동, 호를 String 비교하여 Loop문 처리
         *
         * TO-DO : WHERE 조건 처리
         */
        // List<Long> userIds = Lists.newArrayList();

        if (StringUtils.isEmpty(vote.getTargetDong()) && StringUtils.isEmpty(vote.getJsonArrayTargetGroup())) {
            // 전체 : 별도 조건 없음
        } else if (StringUtils.isNotEmpty(vote.getJsonArrayTargetGroup())) {
            // 선거구별
            Gson gson = new Gson();
            @SuppressWarnings("serial")
            Type longType = new TypeToken<List<Long>>() {}.getType();
            Type stringType = new TypeToken<List<String>>() {}.getType();
            Type jsonObjectType = new TypeToken<List<JsonObject>>() {}.getType();

            List<Long> groupIdList = gson.fromJson(vote.getJsonArrayTargetGroup(), longType);

            List<String> groupDongList = Lists.newArrayList();
            List<String> groupDongHoList = Lists.newArrayList();

            // 선거구 목록을 조회한다.
            VoteDto voteDto = new VoteDto();
            voteDto.setFullYn("Y");
            voteDto.setAptId(vote.getTargetApt());
            voteDto.setGroupIdList(groupIdList);
            List<VoteGroupVo> voteGroupList = this.voteAdminMapper.selectVoteGroupList(voteDto);
            logger.debug(">>> voteGroupList length : " + voteGroupList.size());

            boolean isAll = false;
            for (VoteGroupVo a : voteGroupList) {
                logger.debug(">>> name : " + a.getName() + "/group_type : " + a.getGroupType() + "/target : " + a.getJsonArrayTarget());

                if ("all".equalsIgnoreCase(a.getGroupType())) {
                    // 전체가 포함된 선거구는 해당 아파트의 전체대상
                    isAll = true;
                    break;

                } else if ("dong".equalsIgnoreCase(a.getGroupType())) {

                    List<String> dongList = gson.fromJson(a.getJsonArrayTarget(), stringType);
                    for (String d : dongList) {
                        logger.debug(">>> dong : " + d);
                        groupDongList.add(d);
                    }

                } else if ("ho".equalsIgnoreCase(a.getGroupType())) {

                    List<JsonObject> dongList = gson.fromJson(a.getJsonArrayTarget(), jsonObjectType);

                    for (JsonObject d : dongList) {
                        logger.debug(">>> dong : " + d.get("dong").getAsString() + "/ ho : " + d.get("ho"));
                        List<String> hoList = gson.fromJson(d.get("ho"), stringType);
                        for (String h : hoList) {
                            logger.debug(">>> ho : " + h);
                            groupDongHoList.add(d.get("dong").getAsString() + h);
                        }

                    }
                }
            }

            if (!isAll) { // 전체가 포함된 경우 전체 아파트대상이 됨
                vote.setDongList(groupDongList); // 동 리스트
                vote.setHoList(groupDongHoList); // CONCAT(동, 호) 리스트
            }


        } else if (StringUtils.isNotEmpty(vote.getTargetDong()) && StringUtils.isNotEmpty(vote.getJsonArrayTargetHo())) {
            // 동/호별
            Gson gson = new Gson();
            @SuppressWarnings("serial")
            Type collectionType = new TypeToken<List<String>>() {}.getType();
            List<String> hoList = gson.fromJson(vote.getJsonArrayTargetHo(), collectionType);
            vote.setHoList(hoList);

        } else if (StringUtils.isNotEmpty(vote.getTargetDong()) && StringUtils.isEmpty(vote.getJsonArrayTargetHo())) {
            // 동별 : targetDong 비교
        }

        return this.voteAdminMapper.getYetVoteList(vote);
    }



    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#selectVoterList(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional(readOnly = true)
    public List<VoterVo> selectVoterList(VoteDto voteDto) throws Exception {
        BaseSecuModel secu = new BaseSecuModel();

        // 투표 오프라인 투표자 목록 레코드 수 조회
        int totalRecordCount = this.voteAdminMapper.selectVoterListCount(voteDto);
        voteDto.getPagingHelper().setTotalRecordCount(totalRecordCount);

        List<Sort> sortList = new ArrayList<Sort>();
        Sort sort = new Sort();
        sort.setColumn("vote_date");
        sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
        sortList.add(sort);
        voteDto.getPagingHelper().setSortList(sortList);

        logger.debug(">>> totalRecordCount : " + totalRecordCount);

        List<VoterVo> voterList = this.voteAdminMapper.selectVoterList(voteDto);
        for (VoterVo voter : voterList) {
            if (!"tablet".equals(voter.getRegisterType())) {
                // 이름, 휴대폰정보 복호화 (voter_name, phone)
                voter.setVoterName(secu.descString(voter.getVoterName()));
                voter.setPhone(secu.descString(voter.getPhone()));
            }
        }

        return voterList;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#selectOfflineVoterList(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional(readOnly = true)
    public List<VoterOfflineVo> selectOfflineVoterList(VoteDto voteDto) throws Exception {

        // 투표 오프라인 투표자 목록 레코드 수 조회
        int totalRecordCount = this.voteAdminMapper.selectOfflineVoterListCount(voteDto);
        voteDto.getPagingHelper().setTotalRecordCount(totalRecordCount);

        List<Sort> sortList = new ArrayList<Sort>();
        Sort sort = new Sort();
        sort.setColumn("reg_date");
        sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
        sortList.add(sort);
        voteDto.getPagingHelper().setSortList(sortList);

        logger.debug(">>> totalRecordCount : " + totalRecordCount);
        return this.voteAdminMapper.selectOfflineVoterList(voteDto);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#deleteVote(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional
    public Long deleteVote(VoteDto voteDto) throws Exception {

        // 투표 삭제처리 : use_yn = 'N'
        return this.voteAdminMapper.deleteVote(voteDto);
    }


    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#updateVote(com.jaha.web.emaul.v2.model.vote.VoteVo)
     */
    @Override
    @Transactional
    public Long updateVote(VoteVo voteVo) throws Exception {
        return this.voteAdminMapper.updateVote(voteVo);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#updateVoteResult(com.jaha.web.emaul.v2.model.vote.VoteVo)
     */
    @Override
    @Transactional
    public Long updateVoteResult(VoteVo voteVo) throws Exception {
        return this.voteAdminMapper.updateVoteResult(voteVo);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#updateVotePushSendDate(com.jaha.web.emaul.v2.model.vote.VoteVo)
     */
    @Override
    @Transactional
    public Long updateVotePushSendDate(VoteVo voteVo) throws Exception {
        return this.voteAdminMapper.updateVotePushSendDate(voteVo);
    }



    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#updateVoteDate(com.jaha.web.emaul.v2.model.vote.VoteVo)
     */
    @Override
    @Transactional
    public Long updateVoteDate(VoteVo voteVo) throws Exception {

        VoteVo updateVote = new VoteVo(); // 정보 수정용 (날자와 상태값을 분리한다. 날자는 전체, 상태갑은 해당 아이디)
        VoteDto voteDto = new VoteDto();
        voteDto.setId(voteVo.getId());
        VoteVo vote = this.getVote(voteDto);

        // 보안투표의 경우 보안키 정보도 업데이트
        if ("Y".equals(vote.getEnableSecurity())) {

            VoteKeyVo voteKeyVo = new VoteKeyVo();
            voteKeyVo.setVkId(vote.getVkId());
            voteKeyVo.setStartDt(voteVo.getStartDate());
            voteKeyVo.setEndDt(voteVo.getEndDate());
            this.voteAdminMapper.updateVoteKey(voteKeyVo);

            // 해당 보안키를 사용하는 모든 투표의 일자 변경
            updateVote.setVkId(vote.getVkId());
            updateVote.setStartDate(voteVo.getStartDate());
            updateVote.setEndDate(voteVo.getEndDate());
            this.voteAdminMapper.updateVoteInfo(updateVote); // 날자 전체 수정

            // 상태정보 업데이트 (해당 아이디)
            return this.voteAdminMapper.updateVoteInfo(voteVo);

        } else {
            return this.voteAdminMapper.updateVoteInfo(voteVo);
        }



    }


    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#getVoteKey(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional(readOnly = true)
    public VoteKeyVo getVoteKey(VoteDto voteDto) throws Exception {
        return this.voteAdminMapper.getVoteKey(voteDto);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#getoteKeyList(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional(readOnly = true)
    public List<VoteKeyVo> getVoteKeyList(VoteDto voteDto) throws Exception {
        return this.voteAdminMapper.getVoteKeyList(voteDto);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#selectVoteKeyList(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional(readOnly = true)
    public List<VoteKeyVo> selectVoteKeyList(VoteDto voteDto) throws Exception {


        // 개표 목록 레코드 수 조회
        int totalRecordCount = this.voteAdminMapper.selectVoteKeyListCount(voteDto);

        logger.debug(">>> totalRecordCount : " + totalRecordCount);
        voteDto.getPagingHelper().setTotalRecordCount(totalRecordCount);

        List<Sort> sortList = new ArrayList<Sort>();
        Sort sort = new Sort();
        sort.setColumn("reg_dt");
        sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
        sortList.add(sort);
        voteDto.getPagingHelper().setSortList(sortList);


        return this.voteAdminMapper.selectVoteKeyList(voteDto);
    }


    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#setVote(boolean, org.springframework.web.multipart.MultipartHttpServletRequest, com.jaha.web.emaul.v2.model.vote.VoteVo)
     */
    @Override
    @Transactional
    public VoteVo setVote(boolean isNew, MultipartHttpServletRequest request, VoteVo voteVo) throws Exception {

        User user = userService.getUser(SessionAttrs.getUserId(request.getSession()));

        logger.debug("\n\n\n\n>>> ----------------- vote " + (isNew ? "등록" : "수정") + "---------------------");
        logger.debug(">>> id : " + voteVo.getId());
        logger.debug(">>> title : " + voteVo.getTitle());
        logger.debug(">>> description : " + voteVo.getDescription());
        logger.debug(">>> enableSecurity : " + voteVo.getEnableSecurity());
        logger.debug(">>> vkId : " + voteVo.getVkId());
        logger.debug(">>> securityLevel : " + voteVo.getSecurityLevel());
        logger.debug(">>> startDay : " + voteVo.getStartDay());
        logger.debug(">>> startHour : " + voteVo.getStartHour());
        logger.debug(">>> startMin : " + voteVo.getStartMin());
        logger.debug(">>> endDay : " + voteVo.getEndDay());
        logger.debug(">>> endHour : " + voteVo.getEndHour());
        logger.debug(">>> endMin : " + voteVo.getEndMin());
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotEmpty(voteVo.getStartDay())) {
            String sDate = voteVo.getStartDay() + " " + voteVo.getStartHour() + ":" + voteVo.getStartMin() + ":00";
            voteVo.setStartDate(transFormat.parse(sDate));
        }
        if (StringUtils.isNotEmpty(voteVo.getEndDay())) {
            String eDate = voteVo.getEndDay() + " " + voteVo.getEndHour() + ":" + voteVo.getEndMin() + ":00";
            voteVo.setEndDate(transFormat.parse(eDate));
        }
        logger.debug(">>> startDate : " + voteVo.getStartDate()); // startDate : startDay + startHour + startMin
        logger.debug(">>> endDate : " + voteVo.getEndDate());
        logger.debug(">>> userType : " + voteVo.getUserType());
        logger.debug(">>> fileCheckYn : " + voteVo.getFileCheckYn());
        logger.debug(">>> question : " + voteVo.getQuestion());
        logger.debug(">>> notiType : " + voteVo.getNotiType());
        logger.debug(">>> notiMsg : " + voteVo.getNotiMsg());
        // typeId를 기준으로 번호표시여부를 numberEnabled에 세팅
        logger.debug(">>> typeId : " + voteVo.getTypeId());
        logger.debug(">>> getNumberEnabled1 : " + voteVo.getNumberEnabled1());
        logger.debug(">>> getNumberEnabled2 : " + voteVo.getNumberEnabled2());
        if ("1".equals(voteVo.getTypeId().toString()) && voteVo.getNumberEnabled1()) { // 기본형
            voteVo.setNumberEnabled(true);
        }
        if ("2".equals(voteVo.getTypeId().toString()) && voteVo.getNumberEnabled2()) { // 복수 후보형
            voteVo.setNumberEnabled(true);
        }
        logger.debug(">>> getNumberEnabled : " + voteVo.getNumberEnabled());
        logger.debug(">>> file1 : " + voteVo.getFile1());
        logger.debug(">>> file2 : " + voteVo.getFile2());
        logger.debug(">>> decYn : " + voteVo.getDecYn());
        logger.debug(">>> useYn : " + voteVo.getUseYn());
        if (isNew) {
            voteVo.setUserId(user.id);
            voteVo.setUserName(user.getFullName());
            voteVo.setRegDate(new Date());
            logger.debug(">>> userId : " + user.id);
            logger.debug(">>> userFullName : " + user.getFullName());
        } else {
            voteVo.setModId(user.id);
            voteVo.setModDate(new Date());
        }
        logger.debug(">>> notiType : " + voteVo.getNotiType());
        if (!"0".equals(voteVo.getNotiType())) {
            voteVo.setPushSendDate(new Date());
        } else {
            voteVo.setPushSendDate(null);
        }
        logger.debug(">>> pushSendDate : " + voteVo.getPushSendDate());

        logger.debug(">>> notiMsg : " + voteVo.getNotiMsg());

        // Field[] userTypeFields = UserType.class.getDeclaredFields();
        List<String> voteUserTypes = Lists.newArrayList();
        voteUserTypes.add(voteVo.getUserType());
        voteVo.setJsonArrayTargetUserTypes(new Gson().toJson(voteUserTypes));
        logger.debug(">>> userType : " + voteVo.getUserType());
        logger.debug(">>> jsonArrayTargetUserType : " + voteVo.getJsonArrayTargetUserTypes());
        logger.debug(">>> jsonArrayTargetHo : " + voteVo.getJsonArrayTargetHo());
        logger.debug(">>> jsonArrayTargetGroup : " + voteVo.getJsonArrayTargetGroup());
        logger.debug("=======================\n\n\n\n\n\n\n\n\n\n\n");

        if (isNew) {
            voteVo.setStatus("ready");
            voteVo.setVisible(true);
            voteVo.setRangeSido("");
            voteVo.setRangeSido("");
            voteVo.setRangeSigungu("");
            voteVo.setTargetApt(user.house.apt.id);
            voteVo.setUseYn("Y");
            voteVo.setVoteResultAvailable(false);
        }
        // TODO : parameter control
        voteVo.setRangeAll(false);
        voteVo.setMultipleChoice(false);

        // NOT NULL column
        logger.debug(">>>---------- NOT NULL ---------");
        logger.debug(">>> rangeAll : " + voteVo.getRangeAll());
        logger.debug(">>> rangeSido : " + voteVo.getRangeSido());
        logger.debug(">>> rangeSigungu : " + voteVo.getRangeSigungu());
        logger.debug(">>> regDate : " + voteVo.getRegDate());
        logger.debug(">>> status : " + voteVo.getStatus());
        logger.debug(">>> visible : " + voteVo.getVisible());
        // logger.debug(">>> typeId : " + voteVo.getTypeId());
        logger.debug(">>> houseLimited : " + voteVo.getHouseLimited());
        logger.debug(">>> voteResultAvailable : " + voteVo.getVoteResultAvailable());
        logger.debug(">>> votersCount : " + voteVo.getVotersCount());
        logger.debug(">>> offlineAvaliable : " + voteVo.getOfflineAvailable());
        logger.debug(">>> securityCheckState : " + voteVo.getSecurityCheckState());
        logger.debug(">>> securityNoticeState : " + voteVo.getSecurityNoticeState());

        if (isNew) {
            this.voteAdminMapper.insertVote(voteVo);
        } else {
            this.voteAdminMapper.updateVote(voteVo);
        }
        // ############# vote ################



        // ############# vote_item ################
        List<VoteItemVo> voteItemList = new ArrayList<VoteItemVo>();

        if (1 == voteVo.getTypeId()) {
            // 기본형 투표항목
            if (voteVo.getTitle1() != null) {

                logger.debug(">>> 기본형 투표항목 : " + voteVo.getTitle1().length);
                for (String title1 : voteVo.getTitle1()) {

                    VoteItemVo voteItemVo = new VoteItemVo();
                    voteItemVo.setParentId(voteVo.getId());
                    voteItemVo.setTitle(title1);
                    voteItemVo.setIsSubjective(false);
                    voteItemVo.setImageCount(0);
                    voteItemVo.setCommitment("");
                    voteItemVo.setProfile("");

                    voteItemList.add(voteItemVo);
                }
            }
        } else if (5 == voteVo.getTypeId()) {
            // 단일 후보형 투표항목
            if (voteVo.getTitle5() != null) {

                logger.debug(">>> 단일후보형 투표항목 : " + voteVo.getTitle5().length);
                logger.debug(">>> " + voteVo.getCommitment5());
                for (int i = 0; i < voteVo.getTitle5().length; i++) {

                    VoteItemVo voteItemVo = new VoteItemVo();
                    voteItemVo.setParentId(voteVo.getId());
                    voteItemVo.setTitle(voteVo.getTitle5()[i]);
                    voteItemVo.setIsSubjective(false);
                    voteItemVo.setImageCount(1);
                    voteItemVo.setCommitment(voteVo.getCommitment5() == null ? "" : voteVo.getCommitment5()[i] == null ? "" : voteVo.getCommitment5()[i]);
                    voteItemVo.setProfile(voteVo.getProfile5() == null ? "" : voteVo.getProfile5()[i] == null ? "" : voteVo.getProfile5()[i]);

                    voteItemList.add(voteItemVo);
                }

                VoteItemVo voteItemVo = new VoteItemVo();
                voteItemVo.setParentId(voteVo.getId());
                voteItemVo.setTitle("반대");
                voteItemVo.setIsSubjective(false);
                voteItemVo.setCommitment(null);
                voteItemVo.setImageCount(0);
                voteItemList.add(voteItemVo);

            }
        } else if (2 == voteVo.getTypeId()) {
            // 복수 후보형 투표항목
            if (voteVo.getTitle2() != null) {

                logger.debug(">>> 복수후보형 투표항목 : " + voteVo.getTitle2().length);
                for (int i = 0; i < voteVo.getTitle2().length; i++) {

                    VoteItemVo voteItemVo = new VoteItemVo();
                    voteItemVo.setParentId(voteVo.getId());
                    voteItemVo.setTitle(voteVo.getTitle2()[i]);
                    voteItemVo.setIsSubjective(false);
                    voteItemVo.setImageCount(1);
                    voteItemVo.setCommitment(voteVo.getCommitment2()[i] == null ? "" : voteVo.getCommitment2()[i]);
                    voteItemVo.setProfile(voteVo.getProfile2()[i] == null ? "" : voteVo.getProfile2()[i]);

                    voteItemList.add(voteItemVo);
                }
            }
        } else if (3 == voteVo.getTypeId()) {
            // 찬반형
            VoteItemVo voteItemVo = new VoteItemVo();
            voteItemVo.setParentId(voteVo.getId());
            voteItemVo.setTitle("찬성");
            voteItemVo.setIsSubjective(false);
            voteItemVo.setCommitment(null);
            voteItemVo.setImageCount(0);
            voteItemList.add(voteItemVo);

            voteItemVo = new VoteItemVo();
            voteItemVo.setParentId(voteVo.getId());
            voteItemVo.setTitle("반대");
            voteItemVo.setIsSubjective(false);
            voteItemVo.setCommitment(null);
            voteItemVo.setImageCount(0);
            voteItemList.add(voteItemVo);
        } else if (6 == voteVo.getTypeId()) {
            // 동의형
            VoteItemVo voteItemVo = new VoteItemVo();
            voteItemVo.setParentId(voteVo.getId());
            voteItemVo.setTitle("서명");
            voteItemVo.setIsSubjective(false);
            voteItemVo.setCommitment(null);
            voteItemVo.setImageCount(0);
            voteItemList.add(voteItemVo);
        }


        if (!isNew) {
            // delete / insert (수정시)
            this.voteAdminMapper.deleteVoteItem(voteVo);
        }
        // 등록시 : INSERT, 수정시 : 새로 INSERT
        if (!voteItemList.isEmpty()) {
            this.voteAdminMapper.insertVoteItemList(voteItemList);
        }
        // ############# vote_item ################



        // ############# vote_key ################
        // 보안투표 보안키 정보 업데이트 (해당 키를 사용하는 투표의 일시 일괄 수정)
        logger.debug(">>> enableSecurity : " + voteVo.getEnableSecurity());
        logger.debug(">>> vkId : " + voteVo.getVkId());
        if ("Y".equalsIgnoreCase(voteVo.getEnableSecurity()) && voteVo.getVkId() != null && voteVo.getStartDate() != null && voteVo.getEndDate() != null) {
            // vote_key.startDt = voteVo.startDate / vote_key.endDt = voteVo.endDate
            this.voteAdminMapper.updateVoteDateByVoteKey(voteVo);

            VoteKeyVo voteKeyVo = new VoteKeyVo();
            voteKeyVo.setStartDt(voteVo.getStartDate());
            voteKeyVo.setEndDt(voteVo.getEndDate());
            voteKeyVo.setVkId(voteVo.getVkId());
            this.voteAdminMapper.updateVoteKey(voteKeyVo);
        }
        // ############# vote_key ################



        boolean isFile = false;
        // ############# vote.file[1][2] ################
        List<MultipartFile> docFiles = request.getFiles("voteDocFile");
        if (!docFiles.isEmpty()) {

            // 파일 저장 규칙은 AS-IS를 유지해야 한다. (기존 데이터 호환)
            long voteParentNum = voteVo.getId() / 1000l;
            File docDir = new File(String.format(rootFilePath + File.separator + "vote" + File.separator + "file" + File.separator + "%s" + File.separator + "%s", voteParentNum, voteVo.getId()));
            logger.debug(">>> docDir : " + docDir);
            if (!docDir.exists()) {
                docDir.mkdirs();
            }

            VoteVo fileVoteVo = new VoteVo();
            fileVoteVo.setId(voteVo.getId());

            for (int i = 0; i < docFiles.size(); i++) {

                String originalFileName = docFiles.get(i).getOriginalFilename();
                if (StringUtils.isNotEmpty(originalFileName)) {
                    try {
                        isFile = true;
                        File dest = new File(docDir, originalFileName);
                        dest.createNewFile();
                        docFiles.get(i).transferTo(dest);

                        if (i == 0) {
                            fileVoteVo.setFile1("/api/vote/file/" + voteVo.getId() + "/" + originalFileName);
                        } else {
                            fileVoteVo.setFile2("/api/vote/file/" + voteVo.getId() + "/" + originalFileName);
                        }
                        logger.debug(">>> " + "/api/vote/file/" + voteVo.getId() + "/" + originalFileName);;
                    } catch (IOException e) {
                        logger.error("\n\n투표 첨부파일 등록중 오류가 발생 : ", e);
                    }
                }
            }
            logger.debug(">>> vote.file : " + isFile);
            if (isFile) {

                fileVoteVo.setModId(user.id);
                fileVoteVo.setModDate(new Date());
                logger.debug(">>> voteVo.file1 : " + fileVoteVo.getFile1());
                logger.debug(">>> voteVo.file2 : " + fileVoteVo.getFile2());
                this.voteAdminMapper.updateVoteFile(fileVoteVo);
            }
        }
        // ############# vote.file[1][2] ################


        isFile = false;
        // ############# vote.photo[1]~[5] ################
        // 투표 사진첨부 파일 [5개]
        List<MultipartFile> votePhotos = request.getFiles("votePhoto");
        if (!votePhotos.isEmpty()) {

            File photoDir = new File(rootFilePath + File.separator + "vote" + File.separator + "vote-image" + File.separator + voteVo.getId());
            logger.debug(">>> photoDir : " + photoDir);
            if (!photoDir.exists()) {
                photoDir.mkdirs();
            }

            VoteVo photoVoteVo = new VoteVo();
            photoVoteVo.setId(voteVo.getId());

            /*
             * if (photoDir.isDirectory()) { for (File delFile : photoDir.listFiles()) { delFile.delete(); } }
             */

            // for (MultipartFile voteImage : voteImages) {
            int photoImageCount = 0;
            for (int i = 0; i < votePhotos.size(); i++) {

                MultipartFile voteImage = votePhotos.get(i);
                String originalFileName = voteImage.getOriginalFilename();
                if (StringUtils.isNotEmpty(originalFileName)) {
                    try {
                        isFile = true;
                        File dest = new File(photoDir, String.format("%s.jpg", (photoImageCount)));
                        dest.createNewFile();
                        voteImage.transferTo(dest);
                        Thumbnails.create(dest);

                        if (photoImageCount == 0) {
                            photoVoteVo.setPhoto1(("/api/vote/vote-image/" + voteVo.getId() + "/" + (photoImageCount)));
                        } else if (photoImageCount == 1) {
                            photoVoteVo.setPhoto2(("/api/vote/vote-image/" + voteVo.getId() + "/" + (photoImageCount)));
                        } else if (photoImageCount == 2) {
                            photoVoteVo.setPhoto3(("/api/vote/vote-image/" + voteVo.getId() + "/" + (photoImageCount)));
                        } else if (photoImageCount == 3) {
                            photoVoteVo.setPhoto4(("/api/vote/vote-image/" + voteVo.getId() + "/" + (photoImageCount)));
                        } else if (photoImageCount == 4) {
                            photoVoteVo.setPhoto5(("/api/vote/vote-image/" + voteVo.getId() + "/" + (photoImageCount)));
                        }
                        logger.debug(">>> " + "/api/vote/vote-image/" + voteVo.getId() + "/" + (i));;

                        photoImageCount++;

                    } catch (IOException e) {
                        logger.error("\n\n투표 이미지파일 등록중 오류가 발생 : ", e);
                    }
                }
            }

            logger.debug(">>> vote.photo : " + isFile);
            if (isFile) {
                logger.debug(">>> voteVo.photo1 : " + photoVoteVo.getPhoto1());
                logger.debug(">>> voteVo.photo2 : " + photoVoteVo.getPhoto2());
                logger.debug(">>> voteVo.photo3 : " + photoVoteVo.getPhoto3());
                logger.debug(">>> voteVo.photo4 : " + photoVoteVo.getPhoto4());
                logger.debug(">>> voteVo.photo5 : " + photoVoteVo.getPhoto5());

                photoVoteVo.setModId(user.id);
                photoVoteVo.setModDate(new Date());
                photoVoteVo.setImageCount(photoImageCount);
                logger.debug(">>> vote image count : " + photoImageCount);
                this.voteAdminMapper.updateVoteImageFile(photoVoteVo);
            }
        }
        // ############# vote.photo[1]~[5] ################



        isFile = false;
        // ############# vote_item.photo ################
        /**
         * TODO : vote_item 에 데이터 저장 <br />
         * 2. 복수후보형 : candidate <br />
         * 5 : 단일후보형 :candidate_single <br />
         */
        if ("2".equalsIgnoreCase(voteVo.getTypeId().toString()) || "5".equalsIgnoreCase(voteVo.getTypeId().toString())) {
            // File dir = new File("/nas/EMaul/vote/vote-item-image/" + voteVo.getId());
            File itemDir = new File(rootFilePath + File.separator + "vote" + File.separator + "vote-item-image" + File.separator + voteVo.getId());
            logger.debug(">>> itemDir : " + itemDir);
            if (!itemDir.exists()) {
                itemDir.mkdirs();
            }

            List<MultipartFile> itemPhotos = request.getFiles("voteItemPhoto" + voteVo.getTypeId());
            for (int i = 0; i < itemPhotos.size(); i++) {

                MultipartFile itemPhoto = itemPhotos.get(i);
                String originalFileName = itemPhoto.getOriginalFilename();
                if (StringUtils.isNotEmpty(originalFileName)) {
                    isFile = true;
                    File dest = new File(itemDir, String.format("%s.jpg", (i)));
                    itemPhoto.transferTo(dest);
                }
            }
        }
        // ############# vote_item.photo ################

        logger.debug("\n\n");
        logger.debug("=====================");
        logger.debug(">>> vote.id : " + voteVo.getId());
        logger.debug("=====================\n\n");
        logger.debug(">>> ----------------- vote " + (isNew ? "등록" : "수정") + "---------------------\n\n\n\n");

        return voteVo;
    }


    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#insertVoteOfflineResult(com.jaha.web.emaul.v2.model.vote.VoteOfflineResultVo)
     */
    @Override
    @Transactional
    public Long insertVoteOfflineResult(VoteOfflineResultVo voteOfflineResultVo) throws Exception {
        return this.voteAdminMapper.insertVoteOfflineResult(voteOfflineResultVo);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#deleteVoteOfflineResult(java.lang.Long)
     */
    @Override
    @Transactional
    public Long deleteVoteOfflineResult(Long voteId) throws Exception {
        return this.voteAdminMapper.deleteVoteOfflineResult(voteId);
    }


    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#getVoteOfflineResult(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public VoteOfflineResultVo getVoteOfflineResult(Long voteId) throws Exception {
        return this.voteAdminMapper.getVoteOfflineResult(voteId);
    }


    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#openVoteKey(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional
    public Map<String, Object> openVoteKey(VoteDto voteDto) throws Exception {

        VoteCrypto voteCrypto = new VoteCrypto();

        VoteKeyVo voteKeyVo = this.voteAdminMapper.getVoteKey(voteDto);
        List<VoteVo> voteList = this.voteAdminMapper.selectVoteList(voteDto);

        int decCnt = 0;
        for (VoteVo vote : voteList) {

            if ("N".equals(vote.getDecYn()) || StringUtils.isEmpty(vote.getDecYn())) {

                voteDto.setId(vote.getId()); // vote_id
                List<VoterSecurityVo> securityVoterList = this.voteAdminMapper.selectVoterSecurityList(voteDto);

                for (VoterSecurityVo voterSecurity : securityVoterList) {

                    try {
                        VoteCrypto.Item item = voteCrypto.decrypt(voterSecurity.getItemIdEnc(), voteKeyVo.getKeyGrantDec());

                        logger.debug(">>> enc item : " + item.toString());
                        voterSecurity.setItemId(item.voteItemId);
                        this.voteAdminMapper.updateVoterSecurityItem(voterSecurity);

                    } catch (Exception e) {
                        logger.error(">>> 보안투표 개표 오류 : " + e.toString());
                    }
                }

                VoteVo voteVo = new VoteVo();
                voteVo.setId(vote.getId());
                voteVo.setDecYn("Y");
                voteAdminMapper.updateVoteDecYn(voteVo);
                decCnt++;
            }
        }

        VoteKeyVo inVoteKeyVo = new VoteKeyVo();
        inVoteKeyVo.setVkId(voteKeyVo.getVkId());
        inVoteKeyVo.setUptDt(new Date());
        this.voteAdminMapper.updateVoteKey(inVoteKeyVo);

        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("decCnt", decCnt);

        return result;
    }


    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#getAptDong(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getAptDong(Long aptId) throws Exception {

        return this.voteAdminMapper.getAptDong(aptId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#getAptHo(java.lang.Long, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getAptHo(Long aptId, String dong) throws Exception {

        VoteDto voteDto = new VoteDto();
        voteDto.setAptId(aptId);
        voteDto.setDong(dong);
        return this.voteAdminMapper.getAptHo(voteDto);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#getAptHoRow(java.lang.Long, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public String getAptHoRow(Long aptId, String dong) throws Exception {

        VoteDto voteDto = new VoteDto();
        voteDto.setAptId(aptId);
        voteDto.setDong(dong);
        return this.voteAdminMapper.getAptHoRow(voteDto);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#getAptHoColumn(java.lang.Long, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public String getAptHoColumn(Long aptId, String dong) throws Exception {

        VoteDto voteDto = new VoteDto();
        voteDto.setAptId(aptId);
        voteDto.setDong(dong);
        return this.voteAdminMapper.getAptHoColumn(voteDto);
    }



    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#selectVoteGroupList(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional(readOnly = true)
    public List<VoteGroupVo> selectVoteGroupList(VoteDto voteDto) throws Exception {

        logger.debug(">>> voteDto : " + voteDto.getPagingHelper());

        // 비동기 조회에서는 pagingHelper가 없다.
        if (voteDto.getPagingHelper().getSearch() == null) {
            PagingHelper p = new PagingHelper();
            Search s = new Search();
            p.setSearch(s);
            voteDto.setPagingHelper(p);
        }

        // 투표 선거구 목록 레코드 수 조회
        int totalRecordCount = 0;
        totalRecordCount = this.voteAdminMapper.selectVoteGroupListCount(voteDto);
        voteDto.getPagingHelper().setTotalRecordCount(totalRecordCount);

        List<Sort> sortList = new ArrayList<Sort>();
        Sort sort = new Sort();
        sort.setColumn("reg_date");
        sort.setAscOrDesc(BoardConstants.SortType.DESC.getValue());
        sortList.add(sort);
        voteDto.getPagingHelper().setSortList(sortList);

        logger.debug(">>> totalRecordCount : " + totalRecordCount);
        logger.debug(">>> sort : " + sort.getColumn());
        logger.debug(">>> sortList : " + sortList.size());

        // 투표 목록 조회
        return this.voteAdminMapper.selectVoteGroupList(voteDto);
    }


    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#insertVoteGroup(com.jaha.web.emaul.v2.model.vote.VoteGroupVo)
     */
    @Override
    @Transactional
    public Long insertVoteGroup(VoteGroupVo voteGroupVo) throws Exception {
        return this.voteAdminMapper.insertVoteGroup(voteGroupVo);
    }


    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#getVoteGroup(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional(readOnly = true)
    public VoteGroupVo getVoteGroup(VoteDto voteDto) throws Exception {

        // 투표 상세 조회
        return this.voteAdminMapper.getVoteGroup(voteDto);
    }


    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#deleteVoteGroup(com.jaha.web.emaul.v2.model.vote.VoteDto)
     */
    @Override
    @Transactional
    public Long deleteVoteGroup(VoteDto voteDto) throws Exception {

        // 선거구 삭제처리 : use_yn = 'N'
        return this.voteAdminMapper.deleteVoteGroup(voteDto);
    }


    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#updateVoteGroup(com.jaha.web.emaul.v2.model.vote.VoteGroupVo)
     */
    @Override
    @Transactional
    public Long updateVoteGroup(VoteGroupVo voteGroupVo) throws Exception {
        return this.voteAdminMapper.updateVoteGroup(voteGroupVo);
    }


    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#deleteVoterOffline(com.jaha.web.emaul.v2.model.vote.VoterOfflineVo)
     */
    @Override
    @Transactional
    public Long deleteVoterOffline(VoterOfflineVo voterOfflineVo) throws Exception {
        return this.voteAdminMapper.deleteVoterOffline(voterOfflineVo);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.v2.service.vote.VoteAdminService#insertVoterOfflineList(java.util.List)
     */
    @Override
    @Transactional
    public Long insertVoterOfflineList(List<VoterOfflineVo> list) throws Exception {
        return this.voteAdminMapper.insertVoterOfflineList(list);
    }

}
