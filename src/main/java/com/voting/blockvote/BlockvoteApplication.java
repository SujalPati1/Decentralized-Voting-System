package com.voting.blockvote;

import com.voting.blockvote.model.User;
import com.voting.blockvote.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class BlockvoteApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlockvoteApplication.class, args);
    }
}








