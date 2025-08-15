package com.example.service_marketplace.Controller;

import com.example.service_marketplace.Dto.ServiceDto;
import com.example.service_marketplace.Entity.Service;
import com.example.service_marketplace.Mapper.ServiceMapper;
import com.example.service_marketplace.Repository.ServiceRepository;
import com.example.service_marketplace.Service.ServiceService;
import com.example.service_marketplace.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;
    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    // Create service
    @PostMapping
    public ResponseEntity<ServiceDto> create(@Valid @RequestBody ServiceDto dto) {
        return ResponseEntity.ok(serviceService.createService(dto));
    }

    // Get all services
    @GetMapping
    public ResponseEntity<List<ServiceDto>> getAll() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }


    // Get service by ID
    @GetMapping("/{id}")
    public ResponseEntity<ServiceDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }

    // Get services by provider
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<ServiceDto>> getByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(serviceService.getServicesByProvider(providerId));
    }

    // List active ads
    @GetMapping("/ads")
    public ResponseEntity<List<Service>> listActiveAds() {
        LocalDate now = LocalDate.now();
        List<Service> ads = serviceRepository
                .findByAdActiveTrueAndAdStartDateLessThanEqualAndAdEndDateGreaterThanEqualOrderByAdPriorityDescAdStartDateDesc(now, now);
        return ResponseEntity.ok(ads);
    }

    // Filter services
    @GetMapping("/filter")
    public ResponseEntity<List<ServiceDto>> filterServices(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String categoryName
    ) {
        // If only categoryName is provided, you could resolve it to ID here (future feature)
        return ResponseEntity.ok(
                serviceService.filterServices(title, location, maxPrice, minRating, categoryId)
        );
    }

    // Update service
    @PutMapping("/{id}")
    public ResponseEntity<ServiceDto> update(@PathVariable Long id, @Valid @RequestBody ServiceDto dto) {
        return ResponseEntity.ok(serviceService.updateService(id, dto));
    }

    // Upload service image (API: /services/{id}/image)
    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceDto uploadServiceImage(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) throws IOException {

        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        String fileName = "service_" + id + "_" + System.currentTimeMillis() + ".png";
        Path dst = Paths.get("uploads/services", fileName);
        Files.createDirectories(dst.getParent());
        Files.copy(file.getInputStream(), dst, StandardCopyOption.REPLACE_EXISTING);

        service.setImageUrl("/uploads/services/" + fileName);
        serviceRepository.save(service);

        return serviceMapper.toDto(service);
    }

    // Delete service
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
