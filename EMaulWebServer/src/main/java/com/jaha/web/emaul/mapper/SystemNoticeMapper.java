package com.jaha.web.emaul.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.model.SystemNotice;

@Mapper
public interface SystemNoticeMapper {

    /**
     * 시스템공지사항
     */
    public List<SystemNotice> selectSystemNoticeList(Map<String, Object> params);
    
    public int selectSystemNoticeListCount(Map<String, Object> params);
    
    public SystemNotice selectSystemNotice(Map<String, Object> params);
    
}
