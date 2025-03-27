package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepo extends JpaRepository<User, Long> {

     Optional<User> findByUsername(String username);
     Optional<User> findByEmail(String email);
     @Transactional
      void deleteByUsername(String username);

}
