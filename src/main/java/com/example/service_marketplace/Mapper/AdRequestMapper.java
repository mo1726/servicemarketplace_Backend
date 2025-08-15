// src/main/java/com/example/service_marketplace/Mapper/AdRequestMapper.java
package com.example.service_marketplace.Mapper;

import com.example.service_marketplace.Dto.AdRequestDto;
import com.example.service_marketplace.Entity.AdRequest;
import com.example.service_marketplace.Entity.Service;
import com.example.service_marketplace.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface AdRequestMapper {

    // Entity -> DTO
    @Mappings({
            @Mapping(source = "service.id", target = "serviceId"),
            @Mapping(source = "service.title", target = "serviceTitle"),
            @Mapping(source = "provider.id", target = "providerId"),
            @Mapping(source = "provider.fullName", target = "providerName"),
            @Mapping(source = "status", target = "status")
    })
    AdRequestDto toDto(AdRequest adRequest);

    // DTO -> Entity (IDs only; fetch full entities in service layer if needed)
    @Mappings({
            @Mapping(target = "service", expression = "java(serviceFromId(dto.getServiceId()))"),
            @Mapping(target = "provider", expression = "java(providerFromId(dto.getProviderId()))"),
            // keep everything else default
            @Mapping(target = "status", ignore = true) // usually set in service
    })
    AdRequest toEntity(AdRequestDto dto);

    // Helpers
    default Service serviceFromId(Long id) {
        if (id == null) return null;
        Service s = new Service();
        s.setId(id);
        return s;
    }

    default User providerFromId(Long id) {
        if (id == null) return null;
        User u = new User();
        u.setId(id);
        return u;
    }
}
