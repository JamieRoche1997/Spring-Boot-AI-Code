package ie.spring.report.aicode.service;

import ie.spring.report.aicode.model.Pet;
import ie.spring.report.aicode.repository.PetRepository;
import ie.spring.report.aicode.exception.PetNotFoundException;
import ie.spring.report.aicode.exception.InvalidPetDataException;
import ie.spring.report.aicode.dto.PetSummary;
import ie.spring.report.aicode.dto.PetStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PetServiceImpl implements PetService {

    @Autowired
    private PetRepository petRepository;

    // 1. Create Pet
    @Override
    public Pet createPet(Pet pet) {
        if (pet == null) {
            throw new InvalidPetDataException("Pet data cannot be null");
        }
        // Additional validations can be added here
        return petRepository.save(pet);
    }

    // 2. Read All Pets
    @Override
    public List<Pet> getAllPets() {
        List<Pet> pets = petRepository.findAll();
        if (pets.isEmpty()) {
            throw new PetNotFoundException("No pets found");
        }
        return pets;
    }

    // 3. Read Pet by ID
    @Override
    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet not found with ID: " + id));
    }

    // 4. Update Pet Details
    @Override
    public Pet updatePet(Long id, Pet petDetails) {
        if (petDetails == null) {
            throw new InvalidPetDataException("Pet details cannot be null");
        }
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet not found with ID: " + id));

        // Update fields
        pet.setName(petDetails.getName());
        pet.setAnimalType(petDetails.getAnimalType());
        pet.setBreed(petDetails.getBreed());
        pet.setAge(petDetails.getAge());

        return petRepository.save(pet);
    }

    // 5. Delete Pet by ID
    @Override
    public void deletePetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet not found with ID: " + id));
        petRepository.delete(pet);
    }

    // 6. Delete Pets by Name
    @Override
    public void deletePetsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidPetDataException("Name cannot be null or empty");
        }
        List<Pet> pets = petRepository.findByNameIgnoreCase(name);
        if (pets.isEmpty()) {
            throw new PetNotFoundException("No pets found with name: " + name);
        }
        petRepository.deleteAll(pets);
    }

    // 7. Find Pets by Animal Type
    @Override
    public List<Pet> findPetsByAnimalType(String animalType) {
        if (animalType == null || animalType.trim().isEmpty()) {
            throw new InvalidPetDataException("Animal type cannot be null or empty");
        }
        List<Pet> pets = petRepository.findByAnimalTypeIgnoreCase(animalType);
        if (pets.isEmpty()) {
            throw new PetNotFoundException("No pets found with animal type: " + animalType);
        }
        return pets;
    }

    // 8. Find Pets by Breed
    @Override
    public List<Pet> findPetsByBreed(String breed) {
        if (breed == null || breed.trim().isEmpty()) {
            throw new InvalidPetDataException("Breed cannot be null or empty");
        }
        List<Pet> pets = petRepository.findByBreedIgnoreCaseOrderByAgeAsc(breed);
        if (pets.isEmpty()) {
            throw new PetNotFoundException("No pets found with breed: " + breed);
        }
        return pets;
    }

    // 9. Get Name and Breed Only
    @Override
    public List<PetSummary> getPetNamesAndBreeds() {
        List<PetSummary> petSummaries = petRepository.findAllPetSummaries();
        if (petSummaries.isEmpty()) {
            throw new PetNotFoundException("No pets found");
        }
        return petSummaries;
    }

    // 10. Get Pet Statistics
    @Override
    public PetStatistics getPetStatistics() {
        Double averageAge = petRepository.findAverageAge();
        Integer oldestAge = petRepository.findMaxAge();
        Long totalCount = petRepository.count();

        if (totalCount == 0) {
            throw new PetNotFoundException("No pets found");
        }

        return new PetStatistics(averageAge, oldestAge, totalCount);
    }
}
