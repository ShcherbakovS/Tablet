package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.ApplicationResponseDTO;
import com.cpstablet.tablet.DTO.CapitalCSDTO;
import com.cpstablet.tablet.entity.Application;
import com.cpstablet.tablet.entity.CapitalCS;
import com.cpstablet.tablet.entity.RegistrationApplication;
import com.cpstablet.tablet.entity.User;
import com.cpstablet.tablet.repository.ApplicationRepo;
import com.cpstablet.tablet.repository.RegistrationAppRepo;
import com.cpstablet.tablet.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ApplicationService {

    private final ApplicationRepo appRepo;

    private final RegistrationAppRepo regAppRepo;
    private final UserRepo userRepo;

    public ApplicationResponseDTO findById(Long id) {

        Application application = appRepo.findById(id).orElseThrow(()-> new RuntimeException("Заявка не найдена"));

        return buildAppRespDTO(application);
    }
    public ApplicationResponseDTO buildAppRespDTO(Application app) {

        User user = userRepo.findById(app.getUser().getId()).orElseThrow(()-> new UsernameNotFoundException("Пользователь не найден"));

        return ApplicationResponseDTO.builder()
                .id(app.getId())
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getUserInfo().getFullName())
                .role(user.getRole().name())
                .organisation(user.getUserInfo().getOrganisation())
                .description(app.getDescription())
                .objectsToAdd(app.getObjectsToAdd().stream().map(obj-> createDTO(obj) ).collect(Collectors.toList()))
                .addedObjects(app.getAddedObjects().isEmpty() ? null : app.getAddedObjects().stream().map(obj-> createDTO(obj) ).collect(Collectors.toList()))
                .description(app.getDescription())
                .creationTime(fromDateToString(app.getCreationTime()))
                .build();
    }
    public void changeObjectsList(Long id, List<CapitalCS> capitals) {

        Application application = appRepo.findById(id).orElseThrow(()-> new RuntimeException("Заявка не найдена"));

        if (!application.getObjectsToAdd().isEmpty()) {
            capitals.stream().forEach(o -> { application.getObjectsToAdd().remove(o);
                application.getAddedObjects().add(o);}
            );
        }
    }

    public List<RegistrationApplication> getRegApps() {
        return regAppRepo.findAll();
    }
    private CapitalCSDTO createDTO(CapitalCS capitalCSDTO) {

           return CapitalCSDTO.builder()
                .capitalCSName(capitalCSDTO.getCapitalCSName())
                .codeCCS(capitalCSDTO.getCodeCCS())
                .locationRegion(capitalCSDTO.getLocationRegion())
                .objectType(capitalCSDTO.getObjectType())
                .customer(capitalCSDTO.getCustomer())
                .CIWExecutor(capitalCSDTO.getCIWExecutor())
                .CWExecutor(capitalCSDTO.getCWExecutor())
                .customerSupervisor(capitalCSDTO.getCustomerSupervisor())
                .CWSupervisor(capitalCSDTO.getCWSupervisor())
                .CIWSupervisor(capitalCSDTO.getCIWSupervisor())
                .build();
    }
    private String fromDateToString(LocalDateTime sourceDate) {

        System.out.println(sourceDate.getDayOfMonth() + " ДЕНЬ МЕСЯЦА ПРОВЕРКА!!!" );

        StringBuilder buildDate = new StringBuilder();

        buildDate.append(sourceDate.getDayOfMonth()< 10?  "0" + sourceDate.getDayOfMonth(): sourceDate.getDayOfMonth())
                .append(".")
                .append(sourceDate.getMonthValue()< 10? "0" + sourceDate.getMonthValue(): sourceDate.getMonthValue())
                .append(".")
                .append(sourceDate.getYear());

        return buildDate.toString();
    }
}
