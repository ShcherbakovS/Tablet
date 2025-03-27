package com.cpstablet.tablet.service;


import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.repository.CommentRepo;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class Scheduler {

    private final CommentRepo commentRepo;

    // ежедневно проверка статусов замечаний замена статуса при просрочке"@daily"
    @Scheduled(cron = "@daily" )
    public void commentChecker() {

        List<Comment> commentsToCheck = commentRepo.findAll().stream().
                filter(e-> !e.getEndDateFact().equals(" ")).
                filter(e-> LocalDate.parse(e.getEndDatePlan()).isBefore(LocalDate.now())).
                collect(Collectors.toList());

        commentsToCheck.stream().forEach(e-> e.setCommentStatus("Не устранено с просрочкой"));

        commentRepo.saveAll(commentsToCheck);
    }
}
