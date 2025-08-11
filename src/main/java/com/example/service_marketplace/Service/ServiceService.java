package com.example.service_marketplace.Service;

import com.example.service_marketplace.Dto.ServiceDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ServiceService {

    ServiceDto createService(ServiceDto dto);
    ServiceDto getServiceById(Long id);
    List<ServiceDto> getAllServices();
    List<ServiceDto> getServicesByProvider(Long providerId);
    List<ServiceDto> filterServices(String title, String location, Double maxPrice, Double minRating, Long categoryId);


    ServiceDto updateService(Long id, ServiceDto dto);
    ServiceDto updateImage(Long id, MultipartFile file);
    void deleteService(Long id);
}