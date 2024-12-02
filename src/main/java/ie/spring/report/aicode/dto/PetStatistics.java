package ie.spring.report.aicode.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PetStatistics {
    // Getters and Setters
    private Double averageAge;
    private Integer oldestAge;
    private Long totalCount;

}
