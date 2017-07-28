package com.jaha.web.emaul.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SampleMapper {

    public List<Map<String, Object>> selectSample();
    
    public int insertSample();
    
}
