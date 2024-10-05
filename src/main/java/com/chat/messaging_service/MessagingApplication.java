package com.chat.messaging_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class MessagingApplication {

  public static void main(String[] args) {
    SpringApplication.run(MessagingApplication.class, args);
  }
}
