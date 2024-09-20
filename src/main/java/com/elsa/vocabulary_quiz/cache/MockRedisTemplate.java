package com.elsa.vocabulary_quiz.cache;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MockRedisTemplate {

    // A map to simulate Redis hashes (stores both scores and user mappings)
    private final Map<String, Map<String, Object>> mockRedisData = new HashMap<>();

    public HashOperations<String, String, Object> opsForHash() {
        return new MockHashOperations();
    }

    // Mock hash operations
    private class MockHashOperations implements HashOperations<String, String, Object> {

        @Override
        public Map<String, Object> entries(String key) {
            return mockRedisData.getOrDefault(key, new HashMap<>());  // Get all key-value pairs for the Redis hash
        }

        @Override
        public Cursor<Map.Entry<String, Object>> scan(String key, ScanOptions options) {
            return null;
        }

        @Override
        public RedisOperations<String, ?> getOperations() {
            return null;
        }

        @Override
        public void put(String key, String field, Object value) {
            mockRedisData.computeIfAbsent(key, k -> new HashMap<>()).put(field, value);  // Store key-value pairs
        }

        @Override
        public Boolean putIfAbsent(String key, String hashKey, Object value) {
            return null;
        }

        @Override
        public List<Object> values(String key) {
            return null;
        }

        @Override
        public Long delete(String key, Object... hashKeys) {
            return null;
        }

        @Override
        public Boolean hasKey(String key, Object hashKey) {
            return null;
        }

        @Override
        public Object get(String key, Object hashKey) {
            return mockRedisData.getOrDefault(key, new HashMap<>()).get((String) hashKey);
        }

        @Override
        public List<Object> multiGet(String key, Collection<String> hashKeys) {
            return null;
        }

        @Override
        public Long increment(String key, String field, long delta) {
            Map<String, Object> hash = mockRedisData.computeIfAbsent(key, k -> new HashMap<>());

            try {
                Long currentValue = (Long) hash.getOrDefault(field, 0L);

                long newValue = currentValue + delta;
                hash.put(field, newValue);
                return newValue;
            } catch (Exception e){
                throw new UnsupportedOperationException("Increment operation is only supported for Long values.");
            }
        }

        @Override
        public Double increment(String key, String hashKey, double delta) {
            return null;
        }

        @Override
        public String randomKey(String key) {
            return null;
        }

        @Override
        public Map.Entry<String, Object> randomEntry(String key) {
            return null;
        }

        @Override
        public List<String> randomKeys(String key, long count) {
            return null;
        }

        @Override
        public Map<String, Object> randomEntries(String key, long count) {
            return null;
        }

        @Override
        public Set<String> keys(String key) {
            return null;
        }

        @Override
        public Long lengthOfValue(String key, String hashKey) {
            return null;
        }

        @Override
        public Long size(String key) {
            return null;
        }

        @Override
        public void putAll(String key, Map<? extends String, ?> m) {

        }
    }
}
