package com.voting.blockvote.service;

import com.voting.blockvote.model.Vote;
import com.voting.blockvote.model.VoteLedger;
import com.voting.blockvote.repository.VoteLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockchainService {
    private final VoteLedgerRepository voteLedgerRepository;

    public void logVote(Vote vote){
        VoteLedger last =  voteLedgerRepository.findTopByOrderByIdDesc();

        String data = vote.getVoterEmail() + vote.getCandidate().getId() + vote.getElection().getId();
        String previousHash = last != null ? last.getHash(): "GENESIS";

        String currentHash = sha256(data + previousHash);

        VoteLedger voteLedger = VoteLedger.builder()
                .voterEmail(vote.getVoterEmail())
                .candidateId(vote.getCandidate().getId())
                .electionId(vote.getElection().getId())
                .previousHash(previousHash)
                .hash(currentHash)
                .build();
        voteLedgerRepository.save(voteLedger);
    }

    public String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported",e);
        }
    }

    public List<VoteLedger> getFullLedger() {
        return voteLedgerRepository.findAll();
    }

    public String validateChain(){
        List<VoteLedger> ledgers = voteLedgerRepository.findAll();
        for (int i = 1; i < ledgers.size(); i++) {
            VoteLedger prev = ledgers.get(i - 1);
            VoteLedger curr = ledgers.get(i);

            String expectedPrevHash = prev.getHash();
            if (!curr.getPreviousHash().equals(expectedPrevHash)) {
                return "Chain broken at block ID " + curr.getId();
            }
        }
        return "Ledger chain is valid";
    }
}
