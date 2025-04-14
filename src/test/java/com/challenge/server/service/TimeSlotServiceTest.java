package com.challenge.server.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.domain.time.TimeSlot;
import com.challenge.server.repository.TimeSlotRepository;
import com.challenge.server.repository.custom.TimeSlotRepositoryCustom;

class TimeSlotServiceTest {

	@Mock
	private TimeSlotRepositoryCustom timeSlotCustomRepository;

	@Mock
	private TimeSlotRepository timeSlotRepository;

	@InjectMocks
	private TimeSlotService timeSlotService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("정상적으로 TimeSlot 목록을 반환")
	void getTimeSlot_success() {
		// given
		List<LocalTime> startTimes = List.of(LocalTime.of(9, 0), LocalTime.of(10, 0));
		List<SessionLength> lengths = List.of(SessionLength.thirty, SessionLength.thirty);
		List<TimeSlot> expected = List.of(
			new TimeSlot(LocalTime.of(9, 0), SessionLength.thirty),
			new TimeSlot(LocalTime.of(10, 0), SessionLength.thirty)
		);

		when(timeSlotCustomRepository.findByStartTimesAndSessionLengthInMinutes(startTimes, lengths))
			.thenReturn(expected);

		// when
		List<TimeSlot> result = timeSlotService.getTimeSlot(startTimes, lengths);

		// then
		assertThat(result).hasSize(2);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	@DisplayName("조회된 TimeSlot 개수가 다르면 예외 발생")
	void getTimeSlot_mismatchedSize_throws() {
		List<LocalTime> startTimes = List.of(LocalTime.of(9, 0), LocalTime.of(10, 0));
		List<SessionLength> lengths = List.of(SessionLength.thirty, SessionLength.thirty);

		// 누락된 1개만 반환되도록 설정
		when(timeSlotCustomRepository.findByStartTimesAndSessionLengthInMinutes(startTimes, lengths))
			.thenReturn(List.of(new TimeSlot(LocalTime.of(9, 0), SessionLength.thirty)));

		// when & then
		CustomException ex = catchThrowableOfType(() ->
			timeSlotService.getTimeSlot(startTimes, lengths), CustomException.class);

		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_NULL);
		assertThat(ex.getMessage()).contains("존재하지 않는 TimeSlot");
	}
}
