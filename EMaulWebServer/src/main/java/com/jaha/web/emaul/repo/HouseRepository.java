package com.jaha.web.emaul.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.House;


/**
 * Created by doring on 15. 3. 31..
 */
@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
    House findOneByAptIdAndDongAndHo(Long aptId, String dong, String ho);

    List<House> findByAptId(Long aptId);

    List<House> findByAptIdAndDong(Long aptId, String dong);

    List<House> findByAptIdAndDongAndHoIn(Long aptId, String dong, List<String> hos);

    List<House> findByAptIdAndDongAndHo(Long aptId, String dong, String ho);
}
