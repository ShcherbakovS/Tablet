package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.CommentDTO;
import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.service.CommentService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @Qualifier("myMapper")
    private final ObjectMapper myMapper;

    @PostMapping("/createComment")
    public ResponseEntity<String> createComment(@RequestBody String jsonString) throws JsonProcessingException {

        Long commentId = commentService.create(myMapper.readValue(jsonString, CommentDTO.class)).getCommentId();

        return new ResponseEntity<>(commentId.toString(), HttpStatus.OK);
    }

    @GetMapping("/getAllComments")
    public ResponseEntity<List<CommentDTO>> getAllComments() {
        return new ResponseEntity<>(commentService.getAllComments(), HttpStatus.OK);
    }
    @GetMapping("/getCommentById/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(commentService.findCommentByCommentId(id), HttpStatus.OK);
    }

    @PutMapping("/updateComment/{id}")
    public HttpStatus updateComment(@RequestBody String jsonString, @PathVariable("id") Long id) {

        try {
            return commentService.update(myMapper.readValue(jsonString, CommentDTO.class), id);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @DeleteMapping("/deleteComment/{id}")
    public HttpStatus delete(@PathVariable("id") Long id) {

       return commentService.deleteCommentById(id);

    }

 }
