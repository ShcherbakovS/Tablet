package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.CommentDTO;
import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.repository.CommentRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    private final CommentRepo commentRepo;


    public Comment create(CommentDTO comDTO) {

        LocalDate startDate = LocalDate.parse(comDTO.getStartDate(), formatter);
        System.out.println(startDate);


        Long commentCounter = commentRepo.findCommentsByCodeCCS(comDTO.getCodeCCS()).stream().count();

        return commentRepo.save(Comment.builder().
                serialNumber(commentCounter + 1 ).
                iiNumber(comDTO.getIiNumber()).
                subObject(comDTO.getSubObject()).
                systemName(comDTO.getSystemName()).
                description(comDTO.getDescription()).
                commentStatus(comDTO.getEndDateFact().equals(" ")? "Не устранено" : "Устранено").
                executor(comDTO.getExecutor()).
                userName(comDTO.getUserName()).
                startDate(comDTO.getStartDate()).
                //TODO: без проверки тупо пишем то что прилетело с фронта
                endDatePlan(fromDateToString(LocalDate.parse(comDTO.getStartDate(), formatter).plusDays(10).toString())).
                // TODO: нужна ли проверка на пустую строку?

                endDateFact((comDTO.getEndDateFact().equals(" "))? " " : comDTO.getEndDateFact()).


                commentCategory(comDTO.getCommentCategory()).
                commentExplanation(comDTO.getCommentExplanation()).
                codeCCS(comDTO.getCodeCCS()).
                build());


    }
    public List<CommentDTO> getAllComments(String codeCCS) {

            return commentRepo.findCommentsByCodeCCS(codeCCS).stream().map(comment-> CommentDTO.builder().
                commentId(comment.getCommentId()).
                commentCategory(comment.getCommentCategory()).
                commentExplanation(comment.getCommentExplanation()).
                commentStatus(comment.getCommentStatus()).
                executor(comment.getExecutor()).
                description(comment.getDescription()).
                endDateFact(comment.getEndDateFact()).
                endDatePlan(comment.getEndDatePlan()).
                startDate(comment.getStartDate()).
                serialNumber(comment.getSerialNumber()).
                subObject(comment.getSubObject()).
                systemName(comment.getSystemName()).
                iiNumber(comment.getIiNumber()).
                build()).sorted(Comparator.comparing(CommentDTO::getSerialNumber)).collect(Collectors.toList());

    }

    public HttpStatus update(CommentDTO comDTO, Long id) {

        Comment toUpdate = commentRepo.findCommentByCommentId(id);

        //TODO: логика дат при перезаписи корректировка

        toUpdate.setEndDatePlan(comDTO.getEndDatePlan());
        toUpdate.setStartDate(comDTO.getStartDate());

        toUpdate.setDescription(comDTO.getDescription());
        toUpdate.setIiNumber(comDTO.getIiNumber());
        toUpdate.setExecutor(comDTO.getExecutor());
        toUpdate.setSubObject(comDTO.getSubObject());
        toUpdate.setSystemName(comDTO.getSystemName());
        toUpdate.setCommentExplanation(comDTO.getCommentExplanation());
        toUpdate.setCommentCategory(comDTO.getCommentCategory());


        if(!comDTO.getEndDateFact().equals(" ")) {
            toUpdate.setEndDateFact(comDTO.getEndDateFact());
            toUpdate.setCommentStatus("Устранено");
         } else {
            toUpdate.setEndDateFact(comDTO.getEndDateFact());
            toUpdate.setCommentStatus("Не устранено");
        }
        commentRepo.save(toUpdate);
        return HttpStatus.OK;
    }

    public CommentDTO findCommentByCommentId(Long id) {

        Comment comment = commentRepo.findCommentByCommentId(id);

        System.out.println(comment.getEndDatePlan());

        return CommentDTO.builder().
                commentId(comment.getCommentId()).
                codeCCS(comment.getCodeCCS()).
                commentCategory(comment.getCommentCategory()).
                commentExplanation(comment.getCommentExplanation()).
                commentStatus(comment.getCommentStatus()).
                executor(comment.getExecutor()).
                description(comment.getDescription()).
                endDateFact(comment.getEndDateFact()).
                endDatePlan(comment.getEndDatePlan()).
                startDate(comment.getStartDate()).
                serialNumber(comment.getSerialNumber()).
                subObject(comment.getSubObject()).
                systemName(comment.getSystemName()).
                iiNumber(comment.getIiNumber()).
                build();
    }
    public HttpStatus deleteCommentById(Long id) {

        if(!commentRepo.findByCommentId(id).isEmpty()) {
            commentRepo.deleteById(id);
            return HttpStatus.OK;
        }
        return HttpStatus.NOT_FOUND;
    }
    private String fromDateToString(String date) {

       var sourceDate =  LocalDate.parse(date);

        System.out.println(sourceDate.getDayOfMonth() + " ДЕНЬ МЕСЯЦА ПРОВЕРКА!!!" );

       StringBuilder buildDate = new StringBuilder();

       //TODO: ноль пишется пр редактировании
       buildDate.append(sourceDate.getDayOfMonth()< 10?  "0" + sourceDate.getDayOfMonth(): sourceDate.getDayOfMonth())
               .append(".")
               .append(sourceDate.getMonthValue()< 10? "0" + sourceDate.getMonthValue(): sourceDate.getMonthValue())
               .append(".")
               .append(sourceDate.getYear());

       return buildDate.toString();
    }
    public void checkExecutor(String CIWExecutor, String IINumber) {

        if (commentRepo.findCommentByIiNumber(IINumber).isEmpty()) {
            System.out.println("Список замечаний пуст");
            return;
        }
        System.out.println(CIWExecutor + " Исполнитель по пуско-наладке");

        commentRepo.findCommentByIiNumber(IINumber).stream().forEach(com -> com.setExecutor(CIWExecutor));
    }
}
