package com.voting.blockvote.repository;

import com.voting.blockvote.model.Election;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ElectionRepository extends JpaRepository<Election,Long> {
    List<Election> findByEndTimeAfterAndIsEndedFalse(LocalDateTime currentTime);
}
