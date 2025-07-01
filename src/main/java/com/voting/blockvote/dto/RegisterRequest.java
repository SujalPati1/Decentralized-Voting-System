package com.voting.blockvote.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String voterId;
    private String aadharOrPan;
}
