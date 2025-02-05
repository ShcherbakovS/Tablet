package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.commonDTO.ObjectCommonInfoDTO;
import com.cpstablet.tablet.DTO.commonDTO.SubobjectCommonInfDTO;
import com.cpstablet.tablet.DTO.commonDTO.SystemCommonInfDTO;
import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.entity.PNRSystem;
import com.cpstablet.tablet.entity.SubObject;
import com.cpstablet.tablet.repository.CommentRepo;
import com.cpstablet.tablet.repository.SubObjectRepo;
import com.cpstablet.tablet.repository.SystemRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommonInfoService {

    private final SubObjectRepo subObjectRepo;
    private final SystemRepo systemRepo;
    private final CommentRepo commentRepo;


    public ObjectCommonInfoDTO getObjectCommonInfo(String CCSNumber)  {

        List<PNRSystem> PNRSystems = systemRepo.getAllByCCSNumber(CCSNumber);

        List<Comment> comments = commentRepo.findCommentsByCodeCCS(CCSNumber);

        return ObjectCommonInfoDTO.builder().
                systemsPNRTotalQuantity(PNRSystems.stream().count()).

                systemsPNRQuantityAccepted(
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().equals("Принято в ПНР")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().equals("Ведутся ПНР")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().equals("Проведены ИИ")).count()
                        ).
                actsIITotalQuantity(
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().equals("Проведены ИИ")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().equals("Акт ИИ подписан")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().equals("Акт ИИ на подписи")).count()
                ).
                actsIISignedQuantity(
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().equals("Акт ИИ подписан")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().equals("Проведено КО")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().equals("Акт КО на подписи")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().equals("Проводится КО")).count()
                ).
                actsKOTotalQuantity(
                        PNRSystems.stream().count()
                ).
                actsKOSignedQuantity(
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().equals("Акт КО подписан")).count()
                ).
                commentsTotalQuantity(comments.stream().count()).
                commentsNotResolvedQuantity(comments.stream().filter(e-> e.getCommentStatus().equals("Не устранено")).count()).
                // нулевой показатель нет логики для дефектов
                defectiveActsTotalQuantity(0L).
                // нулевой показатель нет логики для дефектов
                defectiveActsNotResolvedQuantity(0L).
                // нулевой показатель нет логики для подсчета персонала
                busyStaff(0L).
                build();
    }
    public List<SubobjectCommonInfDTO> structureCommonInf(String CCSCode) {

        System.out.println(subObjectRepo.findByCCSCode(CCSCode));

        return subObjectRepo.findByCCSCode(CCSCode).stream().map(e-> getSubObjectCommonInfDTO(e)).collect(Collectors.toList());

    }
    private SubobjectCommonInfDTO getSubObjectCommonInfDTO(SubObject subObject) {


       List<SystemCommonInfDTO> systems = systemRepo.getAllByCCSNumber(subObject.getCCSCode()).
               stream().filter(e -> e.getPNRSystemKO().equals(subObject.getNumberKO())).map( e -> SystemCommonInfDTO.builder().
                       PNRSystemId(e.getPNRSystemId()).
                       numberII(e.getPNRSystemII()).
                       systemName(e.getPNRSystemName()).
                       status(e.getPNRSystemStatus()).
                       comments(commentRepo.findCommentsByCodeCCS(subObject.getCCSCode()).stream()
                               .filter(o -> o.getIiNumber() == Integer.parseInt(e.getPNRSystemII())).count()).
                       build()).collect(Collectors.toList());

       return SubobjectCommonInfDTO.builder().
               id(subObject.getSubObjectId()).
               numberKO(subObject.getNumberKO()).
               subObjectName(subObject.getSubObjectName()).
               comments(0L).
               status(subObject.getStatus()).
               data(systems).
               build();

    }
    public SystemCommonInfDTO getSystemCommonInfo(Long id) {

        PNRSystem PNRSystem = systemRepo.findByPNRSystemId(id);


        SystemCommonInfDTO sciDTO = SystemCommonInfDTO.builder().
                PNRSystemId(PNRSystem.getPNRSystemId()).
                CCSNumber(PNRSystem.getCCSNumber()).
                numberII(PNRSystem.getPNRSystemII()).
                systemName(PNRSystem.getPNRSystemName()).
                comments(commentRepo.findCommentsByCodeCCS(PNRSystem.getCCSNumber()).
                        stream().
                        filter(e-> e.getIiNumber() == Long.valueOf(PNRSystem.getPNRSystemII())).count()).
                status(PNRSystem.getPNRSystemStatus()).
                PNRPlanDate(PNRSystem.getPNRPlanDate()).
                PNRFactDate(PNRSystem.getPNRFactDate()).
                IIPlanDate(PNRSystem.getIIPlanDate()).
                IIFactDate(PNRSystem.getIIFactDate()).
                KOPlanDate(PNRSystem.getKOPlanDate()).
                KOFactDate(PNRSystem.getKOFactDate()).
                CIWExecutor(PNRSystem.getCIWExecutor()).
                CWExecutor(PNRSystem.getCWExecutor()).
                build();
        return sciDTO;


    }

}
