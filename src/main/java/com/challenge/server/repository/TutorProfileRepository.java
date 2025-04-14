package com.challenge.server.repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.challenge.server.domain.profile.TutorProfile;
import com.challenge.server.domain.time.SessionLength;

@Repository
public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {

	Optional<TutorProfile> findByUserId(Long userId);

	@Query("""
		SELECT DISTINCT at.tutorProfile
			FROM AvailableTime at
			JOIN at.timeSlot ts
			LEFT JOIN Reservation r ON r.availableTime = at AND r.reservationDate = :date
			WHERE at.dayOfWeek = :dayOfWeek
			AND r.id IS NULL
			AND ts.startTime <= :start
			AND ts.sessionLength = :sessionLength
		""")
	List<TutorProfile> findAvailableTutorsByDateAndTimeAndSessionLength(@Param("date") LocalDate date,
		@Param("dayOfWeek") DayOfWeek dayOfWeek, @Param("start") LocalTime start,
		@Param("sessionLength") SessionLength sessionLength);

}
