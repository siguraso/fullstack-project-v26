package edu.ntnu.idi.idatt2105.backend.core.user.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findAllByTenantId(Long tenantId);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
