package com.challenge.server.domain.time;

import java.time.LocalTime;

import com.challenge.server.comoon.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "time_slot")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TimeSlot extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalTime startTime;

	@Enumerated(EnumType.STRING)
	private SessionLength sessionLength;

	public TimeSlot(LocalTime startTime, SessionLength sessionLength) {
		this.startTime = startTime;
		this.sessionLength = sessionLength;
	}
}
