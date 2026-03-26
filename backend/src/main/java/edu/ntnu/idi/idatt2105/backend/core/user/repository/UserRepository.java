package edu.ntnu.idi.idatt2105.backend.core.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
