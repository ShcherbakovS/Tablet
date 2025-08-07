package com.cpstablet.tablet.service;


import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.repository.CommentRepo;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class Scheduler {

    private final CommentRepo commentRepo;

    // ежедневно проверка статусов замечаний замена статуса при просрочке"@daily"
    @Scheduled(cron = "@daily" )
    public void commentChecker() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate currentDate = LocalDate.now();

        try {
            List<Comment> commentsToCheck = commentRepo.findAll().stream()
                    .filter(comment -> comment.getEndDateFact().equals(" "))
                    .filter(comment -> !comment.getEndDatePlan().equals(" "))
                    .filter(comment -> isValidAndOverduePlanDate(comment.getEndDatePlan(), formatter, currentDate))
                    .collect(Collectors.toList());

            if (!commentsToCheck.isEmpty()) {
                commentsToCheck.forEach(comment -> comment.setCommentStatus("Не устранено с просрочкой"));
                commentRepo.saveAll(commentsToCheck);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при проверке статусов замечаний: " + e.getMessage(), e);
        }
    }

    private boolean isValidEndDateFact(String endDateFact) {
        return endDateFact != null && !endDateFact.trim().isEmpty() && !endDateFact.equals(" ");
    }

    private boolean isValidAndOverduePlanDate(String endDatePlan, DateTimeFormatter formatter, LocalDate currentDate) {
        try {
            if (endDatePlan == null || endDatePlan.trim().isEmpty()) {
                return false;
            }
            LocalDate planDate = LocalDate.parse(endDatePlan, formatter);
            return planDate.isBefore(currentDate);
        } catch (Exception e) {
            return false;
        }
    }
}
