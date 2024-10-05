package com.chat.messaging_service.document.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeenStatusTracker {
    @JsonProperty("tracker")
    private Map<String, Long> map = new HashMap<>();


    public Long getCurrentSeenMessageNo(String userId) {
        return map.getOrDefault(userId, 0L);
    }
}
