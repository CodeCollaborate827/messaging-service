package com.chat.messaging_service.utils;

import com.chat.messaging_service.document.ChatUser;
import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.document.objects.ConversationMember;
import com.chat.messaging_service.document.objects.ConversationPreview;
import com.chat.messaging_service.document.objects.SeenStatusTracker;
import com.chat.messaging_service.dto.response.ConversationMessageDTO;
import com.chat.messaging_service.dto.response.ConversationWithMessagesDTO;
import com.chat.messaging_service.exception.ApplicationException;
import com.chat.messaging_service.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chat.messaging_service.document.Conversation.*;
import static com.chat.messaging_service.document.objects.ConversationPreview.PreviewType;

@Slf4j
public class ConversationUtils {
  public static String constructConversationName(
      Conversation conversation, String currentUserId, String requestId) {
    if (ConversationType.GROUP.equals(conversation.getConversationType())) {
      return constructGroupConversationName(conversation);
    } else {
      return conversationDirectConversationName(conversation, currentUserId, requestId);
    }
  }

  private static String conversationDirectConversationName(
      Conversation conversation, String currentUserId, String requestId) {
    // a direct conversation only has 2 members
    // return name of the other user as the name for the conversation
    List<ConversationMember> members = getMemberList(conversation);
    for (ConversationMember member : members) {
      boolean isOtherUser = !member.getId().equals(currentUserId);
      if (isOtherUser) {
        return member.getDisplayName();
      }
    }
    // check if this is the conversation of the user with his/her own
    if (checkIfSelfConversation(conversation, currentUserId)) {
      return members.get(0).getDisplayName();
    }
    throw new ApplicationException(ErrorCode.MESSAGING_ERROR3, requestId);
  }

  private static String constructGroupConversationName(Conversation conversation) {
    String groupConversationName = conversation.getGroupConversationName();
    // if there is no name for the group conversation, create a default name by concatinating member
    // names
    if (groupConversationName == null) {
      StringBuilder sb = new StringBuilder();

      List<ConversationMember> members = getMemberList(conversation);
      for (int i = 0; i < members.size() - 1; i++) {
        sb.append(members.get(i).getDisplayName().trim());
        sb.append(", ");
      }

      // add last member
      sb.append(members.get(members.size() - 1).getDisplayName().trim());

      groupConversationName = sb.toString().trim();
    }

    return groupConversationName;
  }

  public static ConversationPreview getMessagePreview(
      Conversation conversation, String currentUserId) {
    // for newly created conversation
    if (conversation.getSeenStatusTracker() == null
        && conversation.getConversationPreview() == null) {
      return null;
    }
    return conversation.getConversationPreview();
  }

  public static boolean checkIsSeen(Conversation conversation, String currentUserId) {
    // for newly created conversation
    if (conversation.getSeenStatusTracker() == null
        && conversation.getConversationPreview() == null) {
      return false;
    }
    long currentSeenMessageNo =
        conversation.getSeenStatusTracker().getCurrentSeenMessageNo(currentUserId);
    return currentSeenMessageNo >= conversation.getCurrentMessageNo();
  }

  public static OffsetDateTime getUpdateAtTime(Conversation conversation) {
    return conversation.getUpdatedAt();
  }

  public static List<String> getConversationAvatar(
      Conversation conversation, String currentUserId, String requestId) {
    if (ConversationType.GROUP.equals(conversation.getConversationType())) {
      return getAvatarForGroupConversation(conversation);
    } else {
      return getAvatarForDirectConversation(conversation, currentUserId, requestId);
    }
  }

  private static List<String> getAvatarForDirectConversation(
      Conversation conversation, String currentUserId, String requestId) {
    // return the other member's avatar as the avatar of the conversation
    List<String> avatarList = new ArrayList<>();
    List<ConversationMember> members = getMemberList(conversation);

    for (ConversationMember member : members) {
      boolean isOtherUser = !member.getId().equals(currentUserId);
      if (isOtherUser) {
        avatarList.add(member.getAvatar());
        return avatarList;
      }
    }

    if (checkIfSelfConversation(conversation, currentUserId)) {
      avatarList.add(members.get(0).getAvatar());
      return avatarList;
    }

    throw new ApplicationException(ErrorCode.MESSAGING_ERROR3, requestId);
  }

  private static boolean checkIfSelfConversation(Conversation conversation, String userId) {
    List<ConversationMember> members = getMemberList(conversation);

    // check if all members in the conversation is the current user
    return members.stream().allMatch(u -> u.getId().equals(userId));
  }

  private static List<String> getAvatarForGroupConversation(Conversation conversation) {
    List<String> avatarList = new ArrayList<>();
    // if the group has an avatar, return it
    if (conversation.getGroupConversationAvatar() != null) {
      avatarList.add(conversation.getGroupConversationAvatar());
    } else {
      // else return list of all first 3 member avatars
      List<ConversationMember> members = getMemberList(conversation);
      for (int i = 0; i < Math.min(3, members.size()); i++) {
        avatarList.add(members.get(i).getAvatar());
      }
    }
    return avatarList;
  }

  public static void addMessageToConversation(
      Conversation conversation, ConversationMessage message) {
    // update conversation preview (last message)
    ConversationPreview conversationPreview =
        ConversationPreview.builder()
            //            .lastMessageSender(message.getSenderId())
            .previewContent(message.getContent())
            .lastUpdated(message.getCreatedAt())
            .build();
    conversation.setConversationPreview(conversationPreview);

    // update conversation's current message no
    long newCurrentMessageNo = conversation.getCurrentMessageNo() + 1L;
    conversation.setCurrentMessageNo(newCurrentMessageNo);

    // update conversation updated at
    conversation.setUpdatedAt(message.getCreatedAt());
  }

