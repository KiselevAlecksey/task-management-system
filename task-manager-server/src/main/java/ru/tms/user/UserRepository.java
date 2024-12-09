package ru.tms.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tms.user.model.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmailContainingIgnoreCase(String email);
}
