package ie.spring.report.aicode.service;

import ie.spring.report.aicode.model.Pet;
import ie.spring.report.aicode.dto.PetSummary;
import ie.spring.report.aicode.dto.PetStatistics;
import java.util.List;

public interface PetService {

    // 1. Create Pet
    Pet createPet(Pet pet);

    // 2. Read All Pets
    List<Pet> getAllPets();

    // 3. Read Pet by ID
    Pet getPetById(Long id);

    // 4. Update Pet Details
    Pet updatePet(Long id, Pet pet);

    // 5. Delete Pet by ID
    void deletePetById(Long id);

    // 6. Delete Pets by Name
    void deletePetsByName(String name);

    // 7. Find Pets by Animal Type
    List<Pet> findPetsByAnimalType(String animalType);

    // 8. Find Pets by Breed
    List<Pet> findPetsByBreed(String breed);

    // 9. Get Name and Breed Only
    List<PetSummary> getPetNamesAndBreeds();

    // 10. Get Pet Statistics
    PetStatistics getPetStatistics();
}

