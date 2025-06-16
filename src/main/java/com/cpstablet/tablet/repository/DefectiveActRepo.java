package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.DefectiveAct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DefectiveActRepo extends JpaRepository<DefectiveAct, Long> {



    List<DefectiveAct> findAllByCodeCCS(String codeCCS);

    List<DefectiveAct> findByCodeCCS(String codeCCS);

}
