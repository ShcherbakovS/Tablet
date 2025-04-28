package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.RegistrationApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationAppRepo extends JpaRepository <RegistrationApplication, Long> {
}
