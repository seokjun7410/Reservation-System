package com.challenge.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotCreateRequest {
	@Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "시간은 HH:mm 형식이어야 합니다.")
	@NotNull
	@Schema(description = "시작 시간", example = "10:30")
	private String startTime;

	@NotNull
	@Schema(description = "수업 시간", example = "30")
	private Integer sessionLength;
}
