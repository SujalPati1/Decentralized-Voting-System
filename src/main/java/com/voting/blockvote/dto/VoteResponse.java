package com.voting.blockvote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class VoteResponse {
    private Long id;
    private String voterEmail;
    private String candidateName;
    private String electionTitle;
}
