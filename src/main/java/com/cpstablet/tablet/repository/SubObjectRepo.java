package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.SubObject;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubObjectRepo extends JpaRepository<SubObject, Long> {

    Optional<SubObject> findAllBySubObjectName(String name);
    List<SubObject> findAllByCCSCode(String CCSCode);
    List<SubObject> findByCCSCode(String CCSCode);

    @Transactional
    @Query("SELECT e FROM SubObject e WHERE e.CCSCode = :CCSCode AND e.numberKO = :KONumber")
    SubObject findBYCCSCodeAndKONumber(String CCSCode, String KONumber);

    @Transactional
    @Query("SELECT e FROM SubObject e WHERE e.CCSCode = :CCSCode AND e.subObjectName = :subObjectName")
    SubObject findBYCCSCodeAndSubObjectName(String CCSCode, String subObjectName);

    @Transactional
    @Modifying
    @Query("DELETE FROM SubObject e WHERE e.CCSCode = :CCSCode")
    void deleteAllByCCSCode(String CCSCode);


}
