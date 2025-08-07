package com.cpstablet.tablet.service.ServiceImpl;

import com.cpstablet.tablet.DTO.*;
import com.cpstablet.tablet.entity.*;
import com.cpstablet.tablet.repository.CapitalCSRepo;
import com.cpstablet.tablet.repository.UserRepo;
import com.cpstablet.tablet.service.ApplicationService;
import com.cpstablet.tablet.service.UserService;
import com.cpstablet.tablet.service.mail.MailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final CapitalCSRepo capitalCSRepo;
    private final ApplicationService appService;

    private final MailService mailService;

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
    public ResponseEntity deleteByUserId(Long userId) {

        User user = userRepo.findById(userId).orElseThrow(()-> new UsernameNotFoundException("Пользователь не найден"));

        if (existsByUsername(user.getUsername())) {
            userRepo.deleteById(userId);
            return ResponseEntity.ok().body("Пользователь " + user.getUserInfo().getFullName() + " успешно удален.");
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity setUserRole(Long userId, String role) {

        User user = userRepo.findById(userId).orElseThrow(
                ()-> new UsernameNotFoundException("Пользователь не найден")
        );


        switch (role) {
            case "NONE":
                user.setRole(Role.NONE);
                user.setIsEnabled(false);
                break;
            case "ADMIN":
                user.setRole(Role.ADMIN);
                user.setIsEnabled(true);
                break;
            case "USER":
                user.setRole(Role.USER);
                user.setIsEnabled(true);
                break;
        }

        userRepo.save(user);

        if(user.getIsFirstRegistered()) {
            mailService.sendEmail(user);
            user.setIsFirstRegistered(false);
            userRepo.save(user);
        }

        return ResponseEntity.ok().body("Пользователю " + user.getUserInfo().getFullName() + " присвоена роль " + user.getRole());
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
                .build()).sorted(Comparator.comparing(CapitalCSDTO::getCapitalCSName)).collect(Collectors.toList());
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

        return userRepo.findAll().stream().map(user-> createUserDTOFromUserEntity(user)
        ).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersByCCS(String ccsCode) {

        CapitalCS capital = capitalCSRepo.findByCodeCCS(ccsCode).orElseThrow(()-> new EntityNotFoundException("При запросе списка пользователей с достуом к объекту " + ccsCode
        + " объект не был найден"));

        return userRepo.findAll().stream().filter(user-> user.getAllowedObjects().contains(capital)).map(user-> createUserDTOFromUserEntity(user)
        ).collect(Collectors.toList());
    }


    @Override
    public List<ApplicationResponseDTO> getApplications(Long id) {

        return userRepo.findById(id).get().getApplications().stream().map(app->  appService.buildAppRespDTO(app)
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

        System.out.println("Пользователь создание запроса");
        StringBuilder defaultValueForDescription = new StringBuilder(applicationRequestDTO.getDescription());
        StringBuilder builder = new StringBuilder("\nОбъекты которых нет в системе:");

        Application userApplication = new Application();

        userApplication.setCreationTime(LocalDateTime.now());
        userApplication.setApproved(false);
        userApplication.setObjectsToAdd(applicationRequestDTO.getObjectsToAdd().
                stream().
                map(s -> {
                    if (capitalCSRepo.findByCodeCCS(s).isEmpty()) {
                        builder.append(" ");
                        builder.append(s);
                        return null;
                    } else {
                        return capitalCSRepo.findByCodeCCS(s).get();
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList()));

        if (builder.equals("Объекты которых нет в системе:")) {
            userApplication.setDescription(defaultValueForDescription.toString());
        } else {
            userApplication.setDescription(defaultValueForDescription.append(builder).toString());
        }

        User user = userRepo.findById(id).orElseThrow(()-> new UsernameNotFoundException("Пользователь не найден"));
        userApplication.setUser(user);
        user.getApplications().add(userApplication);
        userRepo.save(user);

        return HttpStatus.OK;
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepo.findById(id).orElseThrow(()-> new UsernameNotFoundException("Пользователь не найден"));

        return createUserDTOFromUserEntity(user);
    }

    @Override
    public List<String> getUsersByJournalRecords(String ccsCode) {

        CapitalCS capitalCS = capitalCSRepo.findByCodeCCS(ccsCode).orElseThrow(()-> new EntityNotFoundException("ОКС не найден"));

        return capitalCS.getJournalList().stream().map(journalEntry-> journalEntry.getUser()).collect(Collectors.toList());
    }

    private UserDTO createUserDTOFromUserEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .isEnabled(user.isEnabled())
                .userInfo(user.getUserInfo() == null? new UserInfoDTO(): (UserInfoDTO.builder()
                        .fullName(user.getUserInfo().getFullName())
                        .organisation(user.getUserInfo().getOrganisation())
                        .registrationDate(user.getUserInfo().getRegistrationDate())
                        .phoneNumber(user.getUserInfo().getPhoneNumber())
                        .build()))
                .build();
    }

}
