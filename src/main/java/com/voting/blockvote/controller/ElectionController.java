package com.voting.blockvote.controller;

import com.voting.blockvote.dto.CreateElectionRequest;
import com.voting.blockvote.dto.ElectionResponse;
import com.voting.blockvote.service.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/election")
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ElectionResponse createElection(@RequestBody CreateElectionRequest request) {
        return electionService.createElection(request);
    }

    @GetMapping
    public List<ElectionResponse> getAllElections() {
        return electionService.getAllElections();
    }
}
