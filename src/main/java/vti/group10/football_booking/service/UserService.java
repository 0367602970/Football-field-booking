package vti.group10.football_booking.service;

import org.springframework.stereotype.Service;

import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");   
        }
        User savedUser = userRepository.save(user);
        return savedUser;
    }
    public User getById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
