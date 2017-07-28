package com.jaha.web.emaul.repo;

import com.jaha.web.emaul.model.AptContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by doring on 15. 6. 8..
 */
@Repository
public interface AptContactRepository extends JpaRepository<AptContact, Long>{

}
