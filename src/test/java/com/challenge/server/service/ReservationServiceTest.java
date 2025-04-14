package com.challenge.server.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.challenge.server.domain.reservation.Reservation;
import com.challenge.server.dto.ReservationDto;
import com.challenge.server.dto.ReservationResponse;
import com.challenge.server.repository.ReservationRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

	@InjectMocks
	private ReservationService reservationService;

	@Mock
	private ReservationRepository reservationRepository;

	@Test
	void testGetAllReservations() {
		// given: Reservation 엔티티 리스트
		List<Reservation> reservations = new ArrayList<>();
		Reservation reservation = new Reservation(); // 필요한 필드에 대한 초기화가 필요합니다.
		reservations.add(reservation);

		when(reservationRepository.findAll()).thenReturn(reservations);

		// when
		List<Reservation> result = reservationService.getAllReservations();

		// then
		assertEquals(reservations, result);
		verify(reservationRepository, times(1)).findAll();
	}

	@Test
	void testFindMapReservedIdsBetween() {
		// given
		LocalDate startDate = LocalDate.of(2022, 1, 1);
		LocalDate endDate = LocalDate.of(2022, 1, 5);

		// ReservationDto는 인터페이스이므로 Mockito의 mock() 메서드를 사용해 모의 객체를 생성합니다.
		ReservationDto dto1 = mock(ReservationDto.class);
		when(dto1.getReservationDate()).thenReturn(LocalDate.of(2022, 1, 2));
		when(dto1.getAvailableTimeId()).thenReturn(1L);

		ReservationDto dto2 = mock(ReservationDto.class);
		when(dto2.getReservationDate()).thenReturn(LocalDate.of(2022, 1, 2));
		when(dto2.getAvailableTimeId()).thenReturn(2L);

		ReservationDto dto3 = mock(ReservationDto.class);
		when(dto3.getReservationDate()).thenReturn(LocalDate.of(2022, 1, 3));
		when(dto3.getAvailableTimeId()).thenReturn(3L);

		List<ReservationDto> dtoList = Arrays.asList(dto1, dto2, dto3);
		when(reservationRepository.findReservedIdsBetween(startDate, endDate)).thenReturn(dtoList);

		// when
		Map<LocalDate, Set<Long>> result = reservationService.findMapReservedIdsBetween(startDate, endDate);

		// then: 그룹핑한 결과 확인
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.containsKey(LocalDate.of(2022, 1, 2)));
		assertTrue(result.containsKey(LocalDate.of(2022, 1, 3)));
		assertEquals(Set.of(1L, 2L), result.get(LocalDate.of(2022, 1, 2)));
		assertEquals(Set.of(3L), result.get(LocalDate.of(2022, 1, 3)));
		verify(reservationRepository, times(1)).findReservedIdsBetween(startDate, endDate);
	}

	@Test
	void testSave() {
		// given
		Reservation reservation = new Reservation(); // Reservation 객체 초기화

		// when
		reservationService.save(reservation);

		// then
		verify(reservationRepository, times(1)).save(reservation);
	}

	@Test
	void testFindAllByStudentProfileId() {
		// given
		Long studentProfileId = 1L;
		// ReservationResponse 역시 인터페이스이므로 mock()으로 생성합니다.
		ReservationResponse response = mock(ReservationResponse.class);
		List<ReservationResponse> responses = Collections.singletonList(response);

		when(reservationRepository.findAllByStudentProfileId(studentProfileId)).thenReturn(responses);

		// when
		List<ReservationResponse> result = reservationService.findAllByStudentProfileId(studentProfileId);

		// then
		assertNotNull(result);
		assertEquals(responses, result);
		verify(reservationRepository, times(1)).findAllByStudentProfileId(studentProfileId);
	}
}
