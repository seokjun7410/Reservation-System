package com.challenge.server.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTimeCreateRequest {
	@NotEmpty(message = "비어 있을 수 없습니다.")
	@NotNull(message = "null일 수 없습니다.")
	@Valid
	List<AvailableTimeData> requestList;
}
