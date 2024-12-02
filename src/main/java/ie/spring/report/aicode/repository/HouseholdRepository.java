package ie.spring.report.aicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ie.spring.report.aicode.model.Household;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface HouseholdRepository extends JpaRepository<Household, String> {

    // 4. Read Household by ID - including pets details
    @Query("SELECT h FROM Household h LEFT JOIN FETCH h.pets WHERE h.eircode = :eircode")
    Household findHouseholdWithPetsByEircode(@Param("eircode") String eircode);

    // 8. Find Households with no pets
    @Query("SELECT h FROM Household h WHERE h.pets IS EMPTY")
    List<Household> findHouseholdsWithNoPets();

    // 9. Find Households that are owner-occupied
    List<Household> findByOwnerOccupied(boolean ownerOccupied);

    // 10. Get Household Statistics
    // Number of empty houses (number_of_occupants = 0)
    @Query("SELECT COUNT(h) FROM Household h WHERE h.numberOfOccupants = 0")
    Long countEmptyHouses();

    // Number of full houses (number_of_occupants = max_number_of_occupants)
    @Query("SELECT COUNT(h) FROM Household h WHERE h.numberOfOccupants = h.maxNumberOfOccupants")
    Long countFullHouses();
}

