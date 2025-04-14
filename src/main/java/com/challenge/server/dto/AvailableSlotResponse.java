package com.challenge.server.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotResponse {
	private LocalTime startTime;
	private LocalTime endTime;
	private int sessionLength;
}
