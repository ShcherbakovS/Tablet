package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhotosRepo extends JpaRepository<Photo, Long> {

     Photo getPhotoByCommentId(Long id);
     void deleteById(Long id);

     Optional<Photo> findById(Long id);

}
