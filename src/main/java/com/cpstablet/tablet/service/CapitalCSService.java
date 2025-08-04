package com.cpstablet.tablet.service;


import com.cpstablet.tablet.DTO.CapitalCSDTO;
import com.cpstablet.tablet.DTO.CapitalCSInfoDTO;
import com.cpstablet.tablet.DTO.OrganisationDTO;
import com.cpstablet.tablet.entity.CapitalCS;
import com.cpstablet.tablet.entity.CapitalCSInfo;
import com.cpstablet.tablet.repository.ApplicationRepo;
import com.cpstablet.tablet.repository.CapitalCSInfoRepo;
import com.cpstablet.tablet.repository.CapitalCSRepo;
import com.cpstablet.tablet.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CapitalCSService {

    private final CapitalCSRepo capitalCSRepo;
    private final ApplicationRepo appRepo;
    private final UserRepo userRepo;
    private final CalendarService calendarService;

    public HttpStatus create(CapitalCSDTO capitalDTO) {

        if(capitalDTO.getCodeCCS() == null || capitalDTO.getCodeCCS().equals("")) {
            throw new RuntimeException("Создание нового ОКС: пустой код ОКС");
        }

         capitalCSRepo.save(CapitalCS.builder().
                    capitalCSName(capitalDTO.getCapitalCSName()).
                    codeCCS(capitalDTO.getCodeCCS()).
                    locationRegion(capitalDTO.getLocationRegion()).
                    objectType(capitalDTO.getObjectType()).
                    customer(capitalDTO.getCustomer()).
                    CIWExecutor(capitalDTO.getCIWExecutor()).
                    CWExecutor(capitalDTO.getCWExecutor()).
                    customerSupervisor(capitalDTO.getCustomerSupervisor()).
                    CWSupervisor(capitalDTO.getCWSupervisor()).
                    CIWSupervisor(capitalDTO.getCIWSupervisor()).
                    commentCounter(1L).
                    defectiveActCounter(1L).
                    journalEntryCounter(1L).
                    capitalCSInfo(new CapitalCSInfo()).
                    build());

        calendarService.createCalendarDays(LocalDate.now(), capitalDTO.getCodeCCS());

        return HttpStatus.CREATED;

    }

    public CapitalCSDTO findCCS(String codeCCS) {

        capitalCSRepo.findByCodeCCS(codeCCS).orElseThrow(()-> new RuntimeException("Объекта с кодом "+ codeCCS + " не существует"));

        return createDTO(capitalCSRepo.findByCodeCCS(codeCCS).orElseThrow(()-> new RuntimeException("Объекта с кодом "+ codeCCS + " не существует")));


    }
    public List<CapitalCSDTO> findAll() {

        return capitalCSRepo.findAll().stream()
                .sorted(Comparator.comparing(CapitalCS::getCapitalCSName))
                .map(capitalCS -> createDTO(capitalCS)).
                collect(Collectors.toList());
    }


    public HttpStatus deleteCapitalCS(Long id) {

        if(capitalCSRepo.findById(id).isPresent()) {

            CapitalCS capitalCS = capitalCSRepo.findById(id).orElseThrow(()-> new RuntimeException("Объект строительства не найден"));

            userRepo.findAll().stream().forEach(user-> user.getAllowedObjects().remove(capitalCS));
            appRepo.findAll().stream().forEach(app-> {
                app.getAddedObjects().remove(capitalCS);
                app.getObjectsToAdd().remove(capitalCS);
            });

            capitalCSRepo.delete(capitalCS);

            return HttpStatus.OK;
        }

        return HttpStatus.NOT_FOUND;
    }

    public void  update(CapitalCSDTO dto, Long id) {

        CapitalCS capital = capitalCSRepo.findById(id).orElseThrow(()-> new RuntimeException("Объект не найден в системе"));

        if(!dto.getCapitalCSName().equals(" "))capital.setCapitalCSName(dto.getCapitalCSName());
        if(!dto.getCIWExecutor().equals(" "))capital.setCIWExecutor(dto.getCIWExecutor());
        if(!dto.getCIWSupervisor().equals(" "))capital.setCIWSupervisor(dto.getCIWSupervisor());
        if(!dto.getCodeCCS().equals(" "))capital.setCodeCCS(dto.getCodeCCS());
        if(!dto.getCustomer().equals(" "))capital.setCustomer(dto.getCustomer());
        if(!dto.getCustomerSupervisor().equals(" "))capital.setCustomerSupervisor(dto.getCustomerSupervisor());
        if(!dto.getCWExecutor().equals(" "))capital.setCWExecutor(dto.getCWExecutor());
        if(!dto.getCWSupervisor().equals(" "))capital.setCWSupervisor(dto.getCWSupervisor());
        if(!dto.getLocationRegion().equals(" "))capital.setLocationRegion(dto.getLocationRegion());
        if(!dto.getObjectType().equals(" "))capital.setObjectType(dto.getObjectType());

        capitalCSRepo.save(capital);

    }

    public List<CapitalCSDTO> filteredByUserId(Long userId) {

        List<CapitalCSDTO> userCapitals = userRepo.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("Пользователь с ID " + userId +" не найден"))
                .getAllowedObjects().stream().sorted(Comparator.comparing(CapitalCS::getCapitalCSName)).map(capitalCS -> createDTO(capitalCS)).toList();

        List<CapitalCSDTO> filteredCapitals = findAll();

        if(userCapitals != null) {
            filteredCapitals.removeAll(userCapitals);
        }

        return filteredCapitals;
    }

    public void updateCapitalCSInfo(CapitalCSInfo dto, String codeCCS) {

        CapitalCS capitalCS = capitalCSRepo.findByCodeCCS(codeCCS)
                .orElseThrow(() -> new RuntimeException("Объект не найден"));

        CapitalCSInfo info = capitalCS.getCapitalCSInfo();

        if(info == null) {
            info = new CapitalCSInfo();
            info.setCapitalCS(capitalCS);
            capitalCS.setCapitalCSInfo(info);

            info.setOperationalDocsLink(dto.getOperationalDocsLink());
            info.setPreparatoryDocsLink(dto.getPreparatoryDocsLink());
            info.setWorkingDocsLink(dto.getWorkingDocsLink());
            info.setExecutiveDocsLink(dto.getExecutiveDocsLink());

            capitalCS.setCapitalCSInfo(info);

        } else {
            info.setExecutiveDocsLink(dto.getExecutiveDocsLink());
            info.setOperationalDocsLink(dto.getOperationalDocsLink());
            info.setPreparatoryDocsLink(dto.getPreparatoryDocsLink());
            info.setWorkingDocsLink(dto.getWorkingDocsLink());
        }

        capitalCSRepo.save(capitalCS);
    }
    private CapitalCSDTO createDTO(CapitalCS capitalCS) {

        return CapitalCSDTO.builder()
                .capitalCSId(capitalCS.getCapitalCSId())
                .capitalCSName(capitalCS.getCapitalCSName())
                .CIWExecutor(capitalCS.getCIWExecutor())
                .CIWSupervisor(capitalCS.getCIWSupervisor())
                .codeCCS(capitalCS.getCodeCCS())
                .customer(capitalCS.getCustomer())
                .customerSupervisor(capitalCS.getCustomerSupervisor())
                .CWExecutor(capitalCS.getCWExecutor())
                .CWSupervisor(capitalCS.getCWSupervisor())
                .locationRegion(capitalCS.getLocationRegion())
                .objectType(capitalCS.getObjectType())
                .capitalCSInfoDTO(capitalCS.getCapitalCSInfo()==null? CapitalCSInfoDTO.builder().build() :
                        CapitalCSInfoDTO.builder()
                                .executiveDocsLink(capitalCS.getCapitalCSInfo().getExecutiveDocsLink())
                                .operationalDocsLink(capitalCS.getCapitalCSInfo().getOperationalDocsLink())
                                .preparatoryDocsLink(capitalCS.getCapitalCSInfo().getPreparatoryDocsLink())
                                .workingDocsLink(capitalCS.getCapitalCSInfo().getWorkingDocsLink())
                                .build())
                .build();
    }
}
