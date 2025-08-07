package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.DefectiveActDTO;
import com.cpstablet.tablet.entity.CapitalCS;
import com.cpstablet.tablet.entity.DefectiveAct;
import com.cpstablet.tablet.entity.User;
import com.cpstablet.tablet.repository.CapitalCSRepo;
import com.cpstablet.tablet.repository.DefectiveActRepo;
import com.cpstablet.tablet.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefectiveActService {


    private final DefectiveActRepo defectiveActRepo;

    private final CapitalCSRepo capitalCSRepo;

    private final UserRepo userRepo;

    public DefectiveAct create(DefectiveActDTO defActDTO) {

//        LocalDate startDate = LocalDate.parse(defActDTO.getStartDate(), formatter);

        CapitalCS capitalCS = capitalCSRepo.findByCodeCCS(defActDTO.getCodeCCS()).orElseThrow(()-> new RuntimeException("Объект капитпльного строительства не найжен"));
        if(capitalCS.getDefectiveActCounter() == null) {
            capitalCS.setDefectiveActCounter(1L);
            capitalCSRepo.save(capitalCS);

        }

        Long defActCounter = capitalCS.getDefectiveActCounter();

        User user = userRepo.findById(Long.valueOf(defActDTO.getUserName()))
                .orElseThrow(()-> new EntityNotFoundException("При попытке создания замечания, пользователь не найден"));


        DefectiveAct defAct = DefectiveAct.builder()
                .codeCCS(defActDTO.getCodeCCS())
                .defectiveActExplanation(defActDTO.getDefectiveActExplanation())
                .defectiveActStatus(defActDTO.getDefectiveActStatus())
                .equipment(defActDTO.getEquipment())
                .defectiveActStatus(defActDTO.getDefectiveActStatus())
                .executor(defActDTO.getExecutor())
                .description(defActDTO.getDescription())
                .endDatePlan(defActDTO.getEndDatePlan())
                .endDateFact(defActDTO.getEndDateFact())
                .userName(user.getUserInfo().getFullName())
                .userOrganisation(user.getUserInfo().getOrganisation())
                .iiNumber(defActDTO.getIiNumber())
                .startDate(defActDTO.getStartDate())
                .serialNumber(defActCounter + 1)
                .subObject(defActDTO.getSubObject())
                .systemName(defActDTO.getSystemName())
                .manufacturer(defActDTO.getManufacturer())
                .manufacturerNumber(defActDTO.getManufacturerNumber())
                .build();

        capitalCS.setDefectiveActCounter(defActCounter + 1);

        capitalCSRepo.save(capitalCS);

        return defectiveActRepo.save(defAct);
    }
    public List<DefectiveActDTO> getDefectiveActs(String codeCCS) {

        return defectiveActRepo.findAllByCodeCCS(codeCCS).stream().map(defectiveAct-> createDto(defectiveAct))
                .sorted(Comparator.comparing(DefectiveActDTO::getSerialNumber)).collect(Collectors.toList());

    }

    public HttpStatus update(DefectiveActDTO defectiveActDTO, Long id) {

        DefectiveAct toUpdate = defectiveActRepo.findById(id).get();

        //TODO: логика дат при перезаписи корректировка

        toUpdate.setEndDatePlan(defectiveActDTO.getEndDatePlan());
        toUpdate.setStartDate(defectiveActDTO.getStartDate());

        toUpdate.setEquipment(defectiveActDTO.getEquipment());
        toUpdate.setDescription(defectiveActDTO.getDescription());
        toUpdate.setIiNumber(defectiveActDTO.getIiNumber());
        toUpdate.setExecutor(defectiveActDTO.getExecutor());
        toUpdate.setSubObject(defectiveActDTO.getSubObject());
        toUpdate.setSystemName(defectiveActDTO.getSystemName());
        toUpdate.setDefectiveActExplanation(defectiveActDTO.getDefectiveActExplanation());
        toUpdate.setManufacturer(defectiveActDTO.getManufacturer());
        toUpdate.setManufacturerNumber(defectiveActDTO.getManufacturerNumber());

        if(!defectiveActDTO.getEndDateFact().equals(" ")) {
            toUpdate.setEndDateFact(defectiveActDTO.getEndDateFact());
            toUpdate.setDefectiveActStatus("Устранено");
        } else {
            toUpdate.setEndDateFact(defectiveActDTO.getEndDateFact());
            toUpdate.setDefectiveActStatus("Не устранено");
        }
        defectiveActRepo.save(toUpdate);

        return HttpStatus.OK;
    }

    public DefectiveActDTO findCommentByCommentId(Long id) {

        DefectiveAct defectiveAct = defectiveActRepo.findById(id).get();

        System.out.println(defectiveAct.getEndDatePlan());

        return createDto(defectiveAct);
    }

    public HttpStatus deleteDefectiveActById(Long id) {

        if(!defectiveActRepo.findById(id).isEmpty()) {
            defectiveActRepo.deleteById(id);
            return HttpStatus.OK;
        }
        return HttpStatus.NOT_FOUND;
    }


    private DefectiveActDTO createDto(DefectiveAct defectiveAct) {
        return DefectiveActDTO.builder()
                .codeCCS(defectiveAct.getCodeCCS())
                .defectiveActExplanation(defectiveAct.getDefectiveActExplanation())
                .defectiveActStatus(defectiveAct.getDefectiveActStatus())
                .equipment(defectiveAct.getEquipment())
                .defectiveActStatus(defectiveAct.getDefectiveActStatus())
                .executor(defectiveAct.getExecutor())
                .endDatePlan(defectiveAct.getEndDatePlan())
                .endDateFact(defectiveAct.getEndDateFact())
                .id(defectiveAct.getId())
                .iiNumber(defectiveAct.getIiNumber())
                .description(defectiveAct.getDescription())
                .startDate(defectiveAct.getStartDate())
                .serialNumber(defectiveAct.getSerialNumber())
                .subObject(defectiveAct.getSubObject())
                .systemName(defectiveAct.getSystemName())
                .manufacturer(defectiveAct.getManufacturer())
                .manufacturerNumber(defectiveAct.getManufacturerNumber())
                .build();
    }
}
