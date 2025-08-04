package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PhotoRepo extends JpaRepository<Photo, Long> {

     void deleteById(Long id);

     Optional<Photo> findById(Long id);

}
