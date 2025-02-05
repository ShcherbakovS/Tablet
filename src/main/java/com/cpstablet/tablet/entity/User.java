package com.cpstablet.tablet.entity;


import jakarta.persistence.*;
import lombok.*;


@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    private String userInfo;
    private String phoneNumber;
    private String email;
    private Long organisationId;

}
