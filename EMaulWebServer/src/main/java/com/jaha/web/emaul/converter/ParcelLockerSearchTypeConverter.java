package com.jaha.web.emaul.converter;

import com.jaha.web.emaul.model.ParcelLockerForm;
import org.springframework.core.convert.converter.Converter;

public class ParcelLockerSearchTypeConverter implements Converter<String, ParcelLockerForm.SearchType> {

    @Override
    public ParcelLockerForm.SearchType convert(String code) {
        return ParcelLockerForm.SearchType.value(code);
    }
}
