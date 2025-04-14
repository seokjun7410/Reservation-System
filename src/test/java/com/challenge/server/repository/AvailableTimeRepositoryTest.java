package com.challenge.server.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.challenge.server.domain.profile.TutorProfile;
import com.challenge.server.domain.time.AvailableTime;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.domain.time.TimeSlot;
import com.challenge.server.domain.user.User;

@DataJpaTest
class AvailableTimeRepositoryTest {

	@Autowired
	private AvailableTimeRepository availableTimeRepository;

	@Autowired
	private TutorProfileRepository tutorProfileRepository;

	@Autowired
	private TimeSlotRepository timeSlotRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("세션 길이로 AvailableTime 조회")
	void testFindAllBySessionLength() {
		User user = userRepository.save(new User("test@email.com", "테스터"));
		TutorProfile tutor = tutorProfileRepository.save(new TutorProfile("자기소개", user));
		TimeSlot slot = timeSlotRepository.save(new TimeSlot(LocalTime.of(9, 0), SessionLength.thirty));
		AvailableTime availableTime = new AvailableTime(tutor, slot, DayOfWeek.MONDAY);
		availableTimeRepository.save(availableTime);

		List<AvailableTime> result = availableTimeRepository.findAllBySessionLength(SessionLength.thirty);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getTimeSlot().getSessionLength()).isEqualTo(SessionLength.thirty);
	}

	@Test
	@DisplayName("튜터, 세션길이, 요일, 시작시간으로 AvailableTime 조회")
	void testFindByTutorIdAndSessionLengthAndDayOfWeek() {
		User user = userRepository.save(new User("b@email.com", "튜터"));
		TutorProfile tutor = tutorProfileRepository.save(new TutorProfile("바이오", user));
		TimeSlot slot = timeSlotRepository.save(new TimeSlot(LocalTime.of(13, 30), SessionLength.sixty));
		AvailableTime at = availableTimeRepository.save(new AvailableTime(tutor, slot, DayOfWeek.WEDNESDAY));

		Optional<AvailableTime> result = availableTimeRepository.findByTutorIdAndSessionLengthAndDayOfWeek(
			tutor.getId(),
			SessionLength.sixty,
			DayOfWeek.WEDNESDAY,
			LocalTime.of(13, 30)
		);

		assertThat(result).isPresent();
		assertThat(result.get().getTutorProfile().getId()).isEqualTo(tutor.getId());
	}

	@Test
	@DisplayName("ID 리스트로 AvailableTime 조회")
	void testFindByIdIn() {
		User user = userRepository.save(new User("list@email.com", "리스트"));
		TutorProfile tutor = tutorProfileRepository.save(new TutorProfile("소개", user));
		TimeSlot slot = timeSlotRepository.save(new TimeSlot(LocalTime.of(8, 0), SessionLength.thirty));
		AvailableTime at1 = availableTimeRepository.save(new AvailableTime(tutor, slot, DayOfWeek.FRIDAY));
		AvailableTime at2 = availableTimeRepository.save(new AvailableTime(tutor, slot, DayOfWeek.SATURDAY));

		List<AvailableTime> result = availableTimeRepository.findByIdIn(List.of(at1.getId(), at2.getId()));

		assertThat(result).hasSize(2);
	}
}
