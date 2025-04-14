package com.challenge.server.dto;

import java.time.LocalDate;

public interface ReservationDto {
	LocalDate getReservationDate();

	Long getAvailableTimeId();
}
