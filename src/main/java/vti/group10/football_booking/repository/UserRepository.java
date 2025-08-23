package vti.group10.football_booking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vti.group10.football_booking.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findById(Integer userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);
}
