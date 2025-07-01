package com.voting.blockvote.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AuthResponse {
    private String token;
    private String role;
}
