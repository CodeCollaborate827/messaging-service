package com.chat.messaging_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class MessagingApplication {
  // TODO: 1. Resolve the issue find conversation between 2 users
  // TODO: 2. Remove duplicated userid in the converstion memberIds (the case of a user sending a
  // message to himself)
  public static void main(String[] args) {
    SpringApplication.run(MessagingApplication.class, args);
  }
}
