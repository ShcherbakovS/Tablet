package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.PNRSystemDTO;
import com.cpstablet.tablet.entity.PNRSystem;
import com.cpstablet.tablet.entity.SubObject;
import com.cpstablet.tablet.repository.SubObjectRepo;
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
    static String checkValue;
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

    public void updateSystemInfo(PNRSystemDTO systemDTO, Long id) {

        System.out.println(systemDTO.toString());
        System.out.println("\n");

        PNRSystem toUpdate = systemRepo.findByPNRSystemId(id);

        toUpdate.setCWExecutor(systemDTO.getCWExecutor());

        commentService.checkExecutor(systemDTO.getCIWExecutor(), toUpdate.getPNRSystemII());

        //TODO: проверка дат- верхний статус не может быть заполнен при пустом нижнем- РПН->ИИ->КО


        toUpdate.setCIWExecutor(systemDTO.getCIWExecutor());

        toUpdate.setPNRPlanDate(systemDTO.getPNRPlanDate());
        toUpdate.setPNRFactDate(systemDTO.getPNRFactDate());

        toUpdate.setIIPlanDate(systemDTO.getIIPlanDate());
        toUpdate.setIIFactDate(systemDTO.getIIFactDate());

        toUpdate.setKOPlanDate(systemDTO.getKOPlanDate());
        toUpdate.setKOFactDate(systemDTO.getKOFactDate());



        if(toUpdate.getPNRSystemStatus().contains(" КО ") && !systemDTO.getPNRSystemStatus().contains(" КО ")) {
            systemRepo.getAllByCCSNumber(toUpdate.getCCSNumber()).stream().filter(sys-> sys.getPNRSystemKO().equals(toUpdate.getPNRSystemKO())).forEach(s-> {s.setKOPlanDate(systemDTO.getKOPlanDate());
                s.setKOFactDate(systemDTO.getKOFactDate());
                s.setKOPlanDate(systemDTO.getKOPlanDate());
                s.setPNRSystemStatus(getSystemStatus(s));

                systemRepo.save(s);

            });

        } else if (systemDTO.getPNRSystemStatus().contains(" КО ")) {
            systemRepo.getAllByCCSNumber(toUpdate.getCCSNumber())
                    .stream().filter(sys-> sys.getPNRSystemKO().equals(toUpdate.getPNRSystemKO())).forEach(s -> {
                s.setKOPlanDate(systemDTO.getKOPlanDate());
                s.setKOFactDate(systemDTO.getKOFactDate());
                s.setPNRSystemStatus(systemDTO.getPNRSystemStatus());
                systemRepo.save(s);
            });
        } else {
            toUpdate.setPNRSystemStatus(systemDTO.getPNRSystemStatus());
        }

        systemRepo.save(toUpdate);

        checkStatus(id);

    }

    public void checkStatus(Long id)  {

        subObjectService.checkStatus(id);
    }
    public String getSystemStatus(PNRSystem pnrSystem) {

        if(pnrSystem.getKOFactDate() != null && !pnrSystem.getKOFactDate().equals(" ")) {
            return "Акт КО подписан";
        }
        if (pnrSystem.getKOFactDate().equals(" ") && !pnrSystem.getIIFactDate().equals(" ")) {
            return "Акт ИИ подписан";
        }
        if ((pnrSystem.getKOFactDate().equals(" ") && pnrSystem.getIIFactDate().equals(" ")) && !pnrSystem.getPNRFactDate().equals(" ")) {
            return "Принято в ПНР";
        }

        return pnrSystem.getPNRSystemStatus() ;
    }
}


