package ie.spring.report.aicode.service;

import ie.spring.report.aicode.model.AppUser;
import ie.spring.report.aicode.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Create a new user
    public AppUser createUser(AppUser user) {
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Reset user's password
    public AppUser resetPassword(String username, String newPassword) {
        Optional<AppUser> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // Toggle user's unlocked status
    public AppUser toggleUnlocked(String username) {
        Optional<AppUser> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            user.setUnlocked(!user.isUnlocked());
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // Delete a user
    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }

    // Get all users
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a single user
    public AppUser getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

