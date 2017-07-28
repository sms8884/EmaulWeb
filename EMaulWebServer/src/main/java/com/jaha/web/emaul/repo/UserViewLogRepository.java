package com.jaha.web.emaul.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.UserViewLog;

/**
 * Created by shavrani on 16-06-16
 */
@Repository
public interface UserViewLogRepository extends JpaRepository<UserViewLog, Long> {
}
