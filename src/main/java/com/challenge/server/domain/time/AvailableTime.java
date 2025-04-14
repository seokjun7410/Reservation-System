package com.challenge.server.domain.time;

import java.time.DayOfWeek;

import com.challenge.server.comoon.BaseEntity;
import com.challenge.server.domain.profile.TutorProfile;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "available_time", uniqueConstraints = @UniqueConstraint(columnNames = {"tutor_profile_id",
	"time_slot_id", "day_Of_week"}))
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AvailableTime extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tutor_profile_id", nullable = false)
	private TutorProfile tutorProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "time_slot_id", nullable = false)
	private TimeSlot timeSlot;

	@Enumerated(EnumType.STRING)
	private DayOfWeek dayOfWeek;

	public AvailableTime(TutorProfile tutorProfile, TimeSlot timeSlot, DayOfWeek dayOfWeek) {
		this.tutorProfile = tutorProfile;
		this.timeSlot = timeSlot;
		this.dayOfWeek = dayOfWeek;
	}
}
