package com.chat.messaging_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class MessagingApplication {
  // TODO: define a request metadata which contains the requestId and currentUserId, and it will be
  // a part of the Kafka message when processed other components,
  // TODO: store the replied message content along with the message in the database, and getting a
  // message, return the replied message if it exists
  public static void main(String[] args) {
    SpringApplication.run(MessagingApplication.class, args);
  }
}
