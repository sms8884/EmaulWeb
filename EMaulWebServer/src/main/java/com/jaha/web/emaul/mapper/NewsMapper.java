package com.jaha.web.emaul.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NewsMapper {
	
	/*마을뉴스 맵퍼*/	
	public List<Map<String, Object>> selectNewsList(Map<String,Object> params);        
	public int selectNewsListCount(Map<String,Object>params);    	
	public Map<String, Object> getNews(Long postId);	
	public Long insertNews(Map<String, Object> params);	
	public void updateNews(Map<String, Object> params);	
	public void deleteNews(Long postId);
	
	/*사용자 화면*/
	public List<Map<String, Object>> selectUserNewsList(Map<String,Object> params);        
	public int selectUserNewsListCount(Map<String,Object>params);    
	
    
}
