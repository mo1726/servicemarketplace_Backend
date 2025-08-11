package com.example.service_marketplace.Service;

import com.example.service_marketplace.Dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    UserDto updateUser(Long id, UserDto updatedUser);

    UserDto activateUser(Long id);

    UserDto updateAvatar(Long id, MultipartFile file);
    void deleteUser(Long id);
}
