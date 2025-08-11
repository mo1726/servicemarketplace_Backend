package com.example.service_marketplace.Mapper;

import com.example.service_marketplace.Dto.UserDto;
import com.example.service_marketplace.Entity.User;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "avatar", source = "avatar")
    @Mapping(target = "active", source = "active")
    UserDto toDto(User user);

    @Mapping(target = "active", source = "active")
    User toEntity(UserDto userDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserDto dto, @MappingTarget User entity);

}
