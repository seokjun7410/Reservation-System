package com.challenge.server.comoon.response;

import com.challenge.server.comoon.exception.ErrorCode;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponseHandler<T> {
	private int status;
	private String responseMessage;
	private T response;

	public ResponseHandler(final int status, final String message, T data) {
		this.status = status;
		this.responseMessage = message;
		this.response = data;
	}

	public static <T> ResponseHandler<T> response(T data) {
		return ResponseHandler.<T>builder()
			.status(200)
			.responseMessage("SUCCESS")
			.response(data)
			.build();
	}

	public static <T> ResponseHandler<T> errorResponse(final ErrorCode errorCode) {
		return ResponseHandler.<T>builder()
			.status(errorCode.getHttpStatus().value())
			.responseMessage(errorCode.name())
			.response((T)errorCode.getDetail())
			.build();
	}

	public static <T> ResponseHandler<T> errorResponse(final ErrorCode errorCode, final String message) {
		return ResponseHandler.<T>builder()
			.status(errorCode.getHttpStatus().value())
			.responseMessage(errorCode.name())
			.response((T)message)
			.build();
	}
}
