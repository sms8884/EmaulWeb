package com.jaha.web.emaul.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.model.PushLog;

/**
 * Created by shavrani 16-11-24
 */
@Mapper
public interface PushLogMapper {

    public int insertPushLog(PushLog pushLog);

}
