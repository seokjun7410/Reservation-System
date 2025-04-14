package com.challenge.server.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.domain.profile.TutorProfile;
import com.challenge.server.domain.time.AvailableTime;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.domain.time.TimeSlot;
import com.challenge.server.repository.AvailableTimeRepository;
import com.challenge.server.repository.custom.AvailableTimeBulkRepository;

class AvailableTimeServiceTest {

	@Mock
	private AvailableTimeBulkRepository bulkRepository;

	@Mock
	private AvailableTimeRepository availableTimeRepository;

	@InjectMocks
	private AvailableTimeService availableTimeService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("saveAll → bulkRepository saveAll 호출")
	void saveAll_shouldCallBulkRepository() {
		List<AvailableTime> insertList = List.of(new AvailableTime());
		availableTimeService.saveAll(insertList);

		verify(bulkRepository, times(1)).saveAll(insertList);
	}

	@Test
	@DisplayName("deleteAllById → bulkRepository deleteAll 호출")
	void deleteAll_shouldCallBulkRepository() {
		List<AvailableTime> deleteList = List.of(new AvailableTime());
		availableTimeService.deleteAllById(deleteList);

		verify(bulkRepository, times(1)).deleteAll(deleteList);
	}

	@Test
	@DisplayName("isOwner → allMatch가 true인 경우")
	void isOwner_allMatchTrue() {
		TutorProfile tutor = mock(TutorProfile.class);
		AvailableTime at1 = mock(AvailableTime.class);
		AvailableTime at2 = mock(AvailableTime.class);

		when(tutor.isOwner(at1)).thenReturn(true);
		when(tutor.isOwner(at2)).thenReturn(true);

		boolean result = availableTimeService.isOwner(tutor, List.of(at1, at2));
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("isOwner → 하나라도 다르면 false")
	void isOwner_partialFalse() {
		TutorProfile tutor = mock(TutorProfile.class);
		AvailableTime at1 = mock(AvailableTime.class);
		AvailableTime at2 = mock(AvailableTime.class);

		when(tutor.isOwner(at1)).thenReturn(true);
		when(tutor.isOwner(at2)).thenReturn(false);

		boolean result = availableTimeService.isOwner(tutor, List.of(at1, at2));
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("findTutorIdByIdIn → 정상 조회")
	void findTutorIdByIdIn_success() {
		AvailableTime at = new AvailableTime();
		when(availableTimeRepository.findByIdIn(List.of(1L))).thenReturn(List.of(at));

		List<AvailableTime> result = availableTimeService.findTutorIdByIdIn(List.of(1L));
		assertThat(result).hasSize(1);
	}

	@Test
	@DisplayName("findTutorIdByIdIn → 없으면 예외")
	void findTutorIdByIdIn_empty_throw() {
		when(availableTimeRepository.findByIdIn(List.of(1L))).thenReturn(List.of());

		assertThatThrownBy(() -> availableTimeService.findTutorIdByIdIn(List.of(1L)))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("튜터 프로필이 없습니다.");
	}

	@Test
	@DisplayName("findAllBySessionLengthDistinctTimeSlot → 중복 TimeSlot 제거")
	void findAllBySessionLengthDistinctTimeSlot_returnsDistinct() {
		TimeSlot slot = new TimeSlot(LocalTime.of(9, 0), SessionLength.thirty);
		AvailableTime at1 = new AvailableTime();
		at1.setTimeSlot(slot);
		AvailableTime at2 = new AvailableTime();
		at2.setTimeSlot(slot); // 같은 slot (중복)

		when(availableTimeRepository.findAllBySessionLength(SessionLength.thirty))
			.thenReturn(List.of(at1, at2));

		List<AvailableTime> result = availableTimeService.findAllBySessionLengthDistinctTimeSlot(30);
		assertThat(result).hasSize(1);
	}

	@Test
	@DisplayName("findByTutorIdAndSessionLengthAndDayOfWeek → 정상 조회")
	void findByTutorIdAndSessionLengthAndDayOfWeek_success() {
		AvailableTime at = new AvailableTime();
		when(availableTimeRepository.findByTutorIdAndSessionLengthAndDayOfWeek(any(), any(), any(), any()))
			.thenReturn(Optional.of(at));

		AvailableTime result = availableTimeService.findByTutorIdAndSessionLengthAndDayOfWeek(
			1L, SessionLength.thirty, DayOfWeek.MONDAY, LocalTime.of(9, 0));

		assertThat(result).isEqualTo(at);
	}

	@Test
	@DisplayName("findByTutorIdAndSessionLengthAndDayOfWeek → 없으면 예외")
	void findByTutorIdAndSessionLengthAndDayOfWeek_notFound() {
		when(availableTimeRepository.findByTutorIdAndSessionLengthAndDayOfWeek(any(), any(), any(), any()))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> availableTimeService.findByTutorIdAndSessionLengthAndDayOfWeek(
			1L, SessionLength.thirty, DayOfWeek.MONDAY, LocalTime.of(9, 0)))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("AvailableTime 이 없습니다");
	}
}
