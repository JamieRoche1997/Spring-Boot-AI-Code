package ie.spring.report.aicode.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HouseholdDTO(
        @NotBlank(message = "Eircode is mandatory")
        String eircode,

        @Min(value = 0, message = "Number of occupants must be non-negative")
        int numberOfOccupants,

        @Min(value = 1, message = "Max number of occupants must be at least 1")
        int maxNumberOfOccupants,

        @NotNull(message = "Owner occupied status is mandatory")
        Boolean ownerOccupied
) {}
