package com.challenge.server.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;
import com.challenge.server.domain.profile.TutorProfile;
import com.challenge.server.domain.time.AvailableTime;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.domain.time.TimeSlot;
import com.challenge.server.dto.AvailableTimeData;
import com.challenge.server.dto.DailyAvailabilityResponse;
import com.challenge.server.dto.TimeSlotCreateRequest;

class AvailableTimeComponentServiceTest {

	@InjectMocks
	private AvailableTimeComponentService componentService;

	@Mock
	private AvailableTimeService availableTimeService;
	@Mock
	private TutorProfileService tutorProfileService;
	@Mock
	private ReservationService reservationService;
	@Mock
	private TimeSlotService timeSlotService;
	@Mock
	private AvailableTimeCacheService availableTimeCacheService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void registerAvailableTimes_success() {
		Long userId = 1L;
		TutorProfile tutorProfile = mock(TutorProfile.class);
		when(tutorProfileService.getTutorProfileByUserId(userId)).thenReturn(tutorProfile);

		TimeSlotCreateRequest slotRequest = new TimeSlotCreateRequest("09:00", 60);
		AvailableTimeData data = new AvailableTimeData(DayOfWeek.MONDAY, List.of(slotRequest));
		List<AvailableTimeData> requestList = List.of(data);

		SessionLength length = SessionLength.from(60);
		LocalTime time = LocalTime.of(9, 0);
		TimeSlot timeSlot = mock(TimeSlot.class);
		when(timeSlotService.getTimeSlot(List.of(time), List.of(length)))
			.thenReturn(List.of(timeSlot));

		// act
		componentService.registerAvailableTimes(userId, requestList);

		// assert
		verify(availableTimeService).saveAll(anyList());
	}

	@Test
	void registerAvailableTimes_duplicate() {
		Long userId = 1L;
		TutorProfile tutorProfile = mock(TutorProfile.class);
		when(tutorProfileService.getTutorProfileByUserId(userId)).thenReturn(tutorProfile);

		TimeSlotCreateRequest slotRequest = new TimeSlotCreateRequest("09:00", 60);
		AvailableTimeData data = new AvailableTimeData(DayOfWeek.MONDAY, List.of(slotRequest));
		List<AvailableTimeData> requestList = List.of(data);

		SessionLength length = SessionLength.from(60);
		LocalTime time = LocalTime.of(9, 0);
		when(timeSlotService.getTimeSlot(List.of(time), List.of(length)))
			.thenReturn(List.of(mock(TimeSlot.class)));

		doThrow(new org.springframework.dao.DataIntegrityViolationException("Duplicate"))
			.when(availableTimeService).saveAll(anyList());

		// act & assert
		CustomException ex = assertThrows(CustomException.class, () ->
			componentService.registerAvailableTimes(userId, requestList));
		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.CREATE_CONFLICT);
	}

	@Test
	void deleteAllByIds_success() {
		Long userId = 1L;
		List<Long> ids = List.of(10L, 11L);

		TutorProfile tutor = mock(TutorProfile.class);
		List<AvailableTime> times = List.of(mock(AvailableTime.class));

		when(tutorProfileService.getTutorProfileByUserId(userId)).thenReturn(tutor);
		when(availableTimeService.findTutorIdByIdIn(ids)).thenReturn(times);
		when(availableTimeService.isOwner(tutor, times)).thenReturn(true);

		componentService.deleteAllByIds(userId, ids);

		verify(availableTimeService).deleteAllById(times);
	}

	@Test
	void deleteAllByIds_unauthorized() {
		Long userId = 1L;
		List<Long> ids = List.of(1L);
		TutorProfile tutor = mock(TutorProfile.class);
		List<AvailableTime> times = List.of(mock(AvailableTime.class));

		when(tutorProfileService.getTutorProfileByUserId(userId)).thenReturn(tutor);
		when(availableTimeService.findTutorIdByIdIn(ids)).thenReturn(times);
		when(availableTimeService.isOwner(tutor, times)).thenReturn(false);

		CustomException ex = assertThrows(CustomException.class, () ->
			componentService.deleteAllByIds(userId, ids));
		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
	}

	@Test
	void getAvailableTimes_mixedCacheHitAndMiss() {
		LocalDate d1 = LocalDate.of(2024, 6, 1);
		LocalDate d2 = LocalDate.of(2024, 6, 2);
		int sessionLength = 60;

		Map<LocalDate, Set<Long>> reservedMap = Map.of();
		when(reservationService.findMapReservedIdsBetween(d1, d2)).thenReturn(reservedMap);

		// AvailableTime for FRIDAY (d2)
		AvailableTime availableTime = mock(AvailableTime.class);
		when(availableTime.getDayOfWeek()).thenReturn(DayOfWeek.SUNDAY); // d2 = SUN
		when(availableTime.getId()).thenReturn(100L);

		TimeSlot slot = mock(TimeSlot.class);
		LocalTime start = LocalTime.of(9, 0);
		SessionLength sl = SessionLength.from(60);
		when(slot.getStartTime()).thenReturn(start);
		when(slot.getSessionLength()).thenReturn(sl);
		when(availableTime.getTimeSlot()).thenReturn(slot);

		when(availableTimeService.findAllBySessionLengthDistinctTimeSlot(sessionLength))
			.thenReturn(List.of(availableTime));

		// d1 - cache hit
		DailyAvailabilityResponse cached = new DailyAvailabilityResponse(d1, List.of());
		when(availableTimeCacheService.generateKey(d1, sessionLength)).thenReturn("k1");
		when(availableTimeCacheService.get("k1")).thenReturn(cached);
		when(availableTimeCacheService.isHit(cached)).thenReturn(true);

		// d2 - cache miss
		when(availableTimeCacheService.generateKey(d2, sessionLength)).thenReturn("k2");
		when(availableTimeCacheService.get("k2")).thenReturn(null);
		when(availableTimeCacheService.isHit(null)).thenReturn(false);

		// act
		List<DailyAvailabilityResponse> result = componentService.getAvailableTimes(d1, d2, sessionLength);

		// assert
		assertThat(result).hasSize(2);
		assertThat(result.get(0)).isEqualTo(cached);
		assertThat(result.get(1).getDate()).isEqualTo(d2);
		verify(availableTimeCacheService).set(eq("k2"), any(DailyAvailabilityResponse.class));
	}
}
