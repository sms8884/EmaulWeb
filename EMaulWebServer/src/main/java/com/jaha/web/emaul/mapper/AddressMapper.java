package com.jaha.web.emaul.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.model.Address;

/**
 * Created by shavrani on 16-10-18
 */
@Mapper
public interface AddressMapper {

    Address selectVirtualAddress(Map<String, Object> params);

    String createVirtualAddressBuildingNo();

    int insertVirtualAddress(Map<String, Object> params);

}
