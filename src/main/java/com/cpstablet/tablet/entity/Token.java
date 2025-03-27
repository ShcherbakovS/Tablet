package com.cpstablet.tablet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "token_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "accessToken")
    private String accessToken;
    @Column(name = "refreshToken")
    private String refreshToken;
    @Column(name = "loggedOut")
    private boolean loggedOut;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
