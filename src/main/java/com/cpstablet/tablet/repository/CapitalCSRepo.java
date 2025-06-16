package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.CapitalCS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CapitalCSRepo extends JpaRepository<CapitalCS, Long> {

    Optional<CapitalCS> findByCodeCCS(String codeCCS);



}
