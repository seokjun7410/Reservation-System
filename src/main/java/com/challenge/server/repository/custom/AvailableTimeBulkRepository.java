package com.challenge.server.repository.custom;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.challenge.server.domain.time.AvailableTime;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AvailableTimeBulkRepository {

	private final JdbcTemplate jdbcTemplate;

	@Transactional
	public void saveAll(List<AvailableTime> availableTimeList) {
		String sql = """
			INSERT INTO available_time (tutor_profile_id, time_slot_id, day_of_week, created_date, modified_date)
			VALUES (?, ?, ?, ?, ?)
			""";

		LocalDateTime now = LocalDateTime.now();

		jdbcTemplate.batchUpdate(sql, availableTimeList, availableTimeList.size(),
			(PreparedStatement ps, AvailableTime availableTime) -> {
				ps.setLong(1, availableTime.getTutorProfile().getId());
				ps.setLong(2, availableTime.getTimeSlot().getId());
				ps.setString(3, availableTime.getDayOfWeek().name());
				ps.setTimestamp(4, Timestamp.valueOf(now));
				ps.setTimestamp(5, Timestamp.valueOf(now));
			});
	}

	@Transactional
	public void deleteAll(List<AvailableTime> availableTimes) {
		// 삭제할 엔티티의 ID 목록 추출
		List<Long> ids = availableTimes.stream()
			.map(AvailableTime::getId)
			.collect(Collectors.toList());

		// batch delete SQL - PK가 id라고 가정
		String sql = "DELETE FROM available_time WHERE id = ?";

		jdbcTemplate.batchUpdate(sql, ids, ids.size(), (PreparedStatement ps, Long id) -> {
			ps.setLong(1, id);
		});
	}
}
