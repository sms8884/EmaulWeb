package com.jaha.web.emaul.repo;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jaha.web.emaul.model.AptApAccessAuth;

/**
 * Created by shavrani on 16-06-21
 */
@Repository
public interface AptApAccessAuthRepository extends JpaRepository<AptApAccessAuth, Long> {
    
    List<AptApAccessAuth> findByAptApIdAndType(Long aptApId, String type);
    
    AptApAccessAuth findByAptApIdAndTypeAndDongAndHoAndHoType(Long aptApId, String type, String dong, String ho, String hoType);
    
    AptApAccessAuth findByAptApIdAndTypeAndUserId(Long aptApId, String type, Long userId);

    List<AptApAccessAuth> findByAptApId(Long aptApId);
    
    void deleteByAptApId(Long aptApId);
    
    void deleteByAptApIdAndType(Long aptApId, String type);
    
    void deleteByAptApIdAndTypeAndDong(Long aptApId, String type, String dong);
    
    void deleteByAptApIdAndTypeAndDongAndHoEndingWith(Long aptApId, String type, String dong, String ho);
    
}
