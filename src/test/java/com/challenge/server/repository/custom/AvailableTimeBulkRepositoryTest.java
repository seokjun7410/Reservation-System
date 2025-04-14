package com.challenge.server.repository.custom;

import static org.assertj.core.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.challenge.server.CleanUp;
import com.challenge.server.domain.profile.TutorProfile;
import com.challenge.server.domain.time.AvailableTime;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.domain.time.TimeSlot;
import com.challenge.server.domain.user.User;
import com.challenge.server.repository.AvailableTimeRepository;
import com.challenge.server.repository.TimeSlotRepository;
import com.challenge.server.repository.TutorProfileRepository;
import com.challenge.server.repository.UserRepository;

@SpringBootTest
class AvailableTimeBulkRepositoryTest {

	@Autowired
	private AvailableTimeBulkRepository bulkRepository;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TutorProfileRepository tutorProfileRepository;
	@Autowired
	private TimeSlotRepository timeSlotRepository;
	@Autowired
	private AvailableTimeRepository availableTimeRepository;
	@Autowired
	private CleanUp cleanUp;

	@BeforeEach
	void tearDown() {
		cleanUp.truncateAll();
	}

	@Test
	@DisplayName("AvailableTime 엔티티를 대량 저장한다")
	void testSaveAll() {
		// given
		User user = userRepository.save(new User("bulk@tutor.com", "Bulk Tutor"));
		TutorProfile tutor = tutorProfileRepository.save(new TutorProfile("대량 등록 튜터", user));
		TimeSlot slot = timeSlotRepository.save(new TimeSlot(LocalTime.of(10, 0), SessionLength.thirty));

		List<AvailableTime> timeList = List.of(
			new AvailableTime(tutor, slot, DayOfWeek.MONDAY),
			new AvailableTime(tutor, slot, DayOfWeek.TUESDAY),
			new AvailableTime(tutor, slot, DayOfWeek.WEDNESDAY)
		);

		// when
		bulkRepository.saveAll(timeList);

		// then
		List<AvailableTime> saved = availableTimeRepository.findAll();
		assertThat(saved).hasSizeGreaterThanOrEqualTo(3);
	}

	@Test
	@DisplayName("AvailableTime 엔티티를 대량 삭제한다")
	void testDeleteAll() {
		// given
		User user = userRepository.save(new User("delete@tutor.com", "Delete Tutor"));
		TutorProfile tutor = tutorProfileRepository.save(new TutorProfile("삭제용 튜터", user));
		TimeSlot slot = timeSlotRepository.save(new TimeSlot(LocalTime.of(11, 0), SessionLength.thirty));

		List<AvailableTime> toDelete = availableTimeRepository.saveAll(List.of(
			new AvailableTime(tutor, slot, DayOfWeek.THURSDAY),
			new AvailableTime(tutor, slot, DayOfWeek.FRIDAY)
		));

		// when
		bulkRepository.deleteAll(toDelete);

		// then
		List<AvailableTime> remaining = availableTimeRepository.findAll();
		assertThat(remaining).noneMatch(at -> toDelete.stream().anyMatch(d -> d.getId().equals(at.getId())));
	}
}
