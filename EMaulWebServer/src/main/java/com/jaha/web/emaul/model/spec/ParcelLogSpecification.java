package com.jaha.web.emaul.model.spec;

import com.jaha.web.emaul.model.ParcelLog;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author 이현수(lhs@jahasmart.com), 2016. 7. 11.
 * @description 무인택배함
 */
public class ParcelLogSpecification {

    public static Specification<ParcelLog> lockerIdEq(final Long lockerId) {
        return new Specification<ParcelLog>() {
            @Override
            public Predicate toPredicate(Root<ParcelLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("parcelLocker").get("id"), lockerId);
            }
        };
    }
}
