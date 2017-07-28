/**
 * Copyright (c) 2016 JAHA SMART CORP., LTD ALL RIGHT RESERVED
 *
 * 2016. 8. 27.
 */
package com.jaha.web.emaul.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jaha.web.emaul.model.MetroNews;
import com.jaha.web.emaul.repo.MetroNewsRepository;

/**
 * <pre>
 * Class Name : MetroNewsServiceImpl.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 8. 27.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 8. 27.
 * @version 1.0
 */
@Service
public class MetroNewsServiceImpl implements MetroNewsService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MetroNewsRepository metroNewsRepository;

    /*
     * (non-Javadoc)
     *
     * @see com.jaha.web.emaul.service.MetroNewsService#mergeMetroNews(java.util.List)
     */
    @Override
    @Transactional
    public void mergeMetroNews(List<MetroNews> paramList) {
        if (paramList == null || paramList.isEmpty()) {
            logger.info("<<등록할 뉴스가 없어 메트로신문사 뉴스를 DB에 등록하지 않습니다>>");
        } else {
            for (MetroNews metroNews : paramList) {
                try {
                    if (this.metroNewsRepository.exists(metroNews.getNewsCd())) {
                        if ("M".equals(metroNews.getNewsAction())) { // 수정
                            MetroNews oldMetroNews = this.metroNewsRepository.findOne(metroNews.getNewsCd());
                            metroNews.setRegDate(oldMetroNews.getRegDate());
                            this.metroNewsRepository.saveAndFlush(metroNews);

                            logger.debug("<<메트로신문사 뉴스 DB 수정: {}>>", metroNews.getNewsCd());
                        } else if ("D".equals(metroNews.getNewsAction())) { // 삭제
                            this.metroNewsRepository.delete(metroNews.getNewsCd());

                            logger.debug("<<메트로신문사 뉴스 DB 삭제: {}>>", metroNews.getNewsCd());
                        }
                    } else { // 등록
                        this.metroNewsRepository.saveAndFlush(metroNews);

                        logger.debug("<<메트로신문사 뉴스 DB 등록: {}>>", metroNews.getNewsCd());
                    }
                } catch (Exception e) {
                    logger.error("<<메트로신문사 뉴스 DB 등록 중 오류 발생>>", e);
                }
            }

            // for (MetroNews metroNews : paramList) {
            // }
        }
    }

}
