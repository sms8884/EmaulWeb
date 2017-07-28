package com.jaha.web.emaul.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.UserPrepass;
import com.jaha.web.emaul.model.Vote;
import com.jaha.web.emaul.model.VoteForm;
import com.jaha.web.emaul.model.VoteItem;
import com.jaha.web.emaul.model.VoteKey;
import com.jaha.web.emaul.model.VoteOfflineResult;
import com.jaha.web.emaul.model.VoteType;
import com.jaha.web.emaul.model.Voter;
import com.jaha.web.emaul.model.VoterOffline;
import com.jaha.web.emaul.model.spec.VoteSpecification;
import com.jaha.web.emaul.repo.AptRepository;
import com.jaha.web.emaul.repo.UserPrepassRepository;
import com.jaha.web.emaul.repo.VoteItemRepository;
import com.jaha.web.emaul.repo.VoteKeyRepository;
import com.jaha.web.emaul.repo.VoteOfflineResultRepository;
import com.jaha.web.emaul.repo.VoteRepository;
import com.jaha.web.emaul.repo.VoteTypeRepository;
import com.jaha.web.emaul.repo.VoterOfflineRepository;
import com.jaha.web.emaul.repo.VoterRepository;
import com.jaha.web.emaul.util.ScrollPage;

/**
 * Created by doring on 15. 2. 25..
 */
