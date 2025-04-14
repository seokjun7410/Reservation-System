package com.challenge.server.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;

class JwtServiceTest {

	private final JwtService jwtService = new JwtService();

	@Test
	@DisplayName("정상적인 숫자 토큰이면 Long으로 반환")
	void extractUserId_validToken() {
		// given
		String token = "123";

		// when
		Long userId = jwtService.extractUserId(token);

		// then
		assertThat(userId).isEqualTo(123L);
	}

	@Test
	@DisplayName("Bearer prefix가 있으면 제거하고 파싱")
	void extractUserId_bearerToken() {
		String token = "Bearer 456";

		Long userId = jwtService.extractUserId(token);

		assertThat(userId).isEqualTo(456L);
	}

	@Test
	@DisplayName("빈 토큰이면 IllegalArgumentException 발생")
	void extractUserId_emptyToken_throws() {
		assertThatThrownBy(() -> jwtService.extractUserId(" "))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("missing");
	}

	@Test
	@DisplayName("null 토큰이면 IllegalArgumentException 발생")
	void extractUserId_nullToken_throws() {
		assertThatThrownBy(() -> jwtService.extractUserId(null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("missing");
	}

	@Test
	@DisplayName("숫자가 아닌 토큰이면 CustomException 발생")
	void extractUserId_invalidFormat_throws() {
		String token = "Bearer abc";

		CustomException ex = catchThrowableOfType(() -> jwtService.extractUserId(token), CustomException.class);

		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
		assertThat(ex.getMessage()).contains("Invalid token format");
	}
}
