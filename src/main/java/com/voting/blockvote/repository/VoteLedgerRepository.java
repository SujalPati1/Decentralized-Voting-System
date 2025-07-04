package com.voting.blockvote.repository;

import com.voting.blockvote.model.VoteLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteLedgerRepository extends JpaRepository<VoteLedger,Long> {
    VoteLedger findTopByOrderByIdDesc();    // get last ledger block
}
