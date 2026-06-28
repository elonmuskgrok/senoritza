package com.siva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TaxTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaxTrackerApplication.class, args);
    }

}
