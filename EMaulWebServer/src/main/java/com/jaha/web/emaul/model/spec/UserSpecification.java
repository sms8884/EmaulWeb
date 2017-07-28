/**
 *
 */
package com.jaha.web.emaul.model.spec;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.jaha.web.emaul.model.BaseSecuModel;
import com.jaha.web.emaul.model.User;

/**
 * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 22.
 * @description
 *
 */
public class UserSpecification {

    public static Specification<User> aptIdEq(final long aptId) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("house").get("apt").get("id"), aptId);
            }
        };
    }

    public static Specification<User> idGreaterThan(final long id) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.greaterThan(root.get("id"), id);
            }
        };
    }

    public static Specification<User> aptNameLike(final String aptName) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.like(root.get("house").get("apt").get("name"), "%" + aptName + "%");
            }
        };
    }

    public static Specification<User> emailLike(final String email) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                final BaseSecuModel bsm = new BaseSecuModel();
                return cb.like(root.get("email"), "%" + bsm.encString(email) + "%");
            }
        };
    }

    public static Specification<User> houseDongEq(final String dong) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("house").get("dong"), dong);
            }
        };
    }

    public static Specification<User> houseHoEq(final String ho) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("house").get("ho"), ho);
            }
        };
    }

    public static Specification<User> fullNameEq(final String fullName) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                final BaseSecuModel bsm = new BaseSecuModel();
                return cb.equal(root.get("fullName"), bsm.encString(fullName));
            }
        };
    }

    public static Specification<User> phoneEq(final String phone) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                final BaseSecuModel bsm = new BaseSecuModel();
                return cb.equal(root.get("phone"), bsm.encString(phone));
            }
        };
    }

    public static Specification<User> jahaAuthNotEq(final Boolean isJaha) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.notEqual(root.get("type").get("jaha"), isJaha);
            }
        };
    }

    public static Specification<User> AuthEq(final String searchAuth) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("type").get(searchAuth), true);
            }
        };
    }

    public static Specification<User> AuthNotEq(final String searchAuth) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.notEqual(root.get("type").get(searchAuth), true);
            }
        };
    }

    public static Specification<User> emailEq(final String email) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                final BaseSecuModel bsm = new BaseSecuModel();
                return cb.equal(root.get("email"), bsm.encString(email));
            }
        };
    }

    public static Specification<User> nicknameLike(final String nickname) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.like(root.get("nickname").get("name"), "%" + nickname + "%");
            }
        };
    }

}
