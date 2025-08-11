package com.example.service_marketplace.Service.Impl;

import com.example.service_marketplace.Dto.ServiceDto;
import com.example.service_marketplace.Entity.Category;
import com.example.service_marketplace.Entity.Review;
import com.example.service_marketplace.Entity.Service;
import com.example.service_marketplace.Entity.User;
import com.example.service_marketplace.Mapper.ServiceMapper;
import com.example.service_marketplace.Repository.CategoryRepository;
import com.example.service_marketplace.Repository.ServiceRepository;
import com.example.service_marketplace.Repository.UserRepository;
import com.example.service_marketplace.Service.ServiceService;
import com.example.service_marketplace.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final ServiceMapper serviceMapper;
    private final CategoryRepository categoryRepository;

    private static String norm(String s) {
        if (s == null) return null;
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toLowerCase();
    }

    @Override
    public ServiceDto createService(ServiceDto dto) {
        User provider = userRepository.findById(dto.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Service service = serviceMapper.toEntity(dto);
        service.setProvider(provider);
        service.setCategory(category);

        return serviceMapper.toDto(serviceRepository.save(service));
    }

    @Override
    public ServiceDto getServiceById(Long id) {
        return serviceMapper.toDto(
                serviceRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Service not found"))
        );
    }

    @Override
    public List<ServiceDto> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(serviceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceDto> getServicesByProvider(Long providerId) {
        return serviceRepository.findByProviderId(providerId)
                .stream()
                .map(serviceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceDto> filterServices(String title, String location, Double maxPrice, Double minRating, Long categoryId) {
        String titleQ = (title != null && !title.isBlank()) ? norm(title) : null;
        String locQ   = (location != null && !location.isBlank()) ? norm(location) : null;

        List<Service> services = serviceRepository.findAll().stream()
                .filter(s -> titleQ == null || (s.getTitle() != null && norm(s.getTitle()).contains(titleQ)))
                .filter(s -> locQ == null || (s.getLocation() != null && norm(s.getLocation()).contains(locQ)))
                .filter(s -> maxPrice == null || (s.getPrice() != null && s.getPrice() <= maxPrice))
                .filter(s -> {
                    if (minRating == null) return true;
                    if (s.getReviews() == null || s.getReviews().isEmpty()) return false;
                    double avg = s.getReviews().stream().mapToDouble(Review::getRating).average().orElse(0);
                    return avg >= minRating;
                })
                .filter(s -> categoryId == null ||
                        (s.getCategory() != null &&
                                s.getCategory().getId() != null &&
                                s.getCategory().getId().equals(categoryId)))
                .toList();

        return services.stream().map(serviceMapper::toDto).toList();
    }

    @Override
    public ServiceDto updateService(Long id, ServiceDto dto) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        if (dto.getTitle() != null)       service.setTitle(dto.getTitle());
        if (dto.getDescription() != null) service.setDescription(dto.getDescription());
        if (dto.getLocation() != null)    service.setLocation(dto.getLocation());
        if (dto.getPrice() != null)       service.setPrice(dto.getPrice());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            service.setCategory(category);
        }

        return serviceMapper.toDto(serviceRepository.save(service));
    }

    @Override
    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    // ✅ Save image to /uploads/services and set relative URL
    @Override
    public ServiceDto updateImage(Long id, MultipartFile file) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            Path root = Paths.get("uploads/services").toAbsolutePath();
            Files.createDirectories(root);

            String ext = getExt(file.getOriginalFilename());
            String fileName = "svc_" + id + "_" + UUID.randomUUID() + (ext.isEmpty() ? ".jpg" : ext);
            Path path = root.resolve(fileName);

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // store relative path
            service.setImageUrl("/uploads/services/" + fileName);
            return serviceMapper.toDto(serviceRepository.save(service));
        } catch (IOException e) {
            throw new RuntimeException("Failed to store service image", e);
        }
    }

    private static String getExt(String name) {
        if (name == null) return "";
        int i = name.lastIndexOf('.');
        return (i >= 0 ? name.substring(i) : "");
    }
}
