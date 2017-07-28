
package com.jaha.web.emaul.model.spec;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.model.Vote;
import com.jaha.web.emaul.model.VoteType;

/**
 * <pre>
 * Class Name : VoteSpecification.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 9. 19.     Administrator      Generation
 * </pre>
 *
 * @author Administrator
 * @since 2016. 9. 19.
 * @version 1.0
 */
public class VoteSpecification {

    public static Specification<Vote> typeIdIn(List<VoteType> voteTypes) {
        return new Specification<Vote>() {
            @Override
            public Predicate toPredicate(Root<Vote> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.in(root.get("type")).value(voteTypes);
            }
        };
    }

    /**
     * 투표 상태 조회 (done, active)
     *
     * @param status
     * @return
     */
    public static Specification<Vote> statusEq(String status) {
        return new Specification<Vote>() {
            @Override
            public Predicate toPredicate(Root<Vote> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("status"), status);
            }
        };
    }

    /**
     * 투표 종류 조회
     *
     * @param typeId
     * @return
     */
    public static Specification<Vote> typeIdEq(long typeId) {
        return new Specification<Vote>() {
            @Override
            public Predicate toPredicate(Root<Vote> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("type").get("id"), typeId);
            }
        };
    }

    /**
     * 투표 등록일 조회
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Specification<Vote> regDate(Date startDate, Date endDate) {
        return new Specification<Vote>() {
            @Override
            public Predicate toPredicate(Root<Vote> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.add(Calendar.DATE, 1);
                return cb.between(root.get("regDate"), startDate, cal.getTime());
            }
        };
    }

    /**
     * 제목 조회
     *
     * @param title
     * @return
     */
    public static Specification<Vote> titleLike(String title) {
        return new Specification<Vote>() {
            @Override
            public Predicate toPredicate(Root<Vote> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.like(root.get("title"), "%" + title + "%");
            }
        };
    }

    /**
     * 아파트이름조회
     *
     * @return
     */
    public static Specification<Vote> targetAptNameLike(List<Apt> targetApt) {
        return new Specification<Vote>() {
            @Override
            public Predicate toPredicate(Root<Vote> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.in(root.get("targetApt")).value(targetApt);
            }
        };
    }
}
