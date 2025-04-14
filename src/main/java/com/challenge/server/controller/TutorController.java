package com.challenge.server.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.server.comoon.response.ResponseHandler;
import com.challenge.server.controller.validation.AvailableTimeValidation;
import com.challenge.server.dto.AvailableTimeCreateRequest;
import com.challenge.server.dto.AvailableTimeDeleteRequest;
import com.challenge.server.dto.TutorResponse;
import com.challenge.server.service.AvailableTimeComponentService;
import com.challenge.server.service.JwtService;
import com.challenge.server.service.TutorProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/tutor")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tutor", description = "튜터 관련 기능 API")
public class TutorController {

	private final TutorProfileService tutorProfileService;
	private final JwtService jwtService;
	private final AvailableTimeComponentService availableTimeComponentService;
	private final AvailableTimeValidation availableTimeValidation;

	/**
	 * 시간대 & 수업 길이로 수업 가능한 튜터 조회
	 */
	@GetMapping("/v1")
	@Operation(
		summary = "예약 가능한 튜터 조회",
		description = "특정 날짜, 시작 시간, 수업 길이에 따라 예약 가능한 튜터 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "튜터 목록 조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "400", description = "요청 파라미터 오류")
	})
	public ResponseEntity<ResponseHandler<List<TutorResponse>>> getAvailableTutors(
		@Parameter(description = "Token", required = true, example = "1")
		@RequestHeader("token") String token,

		@Parameter(description = "수업 날짜", example = "2025-10-06", required = true)
		@RequestParam("date") LocalDate date,

		@DateTimeFormat(pattern = "HH:mm")
		@Parameter(
			description = "수업 시작 시간 (HH:mm)",
			required = true,
			schema = @Schema(type = "string", format = "time", example = "09:00")
		)
		@RequestParam("start") LocalTime start,

		@Parameter(description = "수업 길이 (단위: 분)", example = "30", required = true)
		@RequestParam("sessionLength") int sessionLength) {
		availableTimeValidation.validOrException(date);
		List<TutorResponse> tutors = tutorProfileService.getAvailableTutorProfile(date, start, sessionLength);
		return ResponseEntity.ok(ResponseHandler.response(tutors));
	}

	/**
	 * 사용 가능한 시간대를 등록합니다.
	 */
	@PostMapping("/available-times/v1")
	@Operation(
		summary = "사용 가능 시간 등록",
		description = "튜터가 요일별로 수업 가능한 시간대를 등록합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "시간대 등록 성공"),
		@ApiResponse(responseCode = "400", description = "입력값 유효성 실패"),
		@ApiResponse(responseCode = "409", description = "중복된 시간대")
	})
	public ResponseEntity<Void> registerAvailableTimes(
		@Parameter(description = "Token", required = true, example = "1")
		@RequestHeader("token") String token,

		@Parameter(description = "등록할 시간대 목록", required = true)
		@RequestBody @Valid AvailableTimeCreateRequest wrapper) {

		availableTimeValidation.validOrException(wrapper.getRequestList());
		Long userId = jwtService.extractUserId(token);
		availableTimeComponentService.registerAvailableTimes(userId, wrapper.getRequestList());

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * 사용 가능한 시간대를 삭제합니다.
	 */
	@DeleteMapping("/available-times/v1")
	@Operation(
		summary = "사용 가능 시간 삭제",
		description = "튜터가 기존에 등록한 사용 가능한 시간대를 삭제합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "삭제 성공"),
		@ApiResponse(responseCode = "403", description = "해당 시간대에 대한 권한 없음"),
		@ApiResponse(responseCode = "404", description = "시간대 없음")
	})
	public ResponseEntity<Void> deleteAvailableTimes(
		@Parameter(description = "Token", required = true, example = "1")
		@RequestHeader("token") String token,

		@Parameter(description = "삭제할 시간대 ID 목록", required = true)
		@RequestBody @Valid AvailableTimeDeleteRequest availableTimeDeleteRequest) {

		Long userId = jwtService.extractUserId(token);
		availableTimeComponentService.deleteAllByIds(userId, availableTimeDeleteRequest.getAvailableTimeIds());

		return ResponseEntity.noContent().build();
	}
}
