package com.challenge.server.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;

import com.challenge.server.comoon.annotaion.ComponentService;
import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;
import com.challenge.server.domain.profile.TutorProfile;
import com.challenge.server.domain.time.AvailableTime;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.dto.AvailableSlotResponse;
import com.challenge.server.dto.AvailableTimeData;
import com.challenge.server.dto.DailyAvailabilityResponse;
import com.challenge.server.dto.TimeSlotCreateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ComponentService
@RequiredArgsConstructor
@Slf4j
public class AvailableTimeComponentService {

	private final AvailableTimeService availableTimeService;
	private final TutorProfileService tutorProfileService;
	private final ReservationService reservationService;
	private final TimeSlotService timeSlotService;
	private final AvailableTimeCacheService availableTimeCacheService;

	/**
	 * 등록된 튜터의 사용 가능한 시간대를 등록합니다.
	 *
	 * @param userId
	 * @param requests
	 */
	public void registerAvailableTimes(Long userId, List<AvailableTimeData> requests) {
		// 1. 인증된 사용자 ID를 기반으로 TutorProfile 조회
		TutorProfile tutorProfile = tutorProfileService.getTutorProfileByUserId(userId);

		// 2. 각 요청(AvailableTimeData)을 처리하여 AvailableTime 엔티티 생성
		List<AvailableTime> insertList = createAvailableTimes(requests, tutorProfile);

		// 3. 생성된 AvailableTime 목록을 저장
		try {
			availableTimeService.saveAll(insertList);
		} catch (DataIntegrityViolationException ex) {
			throw new CustomException(ErrorCode.CREATE_CONFLICT, "중복된 AvailableTime 등록 요청입니다.");
		}
	}

	/**
	 * 사용 가능한 시간대를 삭제합니다.
	 *
	 * @param userId
	 * @param availableTimeIds
	 */
	public void deleteAllByIds(Long userId, List<Long> availableTimeIds) {
		TutorProfile tutorProfile = tutorProfileService.getTutorProfileByUserId(userId);

		List<AvailableTime> availableTimes = availableTimeService.findTutorIdByIdIn(availableTimeIds);

		if (!availableTimeService.isOwner(tutorProfile, availableTimes)) {
			throw new CustomException(ErrorCode.UNAUTHORIZED, "해당 사용자는 이 튜터의 사용 가능한 시간을 삭제할 수 없습니다.");
		}

		availableTimeService.deleteAllById(availableTimes);
	}

	/**
	 * 기간 내에 (예약된 시간대와 겹치지 않는) 예약 가능한 시간대를 조회합니다.
	 *
	 * @param startDate
	 * @param endDate
	 * @param sessionLength
	 * @return
	 */
	public List<DailyAvailabilityResponse> getAvailableTimes(LocalDate startDate, LocalDate endDate,
		int sessionLength) {
		// 1. 예약 정보 조회 (기간 내)
		Map<LocalDate, Set<Long>> reservedMap = reservationService.findMapReservedIdsBetween(startDate,
			endDate);

		// 2. 요일별 AvailableTime 조회
		List<AvailableTime> allAvailable = availableTimeService.findAllBySessionLengthDistinctTimeSlot(sessionLength);
		Map<DayOfWeek, List<AvailableTime>> availableByDay = allAvailable.stream()
			.collect(Collectors.groupingBy(AvailableTime::getDayOfWeek));

		// 3. 날짜별 응답 구성
		List<DailyAvailabilityResponse> result = new ArrayList<>();
		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			String cacheKey = availableTimeCacheService.generateKey(date, sessionLength);

			DailyAvailabilityResponse cached = availableTimeCacheService.get(cacheKey);
			if (availableTimeCacheService.isHit(cached)) {
				// 캐시에서 조회
				log.trace("[hit] DailyAvailabilityResponse cached");
				result.add(cached);
				continue;
			}

			DayOfWeek dow = date.getDayOfWeek();

			List<AvailableTime> candidates = availableByDay.getOrDefault(dow, List.of());
			Set<Long> reservedIds = reservedMap.getOrDefault(date, Set.of());

			List<AvailableSlotResponse> slots = candidates.stream()
				.filter(at -> !reservedIds.contains(at.getId()))
				.map(at -> new AvailableSlotResponse(
					at.getTimeSlot().getStartTime(),
					at.getTimeSlot().getStartTime().plusMinutes(at.getTimeSlot().getSessionLength().getLength()),
					at.getTimeSlot().getSessionLength().getLength()))
				.toList();

			DailyAvailabilityResponse response = new DailyAvailabilityResponse(date, slots);
			availableTimeCacheService.set(cacheKey, response);
			result.add(response);
		}
		return result;
	}

	/**
	 * List<AvailableTimeData> 를 분해하여 List<AvailableTime> 엔티티를 생성합니다.
	 *
	 * @param requests
	 * @param tutorProfile
	 * @return
	 */
	private List<AvailableTime> createAvailableTimes(List<AvailableTimeData> requests,
		TutorProfile tutorProfile) {
		List<AvailableTime> insertList = new ArrayList<>();

		for (AvailableTimeData request : requests) {
			DayOfWeek day = request.getDayOfWeek();
			// 요청된 요일에 대해 각 시작 시간마다 AvailableTime 생성
			List<TimeSlotCreateRequest> slotRequests = request.getTimeSlotCreateRequestList();

			List<LocalTime> startTimes = slotRequests.stream()
				.map(m -> LocalTime.of(Integer.parseInt(m.getStartTime().split(":")[0]),
					Integer.parseInt(m.getStartTime().split(":")[1])))
				.toList();

			List<SessionLength> sessionLengths = slotRequests.stream()
				.map(dto -> SessionLength.from(dto.getSessionLength()))
				.toList();

			List<AvailableTime> availableTimes = timeSlotService.getTimeSlot(startTimes, sessionLengths)
				.stream()
				.map(m -> new AvailableTime(tutorProfile, m, day))
				.collect(Collectors.toList());

			insertList.addAll(availableTimes);
		}
		return insertList;
	}
}
