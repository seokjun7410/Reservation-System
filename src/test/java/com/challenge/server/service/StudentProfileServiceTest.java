package com.challenge.server.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;
import com.challenge.server.domain.profile.StudentProfile;
import com.challenge.server.repository.StudentProfileRepository;

class StudentProfileServiceTest {

	@Mock
	private StudentProfileRepository studentProfileRepository;

	@InjectMocks
	private StudentProfileService studentProfileService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("정상적으로 StudentProfile 조회")
	void findByUserId_success() {
		// given
		Long userId = 1L;
		StudentProfile profile = new StudentProfile();
		when(studentProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

		// when
		StudentProfile result = studentProfileService.findByUserId(userId);

		// then
		assertThat(result).isEqualTo(profile);
		verify(studentProfileRepository).findByUserId(userId);
	}

	@Test
	@DisplayName("StudentProfile 조회 실패 시 CustomException 발생")
	void findByUserId_notFound_throws() {
		// given
		Long userId = 2L;
		when(studentProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

		// when & then
		CustomException ex = catchThrowableOfType(
			() -> studentProfileService.findByUserId(userId),
			CustomException.class
		);

		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
		assertThat(ex.getMessage()).contains("StudentProfile not found");
	}
}
