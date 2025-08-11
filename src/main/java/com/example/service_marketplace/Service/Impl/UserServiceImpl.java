package com.example.service_marketplace.Service.Impl;




import com.example.service_marketplace.Dto.UserDto;
import com.example.service_marketplace.Entity.User;
import com.example.service_marketplace.Mapper.UserMapper;
import com.example.service_marketplace.Repository.UserRepository;
import com.example.service_marketplace.Service.UserService;
import com.example.service_marketplace.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;




@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // If you use MapStruct:
        userMapper.updateUserFromDto(dto, user);

        // Ensure active is handled (MapStruct will handle it if not null; keep this if not using MapStruct)
        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }

        userRepository.save(user);
        return userMapper.toDto(user);
    }


    @Override
    public UserDto activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setActive(true);
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    public UserDto updateAvatar(Long id, MultipartFile file) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Save file to disk
            String fileName = "avatar_" + id + "_" + System.currentTimeMillis() + ".png";
            Path filePath = Paths.get("uploads/avatars", fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            // Store only the relative path in DB
            user.setAvatar("/uploads/avatars/" + fileName);
            userRepository.save(user);

            return userMapper.toDto(user); // Now contains avatar path
        } catch (IOException e) {
            throw new RuntimeException("Error processing avatar", e);
        }
    }






    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
