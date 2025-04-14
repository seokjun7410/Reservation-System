package com.challenge.server.domain.reservation;

import java.time.LocalDate;

import com.challenge.server.comoon.BaseEntity;
import com.challenge.server.domain.profile.StudentProfile;
import com.challenge.server.domain.time.AvailableTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservation", uniqueConstraints = @UniqueConstraint(columnNames = {"available_time_id",
	"reservation_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "available_time_id", nullable = false)
	private AvailableTime availableTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_profile_id", nullable = false)
	private StudentProfile studentProfile;

	private LocalDate reservationDate;
}
