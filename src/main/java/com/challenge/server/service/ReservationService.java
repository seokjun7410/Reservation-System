package com.challenge.server.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.server.domain.reservation.Reservation;
import com.challenge.server.dto.ReservationDto;
import com.challenge.server.dto.ReservationResponse;
import com.challenge.server.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

	private final ReservationRepository reservationRepository;

	public List<Reservation> getAllReservations() {
		return reservationRepository.findAll();
	}

	/**
	 * 기간 내에 Map<날짜, 예약된 AvailableTime.id들> 형식으로 조회합니다.
	 *
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Map<LocalDate, Set<Long>> findMapReservedIdsBetween(LocalDate startDate, LocalDate endDate) {
		List<ReservationDto> reservedIdsBetween = reservationRepository.findReservedIdsBetween(startDate, endDate);
		// Map<날짜, 예약된 AvailableTime.id들>
		return reservedIdsBetween.stream()
			.collect(Collectors.groupingBy(
				ReservationDto::getReservationDate,
				Collectors.mapping(ReservationDto::getAvailableTimeId, Collectors.toSet())
			));
	}

	public void save(Reservation reservation) {
		reservationRepository.save(reservation);
	}

	public List<ReservationResponse> findAllByStudentProfileId(Long studentProfileId) {
		return reservationRepository.findAllByStudentProfileId(studentProfileId);
	}
}
