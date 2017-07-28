package com.jaha.web.emaul.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.model.AptScheduler;

@Mapper
public interface AptSchedulerMapper {

    /**
     * 아파트 일정 목록조회
     * @return
     */
    public List<AptScheduler> selectAptSchedulerList(Map<String, Object> params);
    
    public int selectAptSchedulerListCount(Map<String, Object> params);
    
}
