package com.sportygroup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class SportsBettingSettlementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SportsBettingSettlementApplication.class, args);
    }
}
