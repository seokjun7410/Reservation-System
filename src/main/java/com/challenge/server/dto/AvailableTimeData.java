package com.challenge.server.dto;

import java.time.DayOfWeek;
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
public class AvailableTimeData {
	@NotNull(message = "Day of the week cannot be null")
	private DayOfWeek dayOfWeek;

	@NotEmpty(message = "createAvailableTimeDto cannot be null")
	@Valid
	private List<TimeSlotCreateRequest> timeSlotCreateRequestList;
}
