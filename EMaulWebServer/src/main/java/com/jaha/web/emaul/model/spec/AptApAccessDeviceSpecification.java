package com.jaha.web.emaul.model.spec;

import org.springframework.data.jpa.domain.Specification;

import com.jaha.web.emaul.model.AptApAccessDevice;
import com.jaha.web.emaul.model.BaseSecuModel;
import com.jaha.web.emaul.util.StringUtil;

/**
 * Created by shavrani on 16-08-09
 */
public class AptApAccessDeviceSpecification {

    /**
     * like문 공통 method
     */
    private static String getLike(String str) {
        if (StringUtil.isNull(str)) {
            return "%";
        } else {
            return "%" + str.toLowerCase() + "%";
        }
    }

    public static Specification<AptApAccessDevice> idEq(final Long id) {
        return (root, query, cb) -> {
            return cb.equal(root.get("id"), id);
        };
    }

    public static Specification<AptApAccessDevice> aptIdEq(final Long aptId) {
        return (root, query, cb) -> {
            return root.get("user") == null ? null : cb.equal(root.get("user").get("house").get("apt").get("id"), aptId);
        };
    }

    public static Specification<AptApAccessDevice> typeEq(final String type) {
        return (root, query, cb) -> {
            return cb.equal(root.get("type"), type);
        };
    }

    public static Specification<AptApAccessDevice> accessKeyEq(final String accessKey) {
        return (root, query, cb) -> {
            return cb.equal(root.get("accessKey"), accessKey);
        };
    }

    public static Specification<AptApAccessDevice> userNmLike(final String userNm) {
        return (root, query, cb) -> {
            return root.get("user") == null ? null : cb.equal(cb.lower(root.get("user").get("fullName")), new BaseSecuModel().encString(userNm));
        };
    }

    public static Specification<AptApAccessDevice> deactive(final String deactive) {
        return (root, query, cb) -> {
            if ("Y".equals(deactive)) {
                return cb.isNotNull(root.get("deactiveDate"));
            } else {
                return cb.isNull(root.get("deactiveDate"));
            }
        };
    }
}
