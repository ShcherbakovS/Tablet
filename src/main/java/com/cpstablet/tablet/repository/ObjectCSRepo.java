package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.CapitalCS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectCSRepo extends JpaRepository<CapitalCS, Long> {
}
