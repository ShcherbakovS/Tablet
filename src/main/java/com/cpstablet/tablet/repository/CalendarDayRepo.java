package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.CalendarDay;
import com.cpstablet.tablet.entity.CapitalCS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarDayRepo extends JpaRepository<CalendarDay, Long> {


    CalendarDay getById(Long id);
    List<CalendarDay> findByCapitalCS(CapitalCS capitalCS);

}
