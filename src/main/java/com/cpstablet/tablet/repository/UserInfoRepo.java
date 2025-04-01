package com.cpstablet.tablet.repository;

import com.cpstablet.tablet.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepo extends JpaRepository<UserInfo, Long> {

}
