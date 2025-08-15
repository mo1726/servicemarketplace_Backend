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
            @Mapping(source = "provider.fullName",  target = "providerName"),
            @Mapping(source = "provider.fullName",  target = "fullName"),
            @Mapping(source = "provider",           target = "provider"),
            // ✅ map ad fields
            @Mapping(source = "adActive",           target = "adActive"),
            @Mapping(source = "adStartDate",        target = "adStartDate"),
            @Mapping(source = "adEndDate",          target = "adEndDate"),
            @Mapping(source = "adPriority",         target = "adPriority")
    })
    ServiceDto toDto(Service service);

    @Mappings({
            @Mapping(source = "providerId", target = "provider.id"),
            @Mapping(source = "categoryId", target = "category.id"),
            // ✅ reverse mapping for ad fields
            @Mapping(source = "adActive",    target = "adActive"),
            @Mapping(source = "adStartDate", target = "adStartDate"),
            @Mapping(source = "adEndDate",   target = "adEndDate"),
            @Mapping(source = "adPriority",  target = "adPriority")
    })
    Service toEntity(ServiceDto dto);

    @AfterMapping
    default void ensureOnlyFullName(Service src, @MappingTarget ServiceDto dst) {
        if (src == null) return;
        User p = src.getProvider();
        if (p == null) return;

        String full = p.getFullName();
        if (full != null && !full.isBlank()) {
            dst.setProviderName(full);
            dst.setFullName(full);
        } else {
            dst.setProviderName(null);
        }
    }
}
