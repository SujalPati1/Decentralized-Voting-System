package com.voting.blockvote.controller;


import com.voting.blockvote.dto.CandidateResponse;
import com.voting.blockvote.dto.CreateCandidateRequest;
import com.voting.blockvote.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {
    private final CandidateService candidateService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CandidateResponse createCandidate(@RequestBody CreateCandidateRequest request){
        return candidateService.createCandidate(request);
    }
    @GetMapping("/election/{electionId}")
    public List<CandidateResponse> getCandidatesForElection(@PathVariable Long electionId){
        return candidateService.getCandidatesByElection(electionId);
    }
}