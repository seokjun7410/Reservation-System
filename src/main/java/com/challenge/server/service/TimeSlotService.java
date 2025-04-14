package com.challenge.server.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.domain.time.TimeSlot;
import com.challenge.server.repository.custom.TimeSlotRepositoryCustom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeSlotService {
	private final TimeSlotRepositoryCustom timeSlotCustomRepository;

	/**
	 * 등록된 TimeSlot을 생성합니다.
	 *
	 * @param startTimes
	 * @param sessionLength
	 * @return
	 */
	public List<TimeSlot> getTimeSlot(List<LocalTime> startTimes, List<SessionLength> sessionLength) {
		List<TimeSlot> timeSlotList = timeSlotCustomRepository.findByStartTimesAndSessionLengthInMinutes(startTimes,
			sessionLength);

		if (timeSlotList.size() != startTimes.size() || timeSlotList.size() != sessionLength.size()) {
			log.debug("timeSlotList,size()= {}, startTimes.size() = {}, sessionLength.size() = {}", timeSlotList.size(),
				startTimes.size(), sessionLength.size());
			throw new CustomException(ErrorCode.NOT_NULL, "존재하지 않는 TimeSlot입니다. 관리자에게 문의해주세요");
		}

		return timeSlotList;
	}
}
