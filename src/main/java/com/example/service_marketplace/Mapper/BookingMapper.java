package com.example.service_marketplace.Mapper;

import com.example.service_marketplace.Dto.BookingDto;
import com.example.service_marketplace.Entity.Booking;
import com.example.service_marketplace.Entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ServiceMapper.class})
public interface BookingMapper {

    // Entity → DTO
    @Mappings({
            @Mapping(source = "user.id",    target = "userId"),
            @Mapping(source = "service.id", target = "serviceId"),
            @Mapping(source = "user",       target = "user"),
            @Mapping(source = "service",    target = "service"),
    })
    BookingDto toDto(Booking booking);

    // DTO → Entity
    @Mappings({
            @Mapping(source = "userId",    target = "user.id"),
            @Mapping(source = "serviceId", target = "service.id"),
    })
    Booking toEntity(BookingDto dto);

    // ✅ Fill flat user fields for the FE
    @AfterMapping
    default void fillUserFields(Booking booking, @MappingTarget BookingDto dto) {
        User u = booking != null ? booking.getUser() : null;
        if (u == null) return;

        if (dto.getUserFullName() == null || dto.getUserFullName().isBlank()) {
            // Prefer fullName; fallback to username/email
            String full = safe(u, "getFullName");
            if (isNotBlank(full)) dto.setUserFullName(full);
            else {
                String username = safe(u, "getUsername"); // in your entity returns email
                String email    = safe(u, "getEmail");
                dto.setUserFullName(isNotBlank(full) ? full : (isNotBlank(username) ? username : email));
            }
        }

        if (dto.getUserEmail() == null) dto.setUserEmail(safe(u, "getEmail"));
        if (dto.getUserPhone() == null) dto.setUserPhone(safe(u, "getPhone"));
        if (dto.getUserAvatarUrl() == null) dto.setUserAvatarUrl(safe(u, "getAvatar"));
    }

    private static String safe(User u, String getter) {
        try {
            Object v = User.class.getMethod(getter).invoke(u);
            return v != null ? v.toString() : null;
        } catch (Exception ignored) {
            return null;
        }
    }
    private static boolean isNotBlank(String s) { return s != null && !s.isBlank(); }
}
