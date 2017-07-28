package com.jaha.web.emaul.model.spec;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.jaha.web.emaul.model.ParcelLocker;

/**
 * @author 이현수(lhs@jahasmart.com), 2016. 7. 7.
 * @description
 */
public class ParcelLockerSpecification {

    public static Specification<ParcelLocker> nameLike(final String name) {
        return new Specification<ParcelLocker>() {
            @Override
            public Predicate toPredicate(Root<ParcelLocker> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return StringUtils.hasText(name) ? cb.like(root.get("name"), "%" + name + "%") : null;
            }
        };
    }

    public static Specification<ParcelLocker> uuidEq(final String uuid) {
        return new Specification<ParcelLocker>() {
            @Override
            public Predicate toPredicate(Root<ParcelLocker> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return StringUtils.hasText(uuid) ? cb.equal(root.get("uuid"), uuid) : null;
            }
        };
    }

    public static Specification<ParcelLocker> isDeletedEq(final ParcelLocker.Deleted deleted) {
        return new Specification<ParcelLocker>() {
            @Override
            public Predicate toPredicate(Root<ParcelLocker> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("deleted"), deleted);
            }
        };
    }

    public static Specification<ParcelLocker> aptIdEq(final Long aptId) {
        return new Specification<ParcelLocker>() {
            @Override
            public Predicate toPredicate(Root<ParcelLocker> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("apt").get("id"), aptId);
            }
        };
    }

}
