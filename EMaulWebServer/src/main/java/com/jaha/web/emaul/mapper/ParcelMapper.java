package com.jaha.web.emaul.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.model.ParcelLog;
import com.jaha.web.emaul.model.ParcelSmsPolicy;

@Mapper
public interface ParcelMapper {

    /**
     * 무인택배 SMS발송내역
     * 
     * @Param
     * @return
     */
    public List<ParcelLog> selectParcelSmsList(Map<String, Object> params);

    public int selectParcelSmsListCount(Map<String, Object> params);

    public ParcelSmsPolicy selectParcelSmsPolicy(Map<String, Object> params);

    public void saveParcelSmsPolicy(Map<String, Object> params);

}
