package ru.ageeva.userservice.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.ageeva.userservice.dto.UserCreateDto;
import ru.ageeva.userservice.dto.UserResponseDto;
import ru.ageeva.userservice.dto.UserUpdateDto;
import ru.ageeva.userservice.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto(UserEntity entity);

    UserEntity toEntity(UserCreateDto createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserUpdateDto updateDto, @MappingTarget UserEntity entity);
}
