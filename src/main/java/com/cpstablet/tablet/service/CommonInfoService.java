package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.commonInfoDTO.ObjectCommonInfoDTO;
import com.cpstablet.tablet.DTO.commonInfoDTO.SubobjectCommonInfDTO;
import com.cpstablet.tablet.DTO.commonInfoDTO.SystemCommonInfDTO;
import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.entity.PNRSystem;
import com.cpstablet.tablet.entity.SubObject;
import com.cpstablet.tablet.repository.CommentRepo;
import com.cpstablet.tablet.repository.SubObjectRepo;
import com.cpstablet.tablet.repository.SystemRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ofPattern;

@Service
@AllArgsConstructor
public class CommonInfoService {

    private final SubObjectRepo subObjectRepo;
    private final SystemRepo systemRepo;
    private final CommentRepo commentRepo;

    public ObjectCommonInfoDTO getObjectCommonInfo(String CCSNumber)  {

        List<PNRSystem> PNRSystems = systemRepo.getAllByCCSNumber(CCSNumber);
        List<SubObject> subObjects = subObjectRepo.findByCCSCode(CCSNumber);
        List<Comment> comments = commentRepo.findCommentsByCodeCCS(CCSNumber);

        //TODO: динамику рассчитывать за семь дней, возможно добавить фильтрацию по мониторингу дат (текущая - 7)

        ObjectCommonInfoDTO dto = ObjectCommonInfoDTO.builder().
                // принято в ПНР всего
                systemsPNRTotalQuantity(
                        (long) PNRSystems.size()
                ).
                // принято в ПНР подписано
                        //{ label: 'Акт КО на подписи', value: 'Акт КО на подписи' },
                systemsPNRQuantityAccepted(
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().contains("Принято в ПНР")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().contains("Ведутся ПНР")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().contains("Проведены ИИ")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().contains("Акт ИИ подписан")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().contains("Акт ИИ на подписи")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().contains("Проведено КО")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().contains("Акт КО на подписи")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().contains("Акт КО подписан")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().contains("Проводится КО")).count()
                        ).
                systemsPNRDynamic(PNRSystems.stream().filter(s-> checkDateToPeriod(s.getPNRFactDate()))
                        .count()).
                //актов ИИ всего
                actsIITotalQuantity((long) PNRSystems.size()
                ).
                // актов ИИ подписано
                actsIISignedQuantity(
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().contains("Акт ИИ подписан")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().contains("Проведено КО")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().contains("Акт КО на подписи")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().contains("Проводится КО")).count() +
                        PNRSystems.stream().filter(e-> e.getPNRSystemStatus().contains("Акт КО подписан")).count()
                ).
                actsIIDynamic( PNRSystems.stream().filter(s-> checkDateToPeriod(s.getIIFactDate()))
                        .count()).
                // АКТЫ КО ПОДСЧЕТ ПО НЕ ПО СИСТЕМАМ А ПОПОДЪОБЪЕКТАМ СТАТУСЫ ПЕРЕПРОВЕРИТЬ
                actsKOTotalQuantity(
                        subObjects.stream().map(sub-> sub.getNumberKO()).collect(Collectors.toSet()).stream().count()
                ).
                // актов КО подписано

                actsKOSignedQuantity(
                        subObjects.stream().filter(e-> e.getStatus().contains("Акт КО подписан")).count()
                        // все системы подобъекта имеют статус "Акт КО подписан"
                ).
                // динамика по КО неверная
                actsKODynamic(systemRepo.getAllByStatus(CCSNumber, "Акт КО подписан").stream().filter(s-> checkDateToPeriod(s.getKOFactDate())).
                        count()).

                commentsTotalQuantity(comments.stream().count()).
                commentsNotResolvedQuantity(comments.stream().filter(e-> e.getCommentStatus().contains("Не устранено")).count()).
                commentsDynamic(comments.stream().filter(com-> checkDateToPeriod(com.getEndDateFact())).count()).

                systemsLag(systemsLagCount(PNRSystems)).
                // нулевой показатель нет логики для дефектов
                defectiveActsTotalQuantity(0L).
                // нулевой показатель нет логики для дефектов
                defectiveActsNotResolvedQuantity(0L).
                // нулевой показатель нет логики для подсчета персонала
                busyStaff(0L).
                build();

        dto.setSystemsLag(systemsLagCount(PNRSystems));
        dto.setActsIILag(actsIILagCount(PNRSystems));
        dto.setActsKOLag(actsKOLagCount(subObjects));
        dto.setCommentsLag(commentsLagCount(comments));

        return dto;
    }

    public List<SubobjectCommonInfDTO> structureCommonInf(String CCSCode) {

       subObjectRepo.findByCCSCode(CCSCode).stream().map(e-> getSubObjectCommonInfDTO(e))
                .forEach(System.out::println);

        return subObjectRepo.findByCCSCode(CCSCode).stream().map(e-> getSubObjectCommonInfDTO(e)).sorted(
                Comparator.comparing(SubobjectCommonInfDTO::getId)).collect(Collectors.toList());

    }
    private SubobjectCommonInfDTO getSubObjectCommonInfDTO(SubObject subObject) {

        // TODO ощибка в формировании списка- формировать через связанные сущности подобъекта, а не акта КО напрямую получаем дублирование в списке

       List<SystemCommonInfDTO> systems = subObject.getPNRSystems().
               stream().filter(e -> e.getPNRSystemKO().equals(subObject.getNumberKO())).map( e -> SystemCommonInfDTO.builder().
                       PNRSystemId(e.getPNRSystemId()).
                       numberII(e.getPNRSystemII()).
                       systemName(e.getPNRSystemName()).
                       CIWExecutor(e.getCIWExecutor()).
                       CWExecutor(e.getCWExecutor()).
                       status(e.getPNRSystemStatus()).
                       comments(commentRepo.findCommentsByCodeCCS(subObject.getCCSCode()).stream()
                               .filter(o -> o.getIiNumber().equals(e.getPNRSystemII())).filter(o-> o.getCommentStatus().contains("Не устранено")).count()).
                       build()).sorted(Comparator.comparing(SystemCommonInfDTO::getPNRSystemId)).collect(Collectors.toList());


       return SubobjectCommonInfDTO.builder().
               id(subObject.getSubObjectId()).
               numberKO(subObject.getNumberKO()).
               subObjectName(subObject.getSubObjectName()).
               comments(commentRepo.findCommentsByCodeCCS(subObject.getCCSCode()).stream().
                       filter(comment-> comment.getSubObject().equals(subObject.getSubObjectName())).
                       filter(comment -> comment.getCommentStatus().contains("Не устранено")).count()).
               status(subObject.getStatus()).
               data(systems).
               build();

    }
    public SystemCommonInfDTO getSystemCommonInfo(Long id) {

        PNRSystem PNRSystem = systemRepo.findByPNRSystemId(id);

        return SystemCommonInfDTO.builder().
                PNRSystemId(PNRSystem.getPNRSystemId()).
                CCSNumber(PNRSystem.getCCSNumber()).
                numberII(PNRSystem.getPNRSystemII()).
                systemName(PNRSystem.getPNRSystemName()).
                comments(commentRepo.findCommentsByCodeCCS(PNRSystem.getCCSNumber()).
                        stream().
                        filter(e-> e.getIiNumber().equals(PNRSystem.getPNRSystemII())).
                filter(comm-> comm.getCommentStatus().contains("Не устранено")).count()).
                status(PNRSystem.getPNRSystemStatus()).
                PNRPlanDate((PNRSystem.getPNRPlanDate() == null)? " " : PNRSystem.getPNRPlanDate()).
                PNRFactDate((PNRSystem.getPNRFactDate() == null)? " " : PNRSystem.getPNRFactDate()).
                IIPlanDate((PNRSystem.getIIPlanDate() == null)? " " : PNRSystem.getIIPlanDate()).
                IIFactDate((PNRSystem.getIIFactDate() == null)? " " :PNRSystem.getIIFactDate()).
                KOPlanDate((PNRSystem.getKOPlanDate() == null)? " " : PNRSystem.getKOPlanDate()).
                KOFactDate((PNRSystem.getKOFactDate() == null)? " " : PNRSystem.getKOFactDate()).
                CIWExecutor(PNRSystem.getCIWExecutor()).
                CWExecutor(PNRSystem.getCWExecutor()).
                build();
    }
    private String formatDate(String dateFromDB) {

        LocalDate date = LocalDate.parse(dateFromDB);
        StringBuilder buildDate = new StringBuilder();
        buildDate.append(date.getDayOfMonth()).append(".");
        if (date.getMonthValue() <= 9) {
            buildDate.append("0").append(date.getMonthValue());
        } else {
            buildDate.append(date.getMonthValue());
        }
        buildDate.append(".").append(date.getYear());

        return buildDate.toString();

    }

    private boolean checkDateToPeriod(String date) {

        if(date.equals(" ")) {
            return false;
        }

        String[] valuesFromDate = date.split("[/\\\\.,]");

        LocalDate sourceDate = LocalDate.of(Integer.valueOf(valuesFromDate[2]), Integer.valueOf(valuesFromDate[1]),
                Integer.valueOf(valuesFromDate[0]));

        LocalDate nowDate = LocalDate.now();


        if(sourceDate.isBefore(nowDate) & sourceDate.isAfter(nowDate.minusDays(7) )) {
            System.out.println("true");
            return true;
        }

        return false;
     }
    private boolean dateLag(String sourceDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        LocalDate date = LocalDate.parse(sourceDate, formatter);

        return date.isBefore(LocalDate.now());

    }
    // отставание от плана
    private Long systemsLagCount(List<PNRSystem> systems) {

        return systems.stream().filter(s-> !s.getPNRPlanDate().equals(" ")).filter(s-> s.getPNRFactDate().equals(" "))
                .filter(s-> dateLag(s.getPNRPlanDate())).count();
    }
    private Long actsIILagCount(List<PNRSystem> systems) {

        return systems.stream().filter(s-> !s.getIIPlanDate().equals(" ")).filter(s-> s.getIIPlanDate().equals(" "))
                .filter(s-> dateLag(s.getIIPlanDate())).count();
    }
    private Long commentsLagCount(List<Comment> comments) {

        return comments.stream().filter(comment-> !comment.getEndDatePlan().equals(" ")).filter(comment-> comment.getEndDateFact().equals(" "))
                .filter(comment-> dateLag(comment.getEndDatePlan())).count();
    }
    private Long actsKOLagCount(List<SubObject> subObjects) {
        if(!subObjects.isEmpty()) {
            return subObjects.stream().map(sub -> sub.getPNRSystems().get(0)).filter(sys -> !sys.getKOPlanDate().equals(" "))
                    .filter(sys -> dateLag(sys.getKOPlanDate())).count() - subObjects.stream().map(sub -> sub.getPNRSystems().get(0))
                    .filter(sys -> !sys.getKOFactDate().equals(" ")).filter(sys -> dateLag(sys.getKOFactDate())).count();
        }
        System.out.println("Метод рассчета лага КО получил пустую коллекцию");

        return 0L;
    }
}
