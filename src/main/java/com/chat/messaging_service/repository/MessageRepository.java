package com.chat.messaging_service.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, String> {}
