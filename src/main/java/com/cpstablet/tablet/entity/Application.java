package com.cpstablet.tablet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_application")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "application_to_add",
            joinColumns = @JoinColumn(name = "capitalcs_id"),
            inverseJoinColumns = @JoinColumn(name = "capitalcs-id")

    )
    List<CapitalCS> objectsToAdd;

    private String description;

    private LocalDateTime creationTime;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isApproved;

}
