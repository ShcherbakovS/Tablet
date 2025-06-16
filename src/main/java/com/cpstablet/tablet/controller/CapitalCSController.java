package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.CalendarDayDTO;
import com.cpstablet.tablet.DTO.CapitalCSDTO;
import com.cpstablet.tablet.entity.CapitalCSInfo;
import com.cpstablet.tablet.service.CalendarService;
import com.cpstablet.tablet.service.CapitalCSService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/capitals")
@AllArgsConstructor
public class CapitalCSController {

    private final CapitalCSService capitalService;
    private final CalendarService calendarService;

    @Qualifier("myMapper")
    private final ObjectMapper myMapper;

    @PostMapping("/createObject")
    @PreAuthorize("hasRole('ADMIN')")
    public HttpStatus createNewObject(@RequestBody String jsonString) {

        try {
            return capitalService.create(myMapper.readValue(jsonString, CapitalCSDTO.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/updateCapitalCS/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HttpStatus updateCCSInfo(@RequestBody String jsonString, @PathVariable("id") Long id) {
        try {
            capitalService.update(myMapper.readValue(jsonString, CapitalCSDTO.class), id);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return HttpStatus.CREATED;
    }

    @GetMapping("/findByCodeCCS/{codeCCS}")
    public ResponseEntity<CapitalCSDTO> getByCodeCCS(@PathVariable("codeCCS") String codeCCS) {
        return new ResponseEntity<>(capitalService.findCCS(codeCCS), HttpStatus.OK);
    }

    @GetMapping("/getAll/{userId}")
    public ResponseEntity<List<CapitalCSDTO>> getFilteredBuUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(capitalService.filteredByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<CapitalCSDTO>> getAll() {
        return new ResponseEntity<>(capitalService.findAll(), HttpStatus.OK);

    }

    @DeleteMapping("/deleteCapitalCS/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HttpStatus deleteCapitalCS(@PathVariable("id") Long id) {
        return capitalService.deleteCapitalCS(id);
    }

    @PutMapping("/updateCapitalCSInfo/{id}")
    public HttpStatus updateCCSDocsInfo(@RequestBody String jsonString, @PathVariable("id") String codeCCS) {
        try {
            capitalService.updateCapitalCSInfo(myMapper.readValue(jsonString, CapitalCSInfo.class), codeCCS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return HttpStatus.CREATED;
    }

    @GetMapping("/getStaff/{codeCCS}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<CalendarDayDTO> getPersonal(@PathVariable String codeCCS) {

        return calendarService.createWeeklyCalendar(LocalDate.now(), codeCCS);
    }

    @PutMapping("/updateStaffInf")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<HttpStatus> updateCalendarData(@RequestBody List<CalendarDayDTO> dates) {
        try {
            if (dates == null || dates.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            for (CalendarDayDTO day : dates) {
                if (day.getId() == null) {
                    return ResponseEntity.badRequest().build();
                }
            }

            HttpStatus result = calendarService.updateStaffInfo(dates);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
