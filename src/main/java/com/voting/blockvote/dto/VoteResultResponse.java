package com.voting.blockvote.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteResultResponse {
    private String candidateName;
    private String partyName;
    private Long voteCount;
}
