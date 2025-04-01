package com.cpstablet.tablet.service.ServiceImpl;

import com.cpstablet.tablet.DTO.*;
import com.cpstablet.tablet.entity.*;
import com.cpstablet.tablet.repository.CapitalCSRepo;
import com.cpstablet.tablet.repository.UserInfoRepo;
import com.cpstablet.tablet.repository.UserRepo;
import com.cpstablet.tablet.service.ApplicationService;
import com.cpstablet.tablet.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final CapitalCSRepo capitalCSRepo;

    private final ApplicationService appService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).
                orElseThrow(()-> new UsernameNotFoundException("Пользователь с именем "+ username + " не найден"));
    }

    @Override
    public boolean existsByUsername(String username) {
        User user = userRepo.findByUsername(username).orElse(null);

        if(user != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        User user = userRepo.findByEmail(email).orElse(null);

        if(user != null) {
            return true;
        }
        return false;
    }
    @Override
    public ResponseEntity deleteByUserName(String username) {

        if (existsByUsername(username)) {
            userRepo.deleteByUsername(username);
            return ResponseEntity.ok().body("Пользователь " + username + " успешно удален.");
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity setUserRole(String username) {
        User user = userRepo.findByUsername(username).orElseThrow(
                ()-> new UsernameNotFoundException("Пользователь с именем " + username + " не найден")
        );
        user.setRole(Role.ADMIN);
        userRepo.save(user);

        return ResponseEntity.ok().body("Пользователю " + username + " присвоена роль Администратор");
    }

    @Override
    public List<CapitalCSDTO> getAllowedObjects(Long id) {

        User user = userRepo.findById(id).orElseThrow(()-> new UsernameNotFoundException("Пользователя с таким id не существует"));

        return user.getAllowedObjects().stream().map(capital -> CapitalCSDTO.builder().
                capitalCSName(capital.getCapitalCSName()).
                CIWExecutor(capital.getCIWExecutor()).
                CWExecutor(capital.getCWExecutor()).
                codeCCS(capital.getCodeCCS()).
                customer(capital.getCustomer()).
                customerSupervisor(capital.getCustomerSupervisor()).
                locationRegion(capital.getLocationRegion()).
                objectType(capital.getObjectType())
                .build()).collect(Collectors.toList());
    }

    @Override
    public void setObjectToAllowed(Long id, String ccsCode) {

        User user = userRepo.findById(id).orElseThrow(()-> new UsernameNotFoundException("Пользователя с таким id не существует"));
        user.getAllowedObjects().add(capitalCSRepo.findByCodeCCS(ccsCode).orElseThrow(()-> new RuntimeException("Объекта с кодом " +
                ccsCode + " не существует")));

        userRepo.save(user);

    }

    @Override
    public List<UserDTO> getAllUsers() {

        return userRepo.findAll().stream().map(user->
                UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .userInfo(UserInfoDTO.builder()
                                .fullName(user.getUserInfo().getFullName())
                                .organisation(user.getUserInfo().getOrganisation())
                                .phoneNumber(user.getUserInfo().getPhoneNumber())
                                .build())
                        .build()

        ).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponseDTO> getApplications(Long id) {

        return userRepo.findById(id).get().getApplications().stream().map(app-> appService.buildAppRespDTO(app)
                ).collect(Collectors.toList());
    }


    @Override
    public HttpStatus updateUserInfo(UserInfoDTO userInfoDTO) {

        User user = userRepo.findById(userInfoDTO.getId()).orElseThrow(()-> new UsernameNotFoundException("Пользователь не найден"));
        user.getUserInfo().setPhoneNumber(userInfoDTO.getPhoneNumber());
        user.getUserInfo().setOrganisation(userInfoDTO.getOrganisation());
        user.getUserInfo().setFullName(userInfoDTO.getFullName());

        userRepo.save(user);

        return HttpStatus.OK;
    }

    @Override
    public HttpStatus createApplication(ApplicationRequestDTO applicationRequestDTO, Long id) {

        Application userApplication = new Application();
        userApplication.setCreationTime(LocalDateTime.now());
        userApplication.setApproved(false);
        userApplication.setDescription(applicationRequestDTO.getDescription());
        userApplication.setObjectsToAdd(applicationRequestDTO.getObjectsToAdd().stream().
                map(s-> capitalCSRepo.findByCodeCCS(s).orElse(null)).
                collect(Collectors.toList()));


        User user = userRepo.findById(id).orElseThrow(()-> new UsernameNotFoundException("Пользователь не найден"));
        user.getApplications().add(userApplication);
        userRepo.save(user);

        return HttpStatus.OK;
    }
}
