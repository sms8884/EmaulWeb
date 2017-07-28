package com.jaha.web.emaul.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.User;
import com.jaha.web.emaul.model.UserNickname;

/**
 * Created by doring on 15. 3. 9..
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findOneByEmail(String email);

    @SuppressWarnings("rawtypes")
    @Override
    Page<User> findAll(Specification spec, Pageable pagealbe);

    @Override
    Page<User> findAll(Pageable pagealbe);

    Page<User> findByHouseIdIn(List<Long> houseIds, Pageable pageable);

    // @SuppressWarnings("rawtypes")
    // Page<User> findByHouseIdIn(List<Long> houseIds, Specification spec, Pageable pageable);

    List<User> findByHouseIdIn(List<Long> houseIds);

    // 닉네임으로 사용자 검색
    User findOneByNickname(UserNickname userNickName);

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 2.
     * @description user 테이블의 마지막 로그인 시간 업데이트
     *
     * @param userId
     */
    @Modifying
    @Query("UPDATE User SET last_login_date = now() WHERE id = :userId")
    void updateLastLoginDate(@Param("userId") long userId);

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 8.
     * @description 사용자명과 폰번호로 조회
     *
     * @param fullName
     * @param phoneNumber
     * @return
     */
    User findOneByFullNameAndPhone(String fullName, String phoneNumber);

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 8.
     * @description 이메일, 사용자명과 폰번호로 조회
     *
     * @param fullName
     * @param phoneNumber
     * @return
     */
    User findOneByEmailAndFullNameAndPhone(String email, String fullName, String phoneNumber);

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 9.
     * @description user 테이블의 비밀번호 업데이트
     *
     * @param passwordHash
     * @param email
     */
    @Modifying
    @Query("UPDATE User SET password_hash = :password WHERE email = :email")
    void updatePassword(@Param("password") String passwordHash, @Param("email") String email);

    /**
     * 암호화된 사용자명으로 사용자 목록 조회
     *
     * @param fullName
     * @return
     */
    List<User> findByFullName(String fullName);

    /**
     * 닉네임이 null인 사용자 목록 조회
     *
     * @return
     */
    List<User> findByNicknameIsNull();

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 8.
     * @description 사용자명과 폰번호로 조회
     *
     * @param fullName
     * @param phoneNumber
     * @return
     */
    List<User> findByFullNameAndPhone(String fullName, String phoneNumber);

    /**
     * @author 전강욱(realsnake@jahasmart.com), 2016. 6. 8.
     * @description 이메일, 사용자명과 폰번호로 조회
     *
     * @param fullName
     * @param phoneNumber
     * @return
     */
    List<User> findByEmailAndFullNameAndPhone(String email, String fullName, String phoneNumber);

    List<User> findByPhone(String phone);

    /**
     * @author shavrani 2016-10-18
     * @description 이메일, 폰번호로 조회
     */
    List<User> findByEmailAndPhone(String email, String phoneNumber);


    /**
     * @author 조영태(cyt@jahasmart.com), 2016. 11. 22
     * @description 사용자번호 사용하여 해당 고객정보를 조회한다. <br/>
     *
     * @param userId
     * @param passwordHash
     * @return
     */
    User findOneById(Long id);

}
