package com.voting.blockvote.controller;


import com.voting.blockvote.dto.VoteRequest;
import com.voting.blockvote.dto.VoteResponse;
import com.voting.blockvote.dto.VoteResultResponse;
import com.voting.blockvote.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;

    @PostMapping
    @PreAuthorize("hasAuthority('VOTER')")
    public VoteResponse castVote(@RequestBody VoteRequest request, Principal principal) {
        return voteService.castVote(request,principal.getName());
    }

    @GetMapping("/election/{electionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<VoteResponse> getVotesByElection(@PathVariable Long electionId) {
        return voteService.getVotesByElection(electionId);  
    }

    @GetMapping("/results/{electionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<VoteResultResponse> getElectionResults(@PathVariable Long electionId) {
        return voteService.getResultsByElection(electionId);
    }
}
