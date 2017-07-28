package com.jaha.web.emaul.model.th;

import java.util.List;

/**
 * Created by doring on 15. 4. 9..
 */
public class VoteResult {
    public List<VoteItemStatus> statuses;
    public List<Voter> voterList;
    public Integer voterCount;

    public static class VoteItemStatus {
        public String name;
        public Integer count;
        public String rate;
    }

    public static class Voter {
        public String name;
        public String signUrl;
        public Long userId;
        public String dong;
        public String ho;
        public String phone;
        public String voteDate;

    }
}
