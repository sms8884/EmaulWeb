package com.jaha.web.emaul.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jaha.web.emaul.constants.Constants;
import com.jaha.web.emaul.model.MetroNews;
import com.jaha.web.emaul.service.MetroNewsService;
import com.jaha.web.emaul.util.CommonUtil;
import com.jaha.web.emaul.util.FtpUtils;

/**
 * <pre>
 * Class Name : MetroNewsController.java
 * Description : Description
 *
 * Modification Information
 *
 * Mod Date         Modifier    Description
 * -----------      --------    ---------------------------
 * 2016. 8. 25.     전강욱      Generation
 * </pre>
 *
 * @author 전강욱
 * @since 2016. 8. 25.
 * @version 1.0
 */
@Controller
public class MetroNewsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetroNewsController.class);

    @Value("${file.path.metro-news}")
    private String filePathMetroNews;

    @Autowired
    private FtpUtils ftpUtils;

    @Autowired
    private MetroNewsService metroNewsService;

    @RequestMapping(value = "/api/public/metro-news/batch-add")
    @ResponseBody
    public String handleMetroNews(Model model, HttpServletRequest request) throws Exception {
        String datetimeBefore5min = CommonUtil.getNextDatetime(-5);
        LOGGER.debug("<<메트로신문사 FTP에서 가져올 뉴스 대상 시간 {}>>", datetimeBefore5min);

        List<File> jsonFileList = this.ftpUtils.downloadFtpFile(this.filePathMetroNews, Constants.METRO_FTP_SERVER_IP, Constants.METRO_FTP_SERVER_PORT, Constants.METRO_FTP_ID, Constants.METRO_FTP_PW,
                Constants.METRO_FTP_ROOT_DIR, Long.valueOf(datetimeBefore5min));

        if (jsonFileList != null && jsonFileList.size() > 0) {
            // {"media_id":"metroseoul","media_name":"메트로신문","news_cd":"2016082500129","news_action":"U","news_app_ndt":"C","news_app_edt":"C","news_cate_cd":"160000000","news_cate_nm":"산업","news_editors":[{"writer_name":"나원재","writer_email":"nwj@metroseoul.co.kr","writer_byline":"나원재
            // 기자"}],"news_title":"SK하이닉스, 반도체 방진복 제조·세정 ‘자회사형 표준사업장’
            // 설립","news_subtitle":"","news_content":"[!{IMG::http:\/\/cdn.emetro.co.kr\/imagebank\/2016\/08\/25\/0480\/20160825000134.jpg::C::480::자회사형 장애인 표준사업장 간담회 및 협약식에서 참석자들이 기념촬영을 하고 있다. 첫 줄
            // 오른쪽에서 네 번째 SK하이닉스 경영지원부문장 김준호 사장, 다섯 번째 고용노동부 이기권 장관, 여섯 번째 한국장애인고용공단 박승규 이사장. \/SK하이닉스}!]\n\nSK하이닉스가 25일 서울 중구 은행회관에서 한국장애인고용공단과 '자회사형 표준사업장' 설립을 위한 양해각서(MOU)를 체결하고, 장애인 고용 촉진에
            // 나섰다.\n\n이날 협약식에는 고용노동부 이기권 장관과 한국장애인고용공단 박승규 이사장, SK하이닉스 경영지원부문장 김준호 사장 등이 참석했다.\n\n이 자회사형 표준사업장은 SK하이닉스의 사업과 연계해 '반도체 방진복 제조와 세정'을 맡는다.\n\n이후에는 반도체 클린룸에서 사용하는 부자재를 유통하는 업종으로 영역을 확대하게
            // 된다. SK하이닉스는 이번 자회사형 표준사업장 설립을 통해 사업 초기 연도인 오는 2018년까지 장애인 120여명을 고용하게 되며 향후 고용 규모를 꾸준히 늘려갈 예정이다.\n\nSK하이닉스에 따르면 자회사형 표준사업장은 오는 11월까지 부지선정, 사업장 설계 및 법인 설립을 마치고 연내 기공식 및 내년 하반기 준공을 거쳐
            // 본격적인 운영에 들어갈 전망이다.\n\n그간 SK하이닉스는 반도체 업종 특성상 장애인에 적합한 직무 발굴에 어려움을 겪는 등 장애인 고용 확대에 부진을 겪어오면서도, 장애인 고용률 향상이라는 사회적 책임의 이행을 위해 다양한 방안을 모색해왔다.\n\nSK하이닉스 이일우 HR실장은 \"반도체 사업과 연계한 자회사형 표준사업장을 통해
            // 장애인 고용을 확대할 것\"이라며 \"사업장의 원활한 출범과 운영을 위해 가능한 자원을 최적으로 활용하고 모회사를 통한 장애인 직접고용도 점진적으로 확대할 것\"이라고 설명했다.\n\n한편 클린룸 소모품 시장은 반도체뿐만 아니라 제약, 생명공학, 항공·우주 산업 발전에 따라 매년 4.5%씩 성장할 것으로 전망되고
            // 있다.","main_image_url":"http:\/\/cdn.emetro.co.kr\/imagebank\/2016\/08\/25\/0640\/20160825000134.jpg"}

            List<MetroNews> paramList = new ArrayList<MetroNews>();

            for (File jsonFile : jsonFileList) {
                try {
                    JSONObject obj = new JSONObject(FileUtils.readFileToString(jsonFile));

                    MetroNews metroNews = new MetroNews();
                    metroNews.setNewsCd(obj.getString("news_cd"));
                    metroNews.setNewsAction(obj.getString("news_action"));

                    metroNews.setNewsAppNdt(this.convertStringToDate(obj.getString("news_app_ndt")));
                    metroNews.setNewsAppEdt(this.convertStringToDate(obj.getString("news_app_edt")));

                    metroNews.setNewsCateCd(obj.getString("news_cate_cd"));
                    metroNews.setNewsCateNm(obj.getString("news_cate_nm"));
                    metroNews.setNewsTitle(obj.getString("news_title"));
                    metroNews.setNewsSubtitle(obj.getString("news_subtitle"));
                    metroNews.setNewsContent(obj.getString("news_content"));

                    JSONArray newsEditors = obj.getJSONArray("news_editors");
                    int length = newsEditors.length();
                    String convertNewsEditors = StringUtils.EMPTY;

                    for (int i = 0; i < length; i++) {
                        JSONObject newsEditor = (JSONObject) newsEditors.get(i);

                        String writerName = (String) newsEditor.get("writer_name");
                        String writerEmail = (String) newsEditor.get("writer_email");
                        String writerByline = (String) newsEditor.get("writer_byline");

                        convertNewsEditors += writerName + "|" + writerEmail + "|" + writerByline;
                        if (i < (length - 1)) {
                            convertNewsEditors += ",";
                        }
                    }

                    metroNews.setNewsEditors(convertNewsEditors);
                    metroNews.setMainImageUrl(obj.getString("main_image_url"));

                    paramList.add(metroNews);

                } catch (Exception e) {
                    LOGGER.error("<<메트로신문사 뉴스 등록 중 오류 발생>>", e);
                }
            }

            this.metroNewsService.mergeMetroNews(paramList);
        }

        return "Metro News FTP download and DB insert OK!";
    }

    static final String METRO_NEWS_DATE_FORMAT = "yyyy-MM-ddHH:mm:ss";
    static final SimpleDateFormat METRO_NEWS_SDF = new SimpleDateFormat(METRO_NEWS_DATE_FORMAT, Locale.KOREA);

    /**
     * 2016-08-22T18:00:00+09:00 형태의 문자를 Date 형으로 변환한다.
     *
     * @param param
     * @return
     */
    private Date convertStringToDate(String param) {
        Date datetime = null;

        try {
            String[] temps = param.split("[+]", -1);
            String time = StringUtils.remove(temps[0], 'T');

            datetime = METRO_NEWS_SDF.parse(time);

        } catch (Exception e) {
            LOGGER.error("<<메트로신문사 뉴스 등록 중 날짜 변환 중 오류 발생>> 인자로 받은 날짜: {}", param);
        }

        return (datetime == null) ? new Date() : datetime;
    }

}
