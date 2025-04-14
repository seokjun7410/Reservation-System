package com.challenge.server.repository.custom;

import java.time.LocalTime;
import java.util.List;

import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.domain.time.TimeSlot;

public interface TimeSlotRepositoryCustom {
	/**
	 * startTime + sessionLength 조합으로 TimeSlot 조회
	 *
	 * @param startTimes
	 * @param sessionLength
	 * @return
	 */
	List<TimeSlot> findByStartTimesAndSessionLengthInMinutes(List<LocalTime> startTimes,
		List<SessionLength> sessionLength);

}
