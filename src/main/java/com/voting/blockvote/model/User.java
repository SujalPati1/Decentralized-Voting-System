package com.voting.blockvote.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String fullname;

    private String aadharOrPan;

    private String voterId;

    @Enumerated(EnumType.STRING)
    private Role role;
}
