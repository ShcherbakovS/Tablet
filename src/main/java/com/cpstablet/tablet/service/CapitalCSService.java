package com.cpstablet.tablet.service;


import com.cpstablet.tablet.DTO.CapitalCSDTO;
import com.cpstablet.tablet.entity.CapitalCS;
import com.cpstablet.tablet.entity.User;
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

    private final UserRepo userRepo;


    public HttpStatus create(CapitalCSDTO capitalDTO) {


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
            capitalCSRepo.deleteById(capitalCSId);

            return HttpStatus.OK;
        }

        return HttpStatus.NOT_FOUND;
    }

    public void  update(CapitalCSDTO readValue, Long id) {

        capitalCSRepo.save(CapitalCS.builder().
            capitalCSId(id).
            locationRegion(readValue.getLocationRegion()).
            objectType(readValue.getObjectType()).
            customer(readValue.getCustomer()).
                CIWExecutor(readValue.getCIWExecutor()).
                CWExecutor(readValue.getCWExecutor()).
                build());

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
}
