package com.cpstablet.tablet.service;

import com.cpstablet.tablet.entity.PNRSystem;
import com.cpstablet.tablet.entity.SubObject;
import com.cpstablet.tablet.repository.SubObjectRepo;
import com.cpstablet.tablet.repository.SystemRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SubObjectService {

    final SubObjectRepo subObjectRepo;
    final SystemRepo systemRepo;

    public List<SubObject> findAllCCSCode(String CCSCode) {
        return subObjectRepo.findByCCSCode(CCSCode);
    }

    public void checkStatus(Long systemID)  {

        System.out.println("here we are!");

       PNRSystem system = systemRepo.findByPNRSystemId(systemID);

        System.out.println("system " + system.getPNRSystemName());

       Set<String> statuses = systemRepo.getAllByCCSNumber(system.getCCSNumber()).stream().
               filter(s -> s.getPNRSystemKO().equals(system.getPNRSystemKO())).
               map(s -> s.getPNRSystemStatus()).collect(Collectors.toSet());

        System.out.println(statuses.size() + " statuses contains");

        SubObject toUpdate = subObjectRepo.findBYCCSCodeAndKONumber(system.getCCSNumber(), system.getPNRSystemKO());

        System.out.println("subobjectName " + toUpdate.getSubObjectName());

        if (statuses.contains("Принято в ПНР") || statuses.contains("Ведутся ПНР") || statuses.contains("Проведены ИИ")) {
            System.out.println("Ведутся ПНР");
            toUpdate.setStatus("Ведутся ПНР");
        }
        if(statuses.size() == 1 & statuses.contains("Акт ИИ подписан")) {
            toUpdate.setStatus("Акт ИИ подписан");
            System.out.println("Акт ИИ подписан");
        }
        if (statuses.size() == 1 & statuses.contains("Ведутся СМР")) {
            System.out.println("Ведутся СМР");
            toUpdate.setStatus("Ведутся СМР");
        }
        if(statuses.size() == 1 & statuses.contains("Предъявлено в ПНР"))   {
            System.out.println("Предъявлено в ПНР");
            toUpdate.setStatus("Предъявлено в ПНР");
        }
        if(statuses.size() == 1 & statuses.contains("Завершены СМР"))   {
            toUpdate.setStatus("Завершены СМР");
            System.out.println("Завершены СМР");
        }
        if((statuses.size() == 2 & statuses.contains("Завершены СМР")) & statuses.contains("Предъявлено в ПНР"))  {
            toUpdate.setStatus("Завершены СМР");
            System.out.println("Завершены СМР");
        }
        if (statuses.size() <= 3 & statuses.contains("Ведутся СМР") & (statuses.contains("Завершены СМР") && statuses.contains("Предъявлены в ПНР")))  {
            toUpdate.setStatus("Ведутся СМР");
            System.out.println("Ведутся СМР");
        }
        if(statuses.size() == 1 & statuses.stream().collect(Collectors.toList()).get(0).contains(" КО")) {
            toUpdate.setStatus(statuses.stream().collect(Collectors.toList()).get(0));
            System.out.println(statuses.stream().collect(Collectors.toList()).get(0));
        }
        subObjectRepo.save(toUpdate);
    }
}
