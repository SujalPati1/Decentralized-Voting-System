package com.voting.blockvote.service;


import com.voting.blockvote.dto.CandidateResponse;
import com.voting.blockvote.dto.CreateCandidateRequest;
import com.voting.blockvote.model.Candidate;
import com.voting.blockvote.model.Election;
import com.voting.blockvote.repository.CandidateRepository;
import com.voting.blockvote.repository.ElectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor

public class CandidateService {
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;

    public CandidateResponse createCandidate(CreateCandidateRequest request) {
        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new RuntimeException("Election not found with ID:"  + request.getElectionId()));

        Candidate candidate = Candidate.builder()
                .fullName(request.getFullName())
                .partyName(request.getPartyName())
                .bio(request.getBio())
                .election(election)
                .build();

        Candidate saved = candidateRepository.save(candidate);

        return CandidateResponse.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .partyName(saved.getPartyName())
                .bio(saved.getBio())
                .electionTitle(election.getTitle())
                .build();
    }

    public List<CandidateResponse> getCandidatesByElection(Long electionId) {
        return candidateRepository.findByElectionId(electionId)
                .stream()
                .map(c -> CandidateResponse.builder()
                        .id(c.getId())
                        .fullName(c.getFullName())
                        .partyName(c.getPartyName())
                        .bio(c.getBio())
                        .electionTitle(c.getElection().getTitle())
                        .build())
                .collect(toList());
    }
}
