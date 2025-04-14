package com.challenge.server.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewReservationRequest {
	@NotNull
	@Schema(description = "수업 시간", example = "30")
	private int sessionLength;
	@NotNull
	@Schema(description = "튜터 아이디", example = "1")
	private Long tutorId;
	@NotNull
	@Schema(description = "시작 시간", example = "09:00")
	private LocalTime start;
	@NotNull
	@Schema(description = "수업 날짜", example = "2025-10-06")
	private LocalDate reservationDate;
}
