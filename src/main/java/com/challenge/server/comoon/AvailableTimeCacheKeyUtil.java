package com.challenge.server.comoon;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

@Component
public class AvailableTimeCacheKeyUtil {
	public String generateKey(LocalDate date, int sessionLength) {
		return String.format("available-times::%s::%d", date, sessionLength);
	}
}

