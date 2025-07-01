package com.voting.blockvote.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CandidateResponse {
    private Long id;

    private String fullName;

    private String partyName;

    private String bio;

    private String electionTitle;
}
