package com.cpstablet.tablet.controller;

import com.cpstablet.tablet.DTO.CommentDTO;
import com.cpstablet.tablet.DTO.PhotoDTO;
import com.cpstablet.tablet.entity.Comment;
import com.cpstablet.tablet.entity.Photo;
import com.cpstablet.tablet.service.CommentService;
import com.cpstablet.tablet.service.FileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    private final FileService fileService;

    @Qualifier("myMapper")
    private final ObjectMapper myMapper;

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/createComment")
    public ResponseEntity<String> createComment(@RequestBody String jsonString) throws JsonProcessingException {

        Long commentId = commentService.create(myMapper.readValue(jsonString, CommentDTO.class)).getCommentId();

        return new ResponseEntity<>(commentId.toString(), HttpStatus.OK);
    }
    @GetMapping("/getAllComments/{codeCCS}")
    public ResponseEntity<List<CommentDTO>> getAllComments(@PathVariable("codeCCS") String codeCCS) {
        return new ResponseEntity<>(commentService.getAllComments(codeCCS), HttpStatus.OK);
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
    @PostMapping(value = "/addPhoto/{id}")
    public ResponseEntity<PhotoDTO> addPhoto(@PathVariable("id") Long id, @RequestParam("photo") MultipartFile photo) throws IOException {
        Photo photoToSave = fileService.addPhotoToComment(id, photo);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(convertToDTO(photoToSave));
    }

    @GetMapping(value = "/getPhoto/{id}")
    public ResponseEntity<byte[]> getPhotoImage(@PathVariable("id") Long id) {
        byte[] imageData = fileService.getPhotoFromComment(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }

    @DeleteMapping("/removePhoto/{id}")
    public ResponseEntity<Void> removeCommentPhoto(@PathVariable("id")  Long id) {
        fileService.removePhotoFromComment(id);
        return ResponseEntity.noContent().build();
    }
    private PhotoDTO convertToDTO(Photo photo) {
        return PhotoDTO.builder()
                .id(photo.getId())
                .fileName(photo.getFileName())
                .contentType(photo.getContentType())
                .size(photo.getSize())
                .build();
    }

 }
