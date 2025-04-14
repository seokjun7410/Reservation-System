package com.challenge.server.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.challenge.server.comoon.AvailableTimeCacheKeyUtil;
import com.challenge.server.dto.DailyAvailabilityResponse;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
	AvailableTimeCacheService.class,
	AvailableTimeCacheServiceTest.TestConfig.class
})
class AvailableTimeCacheServiceTest {

	@Autowired
	private AvailableTimeCacheService availableTimeCacheService;
	@Autowired
	@Qualifier("redisDailyAvailabilityResponseTemplate")
	private RedisTemplate<String, DailyAvailabilityResponse> redisTemplate;
	private ValueOperations<String, DailyAvailabilityResponse> valueOperations;

	@BeforeEach
	void setUp() {
		// redisTemplate.opsForValue() 호출 시 설정한 목(ValueOperations)을 가져옵니다.
		valueOperations = redisTemplate.opsForValue();
	}

	@Test
	void testGenerateKey() {
		// given
		LocalDate date = LocalDate.of(2023, 5, 31);
		int sessionLength = 60;
		String expectedKey = "available:2023-05-31:60";

		// when
		String actualKey = availableTimeCacheService.generateKey(date, sessionLength);

		// then
		assertThat(actualKey).isEqualTo(expectedKey);
	}

	@Test
	void testGet() {
		// given
		String cacheKey = "available:2023-05-31:60";
		DailyAvailabilityResponse expectedResponse = mock(DailyAvailabilityResponse.class);
		when(valueOperations.get(cacheKey)).thenReturn(expectedResponse);

		// when
		DailyAvailabilityResponse actualResponse = availableTimeCacheService.get(cacheKey);

		// then
		assertThat(actualResponse).isEqualTo(expectedResponse);
		verify(valueOperations, times(1)).get(cacheKey);
	}

	@Test
	void testIsHit_whenResponseIsNotNull() {
		// given
		DailyAvailabilityResponse response = mock(DailyAvailabilityResponse.class);

		// when
		boolean isHit = availableTimeCacheService.isHit(response);

		// then
		assertThat(isHit).isTrue();
	}

	@Test
	void testIsHit_whenResponseIsNull() {
		// given
		DailyAvailabilityResponse response = null;

		// when
		boolean isHit = availableTimeCacheService.isHit(response);

		// then
		assertThat(isHit).isFalse();
	}

	@Test
	void testSet() {
		// given
		String cacheKey = "available:2023-05-31:60";
		DailyAvailabilityResponse response = mock(DailyAvailabilityResponse.class);
		Duration expectedTtl = Duration.ofSeconds(30); // TTL 값

		// when
		availableTimeCacheService.set(cacheKey, response);

		// then
		verify(valueOperations, times(1))
			.set(eq(cacheKey), eq(response), eq(expectedTtl));
	}

	@Test
	void testEvict() {
		// given
		String cacheKey = "available:2023-05-31:60";

		// when
		availableTimeCacheService.evict(cacheKey);

		// then
		verify(redisTemplate, times(1)).delete(cacheKey);
	}

	@Configuration
	static class TestConfig {
		@Bean
		public AvailableTimeCacheKeyUtil availableTimeCacheKeyUtil() {
			return new AvailableTimeCacheKeyUtil() {
				@Override
				public String generateKey(LocalDate date, int sessionLength) {
					return String.format("available:%s:%d", date, sessionLength);
				}
			};
		}

		@Bean(name = "redisDailyAvailabilityResponseTemplate")
		public RedisTemplate<String, DailyAvailabilityResponse> redisTemplate() {
			// RedisTemplate 목 객체 생성
			RedisTemplate<String, DailyAvailabilityResponse> redisTemplate = mock(RedisTemplate.class);
			// ValueOperations 목 객체 생성
			ValueOperations<String, DailyAvailabilityResponse> valueOps = mock(ValueOperations.class);
			// lenient를 사용하여 불필요한 스터빙 경고를 피합니다.
			Mockito.lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
			return redisTemplate;
		}
	}
}