  public static Conversation createDirectConversation(ChatUser user1, ChatUser user2) {

    List<ConversationMember> members = new ArrayList<>();
    members.add(convertToConversationMember(user1));
    members.add(convertToConversationMember(user2));

    SeenStatusTracker seenStatusTracker = new SeenStatusTracker();
    List<String> memberIds = List.of(user1.getId(), user2.getId());
    seenStatusTracker.init(memberIds);

    Conversation conversation =
        builder()
            .conversationType(ConversationType.DIRECT)
            .seenStatusTracker(seenStatusTracker)
            .memberDetails(contructMemberMap(members))
            .memberIds(memberIds)
            .build();

    ConversationPreview conversationPreview =
        ConversationUtils.createConversationPreview(conversation, PreviewType.CONVERSATION_CREATED);
    conversation.setConversationPreview(conversationPreview);

    return conversation;
  }

  public static Conversation createSelfConversation(ChatUser user1) {
    List<ConversationMember> members = new ArrayList<>();
    members.add(convertToConversationMember(user1));

    SeenStatusTracker seenStatusTracker = new SeenStatusTracker();
    seenStatusTracker.init(List.of(user1.getId()));

    List<String> memberIds = List.of(user1.getId()); // only one member himself

    Conversation conversation =
            builder()
                    .conversationType(ConversationType.SELF)
                    .seenStatusTracker(seenStatusTracker)
                    .memberDetails(contructMemberMap(members))
                    .memberIds(memberIds)
                    .build();

    ConversationPreview conversationPreview =
            ConversationUtils.createConversationPreview(conversation, PreviewType.CONVERSATION_CREATED);
    conversation.setConversationPreview(conversationPreview);

    return conversation;
  }
  public static Conversation createGroupConversation(
      List<ChatUser> chatUsers, String conversationName) {
    // create a group conversation for those chat users
    List<ConversationMember> conversationMembers =
        chatUsers.stream()
            .map(
                u ->
                    ConversationMember.builder()
                        .id(u.getId())
                        .displayName(u.getDisplayName())
                        .avatar(u.getAvatar())
                        .build())
            .toList();
    List<String> memberIds = chatUsers.stream().map(u -> u.getId()).toList();
    SeenStatusTracker seenStatusTracker = new SeenStatusTracker();
    seenStatusTracker.init(memberIds);

    Conversation conversation =
        builder()
            .conversationType(ConversationType.GROUP)
            .groupConversationName(conversationName)
            .memberDetails(contructMemberMap(conversationMembers))
            .memberIds(memberIds)
            .build();
    ConversationPreview conversationPreview =
        createConversationPreview(conversation, PreviewType.CONVERSATION_CREATED);
    conversation.setConversationPreview(conversationPreview);
    return conversation;
  }

  public static ConversationWithMessagesDTO convertToConversationWithMessageDTO(
      Conversation conversation, List<ConversationMessageDTO> conversationMessageDTOs) {
    return ConversationWithMessagesDTO.builder()
        .conversationId(conversation.getId())
        .conversationCurrentMessageNo(conversation.getCurrentMessageNo())
        .seenStatusTracker(conversation.getSeenStatusTracker())
        .members(getMemberList(conversation))
        .messages(conversationMessageDTOs)
        .build();
  }

  public static ConversationMember convertToConversationMember(ChatUser user) {
    return ConversationMember.builder()
        .id(user.getId())
        .avatar(user.getAvatar())
        .displayName(user.getDisplayName())
        .build();
  }

  public static ConversationPreview createConversationPreview(
      Conversation conversation, PreviewType type, ConversationMessage lastMessage) {
    ConversationPreview preview = null;
    if (type == PreviewType.CONVERSATION_CREATED) {
      preview =
          ConversationPreview.builder()
              .previewContent("Conversation created") // TODO: this should be a constant
              .lastUpdated(conversation.getCreatedAt())
              .previewType(type)
              .build();
    } else if (type == PreviewType.USER_ADDED) {
      preview =
          ConversationPreview.builder()
              .previewContent("User added") // TODO: this should be a constant
              .lastUpdated(conversation.getUpdatedAt())
              .previewType(type)
              .build();
    } else if (type == PreviewType.NEW_MESSAGE) {
      preview =
          ConversationPreview.builder()
              .previewContent(lastMessage.getContent())
              .lastUpdated(lastMessage.getCreatedAt())
              .previewType(type)
              .build();
    }

    return preview;
  }

  public static ConversationPreview createConversationPreview(
      Conversation conversation, PreviewType type) {
    return createConversationPreview(conversation, type, null);
  }

  private static Map<String, ConversationMember> contructMemberMap(List<ConversationMember> members) {
    Map<String, ConversationMember> memberMap = new HashMap<>();
    for (ConversationMember member : members) {
      memberMap.put(member.getId(), member);
    }

    return memberMap;
  }


  public static List<ConversationMember> getMemberList(Conversation conversation) {
    return new ArrayList<>(conversation.getMemberDetails().values());
  }

  public static void addMemberToConversation(Conversation conversation, ConversationMember member) {
    if (conversation.getMemberIds().contains(member.getId())) {
      log.warn("User {} is already in conversation {}", member.getId(), conversation.getId());
      return;
    }

    conversation.getMemberDetails().put(member.getId(), member);
    conversation.getMemberIds().add(member.getId());
  }

  public static boolean checkUserInConversation(ChatUser chatUser, Conversation conversation) {
    return conversation.getMemberIds().stream()
            .anyMatch(memberId -> memberId.equals(chatUser.getId()));
  }
}
