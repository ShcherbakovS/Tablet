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
                .objectsToAdd(app.getObjectsToAdd().stream().map(obj->
                        CapitalCSDTO.builder()
                                .capitalCSName(obj.getCapitalCSName())
                                .codeCCS(obj.getCodeCCS())
                                .locationRegion(obj.getLocationRegion())
                                .objectType(obj.getObjectType())
                                .customer(obj.getCustomer())
                                .CIWExecutor(obj.getCIWExecutor())
                                .CWExecutor(obj.getCWExecutor())
                                .customerSupervisor(obj.getCustomerSupervisor())
                                .CWSupervisor(obj.getCWSupervisor())
                                .CIWSupervisor(obj.getCIWSupervisor())
                                .build() ).collect(Collectors.toList()))
                .description(app.getDescription())
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
}
