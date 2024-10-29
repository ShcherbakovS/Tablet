package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.CommentDTO;
import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class CommentController {

    private CommentService commentService;

    @PostMapping("/createComment")
    public ResponseEntity<Comment> createComment(@RequestBody CommentDTO commentDTO) {
        return new ResponseEntity<>(commentService.create(commentDTO), HttpStatus.OK);
    }

    @GetMapping("/getAllComments")
    public ResponseEntity<List<Comment>> getAllComments() {
        return new ResponseEntity<>(commentService.getAllComments(), HttpStatus.OK);
    }

    @DeleteMapping("/deleteComment/{id}")
    public HttpStatus delete(@PathVariable Long id) {
        commentService.deleteCommentById(id);

        return HttpStatus.OK;
    }

    //TODO: обновить данные замечания
//    @PutMapping("/updateComment/{id}")
//    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody ) {
//
//
//        return new ResponseEntity<>(commentService.update())
//    }

 }
