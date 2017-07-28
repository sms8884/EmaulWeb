/**
 *
 */
package com.jaha.web.emaul.model.spec;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.jaha.web.emaul.model.BoardPost;

/**
 * @author 조영태(cyt@jahasmart.com), 2016. 11. 17.
 * @description
 *
 */
public class BoardSpecification {

    public static Specification<BoardPost> categoryIdEq(final long categoryId) {
        return new Specification<BoardPost>() {
            @Override
            public Predicate toPredicate(Root<BoardPost> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("category").get("id"), categoryId);
            }
        };
    }

    public static Specification<BoardPost> displayYn(final String displayYn) {
        return new Specification<BoardPost>() {
            @Override
            public Predicate toPredicate(Root<BoardPost> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("displayYn"), displayYn);
            }
        };
    }

    public static Specification<BoardPost> nicknameLike(final String nickname) {
        return new Specification<BoardPost>() {
            @Override
            public Predicate toPredicate(Root<BoardPost> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.like(root.get("user").get("nickname").get("name"), "%" + nickname + "%");
            }
        };
    }

    public static Specification<BoardPost> contentLike(final String content) {
        return new Specification<BoardPost>() {
            @Override
            public Predicate toPredicate(Root<BoardPost> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.like(root.get("content"), "%" + content + "%");
            }
        };
    }


    public static Specification<BoardPost> titleLike(final String title) {
        return new Specification<BoardPost>() {
            @Override
            public Predicate toPredicate(Root<BoardPost> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.like(root.get("title"), "%" + title + "%");
            }
        };
    }


}
