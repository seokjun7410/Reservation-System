package com.challenge.server.controller.validation;

import java.time.LocalDate;

import com.challenge.server.comoon.annotaion.ComponentService;
import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;
import com.challenge.server.dto.NewReservationRequest;

@ComponentService
public class ReservationValidation {
	public void validOrException(NewReservationRequest request) {
		if (request.getReservationDate().isBefore(LocalDate.now())) {
			throw new CustomException(ErrorCode.REQUEST_VALIDATION, "예약 날짜는 오늘 이후여야 합니다.");
		}
	}
}
