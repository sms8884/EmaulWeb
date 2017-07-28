package com.jaha.web.emaul.service;

import com.jaha.web.emaul.model.*;
import com.jaha.web.emaul.util.ScrollPage;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Created by doring on 15. 2. 25..
 */
public interface VoteService {
    ScrollPage<Vote> getVotes(Long targetApt, String targetDong, Long lastVoteId);

    ScrollPage<Vote> getPolls(Long targetApt, String targetDong, Long lastPollId);

    Page<Vote> getVotes(Pageable pageable);
    Page<Vote> getVotes(Long targetApt, Pageable pageable);
    Page<Vote> getVotes(VoteForm.Search searchForm, Pageable pageable);

    Page<Vote> getPolls(Long targetApt, Pageable pageable);

    Vote getVote(Long voteId);

    VoteItem getVoteItem(Long voteItemId);

    List<VoteItem> getVoteItems(Long voteId);

    VoteType getVoteType(String main, String sub);

    Boolean isAlreadyVoted(Long userId, Long voteId);
    Boolean isAlreadyVotedHouse(Long voteId, Long aptId, String dong, String ho);
    Voter getOnlineVoted(Long voteId, Long aptId, String dong, String ho);
    VoterOffline getOfflineVoted(Long voteId, Long aptId, String dong, String ho);
    VoterOffline putOfflineVoter(Long voteId, Long aptId, String dong, String ho, String name);
    List<VoterOffline> getOfflineVoters(Long voteId);
    VoteOfflineResult getOfflineResult(Long itemId);

    String getVoteStatus(Long voteId);

    Vote saveAndFlush(Vote vote);
    void updateVoteFiles(Long voteId, String file1, String file2);
    List<VoteItem> save(List<VoteItem> items);
    VoteItem saveAndFlush(VoteItem voteItem);

    Voter save(Voter voter);
    VoteOfflineResult saveAndFlush(VoteOfflineResult result);

    List<Voter> getVoters(Long voteId);

    List<Voter> getVotersByItem(Long voteId, Long voteItemId);

    List<UserPrepass> getUserPrepass(Long aptId, String dong, List<String> hoList);
    List<UserPrepass> getUserPrepass(Apt apt, String dong, List<String> hoList);
    List<UserPrepass> getUserPrepass(Long aptId, String dong);
    List<UserPrepass> getUserPrepass(Apt apt, String dong);
    List<UserPrepass> getUserPrepass(Long aptId);
    List<UserPrepass> getUserPrepass(Apt apt);

    void delete(Long voteId);
    void deleteVoteItems(List<VoteItem> items);
    void deleteOfflineResult(Long voteId);

    //보안투표 관련 추가사항들 - 강지만 2016-03-04
    Page<VoteKey> getVoteKeys(Long targetApt, Pageable pageable);
    Page<VoteKey> getVoteKeys(Long targetApt, String keyGrantYn, Pageable pageable);
    List<VoteKey> getVoteKeysAvailable(Long targetApt);
    VoteKey getVoteKey(Long vkId);


}
