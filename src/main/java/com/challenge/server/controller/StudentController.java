package com.challenge.server.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.server.comoon.response.ResponseHandler;
import com.challenge.server.controller.validation.AvailableTimeValidation;
import com.challenge.server.dto.DailyAvailabilityResponse;
import com.challenge.server.dto.ReservationResponse;
import com.challenge.server.service.AvailableTimeComponentService;
import com.challenge.server.service.JwtService;
import com.challenge.server.service.ReservationComponentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Student", description = "학생 관련 기능 API")
public class StudentController {
	private final AvailableTimeComponentService availableTimeComponentService;
	private final AvailableTimeValidation validation;
	private final ReservationComponentService reservationComponentService;
	private final JwtService jwtService;

	/**
	 * 신청한 수업을 조회합니다.
	 */
	@GetMapping("/reservation/v1")
	@Operation(
		summary = "신청한 수업 조회",
		description = "로그인한 사용자의 예약 내역을 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "예약 내역 조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	public ResponseEntity<ResponseHandler<List<ReservationResponse>>> getAvailableTutors(
		@Parameter(description = "Token", required = true, example = "2")
		@RequestHeader("token") String token) {

		Long userId = jwtService.extractUserId(token);
		List<ReservationResponse> reservationResponses = reservationComponentService.getReservationByUserId(userId);
		return ResponseEntity.ok(ResponseHandler.response(reservationResponses));
	}

	/**
	 * 기간 & 수업 길이로 현재 수업 가능한 시간대 조회
	 */
	@GetMapping("/available-times/v1")
	@Operation(
		summary = "예약 가능한 시간대 조회",
		description = "주어진 기간(startDate ~ endDate)과 수업 길이(sessionLength)에 따라 예약 가능한 시간대를 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "가능 시간대 조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "400", description = "요청 파라미터 오류")
	})
	public ResponseEntity<ResponseHandler<List<DailyAvailabilityResponse>>> getAvailableTimeslots(
		@Parameter(description = "Token", required = true, example = "2")
		@RequestHeader("token") String token,

		@Parameter(description = "조회 시작일 (yyyy-MM-dd)", required = true, example = "2025-10-01")
		@RequestParam("startDate") LocalDate startDate,

		@Parameter(description = "조회 종료일 (yyyy-MM-dd)", required = true, example = "2025-10-30")
		@RequestParam("endDate") LocalDate endDate,

		@Parameter(description = "수업 길이 (단위: 분)", required = true, example = "30")
		@RequestParam("sessionLength") int sessionLength
	) {
		validation.validOrException(startDate);
		validation.validOrException(endDate);
		List<DailyAvailabilityResponse> result = availableTimeComponentService.getAvailableTimes(startDate, endDate,
			sessionLength);
		return ResponseEntity.ok(ResponseHandler.response(result));
	}
}
