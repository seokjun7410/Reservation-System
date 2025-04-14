package com.challenge.server.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.challenge.server.domain.reservation.Reservation;
import com.challenge.server.dto.ReservationDto;
import com.challenge.server.dto.ReservationResponse;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	Optional<Reservation> findByAvailableTimeIdAndReservationDateAndStudentProfileId(Long availableTimeId,
		LocalDate reservationDate, Long studentProfileId);

	@Query("""
			SELECT r.reservationDate AS reservationDate, r.availableTime.id AS availableTimeId
			FROM Reservation r
			WHERE r.reservationDate BETWEEN :start AND :end
		""")
	List<ReservationDto> findReservedIdsBetween(@Param("start") LocalDate startDate, @Param("end") LocalDate endDate);

	@Query("""
			SELECT
			r.id AS reservationId,
			tp.bio AS bio,
			u.name AS tutorName,
			ts.startTime AS startTime,
			ts.sessionLength AS sessionLength,
			at.dayOfWeek AS dayOfWeek,
			r.reservationDate AS reservationDate,
			r.createdDate AS createdAt
			FROM Reservation r
			JOIN r.availableTime at
			JOIN at.timeSlot ts
			JOIN at.tutorProfile tp
			JOIN tp.user u
			WHERE r.studentProfile.id = :studentProfileId
			ORDER BY r.reservationDate DESC
		""")
	List<ReservationResponse> findAllByStudentProfileId(@Param("studentProfileId") Long studentProfileId);
}
