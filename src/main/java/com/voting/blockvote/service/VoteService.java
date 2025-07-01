package com.voting.blockvote.service;


import com.voting.blockvote.dto.CandidateResponse;
import com.voting.blockvote.dto.VoteRequest;
import com.voting.blockvote.dto.VoteResponse;
import com.voting.blockvote.model.Candidate;
import com.voting.blockvote.model.Election;
import com.voting.blockvote.model.Vote;
import com.voting.blockvote.repository.CandidateRepository;
import com.voting.blockvote.repository.ElectionRepository;
import com.voting.blockvote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;

    public VoteResponse castVote(VoteRequest request,String voterEmail) {
        if(voteRepository.findByVoterEmailAndElectionId(voterEmail,request.getElectionId()).isPresent()){
            throw new RuntimeException("You have already voted in this election");
        }

        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new RuntimeException("candidate not found"));

        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new RuntimeException("election not found"));

        Vote vote = Vote.builder()
                .voterEmail(voterEmail)
                .candidate(candidate)
                .election(election)
                .build();

        Vote saved = voteRepository.save(vote);

        return VoteResponse.builder()
                .id(saved.getId())
                .voterEmail(voterEmail)
                .candidateName(candidate.getFullName())
                .electionTitle(election.getTitle())
                .build();
    }
    public List<VoteResponse> getVotesByElection(Long electionId) {
        return voteRepository.findByElectionId(electionId)
                .stream()
                .map(v -> VoteResponse.builder()
                        .id(v.getId())
                        .voterEmail(v.getVoterEmail())
                        .candidateName(v.getCandidate().getFullName())
                        .electionTitle(v.getElection().getTitle())
                        .build())
                .collect(toList());
    }
}
