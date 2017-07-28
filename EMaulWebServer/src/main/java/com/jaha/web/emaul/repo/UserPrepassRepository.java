package com.jaha.web.emaul.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.UserPrepass;


/**
 * Created by doring on 15. 3. 9..
 */
@Repository
public interface UserPrepassRepository extends JpaRepository<UserPrepass, Long> {
    List<UserPrepass> findByAptId(Long aptId);

    List<UserPrepass> findByAptIdAndDongAndHoIn(Long aptId, String dong, List<String> ho);

    List<UserPrepass> findByAptIdAndDong(Long aptId, String dong);

    UserPrepass findOneByFullNameAndPhoneAndAptIdAndDongAndHo(String fullName, String phone, Long aptId, String dong, String ho);
}
