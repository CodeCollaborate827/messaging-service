package com.chat.messaging_service.utils;

import com.chat.messaging_service.document.ChatUser;
import com.chat.messaging_service.document.Conversation;
import com.chat.messaging_service.document.ConversationMessage;
import com.chat.messaging_service.document.objects.ConversationMember;
import com.chat.messaging_service.document.objects.ConversationPreview;
import com.chat.messaging_service.exception.ApplicationException;
import com.chat.messaging_service.exception.ErrorCode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConversationUtils {
  public static String constructConversationName(
      Conversation conversation, String currentUserId, String requestId) {
    if (conversation.isGroupConversation()) {
      return constructGroupConversationName(conversation);
    } else {
      return conversationDirectConversationName(conversation, currentUserId, requestId);
    }
  }

  private static String conversationDirectConversationName(
      Conversation conversation, String currentUserId, String requestId) {
    // a direct conversation only has 2 members
    // return name of the other user as the name for the conversation
    List<ConversationMember> members = conversation.getMembers();
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

      List<ConversationMember> members = conversation.getMembers();
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

    return Conversation.builder()
        .isGroupConversation(true)
        .groupConversationName(conversationName)
        .members(conversationMembers)
        .build();
  }

  public static List<String> getConversationAvatar(
      Conversation conversation, String currentUserId, String requestId) {
    if (conversation.isGroupConversation()) {
      return getAvatarForGroupConversation(conversation);
    } else {
      return getAvatarForDirectConversation(conversation, currentUserId, requestId);
    }
  }

  private static List<String> getAvatarForDirectConversation(
      Conversation conversation, String currentUserId, String requestId) {
    // return the other member's avatar as the avatar of the conversation
    List<String> avatarList = new ArrayList<>();
    List<ConversationMember> members = conversation.getMembers();

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
    List<ConversationMember> members = conversation.getMembers();

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
      List<ConversationMember> members = conversation.getMembers();
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
            .lastMessageSender(message.getSenderId())
            .lastMessageContent(message.getContent())
            .lastMessageTime(message.getCreatedAt())
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

    Conversation conversation =
        Conversation.builder().isGroupConversation(false).members(members).build();

    return conversation;
  }

  private static ConversationMember convertToConversationMember(ChatUser user) {
    return ConversationMember.builder()
        .id(user.getId())
        .avatar(user.getAvatar())
        .displayName(user.getDisplayName())
        .build();
  }
}
