package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.Subobject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubobjectRepo extends JpaRepository<Subobject, Long> {
}
