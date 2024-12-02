package ie.spring.report.aicode.service;

import ie.spring.report.aicode.model.Household;
import ie.spring.report.aicode.dto.HouseholdStatistics;
import java.util.List;

public interface HouseholdService {

    // 1. Create Household
    Household createHousehold(Household household);

    // 2. Read All Households
    List<Household> getAllHouseholds();

    // 3. Read Household by ID - no pets details
    Household getHouseholdByEircodeNoPets(String eircode);

    // 4. Read Household by ID - including pets details
    Household getHouseholdByEircodeWithPets(String eircode);

    // 5. Update Household Details
    Household updateHousehold(String eircode, Household householdDetails);

    // 6. Delete Household by ID
    void deleteHousehold(String eircode);

    // 7. Delete Pets by Name
    void deletePetsByName(String name);

    // 8. Find Households with no pets
    List<Household> findHouseholdsWithNoPets();

    // 9. Find Households that are owner-occupied
    List<Household> findOwnerOccupiedHouseholds();

    // 10. Get Household Statistics
    HouseholdStatistics getHouseholdStatistics();
}

