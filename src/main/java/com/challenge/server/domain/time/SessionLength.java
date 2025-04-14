package com.challenge.server.domain.time;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SessionLength {
	thirty(30), sixty(60);

	private final int length;

	public static SessionLength from(int value) {
		for (SessionLength ti : values()) {
			if (ti.length == value) {
				return ti;
			}
		}
		throw new IllegalArgumentException("Invalid sessionLength: " + value);
	}
}
