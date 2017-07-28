package com.jaha.web.emaul.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.jaha.web.emaul.util.TagUtils;

/**
 * Created by doring on 15. 3. 9..
 */
@Entity
@Table(name = "board_post", indexes = {@Index(name = "idx_date_post", columnList = "regDate"), @Index(name = "idx_range_sido_post", columnList = "rangeSido"),
        @Index(name = "idx_range_sigungu_post", columnList = "rangeSigungu")})
public class BoardPost {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne(cascade = {})
    @JoinColumn(name = "categoryId", referencedColumnName = "id")
    public BoardCategory category;

    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 0")
    public Boolean rangeAll = false;

    @Column(length = 20, nullable = false)
    public String rangeSido;

    @Column(length = 20, nullable = false)
    public String rangeSigungu;

    @Column(length = 3000)
    public String title;

    @Column(columnDefinition = "TEXT NULL DEFAULT NULL")
    public String content;

    @Column(nullable = false)
    public Date regDate;

    @ManyToOne(targetEntity = User.class, cascade = {})
    @JoinColumn(name = "userId", referencedColumnName = "id")
    public User user;

    public Integer imageCount;

    @Column(length = 200)
    public String file1;
    @Column(length = 200)
    public String file2;

    @Column(nullable = false, columnDefinition = "BIGINT(20) NOT NULL DEFAULT 1")
    public Long viewCount = 1l;

    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 0")
    public Boolean blocked = false;

    @Column(nullable = false, columnDefinition = "BIGINT(20) NOT NULL DEFAULT 0")
    public Long commentCount = 0l;

    @Column(length = 10)
    public String newsType; // 뉴스타입

    @Column(length = 10)
    public String newsCategory;// 뉴스카테고리

    @Column(length = 1)
    public String displayYn;// 게시여부

    @ManyToMany
    @JoinTable(name = "board_post_has_tag", joinColumns = @JoinColumn(name = "postId"), inverseJoinColumns = @JoinColumn(name = "tag"))
    public List<BoardTag> tags;

    @Transient
    public Boolean isDeletable = false;

    @Column(nullable = false, columnDefinition = "BIGINT(20) NOT NULL DEFAULT 1")
    public Long countEmpathy = 1l;

    @Transient
    // 작성자명 : 아파트마다 다르게 표시하기 위해서 따로 관리
    public String writerName = null;

    ///////////////////////////////////////////////// 방송 게시판 칼럼 추가 ///////////////////////////////////////////////////
    @Column(length = 10, nullable = true)
    public String voiceGubun;
    @Column(nullable = true, columnDefinition = "SMALLINT(6) NULL DEFAULT NULL")
    public Integer voiceVolume = 0;
    @Column(length = 1, nullable = true)
    public String pushSendYn;
    @Column(nullable = true)
    public Date airSendDate;

    @Column(length = 30)
    public String airReserveDate;

    @Column(length = 1)
    public String airReserveCancelYn;// 방송예약취소여부

    @Column(length = 1)
    public String airReserveYn;// 방송게시판 게시여부

    /** 블라인드 처리 여부 : 2016.11.09 cyt */
    @Column(length = 1)
    public String blindYn;// 블라인드 (숨김)처리 여부 (N : 정상, Y : 블라인드처리)

    ///////////////////////////////////////////////// 방송 게시판 칼럼 추가 ///////////////////////////////////////////////////

    /* 상잔게시판 고정 컬럼 추가 */
    @Column(nullable = false, columnDefinition = "BIT(1) NOT NULL DEFAULT 0")
    public Boolean topFix = false;

    @PostLoad
    public void postPersist() {
        // 게시판 카테고리 모드가 html이고 게시판 title이 비어있는 경우에만 게시판 내용을 변환하여 타이틀로 수정
        if ("html".equals(this.category.contentMode) && StringUtils.isBlank(this.title)) {
            String tempContent = this.content;
            String tempTitle = TagUtils.removeTag(tempContent);

            if (tempTitle.length() > 300) {
                this.title = tempTitle.substring(0, 300);
            } else {
                this.title = tempTitle;
            }
        }
    }

    /** Controller에서 BoardPost(모델) 클래스에 변형된 content를 할당할 경우 바로 DB가 업데이트되는 이슈로 인하여 추가 */
    @Transient
    public String contentOnlyBody;

    public Long modId;

    public Date modDate;

    public void increaseViewCount() {
        viewCount += 1;
    }

    public void delete(Long userId) {
        this.displayYn = "N";
        this.modId = userId;
        this.modDate = new Date();
    }
}
