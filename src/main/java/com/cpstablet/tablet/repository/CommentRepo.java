package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {

    Comment findCommentByCommentId(Long id);
    List<Comment> findCommentsByCodeCCS(String codeCCS);
    Optional<Comment> findByCommentId(Long id);
    List<Comment> findAll();

    List<Comment> findCommentByIiNumber(String IINumber);




}
