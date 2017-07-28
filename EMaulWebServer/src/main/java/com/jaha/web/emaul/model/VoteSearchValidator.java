package com.jaha.web.emaul.model;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class VoteSearchValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return VoteForm.Search.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        VoteForm.Search searchForm = (VoteForm.Search) target;

        Date startDate = searchForm.getStartDate();
        Date endDate = searchForm.getEndDate();

        if(startDate != null && endDate != null) {
            if(startDate.getTime() > endDate.getTime()) {
                Object[] objects = {startDate, endDate};
                errors.reject(StringUtils.EMPTY, objects, "시작일은 종료일보다 늦을 수 없습니다");
            }
        }
    }
}
