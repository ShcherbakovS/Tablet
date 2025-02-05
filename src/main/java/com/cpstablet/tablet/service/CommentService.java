package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.CommentDTO;
import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.repository.CommentRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

    static final String DATA_FORMAT = "dd.MM.yyyy";

    private final CommentRepo commentRepo;
    public Comment create(CommentDTO comDTO) {

        LocalDate startDate = LocalDate.parse(comDTO.getStartDate(), DateTimeFormatter.ofPattern(DATA_FORMAT));

        return commentRepo.save(Comment.builder().
                serialNumber(commentRepo.count() + 1).
                iiNumber(comDTO.getIiNumber()).
                subObject(comDTO.getSubObject()).
                systemName(comDTO.getSystemName()).
                description(comDTO.getDescription()).
                commentStatus(comDTO.getCommentStatus()).
                userName(comDTO.getUserName()).
                startDate(startDate).
                endDatePlan(startDate.plusDays(10) ).
                endDateFact(null).
                commentCategory(comDTO.getCommentCategory()).
                commentExplanation(comDTO.getCommentExplanation()).
                codeCCS(comDTO.getCodeCCS()).
                build());

    }
    public List<Comment> getAllComments() {
        return commentRepo.findAll();
    }
//TODO: внесение изменений в существующее замечание проверить

    public HttpStatus update(CommentDTO comDTO, Long id) {
       Comment toUpdate = commentRepo.findCommentByCommentId(id);

       LocalDate endDateFact = LocalDate.parse(comDTO.getEndDateFact(), DateTimeFormatter.ofPattern(DATA_FORMAT));

        toUpdate.setDescription(comDTO.getDescription());
        toUpdate.setCommentStatus(comDTO.getCommentStatus());
        toUpdate.setEndDateFact(endDateFact);
        toUpdate.setCommentCategory(comDTO.getCommentCategory());

         if (endDateFact.isAfter(toUpdate.getEndDatePlan())) {
             toUpdate.setCommentStatus("Устранено с просрочкой");
        }

        commentRepo.save(toUpdate);
        return HttpStatus.OK;
    }
    public Comment findCommentByCommentId(Long id) {
        return commentRepo.findCommentByCommentId(id);
    }
    public HttpStatus deleteCommentById(Long id) {

        if(!commentRepo.findByCommentId(id).isEmpty()) {
            commentRepo.deleteById(id);
            return HttpStatus.OK;
        }
        return HttpStatus.NOT_FOUND;
    }


    // метод пересчета порядковых номеров
//    private void replaceSerialNumbers() {
//
//        long counter = 1;
//
//        for ( var comment : commentRepo.findAll() ) {
//            comment.setSerialNumber(counter);
//            commentRepo.save(comment);
//            counter++;
//        }
//
//
//
//
//    }


}
