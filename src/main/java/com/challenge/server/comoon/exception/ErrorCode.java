package com.challenge.server.comoon.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("checkstyle:NoWhitespaceBefore")
@Getter
@AllArgsConstructor
@Slf4j
public enum ErrorCode {

	/* ------------------ 400 BAD_REQUEST : 잘못된 요청 ------------------ */

	REQUEST_VALIDATION(BAD_REQUEST, "요청이 잘못되었습니다."),
	NOT_FOUND(BAD_REQUEST, "존재하지 않는 데이터를 요구하는 요청입니다."),
	CREATE_CONFLICT(BAD_REQUEST, "중복되는 데이터 생성 요청입니다."),

	/* ------------------ 401 BAD_REQUEST : 권한 없음 ------------------ */
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
	LOCK_FAILED(LOCKED, " 잠시 후 다시 시도해주세요."),

	/* ------------------ 500 INTERNAL_SERVER_ERROR : 내부 오류 ------------------ */
	NOT_NULL(INTERNAL_SERVER_ERROR, "서버 내부 오류입니다. 관리자에게 문의바랍니다."),

	SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 내부 오류입니다. 관리자에게 문의바랍니다.");

	private final HttpStatus httpStatus;
	private final String detail;
}
