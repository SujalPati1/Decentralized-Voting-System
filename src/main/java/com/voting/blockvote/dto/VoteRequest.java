package com.voting.blockvote.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class VoteRequest {
    private Long electionId;
    private Long candidateId;
}
