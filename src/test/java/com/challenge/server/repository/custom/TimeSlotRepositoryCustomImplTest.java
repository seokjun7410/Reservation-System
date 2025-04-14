package com.challenge.server.repository.custom;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.challenge.server.CleanUp;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.domain.time.TimeSlot;
import com.challenge.server.repository.TimeSlotRepository;

@SpringBootTest
class TimeSlotRepositoryCustomImplTest {

	@Autowired
	private TimeSlotRepository timeSlotRepository;

	@Autowired
	private TimeSlotRepositoryCustom timeSlotRepositoryCustom;

	@Autowired
	private CleanUp cleanUp;

	@BeforeEach
	void tearDown() {
		cleanUp.truncateAll();
	}

	@Test
	@DisplayName("startTime + sessionLength 조합으로 TimeSlot 조회")
	void testFindByStartTimesAndSessionLengthInMinutes() {
		// given
		TimeSlot ts1 = new TimeSlot(LocalTime.of(9, 0), SessionLength.thirty);
		TimeSlot ts2 = new TimeSlot(LocalTime.of(10, 0), SessionLength.thirty);
		TimeSlot ts3 = new TimeSlot(LocalTime.of(11, 0), SessionLength.sixty);

		timeSlotRepository.saveAll(List.of(ts1, ts2, ts3));

		// when
		List<TimeSlot> result = timeSlotRepositoryCustom.findByStartTimesAndSessionLengthInMinutes(
			List.of(LocalTime.of(9, 0), LocalTime.of(11, 0)),
			List.of(SessionLength.thirty, SessionLength.sixty)
		);

		// then
		assertThat(result).hasSize(2);
		assertThat(result).extracting("startTime")
			.contains(LocalTime.of(9, 0), LocalTime.of(11, 0));
		assertThat(result).extracting("sessionLength")
			.contains(SessionLength.thirty, SessionLength.sixty);
	}
}

