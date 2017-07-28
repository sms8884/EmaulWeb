package com.jaha.web.emaul.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by imac1 on 2016. 3. 7..
 */
@Entity
@Table(name = "voter_security")
public class VoterSecurity {
    @Id
    @Column
    public String viId;

    @Column
    public Long voteId;

    @Column
    public String itemIdEnc = "";

    @Column
    public Long itemId;

    @Column
    public String itemIdChkFname = "";

    @Column
    public String regDt;

}
