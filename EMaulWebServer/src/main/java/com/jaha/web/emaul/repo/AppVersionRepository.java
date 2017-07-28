package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by doring on 15. 4. 2..
 */
@Repository
public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {
    AppVersion findByKind(String kind);
}
