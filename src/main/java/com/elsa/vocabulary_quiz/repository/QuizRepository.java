package com.elsa.vocabulary_quiz.repository;

import com.elsa.vocabulary_quiz.cache.MockRedisTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizRepository {

    private final HashOperations<String, String, Object> hashOps;

    private static final String USER_MAPPING_KEY = "userId-username:";
    private static final String ROOM_LEADERBOARD_MAPPING_KEY = "room-leaderboard:";

    public QuizRepository(MockRedisTemplate redisTemplate) {
        this.hashOps = redisTemplate.opsForHash();  // Use the same HashOperations for both user mappings and leaderboard
    }

    public void addUserIdToUsernameMapping(String userId, String username) {
        hashOps.put(USER_MAPPING_KEY, userId, username);  // Store the userId-to-username mapping
    }

    public String getUsernameFromUserId(Long userId) {
        return (String) hashOps.get(USER_MAPPING_KEY, String.valueOf(userId));  // Retrieve the username for the userId
    }

    public void incrementUserScore(String roomId, Long userId, int incrementBy) {
        hashOps.increment(ROOM_LEADERBOARD_MAPPING_KEY + roomId, String.valueOf(userId), incrementBy);  // Increment the user's score in the leaderboard
    }

    public Map<Long, Long> getLeaderboard(String roomId) {
        Map<String, Object> entries = hashOps.entries(ROOM_LEADERBOARD_MAPPING_KEY + roomId);// Retrieve the leaderboard for the quiz room

        return entries.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> Long.parseLong(entry.getKey()),
                        entry -> (Long) entry.getValue()
                ));
    }
}
