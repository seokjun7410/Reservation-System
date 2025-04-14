package com.challenge.server.controller.validation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.challenge.server.comoon.annotaion.ComponentService;
import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;
import com.challenge.server.dto.AvailableTimeData;
import com.challenge.server.dto.TimeSlotCreateRequest;

@ComponentService
public class AvailableTimeValidation {
	public void validOrException(List<AvailableTimeData> requestList) {
		// 평일 중복 검증 (월 ~ 금)
		// 평일에 대해 중복되는 DTO가 있다면 요청이 잘못된 것으로 판단하여 400 Bad Request를 응답
		Set<DayOfWeek> weekdaySet = new HashSet<>();
		for (AvailableTimeData req : requestList) {
			DayOfWeek day = req.getDayOfWeek();
			if (!day.equals("SATURDAY") && !day.equals("SUNDAY")) {
				if (!weekdaySet.add(day)) {
					throw new CustomException(ErrorCode.REQUEST_VALIDATION,
						"요청이 잘못되었습니다. 평일에 대해 중복된 요청이 있습니다. 요일: " + day);
				}
			}

			List<TimeSlotCreateRequest> timeSlotCreateRequestList = req.getTimeSlotCreateRequestList();
			for (TimeSlotCreateRequest timeSlotCreateRequest : timeSlotCreateRequestList) {
				String startTime = timeSlotCreateRequest.getStartTime().split(":")[1];
				if (!(startTime.equals("30") || startTime.equals("00"))) {
					throw new CustomException(ErrorCode.REQUEST_VALIDATION,
						"요청이 잘못되었습니다. 30분 단위만 가능합니다.: " + startTime);
				}
			}
		}
	}

	public void validOrException(LocalDate date) {
		if (date.isBefore(LocalDate.now())) {
			throw new CustomException(ErrorCode.REQUEST_VALIDATION, "예약 날짜는 오늘 이후여야 합니다.");
		}
	}
}
