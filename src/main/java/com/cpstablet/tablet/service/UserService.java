package com.cpstablet.tablet.service;

import com.cpstablet.tablet.DTO.UserDTO;
import com.cpstablet.tablet.entity.CapitalCS;
import com.cpstablet.tablet.entity.User;
import com.cpstablet.tablet.repository.UserRepo;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public User create (UserDTO dto) {

        return userRepo.save(User.builder().
                        userInfo(dto.getUserInfo()).
                        phoneNumber(dto.getPhoneNumber()).
                        email(dto.getEmail()).
                        organisationId(dto.getOrganisationId())
                        .build());
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public User findById (Long id) {

        return userRepo.findByUserId(id);
    }

    public void deleteUserById (Long id) {
        userRepo.deleteById(id);
    }

}
