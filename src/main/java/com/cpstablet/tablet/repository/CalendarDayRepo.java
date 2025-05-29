package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.CalendarDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarDayRepo extends JpaRepository<CalendarDay, Long> {


}
