package com.challenge.server.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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
import com.challenge.server.dto.ReservationDto;
import com.challenge.server.dto.ReservationResponse;

@DataJpaTest
class ReservationRepositoryTest {

	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TutorProfileRepository tutorProfileRepository;
	@Autowired
	private StudentProfileRepository studentProfileRepository;
	@Autowired
	private AvailableTimeRepository availableTimeRepository;
	@Autowired
	private TimeSlotRepository timeSlotRepository;

	@Test
	@DisplayName("availableTimeId + date + studentId로 예약 조회")
	void testFindByAvailableTimeIdAndDateAndStudentProfileId() {
		User tutorUser = userRepository.save(new User("tutor@email.com", "튜터"));
		TutorProfile tutor = tutorProfileRepository.save(new TutorProfile("튜터 소개", tutorUser));

		User studentUser = userRepository.save(new User("student@email.com", "학생"));
		StudentProfile student = studentProfileRepository.save(new StudentProfile("학생 선호도", studentUser));

		TimeSlot slot = timeSlotRepository.save(new TimeSlot(LocalTime.of(10, 0), SessionLength.thirty));
		AvailableTime at = availableTimeRepository.save(new AvailableTime(tutor, slot, DayOfWeek.MONDAY));

		LocalDate date = LocalDate.now().plusDays(1);
		Reservation reservation = reservationRepository.save(
			Reservation.builder()
				.availableTime(at)
				.studentProfile(student)
				.reservationDate(date)
				.build()
		);

		Optional<Reservation> result = reservationRepository.findByAvailableTimeIdAndReservationDateAndStudentProfileId(
			at.getId(), date, student.getId()
		);

		assertThat(result).isPresent();
		assertThat(result.get().getAvailableTime().getId()).isEqualTo(at.getId());
	}

	@Test
	@DisplayName("특정 날짜 범위 내 예약된 시간 ID들 조회")
	void testFindReservedIdsBetween() {
		User user = userRepository.save(new User("x@x.com", "공통유저"));
		TutorProfile tutor = tutorProfileRepository.save(new TutorProfile("튜터 bio", user));
		StudentProfile student = studentProfileRepository.save(new StudentProfile("학생 info", user));

		TimeSlot slot = timeSlotRepository.save(new TimeSlot(LocalTime.of(9, 0), SessionLength.sixty));
		AvailableTime at = availableTimeRepository.save(new AvailableTime(tutor, slot, DayOfWeek.TUESDAY));

		LocalDate today = LocalDate.now();
		reservationRepository.save(Reservation.builder()
			.availableTime(at)
			.studentProfile(student)
			.reservationDate(today.plusDays(1))
			.build());

		reservationRepository.save(Reservation.builder()
			.availableTime(at)
			.studentProfile(student)
			.reservationDate(today.plusDays(3))
			.build());

		List<ReservationDto> result = reservationRepository.findReservedIdsBetween(today, today.plusDays(5));
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getAvailableTimeId()).isEqualTo(at.getId());
	}

	@Test
	@DisplayName("학생 ID로 예약 전체 조회 (ReservationResponse)")
	void testFindAllByStudentProfileId() {
		User tutorUser = userRepository.save(new User("tt@tt.com", "튜터"));
		TutorProfile tutor = tutorProfileRepository.save(new TutorProfile("이건 소개글", tutorUser));

		User studentUser = userRepository.save(new User("ss@ss.com", "학생"));
		StudentProfile student = studentProfileRepository.save(new StudentProfile("선호도", studentUser));

		TimeSlot slot = timeSlotRepository.save(new TimeSlot(LocalTime.of(15, 0), SessionLength.thirty));
		AvailableTime at = availableTimeRepository.save(new AvailableTime(tutor, slot, DayOfWeek.FRIDAY));

		reservationRepository.save(Reservation.builder()
			.availableTime(at)
			.studentProfile(student)
			.reservationDate(LocalDate.now().plusDays(2))
			.build());

		List<ReservationResponse> result = reservationRepository.findAllByStudentProfileId(student.getId());

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getTutorName()).isEqualTo("튜터");
		assertThat(result.get(0).getSessionLength()).isEqualTo(SessionLength.thirty);
	}
}
