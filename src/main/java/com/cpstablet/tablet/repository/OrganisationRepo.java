package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationRepo extends JpaRepository<Organisation, Long> {




}
