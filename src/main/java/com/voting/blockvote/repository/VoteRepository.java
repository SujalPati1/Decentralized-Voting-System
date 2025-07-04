package com.voting.blockvote.repository;

import com.voting.blockvote.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote,Long> {
    Optional<Vote> findByVoterEmailAndElectionId(String email,Long electionId);
    List<Vote> findByElectionId(Long electionId);

    Long countByCandidateId(Long candidateId);
}
