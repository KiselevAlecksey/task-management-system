package ru.tms.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tms.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    UserEmail findByEmailContainingIgnoreCase(String email);
}
