package com.challenge.server.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableTimeDeleteRequest {
	@NotEmpty(message = "삭제할 시간 ID 리스트는 필수입니다.")
	@NotNull
	private List<Long> availableTimeIds;
}
