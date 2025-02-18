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
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final CommentRepo commentRepo;

    public Comment create(CommentDTO comDTO) {

        LocalDate startDate = LocalDate.parse(comDTO.getStartDate(), formatter);

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
    public List<CommentDTO> getAllComments() {

            return commentRepo.findAll().stream().map(comment-> CommentDTO.builder().
                commentCategory(comment.getCommentCategory()).
                commentExplanation(comment.getCommentExplanation()).
                commentStatus(comment.getCommentStatus()).
                description(comment.getDescription()).
                endDateFact(comment.getEndDateFact().format(formatter)).
                endDatePlan(comment.getEndDateFact().format(formatter)).
                startDate(comment.getStartDate().format(formatter)).
                serialNumber(comment.getSerialNumber()).
                subObject(comment.getSubObject()).
                systemName(comment.getSystemName()).
                iiNumber(comment.getIiNumber()).
                build()).collect(Collectors.toList());
    }

    public HttpStatus update(CommentDTO comDTO, Long id) {

        Comment toUpdate = commentRepo.findCommentByCommentId(id);

        toUpdate.setDescription(comDTO.getDescription());

        if(comDTO.getEndDateFact() != null) {
            toUpdate.setEndDateFact(LocalDate.parse(comDTO.getEndDateFact(), formatter));
            if (LocalDate.parse(comDTO.getEndDateFact(),formatter).isAfter(toUpdate.getEndDatePlan())) {
                toUpdate.setCommentStatus("Устранено с просрочкой");
            } else {
                toUpdate.setCommentStatus("Устранено");
            }
         }
        commentRepo.save(toUpdate);
        return HttpStatus.OK;
    }
    public CommentDTO findCommentByCommentId(Long id) {
        Comment comment = commentRepo.findCommentByCommentId(id);

        return CommentDTO.builder().codeCCS(comment.getCodeCCS()).
                commentCategory(comment.getCommentCategory()).
                commentExplanation(comment.getCommentExplanation()).
                commentStatus(comment.getCommentStatus()).
                description(comment.getDescription()).
                endDateFact(comment.getEndDateFact().format(formatter)).
                endDatePlan(comment.getEndDateFact().format(formatter)).
                startDate(comment.getStartDate().format(formatter)).
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
}
