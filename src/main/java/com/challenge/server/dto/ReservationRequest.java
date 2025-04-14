package com.challenge.server.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReservationRequest {
	private Long availableTimeId;
	private Long studentProfileId;
	private LocalDate reservationDate;
}
