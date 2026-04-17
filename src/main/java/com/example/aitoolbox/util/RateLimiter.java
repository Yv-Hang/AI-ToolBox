package com.example.aitoolbox.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimiter {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    // 内存限流备选方案
    private final Map<String, RateLimitInfo> memoryRateLimits = new ConcurrentHashMap<>();

    /**
     * 限流检查
     * @param key 限流键，通常是用户ID或IP地址
     * @param limit 时间窗口内的最大请求数
     * @param windowMs 时间窗口大小（毫秒）
     * @return 是否允许请求
     */
    public boolean isAllowed(String key, int limit, long windowMs) {
        // 优先使用Redis限流
        if (redisTemplate != null) {
            try {
                String redisKey = "rate_limit:" + key;
                
                // 递增计数
                Long currentCount = redisTemplate.opsForValue().increment(redisKey);
                
                if (currentCount == 1) {
                    // 第一次请求，设置过期时间
                    redisTemplate.expire(redisKey, windowMs, TimeUnit.MILLISECONDS);
                }
                
                // 检查是否超过限制
                return currentCount <= limit;
            } catch (Exception e) {
                // Redis异常，降级到内存限流
                e.printStackTrace();
            }
        }

        // 内存限流备选方案
        return memoryRateLimit(key, limit, windowMs);
    }

    /**
     * 内存限流备选方案
     * @param key 限流键
     * @param limit 时间窗口内的最大请求数
     * @param windowMs 时间窗口大小（毫秒）
     * @return 是否允许请求
     */
    private boolean memoryRateLimit(String key, int limit, long windowMs) {
        long now = System.currentTimeMillis();
        RateLimitInfo info = memoryRateLimits.get(key);

        if (info == null || now - info.getLastResetTime() > windowMs) {
            // 新的时间窗口
            memoryRateLimits.put(key, new RateLimitInfo(1, now));
            return true;
        } else {
            // 在时间窗口内
            if (info.getCount() < limit) {
                info.incrementCount();
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 用户级限流
     * @param userId 用户ID
     * @return 是否允许请求
     */
    public boolean isUserAllowed(Long userId) {
        // 每个用户每分钟最多10次请求
        return isAllowed("user:" + userId, 10, 60000);
    }

    /**
     * IP级限流
     * @param ip IP地址
     * @return 是否允许请求
     */
    public boolean isIpAllowed(String ip) {
        // 每个IP每分钟最多30次请求
        return isAllowed("ip:" + ip, 30, 60000);
    }

    /**
     * 接口级限流
     * @param apiName 接口名称
     * @return 是否允许请求
     */
    public boolean isApiAllowed(String apiName) {
        // 每个接口每分钟最多100次请求
        return isAllowed("api:" + apiName, 100, 60000);
    }

    /**
     * 限流信息类
     */
    private static class RateLimitInfo {
        private int count;
        private final long lastResetTime;

        public RateLimitInfo(int count, long lastResetTime) {
            this.count = count;
            this.lastResetTime = lastResetTime;
        }

        public int getCount() {
            return count;
        }

        public long getLastResetTime() {
            return lastResetTime;
        }

        public void incrementCount() {
            this.count++;
        }
    }
}