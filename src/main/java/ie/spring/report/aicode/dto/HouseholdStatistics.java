package ie.spring.report.aicode.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HouseholdStatistics {
    // Getters and Setters
    private Long numberOfEmptyHouses;
    private Long numberOfFullHouses;

    public HouseholdStatistics(Long numberOfEmptyHouses, Long numberOfFullHouses) {
        this.numberOfEmptyHouses = numberOfEmptyHouses;
        this.numberOfFullHouses = numberOfFullHouses;
    }

}
