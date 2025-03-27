package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.PNRSystemDTO;
import com.cpstablet.tablet.entity.PNRSystem;
import com.cpstablet.tablet.repository.SystemRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SystemService {

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.mm.yyyy");
    @Value("${check.emptyValue}")
    static String checkValue;  //TODO: КОСТЫЛЬ с фронта для проверки даты, переписан в проперти
    private final SystemRepo systemRepo;
    private final CommentService commentService;
    private final SubObjectService subObjectService;

    public List<PNRSystemDTO> getSystemsByCCSCode (String codeCCS) {

        return systemRepo.getAllByCCSNumber(codeCCS).stream().map(system->
                PNRSystemDTO.builder().
                        PNRSystemName(system.getPNRSystemName()).
                        PNRSystemRD(system.getPNRSystemRD()).
                        PNRSystemII(system.getPNRSystemII()).
                        PNRSystemKO(system.getPNRSystemKO()).
                        CCSNumber(system.getCCSNumber()).
                        PNRSystemStatus(system.getPNRSystemStatus()).
                        PNRPlanDate((system.getPNRPlanDate() == null)? checkValue : system.getPNRPlanDate()).
                        PNRFactDate((system.getPNRFactDate() == null)? checkValue: system.getPNRFactDate()).
                        IIPlanDate((system.getIIPlanDate() == null)? checkValue : system.getIIPlanDate()).
                        IIFactDate((system.getIIFactDate() == null)? checkValue : system.getIIFactDate()).
                        KOPlanDate((system.getKOPlanDate() == null)? checkValue : system.getKOPlanDate()).
                        KOFactDate((system.getKOFactDate() == null)? checkValue : system.getKOFactDate()).
                        CIWExecutor(system.getCIWExecutor()).
                        CWExecutor(system.getCWExecutor()).
                        build()).collect(Collectors.toList());
    }

    public HttpStatus updateSystemInfo(PNRSystemDTO systemDTO, Long id) {

        System.out.println(systemDTO.toString());
        System.out.println("\n");

        PNRSystem toUpdate = systemRepo.findByPNRSystemId(id);

        toUpdate.setCWExecutor(systemDTO.getCWExecutor());

        commentService.checkExecutor(systemDTO.getCIWExecutor(), toUpdate.getPNRSystemII());

        //TODO: проверка дат- верхний статус не может быть заполнен при пустом нижнем- РПН->ИИ->КО

        toUpdate.setPNRSystemStatus(systemDTO.getPNRSystemStatus());

        toUpdate.setCIWExecutor(systemDTO.getCIWExecutor());

        toUpdate.setPNRPlanDate(systemDTO.getPNRPlanDate());
        toUpdate.setPNRFactDate(systemDTO.getPNRFactDate());

        toUpdate.setIIPlanDate(systemDTO.getIIPlanDate());
        toUpdate.setIIFactDate(systemDTO.getIIFactDate());

        toUpdate.setKOPlanDate(systemDTO.getKOPlanDate());
        toUpdate.setKOFactDate(systemDTO.getKOFactDate());

        systemRepo.save(toUpdate);

        System.out.println("Поле сущности в БД:");
        System.out.println(toUpdate.getPNRSystemStatus());


        subObjectService.checkStatus(id);


        return HttpStatus.OK;

    }

//    private void checkStatus(String status, PNRSystemDTO systemDTO, Long id)  {
//
//        PNRSystem system = systemRepo.findByPNRSystemId(id);
//
//        if(status.contains(" КО")) {
//            systemRepo.getAllByCCSNumber(system.getCCSNumber()).stream().filter(s-> s.getPNRSystemKO().equals(system.getPNRSystemKO())).
//                    forEach(s-> {s.setPNRSystemStatus(status);
//                        s.setKOPlanDate(systemDTO.getKOPlanDate());
//                        //проставить датц по всем системам КО
//                        s.setKOFactDate(systemDTO.getKOFactDate());
//                        systemRepo.save(system);});
//        }
//
//        subObjectService.checkStatus(id);
//    }
}


