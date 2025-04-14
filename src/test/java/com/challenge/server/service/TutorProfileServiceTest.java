package com.challenge.server.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;
import com.challenge.server.domain.profile.TutorProfile;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.domain.user.User;
import com.challenge.server.dto.TutorResponse;
import com.challenge.server.repository.TutorProfileRepository;

class TutorProfileServiceTest {

	@Mock
	private TutorProfileRepository tutorProfileRepository;

	@InjectMocks
	private TutorProfileService tutorProfileService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("userId로 TutorProfile 조회 성공")
	void getTutorProfileByUserId_success() {
		Long userId = 1L;
		TutorProfile profile = new TutorProfile("소개", new User("tutor@test.com", "Tutor"));
		when(tutorProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

		TutorProfile result = tutorProfileService.getTutorProfileByUserId(userId);

		assertThat(result).isEqualTo(profile);
		verify(tutorProfileRepository).findByUserId(userId);
	}

	@Test
	@DisplayName("userId로 TutorProfile 조회 실패 시 예외 발생")
	void getTutorProfileByUserId_notFound() {
		Long userId = 999L;
		when(tutorProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

		CustomException ex = catchThrowableOfType(() ->
			tutorProfileService.getTutorProfileByUserId(userId), CustomException.class);

		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
		assertThat(ex.getMessage()).contains("userId 999에 해당하는 튜터 프로필을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("예약 가능한 튜터 목록 조회 성공 ")
	void getAvailableTutorProfile_success_withMock() {
		// given
		LocalDate date = LocalDate.now();
		LocalTime time = LocalTime.of(9, 0);
		SessionLength length = SessionLength.thirty;

		// mock TutorProfile 1
		TutorProfile tutor1 = mock(TutorProfile.class);
		User user1 = mock(User.class);
		when(tutor1.getId()).thenReturn(1L);
		when(tutor1.getUser()).thenReturn(user1);
		when(tutor1.getBio()).thenReturn("소개1");
		when(user1.getName()).thenReturn("Alice");

		// mock TutorProfile 2
		TutorProfile tutor2 = mock(TutorProfile.class);
		User user2 = mock(User.class);
		when(tutor2.getId()).thenReturn(2L);
		when(tutor2.getUser()).thenReturn(user2);
		when(tutor2.getBio()).thenReturn("소개2");
		when(user2.getName()).thenReturn("Bob");

		// 레포지토리 결과 stub
		when(tutorProfileRepository.findAvailableTutorsByDateAndTimeAndSessionLength(
			eq(date), eq(date.getDayOfWeek()), eq(time), eq(length)
		)).thenReturn(List.of(tutor1, tutor2));

		// when
		List<TutorResponse> result = tutorProfileService.getAvailableTutorProfile(date, time, 30);

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getName()).isEqualTo("Alice");
		assertThat(result.get(1).getName()).isEqualTo("Bob");
	}

}
