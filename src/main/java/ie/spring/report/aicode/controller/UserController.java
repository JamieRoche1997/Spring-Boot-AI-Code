package ie.spring.report.aicode.controller;

import ie.spring.report.aicode.model.AppUser;
import ie.spring.report.aicode.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    // Create a new user - ADMIN only
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_ADMIN")
    public AppUser createUser(@Valid @RequestBody AppUser user) {
        return userService.createUser(user);
    }

    // Reset user's password - ADMIN only
    @PutMapping("/{username}/reset-password")
    @Secured("ROLE_ADMIN")
    public AppUser resetPassword(@PathVariable String username, @RequestParam String newPassword) {
        return userService.resetPassword(username, newPassword);
    }

    // Toggle user's unlocked status - ADMIN only
    @PutMapping("/{username}/toggle-unlocked")
    @Secured("ROLE_ADMIN")
    public AppUser toggleUnlocked(@PathVariable String username) {
        return userService.toggleUnlocked(username);
    }

    // Delete a user - ADMIN only
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    public void deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
    }

    // Get all users - ADMIN only
    @GetMapping
    @Secured("ROLE_ADMIN")
    public List<AppUser> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get a single user - ADMIN only
    @GetMapping("/{username}")
    @Secured("ROLE_ADMIN")
    public AppUser getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }
}

