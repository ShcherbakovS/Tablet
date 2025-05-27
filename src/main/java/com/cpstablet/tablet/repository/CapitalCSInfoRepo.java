package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.CapitalCSInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapitalCSInfoRepo extends JpaRepository<CapitalCSInfo, Long> {

}
