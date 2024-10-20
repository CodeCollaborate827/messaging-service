package com.chat.messaging_service.document.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class SeenStatusTracker {
  @JsonProperty("tracker")
  private Map<String, Long> map = new HashMap<>();

  public Long getCurrentSeenMessageNo(String userId) {
    return map.getOrDefault(userId, 0L);
  }
  public void updateSeenMessageNo(String userId, Long messageNo) {
    // TODO: it should only update when messageNo > current value
    map.put(userId, messageNo);
  }

  public void init(List<String> memberId) {
    memberId.forEach(id -> map.put(id, 0L));

  }
}
