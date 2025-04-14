package com.challenge.server.controller.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;
import com.challenge.server.dto.NewReservationRequest;

class ReservationValidationTest {

	private ReservationValidation reservationValidation;

	@BeforeEach
	void setUp() {
		reservationValidation = new ReservationValidation();
	}

	@Test
	@DisplayName("예약 날짜가 과거일 경우 예외 발생")
	void validOrException_PastDate_ThrowsException() {
		// given
		NewReservationRequest request = new NewReservationRequest();
		request.setReservationDate(LocalDate.now().minusDays(1)); // 어제

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			reservationValidation.validOrException(request);
		});

		assertEquals(ErrorCode.REQUEST_VALIDATION, exception.getErrorCode());
		assertTrue(exception.getMessage().contains("예약 날짜는 오늘 이후여야 합니다."));
	}

	@Test
	@DisplayName("예약 날짜가 오늘이거나 미래면 예외 없음")
	void validOrException_ValidDate_DoesNotThrow() {
		// given
		NewReservationRequest todayRequest = new NewReservationRequest();
		todayRequest.setReservationDate(LocalDate.now());

		NewReservationRequest futureRequest = new NewReservationRequest();
		futureRequest.setReservationDate(LocalDate.now().plusDays(1));

		// then
		assertDoesNotThrow(() -> reservationValidation.validOrException(todayRequest));
		assertDoesNotThrow(() -> reservationValidation.validOrException(futureRequest));
	}
}
