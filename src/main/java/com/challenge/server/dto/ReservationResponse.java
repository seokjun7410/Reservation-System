package com.challenge.server.dto;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import com.challenge.server.domain.time.SessionLength;

public interface ReservationResponse {

	Long getReservationId();

	String getBio();

	String getTutorName();

	LocalTime getStartTime();

	SessionLength getSessionLength();

	DayOfWeek getDayOfWeek();

	LocalDate getReservationDate();

	Timestamp getCreatedAt();
}
