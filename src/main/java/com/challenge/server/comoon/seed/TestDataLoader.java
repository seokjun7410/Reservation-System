package com.challenge.server.comoon.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestDataLoader implements CommandLineRunner {
	private final TestDataService service;

	@Override
	public void run(String... args) {
		service.createTestData();
	}
}
