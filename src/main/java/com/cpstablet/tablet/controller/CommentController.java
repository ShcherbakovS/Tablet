package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.CommentDTO;
import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.service.CommentService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/createComment")
    public ResponseEntity<String> createComment(@RequestBody String jsonString) throws JsonProcessingException {

        Long commentId = commentService.create(new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).
                           enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT).
                   readValue(jsonString, CommentDTO.class)).getCommentId();

        return new ResponseEntity<>(commentId.toString(), HttpStatus.OK);
    }

    @GetMapping("/getAllComments")
    public ResponseEntity<List<Comment>> getAllComments() {
        return new ResponseEntity<>(commentService.getAllComments(), HttpStatus.OK);
    }
    @GetMapping("/getCommentById/{id}")
    public ResponseEntity<Comment> getCommentByCodeCCSAndId(@PathVariable("id") Long id) {
        return new ResponseEntity<>(commentService.findCommentByCommentId(id), HttpStatus.OK);
    }
    //TODO: обновить данные замечания
    @PutMapping("/updateComment/{id}")
    public HttpStatus updateComment(@RequestBody String jsonString, @PathVariable("id") Long id) {

        try {
            return commentService.update(new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).
                    enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT).readValue(jsonString, CommentDTO.class), id);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @DeleteMapping("/deleteComment/{id}")
    public HttpStatus delete(@PathVariable("id") Long id) {

       return commentService.deleteCommentById(id);

    }

 }
