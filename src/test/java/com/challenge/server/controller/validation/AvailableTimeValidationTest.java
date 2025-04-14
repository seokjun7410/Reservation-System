package com.challenge.server.controller.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;
import com.challenge.server.dto.AvailableTimeData;
import com.challenge.server.dto.TimeSlotCreateRequest;

class AvailableTimeValidationTest {

	private AvailableTimeValidation validator;

	@BeforeEach
	void setUp() {
		validator = new AvailableTimeValidation();
	}

	@Test
	@DisplayName("중복 평일이 포함되면 예외 발생")
	void validOrException_DuplicateWeekday_ThrowsException() {
		// given
		AvailableTimeData monday1 = new AvailableTimeData(DayOfWeek.MONDAY, List.of(
			new TimeSlotCreateRequest("09:00", 30)
		));
		AvailableTimeData monday2 = new AvailableTimeData(DayOfWeek.MONDAY, List.of(
			new TimeSlotCreateRequest("10:00", 30)
		));

		List<AvailableTimeData> requestList = List.of(monday1, monday2);

		// when & then
		CustomException ex = assertThrows(CustomException.class, () -> {
			validator.validOrException(requestList);
		});
		assertEquals(ErrorCode.REQUEST_VALIDATION, ex.getErrorCode());
		assertTrue(ex.getMessage().contains("중복된 요청"));
	}

	@Test
	@DisplayName("30분 단위가 아닌 시간 입력 시 예외 발생")
	void validOrException_InvalidTimeFormat_ThrowsException() {
		AvailableTimeData tuesday = new AvailableTimeData(DayOfWeek.TUESDAY, List.of(
			new TimeSlotCreateRequest("09:10", 30) // 10분은 잘못된 입력
		));

		List<AvailableTimeData> requestList = List.of(tuesday);

		CustomException ex = assertThrows(CustomException.class, () -> {
			validator.validOrException(requestList);
		});

		assertEquals(ErrorCode.REQUEST_VALIDATION, ex.getErrorCode());
		assertTrue(ex.getMessage().contains("30분 단위만"));
	}

	@Test
	@DisplayName("예약 날짜가 오늘보다 이전이면 예외 발생")
	void validOrException_PastDate_ThrowsException() {
		LocalDate pastDate = LocalDate.now().minusDays(1);

		CustomException ex = assertThrows(CustomException.class, () -> {
			validator.validOrException(pastDate);
		});

		assertEquals(ErrorCode.REQUEST_VALIDATION, ex.getErrorCode());
		assertTrue(ex.getMessage().contains("예약 날짜는 오늘 이후"));
	}

	@Test
	@DisplayName("유효한 요청이면 예외 없이 통과")
	void validOrException_ValidRequest_DoesNotThrow() {
		AvailableTimeData thursday = new AvailableTimeData(DayOfWeek.THURSDAY, List.of(
			new TimeSlotCreateRequest("14:00", 30),
			new TimeSlotCreateRequest("14:30", 30)
		));

		assertDoesNotThrow(() -> validator.validOrException(List.of(thursday)));
		assertDoesNotThrow(() -> validator.validOrException(LocalDate.now().plusDays(1)));
	}
}
