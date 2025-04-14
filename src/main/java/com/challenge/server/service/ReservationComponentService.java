package com.challenge.server.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.server.comoon.exception.CustomException;
import com.challenge.server.comoon.exception.ErrorCode;
import com.challenge.server.domain.profile.StudentProfile;
import com.challenge.server.domain.reservation.Reservation;
import com.challenge.server.domain.time.AvailableTime;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.dto.NewReservationRequest;
import com.challenge.server.dto.ReservationResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReservationComponentService {

	private final ReservationService reservationService;
	private final AvailableTimeService availableTimeService;
	private final StudentProfileService studentProfileService;
	private final AvailableTimeCacheService availableTimeCacheService;
	private final RedissonClient redissonClient;

	@Transactional
	public void createReservation(NewReservationRequest request, Long userId) {

		// 0. 분산 락 key 생성 (튜터 ID + 날짜 + 시작시간)
		String lockKey = String.format("reservation-lock:%d:%s:%s",
			request.getTutorId(),
			request.getReservationDate(),
			request.getStart());

		RLock lock = redissonClient.getLock(lockKey);
		boolean isLocked = false;

		try {
			// 1. 락 획득 (최대 3초 대기, 5초 후 자동 해제)
			isLocked = lock.tryLock(3, 5, TimeUnit.SECONDS);
			if (!isLocked) {
				throw new CustomException(ErrorCode.LOCK_FAILED, "잠시 후 다시 시도해주세요.");
			}

			// 2. AvailableTime 조회
			AvailableTime availableTime = availableTimeService.findByTutorIdAndSessionLengthAndDayOfWeek(
				request.getTutorId(),
				SessionLength.from(request.getSessionLength()),
				request.getReservationDate().getDayOfWeek(),
				request.getStart()
			);

			// 3. 학생 프로필 조회
			StudentProfile studentProfile = studentProfileService.findByUserId(userId);

			// 4. Reservation 생성
			Reservation reservation = new Reservation();
			reservation.setAvailableTime(availableTime);
			reservation.setReservationDate(request.getReservationDate());
			reservation.setStudentProfile(studentProfile);

			// 5. 저장
			try {
				reservationService.save(reservation);
			} catch (DataIntegrityViolationException ex) {
				throw new CustomException(ErrorCode.CREATE_CONFLICT, "이미 예약된 시간입니다.");
			}

			// 6. 캐시 무효화
			String cacheKey = availableTimeCacheService.generateKey(
				request.getReservationDate(),
				request.getSessionLength()
			);
			availableTimeCacheService.evict(cacheKey);

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new CustomException(ErrorCode.LOCK_FAILED, "예약 중 인터럽트가 발생했습니다.");
		} finally {
			if (isLocked && lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	public List<ReservationResponse> getReservationByUserId(Long userId) {
		StudentProfile studentProfile = studentProfileService.findByUserId(userId);
		return reservationService.findAllByStudentProfileId(studentProfile.getId());
	}
}
