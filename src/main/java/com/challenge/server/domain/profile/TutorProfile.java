package com.challenge.server.domain.profile;

import java.util.Objects;

import com.challenge.server.comoon.BaseEntity;
import com.challenge.server.domain.time.AvailableTime;
import com.challenge.server.domain.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tutor_profile")
@Getter
@NoArgsConstructor
public class TutorProfile extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String bio;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	public TutorProfile(String bio, User user) {
		this.bio = bio;
		this.user = user;
	}

	public boolean isOwner(AvailableTime availableTime) {
		return Objects.equals(availableTime.getTutorProfile().getId(), this.id);
	}
}
