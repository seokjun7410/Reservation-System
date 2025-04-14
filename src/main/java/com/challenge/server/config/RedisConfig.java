package com.challenge.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.challenge.server.dto.DailyAvailabilityResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfig {

	@Bean(name = "redisDailyAvailabilityResponseTemplate")
	public RedisTemplate<String, DailyAvailabilityResponse> redisDailyAvailabilityResponseTemplate(
		RedisConnectionFactory factory) {
		RedisTemplate<String, DailyAvailabilityResponse> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);

		ObjectMapper objectMapper = JsonMapper.builder()
			.addModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.build();

		Jackson2JsonRedisSerializer<DailyAvailabilityResponse> serializer =
			new Jackson2JsonRedisSerializer<>(objectMapper, DailyAvailabilityResponse.class);

		template.setDefaultSerializer(serializer);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(serializer);

		return template;
	}
}
