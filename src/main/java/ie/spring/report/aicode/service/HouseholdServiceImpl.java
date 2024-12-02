package ie.spring.report.aicode.service;

import ie.spring.report.aicode.model.Household;
import ie.spring.report.aicode.model.Pet;
import ie.spring.report.aicode.repository.HouseholdRepository;
import ie.spring.report.aicode.repository.PetRepository;
import ie.spring.report.aicode.exception.HouseholdNotFoundException;
import ie.spring.report.aicode.exception.PetNotFoundException;
import ie.spring.report.aicode.exception.InvalidHouseholdDataException;
import ie.spring.report.aicode.dto.HouseholdStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HouseholdServiceImpl implements HouseholdService {

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private PetRepository petRepository;

    // 1. Create Household
    @Override
    public Household createHousehold(Household household) {
        if (household == null) {
            throw new InvalidHouseholdDataException("Household data cannot be null");
        }
        // Additional validations can be added here
        return householdRepository.save(household);
    }

    // 2. Read All Households
    @Override
    public List<Household> getAllHouseholds() {
        List<Household> households = householdRepository.findAll();
        if (households.isEmpty()) {
            throw new HouseholdNotFoundException("No households found");
        }
        return households;
    }

    // 3. Read Household by ID - no pets details
    @Override
    public Household getHouseholdByEircodeNoPets(String eircode) {
        return householdRepository.findById(eircode)
                .orElseThrow(() -> new HouseholdNotFoundException("Household not found with Eircode: " + eircode));
        // Pets are LAZY loaded; they won't be fetched unless accessed
    }

    // 4. Read Household by ID - including pets details
    @Override
    public Household getHouseholdByEircodeWithPets(String eircode) {
        Household household = householdRepository.findHouseholdWithPetsByEircode(eircode);
        if (household == null) {
            throw new HouseholdNotFoundException("Household not found with Eircode: " + eircode);
        }
        return household;
    }

    // 5. Update Household Details
    @Override
    public Household updateHousehold(String eircode, Household householdDetails) {
        if (householdDetails == null) {
            throw new InvalidHouseholdDataException("Household details cannot be null");
        }
        Household household = getHouseholdByEircodeNoPets(eircode);

        // Update fields
        household.setNumberOfOccupants(householdDetails.getNumberOfOccupants());
        household.setMaxNumberOfOccupants(householdDetails.getMaxNumberOfOccupants());
        household.setOwnerOccupied(householdDetails.isOwnerOccupied());

        return householdRepository.save(household);
    }

    // 6. Delete Household by ID
    @Override
    public void deleteHousehold(String eircode) {
        Household household = getHouseholdByEircodeNoPets(eircode);
        householdRepository.delete(household);
        // Pets will be deleted due to CascadeType.ALL and orphanRemoval = true
    }

    // 7. Delete Pets by Name
    @Override
    public void deletePetsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidHouseholdDataException("Pet name cannot be null or empty");
        }
        List<Pet> pets = petRepository.findByNameIgnoreCase(name);
        if (pets.isEmpty()) {
            throw new PetNotFoundException("No pets found with name: " + name);
        }
        petRepository.deleteAll(pets);
    }

    // 8. Find Households with no pets
    @Override
    public List<Household> findHouseholdsWithNoPets() {
        List<Household> households = householdRepository.findHouseholdsWithNoPets();
        if (households.isEmpty()) {
            throw new HouseholdNotFoundException("No households without pets found");
        }
        return households;
    }

    // 9. Find Households that are owner-occupied
    @Override
    public List<Household> findOwnerOccupiedHouseholds() {
        List<Household> households = householdRepository.findByOwnerOccupied(true);
        if (households.isEmpty()) {
            throw new HouseholdNotFoundException("No owner-occupied households found");
        }
        return households;
    }

    // 10. Get Household Statistics
    @Override
    public HouseholdStatistics getHouseholdStatistics() {
        Long numberOfEmptyHouses = householdRepository.countEmptyHouses();
        Long numberOfFullHouses = householdRepository.countFullHouses();
        return new HouseholdStatistics(numberOfEmptyHouses, numberOfFullHouses);
    }
}

