package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by doring on 15. 3. 10..
 */
@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Long> {
}
