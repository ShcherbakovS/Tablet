package com.cpstablet.tablet.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_info")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "user_phone")
    private String phoneNumber;
    //TODO зааменить организацию в полноценную сущность
    @Column(name = "organisation")
    private String organisation;
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

}
