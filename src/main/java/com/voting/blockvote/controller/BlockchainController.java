package com.voting.blockvote.controller;

import com.voting.blockvote.model.VoteLedger;
import com.voting.blockvote.service.BlockchainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class BlockchainController {
    private final BlockchainService blockchainService;

    @GetMapping
    public List<VoteLedger> getLedger(){
        return blockchainService.getFullLedger();
    }
    @GetMapping("/validate")
    public String validateLedgerChain(){
        return blockchainService.validateChain();
    }
}
