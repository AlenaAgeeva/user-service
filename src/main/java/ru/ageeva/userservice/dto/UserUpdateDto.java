package ru.ageeva.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateDto(
        @NotBlank(message = "FirstName is required")
        String firstName,

        @NotBlank(message = "LastName is required")
        String lastName
) {}
