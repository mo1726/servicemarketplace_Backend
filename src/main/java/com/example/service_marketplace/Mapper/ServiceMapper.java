package com.example.service_marketplace.Mapper;

import com.example.service_marketplace.Dto.ServiceDto;
import com.example.service_marketplace.Entity.Service;
import com.example.service_marketplace.Entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mappings({
            @Mapping(source = "provider.id",        target = "providerId"),
            @Mapping(source = "category.id",        target = "categoryId"),
            @Mapping(source = "category.name",      target = "categoryName"),
            @Mapping(source = "provider.avatar",    target = "providerAvatar"),
            // map full name directly
            @Mapping(source = "provider.fullName",  target = "providerName"),
            @Mapping(source = "provider.fullName",  target = "fullName"),
            @Mapping(source = "provider",           target = "provider")
    })
    ServiceDto toDto(Service service);

    @Mappings({
            @Mapping(source = "providerId", target = "provider.id"),
            @Mapping(source = "categoryId", target = "category.id")
    })
    Service toEntity(ServiceDto dto);

    /** Ensure we never leak email/username as the provider name. */
    @AfterMapping
    default void ensureOnlyFullName(Service src, @MappingTarget ServiceDto dst) {
        if (src == null) return;
        User p = src.getProvider();
        if (p == null) return;

        // take only full name (or null). Do NOT fall back to username/email.
        String full = p.getFullName();
        if (full != null && !full.isBlank()) {
            dst.setProviderName(full);
            dst.setFullName(full);
        } else {
            dst.setProviderName(null);
            // leave fullName as null too
        }
    }
}
