package ie.spring.report.aicode.graphql;

import ie.spring.report.aicode.dto.HouseholdStatistics;
import ie.spring.report.aicode.model.Household;
import ie.spring.report.aicode.service.HouseholdService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class HouseholdControllerGQL {

    @Autowired
    private HouseholdService householdService;

    @SchemaMapping(typeName = "Query", value = "getAllHouseholds")
    public List<Household> getAllHouseholds() {
        return householdService.getAllHouseholds();
    }

    @SchemaMapping(typeName = "Query", value = "getHouseholdByEircode")
    public Household getHouseholdByEircode(@Argument String eircode) {
        return householdService.getHouseholdByEircodeWithPets(eircode);
    }

    @SchemaMapping(typeName = "Query", value = "findHouseholdsWithNoPets")
    public List<Household> findHouseholdsWithNoPets() {
        return householdService.findHouseholdsWithNoPets();
    }

    @SchemaMapping(typeName = "Query", value = "findOwnerOccupiedHouseholds")
    public List<Household> findOwnerOccupiedHouseholds() {
        return householdService.findOwnerOccupiedHouseholds();
    }

    @SchemaMapping(typeName = "Query", value = "getHouseholdStatistics")
    public HouseholdStatistics getHouseholdStatistics() {
        return householdService.getHouseholdStatistics();
    }

    @SchemaMapping(typeName = "Mutation", value = "createHousehold")
    @Secured({"ROLE_ADMIN"})
    public Household createHousehold(@Valid @Argument("household") HouseholdInput householdInput) {
        // Map HouseholdInput to Household entity
        Household household = new Household();
        household.setEircode(householdInput.getEircode());
        household.setNumberOfOccupants(householdInput.getNumberOfOccupants());
        household.setMaxNumberOfOccupants(householdInput.getMaxNumberOfOccupants());
        household.setOwnerOccupied(householdInput.isOwnerOccupied());

        // Use the existing createHousehold method in HouseholdService
        return householdService.createHousehold(household);
    }

    @SchemaMapping(typeName = "Mutation", value = "updateHousehold")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public Household updateHousehold(@Argument String eircode, @Valid @Argument("household") HouseholdInput householdInput) {
        // Map HouseholdInput to Household entity
        Household household = new Household();
        household.setEircode(eircode); // Use the eircode from the argument
        household.setNumberOfOccupants(householdInput.getNumberOfOccupants());
        household.setMaxNumberOfOccupants(householdInput.getMaxNumberOfOccupants());
        household.setOwnerOccupied(householdInput.isOwnerOccupied());

        // Use the existing updateHousehold method in HouseholdService
        return householdService.updateHousehold(eircode, household);
    }

    @SchemaMapping(typeName = "Mutation", value = "deleteHousehold")
    @Secured({"ROLE_ADMIN"})
    public Boolean deleteHousehold(@Argument String eircode) {
        householdService.deleteHousehold(eircode);
        return true;
    }

}

