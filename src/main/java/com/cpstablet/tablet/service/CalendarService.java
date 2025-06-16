package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.CalendarDayDTO;
import com.cpstablet.tablet.entity.CalendarDay;
import com.cpstablet.tablet.entity.CapitalCS;
import com.cpstablet.tablet.repository.CalendarDayRepo;
import com.cpstablet.tablet.repository.CapitalCSRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
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
    @Qualifier("myMapper")
    private final ObjectMapper myMapper;

    public void createCalendarDays(LocalDate creationDay, String codeCCS) {
        if (creationDay == null || codeCCS == null || codeCCS.isEmpty()) {
            throw new IllegalArgumentException("Некорректные входные параметры");
        }

        CapitalCS capitalCS = capitalCSRepo.findByCodeCCS(codeCCS)
                .orElseThrow(() -> new RuntimeException("Объект не найден"));

        LocalDate lastDayOfYear = LocalDate.of(creationDay.getYear(), Month.DECEMBER, 31);
        LocalDate firstDayOfYear = LocalDate.of(creationDay.getYear(), Month.JANUARY, 1);

        List<CalendarDay> calendarDays = Stream.iterate(firstDayOfYear,
                date -> !date.isAfter(lastDayOfYear),
                date -> date.plusDays(1))
                .filter(day -> day.getDayOfWeek().equals(DayOfWeek.MONDAY))
                .map(day -> CalendarDay.builder()
                        .personnelPlan(0L)
                        .personnelFact(0L)
                        .date(day)
                        .capitalCS(capitalCS)
                        .build())
                .collect(Collectors.toList());

        calendarDayRepo.saveAll(calendarDays);
    }

    public List<CalendarDayDTO> createWeeklyCalendar(LocalDate referenceDay, String codeCCS) {

        LocalDate sourceDate;

        if (referenceDay.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            sourceDate = referenceDay;
        } else {

            sourceDate = findMonday();
        }

        CapitalCS capitalCS = capitalCSRepo.findByCodeCCS(codeCCS)
                .orElseThrow(() -> new RuntimeException("Объект не найден"));

        if (capitalCS.getCalendarDays().isEmpty()) {
            createCalendarDays(sourceDate, codeCCS);
        } else if (sourceDate.getMonth().equals(Month.DECEMBER)) {
            createCalendarDays(LocalDate.of(sourceDate.plusYears(1).getYear(), Month.DECEMBER, 1), codeCCS);
        }

        List<LocalDate> days = new ArrayList<>();

        days.add(sourceDate);

        // Добавляем даты до референсной даты
        Stream.iterate(sourceDate, date -> date.minusDays(1))
                .filter(date -> date.getDayOfWeek().equals(DayOfWeek.MONDAY))
                .limit(4)
                .forEach(days::add);

        // Добавляем даты после референсной даты
        Stream.iterate(sourceDate, date -> date.plusDays(1))
                .filter(date -> date.getDayOfWeek().equals(DayOfWeek.MONDAY))
                .limit(4)
                .forEach(days::add);

        System.out.println(days);

        List<CalendarDayDTO> daysDTO = capitalCS.getCalendarDays().stream()
                .filter(calendarDay -> days.contains(calendarDay.getDate()))
                .sorted(Comparator.comparing(CalendarDay::getDate))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        daysDTO.stream().forEach(System.out::println);

        return daysDTO;
    }

    private CalendarDayDTO convertToDTO(CalendarDay calendarDay) {
        return CalendarDayDTO.builder()
                .id(calendarDay.getId())
                .personnelPlan(calendarDay.getPersonnelPlan())
                .personnelFact(calendarDay.getPersonnelFact())
                .date(formatDate(calendarDay.getDate()))
                .build();
    }

    public HttpStatus updateStaffInfo(List<CalendarDayDTO> days) {
        if (days == null || days.isEmpty()) {
            throw new IllegalArgumentException("Список дней пуст");
        }

        List<CalendarDay> updatedDays = new ArrayList<>();

        for (CalendarDayDTO dayDTO : days) {
            try {
                CalendarDay day = calendarDayRepo.findById(dayDTO.getId())
                        .orElseThrow(() -> new IllegalArgumentException("День с ID " + dayDTO.getId() + " не найден"));

                day.setPersonnelFact(dayDTO.getPersonnelFact());
                day.setPersonnelPlan(dayDTO.getPersonnelPlan());
                updatedDays.add(day);
            } catch (Exception e) {
                throw new IllegalArgumentException("Ошибка при обновлении дня с ID " + dayDTO.getId(), e);
            }
        }

        calendarDayRepo.saveAll(updatedDays);
        return HttpStatus.OK;
    }

    private String formatDate(LocalDate date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        return date.format(formatter);

    }
    public LocalDate findMonday() {
       return Stream.iterate(LocalDate.now(), date -> date.minusDays(1))
                .filter(date -> date.getDayOfWeek().equals(DayOfWeek.MONDAY))
                .findFirst().get();
    }

}
