package com.example.service_marketplace.Mapper;

import com.example.service_marketplace.Dto.ReviewDto;
import com.example.service_marketplace.Entity.Review;
import com.example.service_marketplace.Entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mappings({
            @Mapping(source = "service.id", target = "serviceId"),
            @Mapping(source = "user.id",    target = "userId"),
            // we'll fill userFullName/userAvatarUrl in @AfterMapping
            @Mapping(target = "userFullName",  ignore = true),
            @Mapping(target = "userAvatarUrl", ignore = true)
    })
    ReviewDto toDto(Review review);
    @Mappings({
            @Mapping(source = "serviceId", target = "service.id"),
            @Mapping(source = "userId",    target = "user.id")
    })
    Review toEntity(ReviewDto dto);

    @AfterMapping
    default void fillUserFields(Review review, @MappingTarget ReviewDto dto) {
        User u = review.getUser();
        if (u == null) return;

        // try fullName, then first+last, then username
        String full = null;
        try {
            full = (String) User.class.getMethod("getFullName").invoke(u);
        } catch (Exception ignored) {}
        if (full == null || full.isBlank()) {
            String first = safe(u, "getFirstName");
            String last  = safe(u, "getLastName");
            String composed = ((first != null ? first : "") + " " + (last != null ? last : "")).trim();
            full = !composed.isBlank() ? composed : safe(u, "getUsername");
        }
        dto.setUserFullName(full);

        // avatar if your User entity has it (avatarUrl/avatar/photo)
        String avatar = safe(u, "getAvatarUrl");
        if (avatar == null) avatar = safe(u, "getAvatar");
        if (avatar == null) avatar = safe(u, "getPhotoUrl");
        dto.setUserAvatarUrl(avatar);
    }

    private static String safe(User u, String getter) {
        try {
            Object v = User.class.getMethod(getter).invoke(u);
            return v != null ? v.toString() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

}
