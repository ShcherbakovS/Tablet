package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalRepo extends JpaRepository<Journal, Long> {

}
