package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.PNRSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemRepo extends JpaRepository<PNRSystem, Long> {
}
