package com.voting.blockvote.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateElectionRequest {
    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
