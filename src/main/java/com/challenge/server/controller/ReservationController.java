package com.challenge.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.server.controller.validation.ReservationValidation;
import com.challenge.server.dto.NewReservationRequest;
import com.challenge.server.service.JwtService;
import com.challenge.server.service.ReservationComponentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
@Tag(name = "Reservation", description = "수업 예약 관련 API")
public class ReservationController {
	private final ReservationComponentService reservationService;
	private final ReservationValidation reservationValidation;
	private final JwtService jwtService;

	/**
	 * 시간대 & 수업길이, 튜터로 새로운 수업 신청
	 *
	 * @param token
	 * @param request
	 * @return
	 */
	@PostMapping("/v1")
	@Operation(
		summary = "수업 예약 생성",
		description = "시간대, 수업 길이, 튜터 정보를 기반으로 새로운 수업을 신청합니다.",
		responses = {
			@ApiResponse(responseCode = "201", description = "예약 생성 성공"),
			@ApiResponse(responseCode = "400", description = "요청 유효성 검증 실패", content = @Content),
			@ApiResponse(responseCode = "409", description = "중복 예약 시도", content = @Content)
		}
	)
	public ResponseEntity<String> createReservation(
		@Parameter(description = "Token", required = true, example = "2")
		@RequestHeader("token") String token,
		@RequestBody @Valid NewReservationRequest request) {
		Long userId = jwtService.extractUserId(token);
		reservationValidation.validOrException(request);

		reservationService.createReservation(request, userId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
