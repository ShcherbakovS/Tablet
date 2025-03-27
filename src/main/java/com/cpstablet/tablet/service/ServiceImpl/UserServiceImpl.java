package com.cpstablet.tablet.service.ServiceImpl;

import com.cpstablet.tablet.entity.Role;
import com.cpstablet.tablet.entity.User;
import com.cpstablet.tablet.repository.UserRepo;
import com.cpstablet.tablet.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

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
}
