package ru.ageeva.userservice.dto;

public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String passport
) {}
