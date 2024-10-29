package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {

    @Transactional
    public User findByUserId(Long id);

    public void deleteById(Long id);

}
