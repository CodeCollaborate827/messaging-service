package com.chat.messaging_service.service;

import com.chat.messaging_service.document.ChatUser;
import reactor.core.publisher.Mono;

public interface ChatUserService {
  Mono<ChatUser> saveNewChatUser(ChatUser chatUser);
}
