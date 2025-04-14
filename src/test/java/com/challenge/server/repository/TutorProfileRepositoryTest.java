package com.challenge.server.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.challenge.server.domain.profile.StudentProfile;
import com.challenge.server.domain.profile.TutorProfile;
import com.challenge.server.domain.reservation.Reservation;
import com.challenge.server.domain.time.AvailableTime;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.domain.time.TimeSlot;
import com.challenge.server.domain.user.User;

@DataJpaTest
class TutorProfileRepositoryTest {

	@Autowired
	private TutorProfileRepository tutorProfileRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TimeSlotRepository timeSlotRepository;
	@Autowired
	private AvailableTimeRepository availableTimeRepository;
	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private StudentProfileRepository studentProfileRepository;

	@Test
	@DisplayName("예약 없는 튜터만 조회되는지 확인")
	void findAvailableTutors() {
		// given
		LocalDate date = LocalDate.of(2025, 4, 15);
		DayOfWeek dayOfWeek = date.getDayOfWeek();
		LocalTime startTime = LocalTime.of(10, 0);
		SessionLength sessionLength = SessionLength.thirty;

		// 튜터 2명 저장
		User tutorUser1 = new User("t1@t.com", "튜터1");
		User tutorUser2 = new User("t2@t.com", "튜터2");
		userRepository.saveAll(List.of(tutorUser1, tutorUser2));

		TutorProfile tutor1 = new TutorProfile("bio1", tutorUser1);
		TutorProfile tutor2 = new TutorProfile("bio2", tutorUser2);
		tutorProfileRepository.saveAll(List.of(tutor1, tutor2));

		// 시간 슬롯 저장
		TimeSlot timeSlot = new TimeSlot(startTime, sessionLength);
		timeSlotRepository.save(timeSlot);

		// 튜터마다 AvailableTime 등록
		AvailableTime available1 = new AvailableTime(tutor1, timeSlot, dayOfWeek);
		AvailableTime available2 = new AvailableTime(tutor2, timeSlot, dayOfWeek);
		availableTimeRepository.saveAll(List.of(available1, available2));

		// 학생 등록
		User studentUser = new User("s@s.com", "학생");
		userRepository.save(studentUser);
		StudentProfile student = new StudentProfile("학생소개", studentUser);
		studentProfileRepository.save(student);

		// tutor2는 예약이 있음
		Reservation reservation = Reservation.builder()
			.availableTime(available2)
			.studentProfile(student)
			.reservationDate(date)
			.build();
		reservationRepository.save(reservation);

		// when
		List<TutorProfile> result = tutorProfileRepository
			.findAvailableTutorsByDateAndTimeAndSessionLength(
				date, dayOfWeek, startTime, sessionLength);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getUser().getEmail()).isEqualTo("t1@t.com");
	}
}
