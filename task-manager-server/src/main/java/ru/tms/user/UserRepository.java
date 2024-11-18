package ru.tms.user;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.tms.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    UserEmail findByEmailContainingIgnoreCase(String email);
}
