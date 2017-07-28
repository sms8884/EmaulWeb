package com.jaha.web.emaul.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.Address;
import com.jaha.web.emaul.model.Apt;

/**
 * Created by doring on 15. 3. 31..
 */
@Repository
public interface AptRepository extends JpaRepository<Apt, Long> {

    List<Apt> findByNameContainingAndRegisteredAptIsTrueAndVirtualFalse(String name);

    // Apt findByName(String name);
    Apt findByAddress(Address address);

    List<Apt> findByNameLikeAndVirtualFalse(String name);

    List<Apt> findByRegisteredApt(boolean registered);

    List<Apt> findByVirtualFalse();

    List<Apt> findByRegisteredAptAndVirtualFalse(boolean registered);

}
