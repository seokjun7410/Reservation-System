package com.challenge.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.challenge.server.domain.profile.StudentProfile;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
	Optional<StudentProfile> findByUserId(Long userId);
}
