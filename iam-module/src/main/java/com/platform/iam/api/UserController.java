package com.platform.iam.api;

import com.platform.iam.domain.model.User;
import com.platform.iam.domain.model.Vendor;
import com.platform.iam.domain.repository.UserRepository;
import com.platform.iam.domain.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/{userId}/info-mini")
    public ResponseEntity<UserInfo> getUserInfoMini(@PathVariable UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new UserInfo(userId, user.getFullName(), user.getEmail()));
    }
    @GetMapping("/users/{userId}/email")
    public ResponseEntity<String> getUserEmail(@PathVariable UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        return ResponseEntity.ok(user != null ? user.getEmail() : null);
    }
    @GetMapping("/users/{userId}/phone")
    public ResponseEntity<String> getUserPhone(@PathVariable UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        String phone = user != null ? user.getPhoneNumber() : null;
        return ResponseEntity.ok(phone);
    }
    public record UserInfo(UUID userId, String fullName, String email) {}

}