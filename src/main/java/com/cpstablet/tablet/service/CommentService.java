package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.CommentDTO;
import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.repository.CommentRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepo commentRepo;

    public Comment create(CommentDTO comDTO) {

        return commentRepo.save(Comment.builder().
                serialNumber(comDTO.getSerialNumber()).
                subobject(comDTO.getSubobject()).
                startDate(comDTO.getStartDate()).
                endDatePlan(comDTO.getEndDatePlan()).
                endDateFact(comDTO.getEndDateFact()).
                commentCategory(comDTO.getCommentCategory()).
                description(comDTO.getDescription()).
                build());

    }
    public List<Comment> getAllComments() {
        return commentRepo.findAll();
    }
//TODO: внесение изменений в существующее замечание проверить
    public Comment update(CommentDTO comDTO, Long id) {
        return commentRepo.save(Comment.builder().
                serialNumber(comDTO.getSerialNumber()).
                subobject(comDTO.getSubobject()).
                startDate(comDTO.getStartDate()).
                endDatePlan(comDTO.getEndDatePlan()).
                endDateFact(comDTO.getEndDateFact()).
                commentCategory(comDTO.getCommentCategory()).
                description(comDTO.getDescription()).
                build());
    }

    public void deleteCommentById (Long id) {
        commentRepo.deleteById(id);
    }


}
