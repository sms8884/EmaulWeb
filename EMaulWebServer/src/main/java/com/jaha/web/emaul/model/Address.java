package com.jaha.web.emaul.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Created by doring on 15. 4. 14..
 */
@Entity
@Table(name = "address", indexes = {@Index(name = "idx_address_search_index", columnList = "시도명,시군구명,법정읍면동명,법정리명,도로명,행정동명,시군구용건물명")})
public class Address {
    @Column(length = 10)
    public String 법정동코드;
    @Column(length = 40)
    public String 시도명;
    @Column(length = 40)
    public String 시군구명;
    @Column(length = 40)
    public String 법정읍면동명;
    @Column(length = 40)
    public String 법정리명;
    @Column(length = 1)
    public String 산여부;
    public Integer 지번본번;
    public Integer 지번부번;
    @Column(length = 12)
    public String 도로명코드;
    @Column(length = 80)
    public String 도로명;
    @Column(length = 1)
    public String 지하여부;
    public Integer 건물본번;
    public Integer 건물부번;
    @Column(length = 40)
    public String 건축물대장_건물명;
    @Column(length = 100)
    public String 상세건물명;

    @Id
    @Column(length = 25)
    public String 건물관리번호;

    @Column(length = 2)
    public String 읍면동일련번호;
    @Column(length = 10)
    public String 행정동코드;
    @Column(length = 20)
    public String 행정동명;
    @Column(length = 6)
    public String 우편번호;
    @Column(length = 3)
    public String 우편일련번호;
    @Column(length = 40)
    public String 다량배달처명;
    @Column(length = 2)
    public String 이동사유코드;
    @Column(length = 8)
    public String 변경일자;
    @Column(length = 25)
    public String 변경전도로명주소;
    @Column(length = 200)
    public String 시군구용건물명;
    @Column(length = 1)
    public String 공동주택여부;
    @Column(length = 5)
    public String 기초구역번호;
    @Column(length = 1)
    public String 상세주소여부;
    @Column(length = 15)
    public String 비고1;
    @Column(length = 15)
    public String 비고2;
}
