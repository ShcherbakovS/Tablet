package com.cpstablet.tablet.service;


import com.cpstablet.tablet.DTO.CapitalCSDTO;
import com.cpstablet.tablet.DTO.CapitalCSInfoDTO;
import com.cpstablet.tablet.entity.CapitalCS;
import com.cpstablet.tablet.entity.CapitalCSInfo;
import com.cpstablet.tablet.entity.User;
import com.cpstablet.tablet.repository.ApplicationRepo;
import com.cpstablet.tablet.repository.CapitalCSInfoRepo;
import com.cpstablet.tablet.repository.CapitalCSRepo;
import com.cpstablet.tablet.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class CapitalCSService {

    private final CapitalCSRepo capitalCSRepo;
    private final ApplicationRepo appRepo;
    private final UserRepo userRepo;
    private final CapitalCSInfoRepo capitalCSInfoRepo;


    public HttpStatus create(CapitalCSDTO capitalDTO) {

            CapitalCSInfo capitalCSInfo = capitalCSInfoRepo.save(new CapitalCSInfo());

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
                   capitalCSInfo(capitalCSInfo).
                    build());

            return HttpStatus.CREATED;

    }

    public CapitalCS findCCS(String codeCCS) {
        return capitalCSRepo.findByCodeCCS(codeCCS).orElseThrow(()-> new RuntimeException("Объекта с кодом "+ codeCCS + " не существует"));
    }
    public List<CapitalCS> findAll() {

        return capitalCSRepo.findAll();
    }

    public CapitalCS getUserApprovedCapitalCCS(Long userId) {
        return null;
    }
    public HttpStatus deleteCapitalCS(Long capitalCSId) {

        if(capitalCSRepo.findById(capitalCSId).isPresent()) {

            CapitalCS capitalCS = capitalCSRepo.findById(capitalCSId).orElseThrow(()-> new RuntimeException("Объект строительства не найден"));

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

        CapitalCS capital = capitalCSRepo.findById(id).orElseThrow(()-> new RuntimeException("Объекут не найден в системе"));

        if(!dto.getCapitalCSName().equals(" ")) capital.setCapitalCSName(dto.getCapitalCSName());
        if(!dto.getCIWExecutor().equals(" ")) capital.setCIWExecutor(dto.getCIWExecutor());
        if(!dto.getCIWSupervisor().equals(" ")) capital.setCIWSupervisor(dto.getCIWSupervisor());
        if(!dto.getCodeCCS().equals(" ")) capital.setCodeCCS(dto.getCodeCCS());
        if(!dto.getCustomer().equals(" ")) capital.setCustomer(dto.getCustomer());
        if(!dto.getCustomerSupervisor().equals(" ")) capital.setCustomerSupervisor(dto.getCustomerSupervisor());
        if(!dto.getCWExecutor().equals(" ")) capital.setCWExecutor(dto.getCWExecutor());
        if(!dto.getCWSupervisor().equals(" ")) capital.setCWSupervisor(dto.getCWSupervisor());
        if(!dto.getLocationRegion().equals(" ")) capital.setLocationRegion(dto.getLocationRegion());
        if(!dto.getObjectType().equals(" ")) capital.setObjectType(dto.getObjectType());

        capitalCSRepo.save(capital);

    }

    public List<CapitalCS> filteredByUserId(Long userId) {

        List<CapitalCS> userCapitals = userRepo.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("Пользователь с ID " + userId +" не найден"))
                .getAllowedObjects().stream().toList();

        List<CapitalCS> filteredCapitals = capitalCSRepo.findAll();
        if(userCapitals != null) {
            filteredCapitals.removeAll(userCapitals);
        }

        return filteredCapitals;
    }

    public void updateCapitalCSInfo(CapitalCSInfo dto, Long id) {

        CapitalCS capitalCS = capitalCSRepo.findById(id).orElseThrow(()-> new RuntimeException("Объект не найден"));

        CapitalCSInfo info = capitalCS.getCapitalCSInfo();

        info.setExecutiveDocsLink(dto.getExecutiveDocsLink());
        info.setOperationalDocsLink(dto.getOperationalDocsLink());
        info.setPreparatoryDocsLink(dto.getPreparatoryDocsLink());
        info.setWorkingDocsLink(dto.getWorkingDocsLink());

        capitalCSInfoRepo.save(info);

    }
}
