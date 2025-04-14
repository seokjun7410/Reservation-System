package com.challenge.server.repository.custom;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.challenge.server.domain.time.QTimeSlot;
import com.challenge.server.domain.time.SessionLength;
import com.challenge.server.domain.time.TimeSlot;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class TimeSlotRepositoryCustomImpl implements TimeSlotRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public TimeSlotRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
	}

	@Override
	public List<TimeSlot> findByStartTimesAndSessionLengthInMinutes(List<LocalTime> startTimes,
		List<SessionLength> sessionLengths) {
		QTimeSlot timeSlot = QTimeSlot.timeSlot;
		BooleanBuilder builder = new BooleanBuilder();

		// 두 리스트의 크기가 서로 다를 수 있으므로, 최소 크기까지만 처리
		int size = startTimes.size();
		for (int i = 0; i < size; i++) {
			LocalTime start = startTimes.get(i);
			SessionLength sessionLength = sessionLengths.get(i);

			builder.or(
				timeSlot.startTime.eq(start)
					.and(timeSlot.sessionLength.eq(sessionLength))
			);
		}

		return queryFactory.selectFrom(timeSlot)
			.where(builder)
			.fetch();
	}
}
