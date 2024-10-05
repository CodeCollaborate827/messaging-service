package com.chat.messaging_service.service.impl;

import com.chat.messaging_service.document.ChatUser;
import com.chat.messaging_service.exception.ApplicationException;
import com.chat.messaging_service.repository.ChatUserRepository;
import com.chat.messaging_service.service.ChatUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.chat.messaging_service.exception.ErrorCode.MESSAGING_ERROR1;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatUserServiceImpl implements ChatUserService {

    private final ChatUserRepository chatUserRepository;

    @Override
    public Mono<ChatUser> saveNewChatUser(ChatUser chatUser) {
        String id = chatUser.getId();

        // check if user with id is already existed
        if (id != null) {
            return chatUserRepository.existsById(id)
                    .flatMap(existed -> {
                        if (existed) {
                            return Mono.error(new ApplicationException(MESSAGING_ERROR1));
                        } else {
                            return chatUserRepository.save(chatUser);
                        }
                    });
        }

        log.info("Saving chat user: {}", chatUser);
        return chatUserRepository.save(chatUser);

    }
}
