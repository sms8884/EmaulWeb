package com.jaha.web.emaul.service;


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jaha.web.emaul.model.Address;
import com.jaha.web.emaul.model.Apt;
import com.jaha.web.emaul.model.AptContact;
import com.jaha.web.emaul.model.AptFee;
import com.jaha.web.emaul.model.AptFeeAvr;
import com.jaha.web.emaul.model.AptFeePush;
import com.jaha.web.emaul.model.AptInfo;
import com.jaha.web.emaul.model.AptMgtCorp;
import com.jaha.web.emaul.model.AptScheduler;
import com.jaha.web.emaul.model.GasCheck;
import com.jaha.web.emaul.model.House;
import com.jaha.web.emaul.model.User;

/**
 * Created by doring on 15. 3. 31..
 */
public interface HouseService {
    List<Apt> searchApt(String name);

    Apt getApt(Long aptId);

    House saveAndFlush(House house);

    House save(House house);

    House getHouse(Long aptId, String dong, String ho);

    AptFee saveAndFlush(AptFee aptFee);

    AptFee save(AptFee aptFee);

    AptFeeAvr save(AptFeeAvr aptFeeAvr);

    AptFeeAvr getAptFeeAvr(Long aptId, String date, String houseSize);

    List<AptFeeAvr> getAptFeeAvrList(Long aptId, String houseSize);

    void deleteAptFee(Long aptId, String date);

    AptFee getAptFee(Long houseId, String yyyyMM);

    AptFee getLastAptFee(Long houseId);

    List<AptFee> getAptFeeList(Long houseId);

    List<String> getAptFeeRegisteredDate(Long aptId);

    GasCheck saveAndFlush(GasCheck gas);

    List<GasCheck> getGasCheckList(Long userId);

    List<String> getSidoNames();

    List<String> getSigunguNames(String sidoName);

    List<String> getEupMyunDongNames(String sigunguName);

    List<String> getEupMyunDongNames(String sidoName, String sigunguName);

    List<Address> searchBuilding(String sidoName, String sigunguName, String eupmyundongName, String buildingName);

    List<Apt> jahaGetAptList(String registered);

    void jahaNewApt(String addressCode);

    Apt save(Apt apt);

    AptInfo saveAndFlush(AptInfo aptInfo);

    Boolean aptExist(String addressCode);

    List<String> getDongs(Long aptId);

    List<String> getHos(Long aptId, String dong);

    void deleteAptInfoContacts(List<AptContact> list);

    AptInfo updateAptLogo(Long aptId, String url);

    AptInfo updateAptPhoto(Long aptId, String url);

    List<House> getHouses(Long aptId);



    /**
     * 아파트업체 한개를 가져옵니다
     *
     * @param aptId
     * @return
     */
    AptMgtCorp getAptMgtCorp(Long aptId);

    /**
     * 아파트 관리업체 업데이트
     *
     * @param aptMgtCorp
     * @return
     */
    AptMgtCorp saveAndFlush(AptMgtCorp aptMgtCorp);


    /**
     *
     * @param aptFeePush
     * @return
     */
    AptFeePush addAptFeePush(AptFeePush aptFeePush);

    /**
     *
     * @param aptFeePush
     * @return
     */
    AptFeePush modifyAptFeePush(AptFeePush aptFeePush);

    /**
     *
     * @param id
     * @return
     */
    AptFeePush removeAptFeePush(long id);

    /**
     *
     * @return
     */
    Page<AptFeePush> getAptFeePushList(long aptId, Pageable pageable);

    /**
     *
     * @param id
     * @return
     */
    AptFeePush getAptFeePush(long id);

    public Map<String, Object> getAptScheduler(User user, String pageNum, String startDate, String endDate, String searchItem, String searchKeyWord);

    public AptScheduler getAptScheduler(Long id);

    public AptScheduler getAptScheduler(Long id, Long aptId);

    public AptScheduler saveAptScheduler(HttpServletRequest req, User user, AptScheduler params);

    public int updateAptScheduleStatus(Long id);

    public int deleteAptScheduler(AptScheduler params);

    public void sendGcmAptScheduler(AptScheduler aptScheduler);

    Apt getAptByAddressCode(String addressCode);

    /**
     * @author shavrani 2016-10-19
     * @params sidoNm, sggNm 필수
     * @params emdNm, aptNm, aptId 옵션
     * @desc 전국아파트조회 ( 계약된 아파트 표시, 지번주소, 도로명주소 표기 )
     */
    public List<Map<String, Object>> searchAptAll(Map<String, Object> params);

    /**
     * 아파트 아이디, 동, 호로 세대(하우스) 데이터를 조회한다.
     *
     * @param aptId
     * @param dong
     * @param ho
     * @return
     */
    List<House> getHouseList(Long aptId, String dong, String ho);

    int insertHouse(House house);


}
