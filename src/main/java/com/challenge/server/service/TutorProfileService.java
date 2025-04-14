package com.challenge.server.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;
import com.challenge.server.domain.profile.TutorProfile;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.dto.TutorResponse;
import com.challenge.server.repository.TutorProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TutorProfileService {

	private final TutorProfileRepository tutorProfileRepository;

	/**
	 * 튜터 프로필을 조회합니다.
	 *
	 * @param userId
	 * @return
	 */

	@Transactional(readOnly = true)
	public TutorProfile getTutorProfileByUserId(Long userId) {
		return tutorProfileRepository.findByUserId(userId)
			.orElseThrow(
				() -> new CustomException(ErrorCode.NOT_FOUND, "userId " + userId + "에 해당하는 튜터 프로필을 찾을 수 없습니다."));
	}

	@Transactional(readOnly = true)
	public List<TutorResponse> getAvailableTutorProfile(LocalDate date, LocalTime start, int sessionLength) {
		return tutorProfileRepository.findAvailableTutorsByDateAndTimeAndSessionLength(date, date.getDayOfWeek(), start,
				SessionLength.from(sessionLength))
			.stream()
			.map(m -> new TutorResponse(m.getId(), m.getUser().getName(), m.getBio()))
			.collect(Collectors.toList());
	}
}
