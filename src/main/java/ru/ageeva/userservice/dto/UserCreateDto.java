package ru.ageeva.userservice.dto;

import jakarta.validation.constraints.NotBlank;


public record UserCreateDto(
        @NotBlank(message = "FirstName is required")
        String firstName,

        @NotBlank(message = "LastName is required")
        String lastName,

        @NotBlank(message = "Passport is required")
        String passport
) {
}
