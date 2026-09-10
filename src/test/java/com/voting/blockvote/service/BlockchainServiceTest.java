package com.voting.blockvote.service;

import com.voting.blockvote.model.Candidate;
import com.voting.blockvote.model.Election;
import com.voting.blockvote.model.Vote;
import com.voting.blockvote.model.VoteLedger;
import com.voting.blockvote.repository.VoteLedgerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlockchainServiceTest {

    @InjectMocks
    private BlockchainService blockchainService;

    @Mock
    private VoteLedgerRepository voteLedgerRepository;

    private Vote vote;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Election election = Election.builder().id(1L).build();
        Candidate candidate = Candidate.builder().id(2L).build();
        vote = Vote.builder()
                .voterEmail("voter@example.com")
                .candidate(candidate)
                .election(election)
                .build();
    }

    // ---- sha256 ----

    @Test
    void sha256_KnownInput_MatchesKnownDigest() {
        // Known SHA-256 test vectors, computed independently of the class under test.
        assertEquals(
                "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                blockchainService.sha256("abc")
        );
        assertEquals(
                "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                blockchainService.sha256("")
        );
    }

    @Test
    void sha256_SameInput_IsDeterministic() {
        String first = blockchainService.sha256("same-input");
        String second = blockchainService.sha256("same-input");
        assertEquals(first, second);
    }

    @Test
    void sha256_DifferentInput_ProducesDifferentDigest() {
        String hashA = blockchainService.sha256("input-a");
        String hashB = blockchainService.sha256("input-b");
        assertNotEquals(hashA, hashB);
    }

    @Test
    void sha256_Output_IsSixtyFourLowercaseHexChars() {
        String hash = blockchainService.sha256("anything");
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    // ---- logVote / genesis handling ----

    @Test
    void logVote_NoExistingBlocks_ChainsFromGenesis() {
        when(voteLedgerRepository.findTopByOrderByIdDesc()).thenReturn(null);

        blockchainService.logVote(vote);

        ArgumentCaptor<VoteLedger> captor = ArgumentCaptor.forClass(VoteLedger.class);
        verify(voteLedgerRepository, times(1)).save(captor.capture());

        VoteLedger saved = captor.getValue();
        String expectedData = vote.getVoterEmail() + vote.getCandidate().getId() + vote.getElection().getId();
        String expectedHash = blockchainService.sha256(expectedData + "GENESIS");

        assertEquals("voter@example.com", saved.getVoterEmail());
        assertEquals(2L, saved.getCandidateId());
        assertEquals(1L, saved.getElectionId());
        assertEquals("GENESIS", saved.getPreviousHash());
        assertEquals(expectedHash, saved.getHash());
    }

    @Test
    void logVote_ExistingBlock_ChainsFromItsHash() {
        VoteLedger last = VoteLedger.builder()
                .id(1L)
                .hash("previous-block-hash")
                .build();
        when(voteLedgerRepository.findTopByOrderByIdDesc()).thenReturn(last);

        blockchainService.logVote(vote);

        ArgumentCaptor<VoteLedger> captor = ArgumentCaptor.forClass(VoteLedger.class);
        verify(voteLedgerRepository, times(1)).save(captor.capture());

        VoteLedger saved = captor.getValue();
        String expectedData = vote.getVoterEmail() + vote.getCandidate().getId() + vote.getElection().getId();
        String expectedHash = blockchainService.sha256(expectedData + "previous-block-hash");

        assertEquals("previous-block-hash", saved.getPreviousHash());
        assertEquals(expectedHash, saved.getHash());
        assertNotEquals("previous-block-hash", saved.getHash());
    }

    // ---- getFullLedger ----

    @Test
    void getFullLedger_DelegatesToRepositoryFindAll() {
        List<VoteLedger> ledgers = List.of(VoteLedger.builder().id(1L).build());
        when(voteLedgerRepository.findAll()).thenReturn(ledgers);

        List<VoteLedger> result = blockchainService.getFullLedger();

        assertSame(ledgers, result);
        verify(voteLedgerRepository, times(1)).findAll();
    }

    // ---- validateChain ----

    @Test
    void validateChain_EmptyLedger_ReportsValid() {
        when(voteLedgerRepository.findAll()).thenReturn(List.of());

        assertEquals("Ledger chain is valid", blockchainService.validateChain());
    }

    @Test
    void validateChain_SingleGenesisBlock_ReportsValid() {
        VoteLedger genesis = VoteLedger.builder().id(1L).previousHash("GENESIS").hash("hash-1").build();
        when(voteLedgerRepository.findAll()).thenReturn(List.of(genesis));

        assertEquals("Ledger chain is valid", blockchainService.validateChain());
    }

    @Test
    void validateChain_ProperlyLinkedChain_ReportsValid() {
        VoteLedger block1 = VoteLedger.builder().id(1L).previousHash("GENESIS").hash("hash-1").build();
        VoteLedger block2 = VoteLedger.builder().id(2L).previousHash("hash-1").hash("hash-2").build();
        VoteLedger block3 = VoteLedger.builder().id(3L).previousHash("hash-2").hash("hash-3").build();
        when(voteLedgerRepository.findAll()).thenReturn(List.of(block1, block2, block3));

        assertEquals("Ledger chain is valid", blockchainService.validateChain());
    }

    @Test
    void validateChain_TamperedLink_ReportsBreakAtTamperedBlock() {
        VoteLedger block1 = VoteLedger.builder().id(1L).previousHash("GENESIS").hash("hash-1").build();
        // block2's previousHash no longer matches block1's hash, as if block1 was altered or
        // reordered after block2 was appended.
        VoteLedger block2 = VoteLedger.builder().id(2L).previousHash("tampered-hash").hash("hash-2").build();
        when(voteLedgerRepository.findAll()).thenReturn(List.of(block1, block2));

        assertEquals("Chain broken at block ID 2", blockchainService.validateChain());
    }

    @Test
    void validateChain_BreakPartwayThroughLongerChain_IdentifiesExactBlock() {
        VoteLedger block1 = VoteLedger.builder().id(1L).previousHash("GENESIS").hash("hash-1").build();
        VoteLedger block2 = VoteLedger.builder().id(2L).previousHash("hash-1").hash("hash-2").build();
        // block3 breaks the chain here...
        VoteLedger block3 = VoteLedger.builder().id(3L).previousHash("wrong-hash").hash("hash-3").build();
        // ...even though block4 correctly links to block3, the first break found must win.
        VoteLedger block4 = VoteLedger.builder().id(4L).previousHash("hash-3").hash("hash-4").build();
        when(voteLedgerRepository.findAll()).thenReturn(List.of(block1, block2, block3, block4));

        assertEquals("Chain broken at block ID 3", blockchainService.validateChain());
    }
}
