package com.bookstore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class OnlineBookStoreSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreSystemApplication.class, args);
        log.info("Online BookStore System is running...");
    }
}
