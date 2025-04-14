package com.challenge.server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;
import com.challenge.server.domain.profile.StudentProfile;
import com.challenge.server.repository.StudentProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

	private final StudentProfileRepository studentProfileRepository;

	@Transactional(readOnly = true)
	public StudentProfile findByUserId(Long userId) {
		return studentProfileRepository.findByUserId(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "StudentProfile not found"));
	}
}
