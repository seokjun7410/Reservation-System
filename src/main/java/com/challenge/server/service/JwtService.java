package com.challenge.server.service;

import org.springframework.stereotype.Component;

import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;

@Component
public class JwtService {

	/**
	 * 제공된 JWT 토큰에서 사용자 ID를 추출합니다.
	 * 시간상 예제에서는 토큰이 사용자 ID 자체라고 가정합니다
	 */

	public Long extractUserId(String token) {
		if (token == null || token.trim().isEmpty()) {
			throw new IllegalArgumentException("Authorization token is missing");
		}
		if (token.startsWith("Bearer ")) {
			token = token.substring(7);
		}
		try {
			return Long.parseLong(token);
		} catch (NumberFormatException e) {
			throw new CustomException(ErrorCode.UNAUTHORIZED, "Invalid token format: unable to extract user id");
		}
	}
}
