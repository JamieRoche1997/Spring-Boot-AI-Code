package ie.spring.report.aicode.repository;

import ie.spring.report.aicode.dto.HouseholdDTO;
import ie.spring.report.aicode.model.Household;
import ie.spring.report.aicode.model.Pet;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
@Transactional
public class HouseholdControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private PetRepository petRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        petRepository.deleteAll();
        householdRepository.deleteAll();
    }

    // 1. Create Household - Success
    @Test
    public void testCreateHousehold_Success() throws Exception {
        HouseholdDTO householdDTO = new HouseholdDTO("D02XY45", 3, 5, true);

        mockMvc.perform(post("/households")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(householdDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eircode", is("D02XY45")))
                .andExpect(jsonPath("$.numberOfOccupants", is(3)));

        // Verify that the household is saved in the database
        Optional<Household> savedHousehold = householdRepository.findById("D02XY45");
        assertTrue(savedHousehold.isPresent());
        assertEquals(3, savedHousehold.get().getNumberOfOccupants());
    }

    // 1. Create Household - Invalid Data
    @Test
    public void testCreateHousehold_InvalidData() throws Exception {
        // Negative number of occupants
        HouseholdDTO householdDTO = new HouseholdDTO("D02XY45", -1, 5, true);

        mockMvc.perform(post("/households")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(householdDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.message.numberOfOccupants", is("Number of occupants must be non-negative")));
    }

    // 2. Read All Households
    @Test
    public void testGetAllHouseholds_Success() throws Exception {
        Household household1 = new Household();
        household1.setEircode("D02XY45");
        household1.setNumberOfOccupants(3);
        household1.setMaxNumberOfOccupants(5);
        household1.setOwnerOccupied(true);

        Household household2 = new Household();
        household2.setEircode("D02XY46");
        household2.setNumberOfOccupants(2);
        household2.setMaxNumberOfOccupants(4);
        household2.setOwnerOccupied(false);

        householdRepository.save(household1);
        householdRepository.save(household2);

        mockMvc.perform(get("/households"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eircode", is("D02XY45")))
                .andExpect(jsonPath("$[1].eircode", is("D02XY46")));
    }

    // 3. Read Household by Eircode - No Pets
    @Test
    public void testGetHouseholdByEircodeNoPets_Success() throws Exception {
        Household household = new Household();
        household.setEircode("D02XY45");
        household.setNumberOfOccupants(3);
        household.setMaxNumberOfOccupants(5);
        household.setOwnerOccupied(true);
        householdRepository.save(household);

        mockMvc.perform(get("/households/{eircode}", "D02XY45"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eircode", is("D02XY45")))
                .andExpect(jsonPath("$.numberOfOccupants", is(3)))
                .andExpect(jsonPath("$.pets").doesNotExist());
    }

    // 3. Read Household by Eircode - Not Found
    @Test
    public void testGetHouseholdByEircodeNoPets_NotFound() throws Exception {
        mockMvc.perform(get("/households/{eircode}", "INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Household Not Found")))
                .andExpect(jsonPath("$.message", is("Household not found with Eircode: INVALID")));
    }

    // 4. Read Household by Eircode - With Pets
    @Test
    public void testGetHouseholdByEircodeWithPets_Success() throws Exception {
        Household household = new Household();
        household.setEircode("D02XY45");
        household.setNumberOfOccupants(3);
        household.setMaxNumberOfOccupants(5);
        household.setOwnerOccupied(true);
        householdRepository.save(household);

        // Create and save a pet
        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setAnimalType("Dog");
        pet.setBreed("Golden Retriever");
        pet.setAge(3);
        pet.setHousehold(household);
        petRepository.save(pet);

        mockMvc.perform(get("/households/{eircode}/with-pets", "D02XY45"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eircode", is("D02XY45")))
                .andExpect(jsonPath("$.pets", hasSize(1)))
                .andExpect(jsonPath("$.pets[0].name", is("Buddy")));
    }

    // 4. Read Household by Eircode - With Pets - Not Found
    @Test
    public void testGetHouseholdByEircodeWithPets_NotFound() throws Exception {
        mockMvc.perform(get("/households/{eircode}/with-pets", "INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Household Not Found")))
                .andExpect(jsonPath("$.message", is("Household not found with Eircode: INVALID")));
    }

    // 5. Update Household Details - Success
    @Test
    public void testUpdateHousehold_Success() throws Exception {
        // Create and save a household
        Household household = new Household();
        household.setEircode("D02XY45");
        household.setNumberOfOccupants(3);
        household.setMaxNumberOfOccupants(5);
        household.setOwnerOccupied(true);
        householdRepository.save(household);

        // Prepare updated data
        HouseholdDTO householdDTO = new HouseholdDTO("D02XY45", 4, 6, false);

        mockMvc.perform(put("/households/{eircode}", "D02XY45")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(householdDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfOccupants", is(4)))
                .andExpect(jsonPath("$.maxNumberOfOccupants", is(6)))
                .andExpect(jsonPath("$.ownerOccupied", is(false)));

        // Verify that the household is updated in the database
        Optional<Household> updatedHousehold = householdRepository.findById("D02XY45");
        assertTrue(updatedHousehold.isPresent());
        assertEquals(4, updatedHousehold.get().getNumberOfOccupants());
        assertEquals(6, updatedHousehold.get().getMaxNumberOfOccupants());
        assertFalse(updatedHousehold.get().isOwnerOccupied());
    }

    // 5. Update Household Details - Not Found
    @Test
    public void testUpdateHousehold_NotFound() throws Exception {
        HouseholdDTO householdDTO = new HouseholdDTO(null, 4, 6, false);

        mockMvc.perform(put("/households/{eircode}", "INVALID")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(householdDTO)))
                .andExpect(status().isBadRequest());
    }

    // 6. Delete Household by Eircode - Success
    @Test
    public void testDeleteHousehold_Success() throws Exception {
        // Create and save a household
        Household household = new Household();
        household.setEircode("D02XY45");
        household.setNumberOfOccupants(3);
        household.setMaxNumberOfOccupants(5);
        household.setOwnerOccupied(true);
        householdRepository.save(household);

        mockMvc.perform(delete("/households/{eircode}", "D02XY45"))
                .andExpect(status().isOk());

        // Verify that the household is deleted from the database
        Optional<Household> deletedHousehold = householdRepository.findById("D02XY45");
        assertTrue(deletedHousehold.isEmpty());
    }

    // 6. Delete Household by Eircode - Not Found
    @Test
    public void testDeleteHousehold_NotFound() throws Exception {
        mockMvc.perform(delete("/households/{eircode}", "INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Household Not Found")))
                .andExpect(jsonPath("$.message", is("Household not found with Eircode: INVALID")));
    }

    // 7. Delete Pets by Name - Success
    @Test
    public void testDeletePetsByName_Success() throws Exception {
        // Create and save a household and pets
        Household household = new Household();
        household.setEircode("D02XY45");
        householdRepository.save(household);

        Pet pet1 = new Pet();
        pet1.setName("Buddy");
        pet1.setHousehold(household);
        petRepository.save(pet1);

        Pet pet2 = new Pet();
        pet2.setName("Buddy");
        pet2.setHousehold(household);
        petRepository.save(pet2);

        mockMvc.perform(delete("/households/pets/by-name/{name}", "Buddy"))
                .andExpect(status().isOk());

        // Verify that the pets are deleted
        List<Pet> pets = petRepository.findByNameIgnoreCase("Buddy");
        assertTrue(pets.isEmpty());
    }

    // 7. Delete Pets by Name - Not Found
    @Test
    public void testDeletePetsByName_NotFound() throws Exception {
        mockMvc.perform(delete("/households/pets/by-name/{name}", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Pet Not Found")))
                .andExpect(jsonPath("$.message", is("No pets found with name: Unknown")));
    }

    // 8. Find Households with No Pets - Success
    @Test
    public void testFindHouseholdsWithNoPets_Success() throws Exception {
        // Create and save households
        Household household1 = new Household();
        household1.setEircode("D02XY45");
        householdRepository.save(household1);

        Household household2 = new Household();
        household2.setEircode("D02XY46");
        householdRepository.save(household2);

        // Create and save a pet for household1
        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setHousehold(household1);
        petRepository.save(pet);

        mockMvc.perform(get("/households/no-pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].eircode", is("D02XY46")));
    }

    // 9. Find Owner-Occupied Households - Success
    @Test
    public void testFindOwnerOccupiedHouseholds_Success() throws Exception {
        // Create and save households
        Household household1 = new Household();
        household1.setEircode("D02XY45");
        household1.setOwnerOccupied(true);
        householdRepository.save(household1);

        Household household2 = new Household();
        household2.setEircode("D02XY46");
        household2.setOwnerOccupied(false);
        householdRepository.save(household2);

        mockMvc.perform(get("/households/owner-occupied"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].eircode", is("D02XY45")))
                .andExpect(jsonPath("$[0].ownerOccupied", is(true)));
    }

    // 10. Get Household Statistics - Success
    @Test
    public void testGetHouseholdStatistics_Success() throws Exception {
        // Create and save households
        Household emptyHousehold = new Household();
        emptyHousehold.setEircode("D02XY45");
        emptyHousehold.setNumberOfOccupants(0);
        emptyHousehold.setMaxNumberOfOccupants(5);
        householdRepository.save(emptyHousehold);

        Household fullHousehold = new Household();
        fullHousehold.setEircode("D02XY46");
        fullHousehold.setNumberOfOccupants(5);
        fullHousehold.setMaxNumberOfOccupants(5);
        householdRepository.save(fullHousehold);

        mockMvc.perform(get("/households/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfEmptyHouses", is(1)))
                .andExpect(jsonPath("$.numberOfFullHouses", is(1)));
    }
}
