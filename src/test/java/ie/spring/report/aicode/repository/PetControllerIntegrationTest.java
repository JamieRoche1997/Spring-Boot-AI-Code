package ie.spring.report.aicode.repository;

import ie.spring.report.aicode.dto.PetDTO;
import ie.spring.report.aicode.model.Household;
import ie.spring.report.aicode.model.Pet;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private HouseholdRepository householdRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Household household;

    @BeforeEach
    public void setUp() {
        petRepository.deleteAll();
        householdRepository.deleteAll();

        household = new Household();
        household.setEircode("D02XY45");
        household.setNumberOfOccupants(3);
        household.setMaxNumberOfOccupants(5);
        household.setOwnerOccupied(true);
        householdRepository.save(household);
    }

    // 1. Create Pet - Success
    @Test
    public void testCreatePet_Success() throws Exception {
        PetDTO petDTO = new PetDTO("Buddy", "Dog", "Golden Retriever", 3, "D02XY45");

        mockMvc.perform(post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Buddy")))
                .andExpect(jsonPath("$.animalType", is("Dog")));

        // Verify that the pet is saved in the database
        Optional<Pet> savedPet = petRepository.findAll().stream().findFirst();
        assertTrue(savedPet.isPresent());
        assertEquals("Buddy", savedPet.get().getName());
    }

    // 1. Create Pet - Household Not Found
    @Test
    public void testCreatePet_HouseholdNotFound() throws Exception {
        PetDTO petDTO = new PetDTO("Buddy", "Dog", "Golden Retriever", 3, "INVALID");

        mockMvc.perform(post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Household Not Found")))
                .andExpect(jsonPath("$.message", is("Household not found with Eircode: INVALID")));
    }

    // 1. Create Pet - Invalid Data
    @Test
    public void testCreatePet_InvalidData() throws Exception {
        // Age is negative, which violates @Min(0)
        PetDTO petDTO = new PetDTO("", "Dog", "Golden Retriever", -1, "D02XY45");

        mockMvc.perform(post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.message.name", is("Name is mandatory")))
                .andExpect(jsonPath("$.message.age", is("Age must be non-negative")));
    }

    // 2. Read All Pets
    @Test
    public void testGetAllPets_Success() throws Exception {
        // Create and save some pets
        Pet pet1 = new Pet();
        pet1.setName("Buddy");
        pet1.setAnimalType("Dog");
        pet1.setBreed("Golden Retriever");
        pet1.setAge(3);
        pet1.setHousehold(household);

        Pet pet2 = new Pet();
        pet2.setName("Kitty");
        pet2.setAnimalType("Cat");
        pet2.setBreed("Siamese");
        pet2.setAge(2);
        pet2.setHousehold(household);

        petRepository.save(pet1);
        petRepository.save(pet2);

        mockMvc.perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Buddy")))
                .andExpect(jsonPath("$[1].name", is("Kitty")));
    }

    // 3. Read Pet by ID - Success
    @Test
    public void testGetPetById_Success() throws Exception {
        // Create and save a pet
        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setAnimalType("Dog");
        pet.setBreed("Golden Retriever");
        pet.setAge(3);
        pet.setHousehold(household);
        pet = petRepository.save(pet);

        mockMvc.perform(get("/pets/{id}", pet.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Buddy")))
                .andExpect(jsonPath("$.animalType", is("Dog")))
                .andExpect(jsonPath("$.id", is(pet.getId().intValue())));
    }

    // 3. Read Pet by ID - Not Found
    @Test
    public void testGetPetById_NotFound() throws Exception {
        mockMvc.perform(get("/pets/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Pet Not Found")))
                .andExpect(jsonPath("$.message", is("Pet not found with ID: 999")));
    }

    // 4. Update Pet Details - Success
    @Test
    public void testUpdatePet_Success() throws Exception {
        // Create and save a pet
        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setAnimalType("Dog");
        pet.setBreed("Golden Retriever");
        pet.setAge(3);
        pet.setHousehold(household);
        pet = petRepository.save(pet);

        // Prepare updated data
        PetDTO petDTO = new PetDTO("Max", "Dog", "Labrador", 4, "D02XY45");

        mockMvc.perform(put("/pets/{id}", pet.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Max")))
                .andExpect(jsonPath("$.breed", is("Labrador")));

        // Verify that the pet is updated in the database
        Optional<Pet> updatedPet = petRepository.findById(pet.getId());
        assertTrue(updatedPet.isPresent());
        assertEquals("Max", updatedPet.get().getName());
        assertEquals("Labrador", updatedPet.get().getBreed());
    }

    // 4. Update Pet Details - Not Found
    @Test
    public void testUpdatePet_NotFound() throws Exception {
        // Prepare updated data
        PetDTO petDTO = new PetDTO("Max", "Dog", "Labrador", 4, "D02XY45");

        mockMvc.perform(put("/pets/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Pet Not Found")))
                .andExpect(jsonPath("$.message", is("Pet not found with ID: 999")));
    }

    // 5. Delete Pet by ID - Success
    @Test
    public void testDeletePetById_Success() throws Exception {
        // Create and save a pet
        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setAnimalType("Dog");
        pet.setBreed("Golden Retriever");
        pet.setAge(3);
        pet.setHousehold(household);
        pet = petRepository.save(pet);

        mockMvc.perform(delete("/pets/{id}", pet.getId()))
                .andExpect(status().isOk());

        // Verify that the pet is deleted from the database
        Optional<Pet> deletedPet = petRepository.findById(pet.getId());
        assertTrue(deletedPet.isEmpty());
    }

    // 5. Delete Pet by ID - Not Found
    @Test
    public void testDeletePetById_NotFound() throws Exception {
        mockMvc.perform(delete("/pets/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Pet Not Found")))
                .andExpect(jsonPath("$.message", is("Pet not found with ID: 999")));
    }

    // 6. Delete Pets by Name - Success
    @Test
    public void testDeletePetsByName_Success() throws Exception {
        // Create and save pets with name "Buddy"
        Pet pet1 = new Pet();
        pet1.setName("Buddy");
        pet1.setAnimalType("Dog");
        pet1.setBreed("Golden Retriever");
        pet1.setAge(3);
        pet1.setHousehold(household);

        Pet pet2 = new Pet();
        pet2.setName("Buddy");
        pet2.setAnimalType("Cat");
        pet2.setBreed("Tabby");
        pet2.setAge(2);
        pet2.setHousehold(household);

        petRepository.save(pet1);
        petRepository.save(pet2);

        mockMvc.perform(delete("/pets/by-name/{name}", "Buddy"))
                .andExpect(status().isOk());

        // Verify that pets with name "Buddy" are deleted
        List<Pet> pets = petRepository.findByNameIgnoreCase("Buddy");
        assertTrue(pets.isEmpty());
    }

    // 6. Delete Pets by Name - Not Found
    @Test
    public void testDeletePetsByName_NotFound() throws Exception {
        mockMvc.perform(delete("/pets/by-name/{name}", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Pet Not Found")))
                .andExpect(jsonPath("$.message", is("No pets found with name: Unknown")));
    }

    // 7. Find Pets by Animal Type - Success
    @Test
    public void testFindPetsByAnimalType_Success() throws Exception {
        // Create and save pets
        Pet pet1 = new Pet();
        pet1.setName("Buddy");
        pet1.setAnimalType("Dog");
        pet1.setBreed("Golden Retriever");
        pet1.setAge(3);
        pet1.setHousehold(household);

        Pet pet2 = new Pet();
        pet2.setName("Kitty");
        pet2.setAnimalType("Cat");
        pet2.setBreed("Siamese");
        pet2.setAge(2);
        pet2.setHousehold(household);

        petRepository.save(pet1);
        petRepository.save(pet2);

        mockMvc.perform(get("/pets/by-animal-type/{animalType}", "Dog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Buddy")))
                .andExpect(jsonPath("$[0].animalType", is("Dog")));
    }

    // 7. Find Pets by Animal Type - Not Found
    @Test
    public void testFindPetsByAnimalType_NotFound() throws Exception {
        mockMvc.perform(get("/pets/by-animal-type/{animalType}", "Bird"))
                .andExpect(status().isNotFound());
    }

    // 8. Find Pets by Breed - Success
    @Test
    public void testFindPetsByBreed_Success() throws Exception {
        // Create and save pets
        Pet pet1 = new Pet();
        pet1.setName("Buddy");
        pet1.setAnimalType("Dog");
        pet1.setBreed("Golden Retriever");
        pet1.setAge(3);
        pet1.setHousehold(household);

        Pet pet2 = new Pet();
        pet2.setName("Max");
        pet2.setAnimalType("Dog");
        pet2.setBreed("Golden Retriever");
        pet2.setAge(4);
        pet2.setHousehold(household);

        petRepository.save(pet1);
        petRepository.save(pet2);

        mockMvc.perform(get("/pets/by-breed/{breed}", "Golden Retriever"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].breed", is("Golden Retriever")))
                .andExpect(jsonPath("$[1].breed", is("Golden Retriever")));
    }

    // 8. Find Pets by Breed - Not Found
    @Test
    public void testFindPetsByBreed_NotFound() throws Exception {
        mockMvc.perform(get("/pets/by-breed/{breed}", "Unknown Breed"))
                .andExpect(status().isNotFound());
    }

    // 9. Get Name and Breed Only
    @Test
    public void testGetPetNamesAndBreeds_Success() throws Exception {
        // Create and save pets
        Pet pet1 = new Pet();
        pet1.setName("Buddy");
        pet1.setAnimalType("Dog");
        pet1.setBreed("Golden Retriever");
        pet1.setAge(3);
        pet1.setHousehold(household);

        Pet pet2 = new Pet();
        pet2.setName("Kitty");
        pet2.setAnimalType("Cat");
        pet2.setBreed("Siamese");
        pet2.setAge(2);
        pet2.setHousehold(household);

        petRepository.save(pet1);
        petRepository.save(pet2);

        mockMvc.perform(get("/pets/summaries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Buddy")))
                .andExpect(jsonPath("$[0].animalType", is("Dog")))
                .andExpect(jsonPath("$[0].breed", is("Golden Retriever")))
                .andExpect(jsonPath("$[1].name", is("Kitty")))
                .andExpect(jsonPath("$[1].animalType", is("Cat")))
                .andExpect(jsonPath("$[1].breed", is("Siamese")));
    }

    // 10. Get Pet Statistics
    @Test
    public void testGetPetStatistics_Success() throws Exception {
        // Create and save pets
        Pet pet1 = new Pet();
        pet1.setName("Buddy");
        pet1.setAnimalType("Dog");
        pet1.setBreed("Golden Retriever");
        pet1.setAge(3);
        pet1.setHousehold(household);

        Pet pet2 = new Pet();
        pet2.setName("Max");
        pet2.setAnimalType("Dog");
        pet2.setBreed("Labrador");
        pet2.setAge(5);
        pet2.setHousehold(household);

        Pet pet3 = new Pet();
        pet3.setName("Kitty");
        pet3.setAnimalType("Cat");
        pet3.setBreed("Siamese");
        pet3.setAge(2);
        pet3.setHousehold(household);

        petRepository.save(pet1);
        petRepository.save(pet2);
        petRepository.save(pet3);

        mockMvc.perform(get("/pets/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount", is(3)))
                .andExpect(jsonPath("$.averageAge", is(closeTo(3.33, 0.01))))
                .andExpect(jsonPath("$.oldestAge", is(5)));
    }
}
