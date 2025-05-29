package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.CalendarDayDTO;
import com.cpstablet.tablet.entity.CalendarDay;
import com.cpstablet.tablet.entity.CapitalCS;
import com.cpstablet.tablet.repository.CalendarDayRepo;
import com.cpstablet.tablet.repository.CapitalCSRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
в ответе необходимо сформировать коллекцию из семи элементов, центральным элементом
является элемент с текущей датой
проверки дат на три дня до и три дня после текущей даты,
если элементы есть добавляем в лист, элемента нет создаем в бд и после добавляем лист
создавать все сущности на остаток года, до текущей даты
проверка при запросе, если у объекта пусто, создать структуру для него.
пишутся только понедельники
если при запросе текущая дата >= 01.12, то структура создается на следующий год
сформировать коллекцию из dto для отправки по принципу ->
получить текущую дату-> получить неделю года -> найти понедельник этой недели ->
понедельник текущей недели центр коллекции-> три недели до и после по бокам
 */

@Service
@AllArgsConstructor
public class CalendarService {

    private CalendarDayRepo calendarDayRepo;
    private CapitalCSRepo capitalCSRepo;

    public void createCalendarDays(LocalDate creationDay, Long id) {

        /*
            проверка в случае если дата попадает на декабрь-> записать календарь до следующего года
         */


        CapitalCS capitalCS = capitalCSRepo.findById(id).orElseThrow(()-> new RuntimeException("Объект не найден"));

        LocalDate lastDayOfYear = LocalDate.of(creationDay.getYear(), Month.DECEMBER, 31);

        Stream.iterate(creationDay, date -> !date.isAfter(lastDayOfYear), date -> date.plusDays(1))
                .filter(day-> day.getDayOfWeek().equals(DayOfWeek.MONDAY))
                .map(day-> CalendarDay.builder()
                        .personnelPlan(0L)
                        .personnelFact(0l)
                        .date(day)
                        .capitalCS(capitalCS)
                        .build()).collect(Collectors.toList()).stream().forEach(calendarDay -> calendarDayRepo.save(calendarDay));

    }

    public List<CalendarDayDTO> createWeeklyCalendar(LocalDate referenceDay, Long id) {


        CapitalCS capitalCS = capitalCSRepo.findById(id).orElseThrow(()-> new RuntimeException("Объект не найден"));

        if(capitalCS.getCalendarDays().isEmpty()) {

            createCalendarDays(referenceDay, id);

        } else if (referenceDay.getMonth().equals(Month.DECEMBER)) {

            createCalendarDays(LocalDate.of(referenceDay.plusYears(1).getYear(), Month.DECEMBER, 01), id);
        }

        List<CalendarDay> calendarDays = capitalCS.getCalendarDays();


        List<LocalDate> days = new ArrayList<>();

        if(!referenceDay.getDayOfWeek().equals(DayOfWeek.MONDAY)) {

            Stream.iterate(referenceDay, date-> !date.isAfter(referenceDay), date-> date.minusDays(1))
                    .filter(day-> day.getDayOfWeek().equals(DayOfWeek.MONDAY))
                    .limit(4).forEach(day-> days.add(day));

            Stream.iterate(referenceDay, date-> !date.isBefore(referenceDay), date-> date.plusDays(1))
                    .filter(day-> day.getDayOfWeek().equals(DayOfWeek.MONDAY))
                    .limit(3).forEach(day-> days.add(day));
        } else {

            days.add(referenceDay);

            Stream.iterate(referenceDay, date-> !date.isAfter(referenceDay), date-> date.minusDays(1))
                    .filter(day-> day.getDayOfWeek().equals(DayOfWeek.MONDAY))
                    .limit(3).forEach(day-> days.add(day));

            Stream.iterate(referenceDay, date-> !date.isAfter(referenceDay), date-> date.plusDays(1))
                    .filter(day-> day.getDayOfWeek().equals(DayOfWeek.MONDAY))
                    .limit(3).forEach(day-> days.add(day));
        }

        return calendarDays.stream().filter(calendarDay -> days.contains(calendarDay.getDate()))
                .sorted(Comparator.comparing(CalendarDay::getDate))
                .map(calendarDay -> CalendarDayDTO.builder()
                        .id(calendarDay.getId())
                        .personnelPlan(calendarDay.getPersonnelFact())
                        .personnelFact(calendarDay.getPersonnelFact())
                        .date(formatDate(calendarDay.getDate()))
                        .build()).collect(Collectors.toList());
    }


    private String formatDate(LocalDate date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(formatter);

    }


}
