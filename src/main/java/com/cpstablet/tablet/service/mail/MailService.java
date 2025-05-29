package com.cpstablet.tablet.service.mail;

import com.cpstablet.tablet.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    @Async
    public void sendEmail(User user) {

        System.out.println(user.getEmail() + " Мыло пользователя");

                SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tabletsender@yandex.ru");
        message.setTo(user.getEmail());
        message.setSubject("ПланшетПНР регистрация");
        message.setText("Регистрация прошла успешно! \n" +
                "Логин в системе - " + user.getEmail() +"\n");
        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.out.println("Некорректная почта \n" + e.getStackTrace());
        }
    }
}
