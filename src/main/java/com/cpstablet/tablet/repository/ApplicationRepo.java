package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepo extends JpaRepository<Application, Long> {

    Optional<Application> findById(Long id);
}
