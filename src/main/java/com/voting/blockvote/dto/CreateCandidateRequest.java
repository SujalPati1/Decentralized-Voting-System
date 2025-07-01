package com.voting.blockvote.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CreateCandidateRequest {
    private String fullName;

    private String partyName;

    private String bio;

    private Long electionId;
}
