package com.challenge.server.domain.profile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.challenge.server.domain.time.AvailableTime;
import com.challenge.server.domain.user.User;

class TutorProfileTest {

	@Test
	@DisplayName("AvailableTime의 소유자일 경우 true 반환")
	void isOwner_ReturnsTrue_WhenOwnerMatches() {
		// given
		User user = new User("tutor@email.com", "튜터");
		TutorProfile tutorProfile = new TutorProfile("소개글입니다~", user);

		AvailableTime availableTime = new AvailableTime();
		availableTime.setTutorProfile(tutorProfile);

		// tutorProfile의 ID 설정 (테스트용으로 강제 세팅)

		// when
		boolean result = tutorProfile.isOwner(availableTime);

		// then
		assertTrue(result);
	}

	@Test
	@DisplayName("AvailableTime의 소유자가 다를 경우 false 반환 (mock 기반)")
	void isOwner_ReturnsFalse_WhenOwnerDoesNotMatch() {
		// given
		TutorProfile owner2 = mock(TutorProfile.class);
		when(owner2.getId()).thenReturn(2L);

		TutorProfile realOwner = mock(TutorProfile.class);
		when(realOwner.getId()).thenReturn(1L);

		AvailableTime availableTime = mock(AvailableTime.class);
		when(availableTime.getTutorProfile()).thenReturn(realOwner);

		// when
		boolean result = owner2.isOwner(availableTime);

		// then
		assertFalse(result);
	}
}
