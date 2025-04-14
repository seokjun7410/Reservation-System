package com.challenge.server.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyAvailabilityResponse {

	private LocalDate date;                // 2025-04-11
	private List<AvailableSlotResponse> availableSlots;  // [ {tutorId=1, startTime=09:00}, ... ]

	public int date(DailyAvailabilityResponse dailyAvailabilityResponse) {
		return this.date.compareTo(dailyAvailabilityResponse.date);
	}
}
