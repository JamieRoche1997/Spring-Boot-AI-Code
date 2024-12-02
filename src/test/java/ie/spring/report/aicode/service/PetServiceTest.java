package ie.spring.report.aicode.service;

import ie.spring.report.aicode.model.Pet;
import ie.spring.report.aicode.model.Household;
import ie.spring.report.aicode.repository.PetRepository;
import ie.spring.report.aicode.repository.HouseholdRepository;
import ie.spring.report.aicode.exception.PetNotFoundException;
import ie.spring.report.aicode.dto.PetStatistics;
import ie.spring.report.aicode.dto.PetSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private HouseholdRepository householdRepository;

    @InjectMocks
    private PetServiceImpl petService;

    private Pet pet;
    private Household household;

    @BeforeEach
    public void setUp() {
        household = new Household();
        household.setEircode("D02XY45");

        pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setAnimalType("Dog");
        pet.setBreed("Golden Retriever");
        pet.setAge(3);
        pet.setHousehold(household);
    }

    // 1. Create Pet
    @Test
    public void testCreatePet() {
        when(petRepository.save(any(Pet.class))).thenReturn(pet);
        Pet createdPet = petService.createPet(pet);
        assertNotNull(createdPet);
        assertEquals("Buddy", createdPet.getName());
    }

    // 2. Read All Pets
    @Test
    public void testGetAllPets() {
        when(petRepository.findAll()).thenReturn(Arrays.asList(pet));
        List<Pet> pets = petService.getAllPets();
        assertFalse(pets.isEmpty());
        assertEquals(1, pets.size());
    }

    // 3. Read Pet by ID
    @Test
    public void testGetPetById() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        Pet foundPet = petService.getPetById(1L);
        assertNotNull(foundPet);
        assertEquals("Buddy", foundPet.getName());
    }

    @Test
    public void testGetPetById_NotFound() {
        when(petRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(PetNotFoundException.class, () -> petService.getPetById(2L));
    }

    // 4. Update Pet Details
    @Test
    public void testUpdatePet() {
        Pet updatedPetDetails = new Pet();
        updatedPetDetails.setName("Max");
        updatedPetDetails.setAnimalType("Dog");
        updatedPetDetails.setBreed("German Shepherd");
        updatedPetDetails.setAge(5);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenReturn(updatedPetDetails);

        Pet updatedPet = petService.updatePet(1L, updatedPetDetails);
        assertNotNull(updatedPet);
        assertEquals("Max", updatedPet.getName());
    }

    // 5. Delete Pet by ID
    @Test
    public void testDeletePetById() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        doNothing().when(petRepository).delete(pet);
        assertDoesNotThrow(() -> petService.deletePetById(1L));
    }

    // 6. Delete Pets by Name
    @Test
    public void testDeletePetsByName() {
        when(petRepository.findByNameIgnoreCase("Buddy")).thenReturn(Arrays.asList(pet));
        doNothing().when(petRepository).deleteAll(anyList());
        assertDoesNotThrow(() -> petService.deletePetsByName("Buddy"));
    }

    // 7. Find Pets by Animal Type
    @Test
    public void testFindPetsByAnimalType() {
        when(petRepository.findByAnimalTypeIgnoreCase("Dog")).thenReturn(Arrays.asList(pet));
        List<Pet> pets = petService.findPetsByAnimalType("Dog");
        assertFalse(pets.isEmpty());
        assertEquals(1, pets.size());
    }

    // 8. Find Pets by Breed
    @Test
    public void testFindPetsByBreed() {
        when(petRepository.findByBreedIgnoreCaseOrderByAgeAsc("Golden Retriever")).thenReturn(Arrays.asList(pet));
        List<Pet> pets = petService.findPetsByBreed("Golden Retriever");
        assertFalse(pets.isEmpty());
        assertEquals(1, pets.size());
    }

    // 9. Get Name and Breed Only
    @Test
    public void testGetPetNamesAndBreeds() {
        PetSummary petSummary = new PetSummary() {
            @Override
            public String getName() {
                return "Buddy";
            }

            @Override
            public String getAnimalType() {
                return "Dog";
            }

            @Override
            public String getBreed() {
                return "Golden Retriever";
            }
        };
        when(petRepository.findAllPetSummaries()).thenReturn(Arrays.asList(petSummary));
        List<PetSummary> summaries = petService.getPetNamesAndBreeds();
        assertFalse(summaries.isEmpty());
        assertEquals(1, summaries.size());
        assertEquals("Buddy", summaries.get(0).getName());
    }

    // 10. Get Pet Statistics
    @Test
    public void testGetPetStatistics() {
        when(petRepository.findAverageAge()).thenReturn(3.0);
        when(petRepository.findMaxAge()).thenReturn(5);
        when(petRepository.count()).thenReturn(1L);
        PetStatistics stats = petService.getPetStatistics();
        assertNotNull(stats);
        assertEquals(3.0, stats.getAverageAge());
        assertEquals(5, stats.getOldestAge());
        assertEquals(1L, stats.getTotalCount());
    }
}
