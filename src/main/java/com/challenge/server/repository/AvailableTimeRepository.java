package com.challenge.server.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.challenge.server.domain.time.AvailableTime;
import com.challenge.server.domain.time.SessionLength;

@Repository
public interface AvailableTimeRepository extends JpaRepository<AvailableTime, Long> {
	List<AvailableTime> findByIdIn(List<Long> availableTimeIds);

	@Query("SELECT at FROM AvailableTime at JOIN at.timeSlot ts WHERE ts.sessionLength = :length")
	List<AvailableTime> findAllBySessionLength(@Param("length") SessionLength length);

	@Query("""
			SELECT at
			FROM AvailableTime at
			JOIN at.tutorProfile tp
			JOIN at.timeSlot ts
			WHERE tp.id = :tutorId
			AND ts.sessionLength = :sessionLength
			AND at.dayOfWeek = :dayOfWeek
			AND ts.startTime = :start
		""")
	Optional<AvailableTime> findByTutorIdAndSessionLengthAndDayOfWeek(
		@Param("tutorId") Long tutorId,
		@Param("sessionLength") SessionLength sessionLength,
		@Param("dayOfWeek") DayOfWeek dayOfWeek,
		@Param("start") LocalTime start
	);
}
