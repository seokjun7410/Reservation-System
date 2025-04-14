package com.challenge.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.challenge.server.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
