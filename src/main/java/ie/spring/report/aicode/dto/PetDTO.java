package ie.spring.report.aicode.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PetDTO(
        @NotBlank(message = "Name is mandatory")
        String name,

        @NotBlank(message = "Animal type is mandatory")
        String animalType,

        @NotBlank(message = "Breed is mandatory")
        String breed,

        @Min(value = 0, message = "Age must be non-negative")
        int age,

        @NotBlank(message = "Household Eircode is mandatory")
        String householdEircode
) {}
