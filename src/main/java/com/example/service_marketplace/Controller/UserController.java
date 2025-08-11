package com.example.service_marketplace.Controller;

import com.example.service_marketplace.Dto.UserDto;
import com.example.service_marketplace.Entity.User;
import com.example.service_marketplace.Repository.UserRepository;
import com.example.service_marketplace.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository  userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }
    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal com.example.service_marketplace.Entity.User principal) {
        return ResponseEntity.ok(userService.getUserById(principal.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateMe(
            @AuthenticationPrincipal com.example.service_marketplace.Entity.User principal,
            @RequestBody UserDto dto
    ) {
        return ResponseEntity.ok(userService.updateUser(principal.getId(), dto));
    }
    @PutMapping("/me/avatar")
    public ResponseEntity<UserDto> uploadMyAvatar(
            @AuthenticationPrincipal User principal,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        UserDto updatedUser = userService.updateAvatar(principal.getId(), file);
        return ResponseEntity.ok(updatedUser);
    }
    @PutMapping("/activate/{id}")
    public ResponseEntity<UserDto> activateProvider(@PathVariable Long id) {
        UserDto updated = userService.activateUser(id);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(true);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User activated"));
    }





    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

