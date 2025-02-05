package com.cpstablet.tablet.service;


import com.cpstablet.tablet.DTO.CapitalCSDTO;
import com.cpstablet.tablet.entity.CapitalCS;
import com.cpstablet.tablet.repository.CapitalCSRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@AllArgsConstructor
public class CapitalCSService {

    private final CapitalCSRepo capitalCSRepo;


    public CapitalCS create(CapitalCSDTO capitalDTO) {

        return capitalCSRepo.save(CapitalCS.builder().
                        capitalCSName(capitalDTO.getCapitalCSName()).
                        codeCCS(capitalDTO.getCodeCCS()).
                        locationRegion(capitalDTO.getLocationRegion()).
                        objectType(capitalDTO.getObjectType()).
                        customer(capitalDTO.getCustomer()).
                        executorOfPNR(capitalDTO.getExecutorOfPNR()).
                        build());
    }

    public CapitalCS findCCS(String codeCCS) {
        return capitalCSRepo.findByCodeCCS(codeCCS);
    }
    public List<CapitalCS> findAll() {

        return capitalCSRepo.findAll();
    }

    public CapitalCS getUserApprovedCapitalCCS(Long userId) {
        return null;
    }
}
