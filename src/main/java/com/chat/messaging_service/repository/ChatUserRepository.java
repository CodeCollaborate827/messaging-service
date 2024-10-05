package com.chat.messaging_service.repository;

import com.chat.messaging_service.document.ChatUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserRepository extends ReactiveCrudRepository<ChatUser, String> {}
