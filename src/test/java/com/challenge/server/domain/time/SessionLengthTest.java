package com.challenge.server.domain.time;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionLengthTest {

	@Test
	@DisplayName("30 입력 시 thirty 반환")
	void from_ReturnsThirty_WhenValueIs30() {
		SessionLength result = SessionLength.from(30);
		assertEquals(SessionLength.thirty, result);
	}

	@Test
	@DisplayName("60 입력 시 sixty 반환")
	void from_ReturnsSixty_WhenValueIs60() {
		SessionLength result = SessionLength.from(60);
		assertEquals(SessionLength.sixty, result);
	}

	@Test
	@DisplayName("지원되지 않는 값 입력 시 IllegalArgumentException 발생")
	void from_ThrowsException_WhenValueIsInvalid() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			SessionLength.from(45);
		});
		assertTrue(ex.getMessage().contains("Invalid sessionLength"));
	}
}