@Service
public class VoteServiceImpl implements VoteService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private VoteItemRepository voteItemRepository;
    @Autowired
    private VoteTypeRepository voteTypeRepository;
    @Autowired
    private VoterRepository voterRepository;
    @Autowired
    private VoterOfflineRepository voterOfflineRepository;
    @Autowired
    private UserPrepassRepository userPrepassRepository;
    @Autowired
    private VoteOfflineResultRepository voteOfflineResultRepository;
    @Autowired
    private VoteKeyRepository voteKeyRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AptRepository aptRepository;

    @Override
    public ScrollPage<Vote> getVotes(Long targetApt, String targetDong, Long lastVoteId) {
        return getVoteListInner("vote", targetApt, targetDong, lastVoteId);
    }

    @Override
    public ScrollPage<Vote> getPolls(Long targetApt, String targetDong, Long lastPollId) {
        return getVoteListInner("poll", targetApt, targetDong, lastPollId);
    }

    @Override
    public Page<Vote> getVotes(Long targetApt, Pageable pageable) {
        return getVoteListInner("vote", targetApt, pageable);
    }

    @Override
    public Page<Vote> getVotes(VoteForm.Search searchForm, Pageable pageable) {

        List<VoteType> voteTypes = voteTypeRepository.findByMain("vote");
        Specifications<Vote> spec = Specifications.where(VoteSpecification.typeIdIn(voteTypes));

        if (StringUtils.hasText(searchForm.getStatus()) && !"all".equals(searchForm.getStatus()))
            spec = spec.and(VoteSpecification.statusEq(searchForm.getStatus()));

        if (StringUtils.hasText(searchForm.getType()) && !"all".equals(searchForm.getType())) {
            Optional<VoteType> voteType = voteTypes.stream().filter(type -> type.sub.equals(searchForm.getType())).findFirst();

            if (voteType.isPresent())
                spec = spec.and(VoteSpecification.typeIdEq(voteType.get().id));
        }

        if (searchForm.getStartDate() != null && searchForm.getStartDate() != null)
            spec = spec.and(VoteSpecification.regDate(searchForm.getStartDate(), searchForm.getEndDate()));

        if (StringUtils.hasText(searchForm.getTitle()))
            spec = spec.and(VoteSpecification.titleLike("%" + searchForm.getTitle() + "%"));

        if (StringUtils.hasText(searchForm.getTargetAptName())) {
            List<Apt> targetApts = aptRepository.findByNameLikeAndVirtualFalse("%" + searchForm.getTargetAptName() + "%");
            if (targetApts != null && !targetApts.isEmpty())
                spec = spec.and(VoteSpecification.targetAptNameLike(targetApts));
        }

        return voteRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Vote> getVotes(Pageable pageable) {
        return getVoteListInner("vote", pageable);
    }

    @Override
    public Page<Vote> getPolls(Long targetApt, Pageable pageable) {
        return getVoteListInner("poll", targetApt, pageable);
    }

    private ScrollPage<Vote> getVoteListInner(String voteMainType, Long targetApt, String targetDong, Long lastId) {
        if (lastId == null || lastId == 0l) {
            lastId = Long.MAX_VALUE;
        }
        ScrollPage<Vote> ret = new ScrollPage<>();
        List<VoteType> typeList = voteTypeRepository.findByMain(voteMainType);
        List<Long> typeIds = Lists.transform(typeList, input -> input.id);

        List<Vote> temp = voteRepository.findFirst10ByIdLessThanAndTypeIdInAndVisibleIsTrueAndTargetApt(lastId, typeIds, targetApt, new Sort(Sort.Direction.DESC, "regDate"));
        List<Vote> list = temp;
        if (targetDong != null) {
            list = Lists.newArrayList(Collections2.filter(temp, input -> input.targetDong == null || targetDong.equals(input.targetDong)));
        }
        ret.setContent(list);
        final int listSize = list.size();
        if (listSize >= 10) {
            ret.setNextPageToken(String.valueOf(list.get(listSize - 1).id));
        }
        return ret;
    }

    private Page<Vote> getVoteListInner(String voteMainType, Long targetApt, Pageable pageable) {
        List<VoteType> typeList = voteTypeRepository.findByMain(voteMainType);
        List<Long> typeIds = Lists.transform(typeList, input -> input.id);
        Apt apt = aptRepository.findOne(targetApt);

        return voteRepository.findByTypeIdInAndTargetApt(typeIds, apt, pageable);
    }

    private Page<Vote> getVoteListInner(String voteMainType, Pageable pageable) {

        List<VoteType> typeList = voteTypeRepository.findByMain(voteMainType);
        List<Long> typeIds = Lists.transform(typeList, input -> input.id);

        return voteRepository.findByTypeIdIn(typeIds, pageable);
    }

    @Override
    public Vote getVote(Long voteId) {
        return voteRepository.findOne(voteId);
    }

    @Override
    public VoteItem getVoteItem(Long voteItemId) {
        return voteItemRepository.findOne(voteItemId);
    }

    @Override
    public List<VoteItem> getVoteItems(Long voteId) {
        return voteItemRepository.findByParentId(voteId);
    }

    @Override
    public VoteType getVoteType(String main, String sub) {
        return voteTypeRepository.findOneByMainAndSub(main, sub);
    }

    @Override
    public Boolean isAlreadyVoted(Long userId, Long voteId) {
        Voter voter = voterRepository.findOneByUserIdAndVoteId(userId, voteId);
        logger.debug("vote available : " + voter);

        return voter != null;
    }

    @Override
    public Boolean isAlreadyVotedHouse(Long voteId, Long aptId, String dong, String ho) {
        Vote vote = voteRepository.findOne(voteId);
        if (!vote.houseLimited) {
            return false;
        }

        List<Voter> voters = voterRepository.findByVoteIdAndAptIdAndDongAndHo(voteId, aptId, dong, ho);

        VoterOffline existOfflineVoter = voterOfflineRepository.findOneByVoteIdAndAptIdAndDongAndHo(voteId, aptId, dong, ho);

        return (voters != null && !voters.isEmpty()) || existOfflineVoter != null;
    }

    @Override
    public Voter getOnlineVoted(Long voteId, Long aptId, String dong, String ho) {
        List<Voter> voters = voterRepository.findByVoteIdAndAptIdAndDongAndHo(voteId, aptId, dong, ho);
        return voters != null && !voters.isEmpty() ? voters.get(0) : null;
    }

    @Override
    public VoterOffline getOfflineVoted(Long voteId, Long aptId, String dong, String ho) {
        return voterOfflineRepository.findOneByVoteIdAndAptIdAndDongAndHo(voteId, aptId, dong, ho);
    }

    @Override
    @Transactional
    public VoterOffline putOfflineVoter(Long voteId, Long aptId, String dong, String ho, String name) {
        VoterOffline vo = getOfflineVoted(voteId, aptId, dong, ho);
        Voter voter = getOnlineVoted(voteId, aptId, dong, ho);
        if (vo != null || voter != null) {
            return null;
        }
        vo = new VoterOffline();
        vo.aptId = aptId;
        vo.dong = dong;
        vo.ho = ho;
        vo.voteId = voteId;
        vo.fullName = name;
        vo.regDate = new Date();
        return voterOfflineRepository.saveAndFlush(vo);
    }

    @Override
    public List<VoterOffline> getOfflineVoters(Long voteId) {
        return voterOfflineRepository.findByVoteId(voteId);
    }

    @Override
    public VoteOfflineResult getOfflineResult(Long itemId) {
        return voteOfflineResultRepository.findOne(itemId);
    }

    @Override
    public String getVoteStatus(Long voteId) {
        Vote vote = voteRepository.findOne(voteId);
        return vote.status;
    }

    @Override
    public Vote saveAndFlush(Vote vote) {
        return voteRepository.saveAndFlush(vote);
    }

    @Override
    @Transactional
    public void updateVoteFiles(Long voteId, String file1, String file2) {
        voteRepository.updateVoteFiles(voteId, file1, file2);
    }

    @Override
    public List<VoteItem> save(List<VoteItem> items) {
        List<VoteItem> ret = voteItemRepository.save(items);
        voteItemRepository.flush();

        return ret;
    }

    @Override
    public VoteItem saveAndFlush(VoteItem voteItem) {
        return voteItemRepository.saveAndFlush(voteItem);
    }

    @Override
    public Voter save(Voter voter) {
        return voterRepository.saveAndFlush(voter);
    }

    @Override
    public VoteOfflineResult saveAndFlush(VoteOfflineResult result) {
        return voteOfflineResultRepository.saveAndFlush(result);
    }

    @Override
    public List<Voter> getVoters(Long voteId) {
        return voterRepository.findByVoteId(voteId, new Sort(Sort.Direction.DESC, "voteDate"));
    }

    @Override
    public List<Voter> getVotersByItem(Long voteId, Long voteItemId) {
        logger.debug("vote item id : " + voteItemId);

        return voterRepository.findByVoteIdAndVoteItemId(voteId, voteItemId, new Sort(Sort.Direction.DESC, "voteDate"));
    }

    @Override
    public List<UserPrepass> getUserPrepass(Long aptId, String dong, List<String> hoList) {
        return userPrepassRepository.findByAptIdAndDongAndHoIn(aptId, dong, hoList);
    }

    @Override
    public List<UserPrepass> getUserPrepass(Apt apt, String dong, List<String> hoList) {
        return getUserPrepass(apt.id, dong, hoList);
    }

    @Override
    public List<UserPrepass> getUserPrepass(Long aptId, String dong) {
        return userPrepassRepository.findByAptIdAndDong(aptId, dong);
    }

    @Override
    public List<UserPrepass> getUserPrepass(Apt apt, String dong) {
        return getUserPrepass(apt.id, dong);
    }

    @Override
    public List<UserPrepass> getUserPrepass(Long aptId) {
        return userPrepassRepository.findByAptId(aptId);
    }

    @Override
    public List<UserPrepass> getUserPrepass(Apt apt) {
        return getUserPrepass(apt.id);
    }

    @Override
    public void delete(Long id) {
        voteRepository.delete(id);
    }

    @Override
    public void deleteVoteItems(List<VoteItem> items) {
        List<Long> deleteIds = Lists.transform(items, input -> input.id);
        for (Long deleteId : deleteIds) {
            voteItemRepository.delete(deleteId);
        }
    }

    @Override
    public void deleteOfflineResult(Long voteId) {
        voteOfflineResultRepository.delete(voteId);
    }

    // 보안투표 관련 추가사항들 - 강지만 2016-03-04

    @Override
    public Page<VoteKey> getVoteKeys(Long targetApt, Pageable pageable) {
        return voteKeyRepository.findByAptId(targetApt, pageable);
    }

    @Override
    public Page<VoteKey> getVoteKeys(Long targetApt, String keyGrantYn, Pageable pageable) {
        return voteKeyRepository.findByAptIdAndKeyGrantYn(targetApt, keyGrantYn, pageable);
    }


    @Override
    public List<VoteKey> getVoteKeysAvailable(Long targetApt) {
        List<Long> vkIds =
                jdbcTemplate.query("SELECT v.vk_id FROM vote_key v WHERE " + "v.use_yn = 'Y' and v.apt_id=? AND (v.start_dt is null or v.start_dt > now())", new Object[] {targetApt},
                        (rs, rowNum) -> {
                            return rs.getLong("vk_id");
                        });
        return Lists.newArrayList(voteKeyRepository.findByVkIdIn(vkIds));
    }

    @Override
    public VoteKey getVoteKey(Long vkId) {
        return voteKeyRepository.findOne(vkId);
    }
}
