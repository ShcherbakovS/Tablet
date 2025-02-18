package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.SubObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubObjectRepo extends JpaRepository<SubObject, Long> {

    Optional<SubObject> findBySubObjectName(String name);
    List<SubObject> findByCCSCode(String CCSCode);


    boolean findByCCSCodeAndAndNumberKO(String CCSCode, String KONumber);
}
