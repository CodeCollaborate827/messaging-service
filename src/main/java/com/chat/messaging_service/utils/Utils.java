package com.chat.messaging_service.utils;

import com.chat.messaging_service.document.ChatUser;
import com.chat.messaging_service.event.UserRegistrationEvent;

import java.util.ArrayList;

public class Utils {

    public static ChatUser convertToChatUser(UserRegistrationEvent userRegistrationEvent) {
        return ChatUser.builder()
                .id(userRegistrationEvent.getUserId())
                .avatar(userRegistrationEvent.getAvatar())
                .displayName(userRegistrationEvent.getDisplayName())
                .username(userRegistrationEvent.getUsername())
                .conversationIds(new ArrayList<>())
                .build();
    }
}
