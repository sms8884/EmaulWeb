package com.jaha.web.emaul.mapper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jaha.web.emaul.model.CodeGroup;
import com.jaha.web.emaul.model.CommonCode;

@Mapper
public interface CommonMapper {
    public List<CommonCode> selectCodeList(String codeGroup);

    public List<CodeGroup> selectCodeGroupList(CodeGroup codeGroup);

    public int insertCodeGroup(CodeGroup codeGroup);

    public int insertCommonCode(CommonCode commonCode);

    public List<HashMap> getGugunList();

    public List<Map<String, Object>> selectAddressAptList(Map<String, Object> params);

    public Map<String, Object> selectUdsMsg(Map<String, Object> params);

    public int deleteCode(Map<String, Object> params);

    public int insertCode(CommonCode commonCode);

    public Date selectDate();

}
