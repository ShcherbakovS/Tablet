package com.cpstablet.tablet.service.mail;

import com.cpstablet.tablet.DTO.sesurityDTO.RegistrationRequestDTO;
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
    public void sendEmail(RegistrationRequestDTO registration) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tabletsender@yandex.ru");
        message.setTo(registration.getEmail());
        message.setSubject("ПланшетПНР регистрация");
        message.setText("Регистрация прошла успешно! \n" +
                "Логин в системе - " + registration.getEmail() +"\n" +
                "Ваш пароль - " + registration.getPassword());
        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.out.println("Некорректная почта");
        }
    }
}
