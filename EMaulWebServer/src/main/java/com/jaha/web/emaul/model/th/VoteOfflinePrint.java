package com.jaha.web.emaul.model.th;

import java.util.List;

/**
 * Created by Administrator on 2015-05-29.
 */
public class VoteOfflinePrint {
    public List<VoteItemOffStatus> offStatuses;
    public List<VoteItemAdditionStatus> additionStatuses;
    public Long offVoterCount;
    public Long additionVoterCount;

    public static class VoteItemOffStatus {
        public String name;
        public Long count;
        public String rate;
    }

    public static class VoteItemAdditionStatus {
        public String name;
        public Long count;
        public String rate;
    }
}
