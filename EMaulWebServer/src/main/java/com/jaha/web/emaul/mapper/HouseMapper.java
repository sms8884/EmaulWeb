package com.jaha.web.emaul.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.House;

/**
 * Created by shavrani on 16-10-19
 */
@Mapper
public interface HouseMapper {

    List<Map<String, Object>> selectAddressAptList(Map<String, Object> params);

    Apt selectApt(Map<String, Object> params);

    int insertApt(Apt apt);

    int insertHouse(House house);

    House selectHouse(Map<String, Object> params);



}
