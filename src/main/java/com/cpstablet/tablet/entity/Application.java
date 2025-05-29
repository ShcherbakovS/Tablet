package com.cpstablet.tablet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_application")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToMany
    @JoinTable(

            name = "application_to_add",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "capitalcs_id")

    )
    List<CapitalCS> objectsToAdd;

    @ManyToMany
    @JoinTable(
            name = "added_objects",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "capitalcs_id")
    )
   List<CapitalCS> addedObjects;

    private String description;

    private LocalDateTime creationTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isApproved;

}
