package com.challenge.server.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;
import com.challenge.server.domain.profile.TutorProfile;
import com.challenge.server.domain.time.AvailableTime;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.domain.time.TimeSlot;
import com.challenge.server.repository.AvailableTimeRepository;
import com.challenge.server.repository.custom.AvailableTimeBulkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvailableTimeService {

	private final AvailableTimeBulkRepository availableTimeBulkRepository;
	private final AvailableTimeRepository availableTimeRepository;

	@Transactional
	public void saveAll(List<AvailableTime> insertList) {
		availableTimeBulkRepository.saveAll(insertList);
	}

	@Transactional
	public void deleteAllById(List<AvailableTime> availableTimes) {
		availableTimeBulkRepository.deleteAll(availableTimes);
	}

	public boolean isOwner(TutorProfile tutorProfile, List<AvailableTime> availableTimes) {
		return availableTimes.stream().allMatch(tutorProfile::isOwner);
	}

	/**
	 * Id List를 이용해 해당하는 사용 가능한 시간대를 조회합니다.
	 *
	 * @param availableTimeIds
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<AvailableTime> findTutorIdByIdIn(List<Long> availableTimeIds) {
		List<AvailableTime> availableTimes = availableTimeRepository.findByIdIn(availableTimeIds);
		if (availableTimes.isEmpty()) {
			throw new CustomException(ErrorCode.NOT_FOUND, "availableTimeIds 에 해당하는 튜터 프로필이 없습니다.");
		}
		return availableTimes;
	}

	/**
	 * sessionLength 에 해당하는 사용 가능한 시간대를 조회합니다. TimeSlot이 중복되지 않도록 필터링합니다.
	 *
	 * @param sessionLength
	 * @return
	 */
	public List<AvailableTime> findAllBySessionLengthDistinctTimeSlot(int sessionLength) {
		List<AvailableTime> allBySessionLength = availableTimeRepository.findAllBySessionLength(
			SessionLength.from(sessionLength));
		Set<String> seen = new HashSet<>();
		return allBySessionLength.stream()
			.filter(at -> seen.add(generateKey(at.getTimeSlot())))
			.toList();
	}

	/**
	 * tutorId, sessionLength, dayOfWeek, start 를 이용해 해당하는 사용 가능한 시간대를 조회합니다.
	 *
	 * @param tutorId
	 * @param sessionLength
	 * @param dayOfWeek
	 * @param start
	 * @return
	 */
	@Transactional(readOnly = true)
	public AvailableTime findByTutorIdAndSessionLengthAndDayOfWeek(Long tutorId, SessionLength sessionLength,
		DayOfWeek dayOfWeek, LocalTime start) {
		return availableTimeRepository.findByTutorIdAndSessionLengthAndDayOfWeek(tutorId, sessionLength, dayOfWeek,
				start)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당하는 AvailableTime 이 없습니다."));
	}

	private String generateKey(TimeSlot ts) {
		return ts.getStartTime() + "|" + ts.getSessionLength();
	}
}
