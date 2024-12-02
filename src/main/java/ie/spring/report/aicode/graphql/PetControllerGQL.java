package ie.spring.report.aicode.graphql;

import ie.spring.report.aicode.dto.PetStatistics;
import ie.spring.report.aicode.dto.PetSummary;
import ie.spring.report.aicode.model.Household;
import ie.spring.report.aicode.model.Pet;
import ie.spring.report.aicode.service.PetService;
import ie.spring.report.aicode.service.HouseholdService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PetControllerGQL {

    @Autowired
    private PetService petService;

    @Autowired
    private HouseholdService householdService;

    @SchemaMapping(typeName = "Query", value = "getAllPets")
    public List<Pet> getAllPets() {
        return petService.getAllPets();
    }

    @SchemaMapping(typeName = "Query", value = "getPetById")
    public Pet getPetById(@Argument Long id) {
        return petService.getPetById(id);
    }

    @SchemaMapping(typeName = "Query", value = "findPetsByAnimalType")
    public List<Pet> findPetsByAnimalType(@Argument String animalType) {
        return petService.findPetsByAnimalType(animalType);
    }

    @SchemaMapping(typeName = "Query", value = "findPetsByBreed")
    public List<Pet> findPetsByBreed(@Argument String breed) {
        return petService.findPetsByBreed(breed);
    }

    @SchemaMapping(typeName = "Query", value = "getPetNamesAndBreeds")
    public List<PetSummary> getPetNamesAndBreeds() {
        return petService.getPetNamesAndBreeds();
    }

    @SchemaMapping(typeName = "Query", value = "getPetStatistics")
    public PetStatistics getPetStatistics() {
        return petService.getPetStatistics();
    }

    @SchemaMapping(typeName = "Mutation", value = "createPet")
    @Secured({"ROLE_ADMIN"})
    public Pet createPet(@Valid @Argument("pet") PetInput petInput) {
        // Map PetInput to Pet entity
        Pet pet = new Pet();
        pet.setName(petInput.getName());
        pet.setAnimalType(petInput.getAnimalType());
        pet.setBreed(petInput.getBreed());
        pet.setAge(petInput.getAge());

        // Retrieve the household by eircode
        Household household = householdService.getHouseholdByEircodeNoPets(petInput.getEircode());
        pet.setHousehold(household);

        // Use the existing createPet method in PetService
        return petService.createPet(pet);
    }

    @SchemaMapping(typeName = "Mutation", value = "updatePet")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public Pet updatePet(@Argument Long id, @Valid @Argument("pet") PetInput petInput) {
        // Map PetInput to Pet entity
        Pet pet = new Pet();
        pet.setId(id); // Set the ID for updating
        pet.setName(petInput.getName());
        pet.setAnimalType(petInput.getAnimalType());
        pet.setBreed(petInput.getBreed());
        pet.setAge(petInput.getAge());

        // Retrieve the household by eircode
        Household household = householdService.getHouseholdByEircodeNoPets(petInput.getEircode());
        pet.setHousehold(household);

        // Use the existing updatePet method in PetService
        return petService.updatePet(id, pet);
    }

    @SchemaMapping(typeName = "Mutation", value = "deletePetById")
    @Secured({"ROLE_ADMIN"})
    public Boolean deletePetById(@Argument Long id) {
        petService.deletePetById(id);
        return true;
    }

    @SchemaMapping(typeName = "Mutation", value = "deletePetsByName")
    @Secured({"ROLE_ADMIN"})
    public Boolean deletePetsByName(@Argument String name) {
        petService.deletePetsByName(name);
        return true;
    }
}
