package ru.tms.userduplicate;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tms.userduplicate.model.UserDuplicate;

public interface UserRepository extends JpaRepository<UserDuplicate, Long> {
    Boolean existsByEmailContainingIgnoreCase(String email);
}
