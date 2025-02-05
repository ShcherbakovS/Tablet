package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {

    @Transactional
     User findByUserId(Long id);

     void deleteById(Long id);

}
