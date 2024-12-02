package ie.spring.report.aicode.controller;

import ie.spring.report.aicode.dto.PetDTO;
import ie.spring.report.aicode.dto.PetSummary;
import ie.spring.report.aicode.dto.PetStatistics;
import ie.spring.report.aicode.model.Pet;
import ie.spring.report.aicode.model.Household;
import ie.spring.report.aicode.service.PetService;
import ie.spring.report.aicode.service.HouseholdService;
import ie.spring.report.aicode.exception.HouseholdNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private HouseholdService householdService;

    // 1. Create Pet
    @PostMapping
    @Secured("ROLE_ADMIN")
    public Pet createPet(@Valid @RequestBody PetDTO petDTO) {
        // Fetch the household by eircode
        Household household = householdService.getHouseholdByEircodeNoPets(petDTO.householdEircode());
        if (household == null) {
            throw new HouseholdNotFoundException("Household not found with Eircode: " + petDTO.householdEircode());
        }

        // Create a new Pet entity
        Pet pet = new Pet();
        pet.setName(petDTO.name());
        pet.setAnimalType(petDTO.animalType());
        pet.setBreed(petDTO.breed());
        pet.setAge(petDTO.age());
        pet.setHousehold(household);

        return petService.createPet(pet);
    }

    // 2. Read All Pets
    @GetMapping
    public List<Pet> getAllPets() {
        return petService.getAllPets();
    }

    // 3. Read Pet by ID
    @GetMapping("/{id}")
    public Pet getPetById(@PathVariable Long id) {
        return petService.getPetById(id);
    }

    // 4. Update Pet Details
    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public Pet updatePet(@PathVariable Long id, @Valid @RequestBody PetDTO petDTO) {
        // Fetch the household by eircode
        Household household = householdService.getHouseholdByEircodeNoPets(petDTO.householdEircode());
        if (household == null) {
            throw new HouseholdNotFoundException("Household not found with Eircode: " + petDTO.householdEircode());
        }

        // Create a Pet entity with the updated details
        Pet petDetails = new Pet();
        petDetails.setName(petDTO.name());
        petDetails.setAnimalType(petDTO.animalType());
        petDetails.setBreed(petDTO.breed());
        petDetails.setAge(petDTO.age());
        petDetails.setHousehold(household);

        return petService.updatePet(id, petDetails);
    }

    // 5. Delete Pet by ID
    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public void deletePetById(@PathVariable Long id) {
        petService.deletePetById(id);
    }

    // 6. Delete Pets by Name
    @DeleteMapping("/by-name/{name}")
    @Secured("ROLE_ADMIN")
    public void deletePetsByName(@PathVariable String name) {
        petService.deletePetsByName(name);
    }

    // 7. Find Pets by Animal Type
    @GetMapping("/by-animal-type/{animalType}")
    public List<Pet> findPetsByAnimalType(@PathVariable String animalType) {
        return petService.findPetsByAnimalType(animalType);
    }

    // 8. Find Pets by Breed
    @GetMapping("/by-breed/{breed}")
    public List<Pet> findPetsByBreed(@PathVariable String breed) {
        return petService.findPetsByBreed(breed);
    }

    // 9. Get Name and Breed Only
    @GetMapping("/summaries")
    public List<PetSummary> getPetNamesAndBreeds() {
        return petService.getPetNamesAndBreeds();
    }

    // 10. Get Pet Statistics
    @GetMapping("/statistics")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public PetStatistics getPetStatistics() {
        return petService.getPetStatistics();
    }
}

