package com.challenge.server.service;

import java.time.Duration;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.challenge.server.comoon.AvailableTimeCacheKeyUtil;
import com.challenge.server.dto.DailyAvailabilityResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailableTimeCacheService {

	private static final int TTL = 30;
	private final AvailableTimeCacheKeyUtil keyUtil;
	@Qualifier(value = "redisDailyAvailabilityResponseTemplate")
	@Autowired
	private RedisTemplate<String, DailyAvailabilityResponse> redisTemplate;

	public String generateKey(LocalDate date, int sessionLength) {
		return keyUtil.generateKey(date, sessionLength);
	}

	public DailyAvailabilityResponse get(String cacheKey) {
		return redisTemplate.opsForValue().get(cacheKey);
	}

	public boolean isHit(DailyAvailabilityResponse cached) {
		return cached != null;
	}

	public void set(String cacheKey, DailyAvailabilityResponse response) {
		redisTemplate.opsForValue().set(cacheKey, response, Duration.ofSeconds(TTL)); // TTL 60초
	}

	public void evict(String cacheKey) {
		redisTemplate.delete(cacheKey);
	}
}
