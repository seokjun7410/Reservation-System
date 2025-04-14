package com.challenge.server.comoon.seed;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.challenge.server.domain.profile.StudentProfile;
import com.challenge.server.domain.profile.TutorProfile;
import com.challenge.server.domain.time.AvailableTime;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.domain.time.TimeSlot;
import com.challenge.server.domain.user.User;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestDataService {
	private final EntityManager em;

	@Transactional
	public void createTestData() {
		User tutor = new User("tutor@example.com", "튜터");
		User student = new User("student@example.com", "학생");
		em.persist(tutor);
		em.persist(student);

		TutorProfile tutorProfile = new TutorProfile("전문 튜터", tutor);
		StudentProfile studentProfile = new StudentProfile("선호 없음", student);

		em.persist(tutorProfile);
		em.persist(studentProfile);

		LocalTime start = LocalTime.of(0, 0); // 10:00 시작
		LocalTime end = LocalTime.of(23, 0);   // 23:00 까지

		List<TimeSlot> allSlots = new ArrayList<>();
		while (!start.isAfter(end)) {
			// 30분 단위 TimeSlot 생성
			TimeSlot slot30 = new TimeSlot(start, SessionLength.thirty);
			em.persist(slot30);
			allSlots.add(slot30);

			// 60분 단위 TimeSlot 생성
			TimeSlot slot60 = new TimeSlot(start, SessionLength.sixty);
			em.persist(slot60);
			allSlots.add(slot60);

			start = start.plusMinutes(30);
		}
		TimeSlot slot2330 = new TimeSlot(LocalTime.of(23, 30), SessionLength.thirty);
		em.persist(slot2330);
		allSlots.add(slot2330);

		List<AvailableTime> availableTimes = List.of(
			new AvailableTime(tutorProfile, findSlot(allSlots, LocalTime.of(9, 0), SessionLength.thirty),
				DayOfWeek.MONDAY),
			new AvailableTime(tutorProfile, findSlot(allSlots, LocalTime.of(10, 0), SessionLength.thirty),
				DayOfWeek.MONDAY),
			new AvailableTime(tutorProfile, findSlot(allSlots, LocalTime.of(11, 0), SessionLength.sixty),
				DayOfWeek.MONDAY)
		);
		availableTimes.forEach(em::persist);

		em.flush();
	}

	private TimeSlot findSlot(List<TimeSlot> slots, LocalTime time, SessionLength sessionLength) {
		return slots.stream()
			.filter(s -> s.getStartTime().equals(time) && s.getSessionLength() == sessionLength)
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("TimeSlot not found for: " + time + " / " + sessionLength));
	}
}

