package com.chat.messaging_service.service;

import com.chat.messaging_service.repository.ChatUserRepository;
import com.chat.messaging_service.repository.ConversationRepository;
import com.chat.messaging_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ChatUserRepository chatUserRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
}
