package ie.spring.report.aicode.controller;

import ie.spring.report.aicode.dto.HouseholdDTO;
import ie.spring.report.aicode.dto.HouseholdStatistics;
import ie.spring.report.aicode.model.Household;
import ie.spring.report.aicode.service.HouseholdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/households")
public class HouseholdController {

    @Autowired
    private HouseholdService householdService;

    // 1. Create Household
    @PostMapping
    @Secured("ROLE_ADMIN")
    public Household createHousehold(@Valid @RequestBody HouseholdDTO householdDTO) {
        Household household = new Household();
        household.setEircode(householdDTO.eircode());
        household.setNumberOfOccupants(householdDTO.numberOfOccupants());
        household.setMaxNumberOfOccupants(householdDTO.maxNumberOfOccupants());
        household.setOwnerOccupied(householdDTO.ownerOccupied());

        return householdService.createHousehold(household);
    }

    // 2. Read All Households
    @GetMapping
    public List<Household> getAllHouseholds() {
        return householdService.getAllHouseholds();
    }

    // 3. Read Household by ID - no pets details
    @GetMapping("/{eircode}")
    public Household getHouseholdByEircodeNoPets(@PathVariable String eircode) {
        return householdService.getHouseholdByEircodeNoPets(eircode);
    }

    // 4. Read Household by ID - including pets details
    @GetMapping("/{eircode}/with-pets")
    public Household getHouseholdByEircodeWithPets(@PathVariable String eircode) {
        return householdService.getHouseholdByEircodeWithPets(eircode);
    }

    // 5. Update Household Details
    @PutMapping("/{eircode}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public Household updateHousehold(@PathVariable String eircode, @Valid @RequestBody HouseholdDTO householdDTO) {
        Household householdDetails = new Household();
        householdDetails.setNumberOfOccupants(householdDTO.numberOfOccupants());
        householdDetails.setMaxNumberOfOccupants(householdDTO.maxNumberOfOccupants());
        householdDetails.setOwnerOccupied(householdDTO.ownerOccupied());

        return householdService.updateHousehold(eircode, householdDetails);
    }

    // 6. Delete Household by ID
    @DeleteMapping("/{eircode}")
    @Secured("ROLE_ADMIN")
    public void deleteHousehold(@PathVariable String eircode) {
        householdService.deleteHousehold(eircode);
    }

    // 7. Delete Pets by Name
    @DeleteMapping("/pets/by-name/{name}")
    @Secured("ROLE_ADMIN")
    public void deletePetsByName(@PathVariable String name) {
        householdService.deletePetsByName(name);
    }

    // 8. Find Households with no pets
    @GetMapping("/no-pets")
    public List<Household> findHouseholdsWithNoPets() {
        return householdService.findHouseholdsWithNoPets();
    }

    // 9. Find Households that are owner-occupied
    @GetMapping("/owner-occupied")
    public List<Household> findOwnerOccupiedHouseholds() {
        return householdService.findOwnerOccupiedHouseholds();
    }

    // 10. Get Household Statistics
    @GetMapping("/statistics")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public HouseholdStatistics getHouseholdStatistics() {
        return householdService.getHouseholdStatistics();
    }
}

