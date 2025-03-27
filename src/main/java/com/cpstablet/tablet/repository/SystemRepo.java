package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.PNRSystem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemRepo extends JpaRepository<PNRSystem, Long> {

     List<PNRSystem> getAllByCCSNumber(String CCSNumber);

     PNRSystem findByPNRSystemId(Long id);

     @Transactional
     @Query("SELECT  e FROM PNRSystem e WHERE e.CCSNumber = :CCSCode AND e.PNRSystemStatus = :status")
     List<PNRSystem> getAllByStatus(String CCSCode, String status);


}
