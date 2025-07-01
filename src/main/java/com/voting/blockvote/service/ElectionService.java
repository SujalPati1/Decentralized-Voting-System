package com.voting.blockvote.service;


import com.voting.blockvote.dto.CreateElectionRequest;
import com.voting.blockvote.dto.ElectionResponse;
import com.voting.blockvote.model.Election;
import com.voting.blockvote.repository.ElectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ElectionService {
    private final ElectionRepository electionRepository;

    public ElectionResponse createElection(CreateElectionRequest request){
        if(request.getEndTime().isBefore(request.getStartTime())){
            throw new IllegalArgumentException("End time must be before start time");
        }

        Election election = Election.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isEnded(false)
                .build();

        Election saved = electionRepository.save(election);

        return ElectionResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .isEnded(saved.getIsEnded())
                .build();
    }

    public List<ElectionResponse> getAllElections(){
        return electionRepository.findAll().stream()
                .map(e -> ElectionResponse.builder()
                        .id(e.getId())
                        .title(e.getTitle())
                        .description(e.getDescription())
                        .startTime(e.getStartTime())
                        .endTime(e.getEndTime())
                        .isEnded(e.getIsEnded())
                        .build())
                .collect(toList());
    }
}
