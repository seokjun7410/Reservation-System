package com.challenge.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TutorResponse {
	private Long tutorId;
	private String name;
	private String bio;
}
