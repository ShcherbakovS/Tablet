package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.PNRSystemDTO;
import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.entity.PNRSystem;
import com.cpstablet.tablet.repository.SystemRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class SystemService {

    static final String DATA_FORMAT = "dd.MM.yyyy";

    private final SystemRepo systemRepo;

    public List<PNRSystem> getSystems (String codeCCS) {

        return null;
    }

    public HttpStatus updateSystemInfo(PNRSystemDTO systemDTO, Long id) {

        PNRSystem toUpdate = systemRepo.findByPNRSystemId(id);

        toUpdate.setPNRSystemStatus(systemDTO.getPNRSystemStatus());
        toUpdate.setCIWExecutor(systemDTO.getCIWExecutor());
        toUpdate.setCWExecutor(systemDTO.getCWExecutor());

        if(systemDTO.getPNRPlanDate() != null) {
            toUpdate.setPNRPlanDate(parsStringToDate(systemDTO.getPNRPlanDate()));
        }
        if(systemDTO.getPNRFactDate() != null) {
            toUpdate.setPNRFactDate(parsStringToDate(systemDTO.getPNRFactDate()));
        }
        if(systemDTO.getIIPlanDate() != null) {
            toUpdate.setIIPlanDate(parsStringToDate(systemDTO.getIIPlanDate()));
        }
        if(systemDTO.getIIFactDate() != null) {
            toUpdate.setIIFactDate(parsStringToDate(systemDTO.getIIFactDate()));
        }
        if(systemDTO.getKOPlanDate() != null) {
            toUpdate.setKOPlanDate(parsStringToDate(systemDTO.getKOPlanDate()));
        }
        if(systemDTO.getKOFactDate() != null) {
            toUpdate.setKOPlanDate(parsStringToDate(systemDTO.getKOFactDate()));
        }
        systemRepo.save(toUpdate);

        return HttpStatus.OK;

    }
    private LocalDate parsStringToDate(String toParsStaring)  {

        return LocalDate.parse(toParsStaring.trim(), DateTimeFormatter.ofPattern(DATA_FORMAT));
    }
}
