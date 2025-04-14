package com.challenge.server;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

@Repository
public class CleanUp {

	@Autowired
	private EntityManager em;

	private final List<String> tableNames = List.of(
		"reservation",
		"available_time",
		"time_slot"
	);

	@Transactional
	public void truncateAll() {
		em.flush();
		em.clear();

		em.createNativeQuery("SET foreign_key_checks = 0").executeUpdate();
		for (String table : tableNames) {
			em.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
		}
		em.createNativeQuery("SET foreign_key_checks = 1").executeUpdate();
	}
}
