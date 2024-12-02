package ie.spring.report.aicode.repository;

import ie.spring.report.aicode.controller.PetController;
import ie.spring.report.aicode.dto.PetDTO;
import ie.spring.report.aicode.dto.PetStatistics;
import ie.spring.report.aicode.dto.PetSummary;
import ie.spring.report.aicode.exception.GlobalExceptionHandler;
import ie.spring.report.aicode.exception.HouseholdNotFoundException;
import ie.spring.report.aicode.exception.PetNotFoundException;
import ie.spring.report.aicode.model.Household;
import ie.spring.report.aicode.model.Pet;
import ie.spring.report.aicode.service.PetService;
import ie.spring.report.aicode.service.HouseholdService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PetControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PetService petService;

    @Mock
    private HouseholdService householdService;

    @InjectMocks
    private PetController petController;

    private ObjectMapper objectMapper;

    private Household household;
    private Pet pet;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(petController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

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

    // Test cases for each endpoint

    // 1. Create Pet
    @Test
    public void testCreatePet_Success() throws Exception {
        PetDTO petDTO = new PetDTO("Buddy", "Dog", "Golden Retriever", 3, "D02XY45");

        when(householdService.getHouseholdByEircodeNoPets("D02XY45")).thenReturn(household);
        when(petService.createPet(any(Pet.class))).thenReturn(pet);

        mockMvc.perform(post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Buddy")))
                .andExpect(jsonPath("$.animalType", is("Dog")));
    }

    @Test
    public void testCreatePet_HouseholdNotFound() throws Exception {
        PetDTO petDTO = new PetDTO("Buddy", "Dog", "Golden Retriever", 3, "INVALID");

        when(householdService.getHouseholdByEircodeNoPets("INVALID"))
                .thenThrow(new HouseholdNotFoundException("Household not found with Eircode: INVALID"));

        mockMvc.perform(post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Household Not Found")))
                .andExpect(jsonPath("$.message", is("Household not found with Eircode: INVALID")));
    }

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
        List<Pet> pets = Arrays.asList(pet);

        when(petService.getAllPets()).thenReturn(pets);

        mockMvc.perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Buddy")))
                .andExpect(jsonPath("$[0].animalType", is("Dog")));
    }

    // 3. Read Pet by ID
    @Test
    public void testGetPetById_Success() throws Exception {
        when(petService.getPetById(1L)).thenReturn(pet);

        mockMvc.perform(get("/pets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Buddy")))
                .andExpect(jsonPath("$.animalType", is("Dog")));
    }

    @Test
    public void testGetPetById_NotFound() throws Exception {
        when(petService.getPetById(2L))
                .thenThrow(new PetNotFoundException("Pet not found with ID: 2"));

        mockMvc.perform(get("/pets/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Pet Not Found")))
                .andExpect(jsonPath("$.message", is("Pet not found with ID: 2")));
    }

    // 4. Update Pet Details
    @Test
    public void testUpdatePet_Success() throws Exception {
        PetDTO petDTO = new PetDTO("Max", "Dog", "German Shepherd", 5, "D02XY45");

        Pet updatedPet = new Pet();
        updatedPet.setId(1L);
        updatedPet.setName("Max");
        updatedPet.setAnimalType("Dog");
        updatedPet.setBreed("German Shepherd");
        updatedPet.setAge(5);
        updatedPet.setHousehold(household);

        when(householdService.getHouseholdByEircodeNoPets("D02XY45")).thenReturn(household);
        when(petService.updatePet(eq(1L), any(Pet.class))).thenReturn(updatedPet);

        mockMvc.perform(put("/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Max")))
                .andExpect(jsonPath("$.breed", is("German Shepherd")));
    }

    @Test
    public void testUpdatePet_PetNotFound() throws Exception {
        PetDTO petDTO = new PetDTO("Max", "Dog", "German Shepherd", 5, "D02XY45");

        when(householdService.getHouseholdByEircodeNoPets("D02XY45")).thenReturn(household);
        when(petService.updatePet(eq(2L), any(Pet.class)))
                .thenThrow(new PetNotFoundException("Pet not found with ID: 2"));

        mockMvc.perform(put("/pets/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Pet Not Found")))
                .andExpect(jsonPath("$.message", is("Pet not found with ID: 2")));
    }

    @Test
    public void testUpdatePet_HouseholdNotFound() throws Exception {
        PetDTO petDTO = new PetDTO("Max", "Dog", "German Shepherd", 5, "INVALID");

        when(householdService.getHouseholdByEircodeNoPets("INVALID"))
                .thenThrow(new HouseholdNotFoundException("Household not found with Eircode: INVALID"));

        mockMvc.perform(put("/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Household Not Found")))
                .andExpect(jsonPath("$.message", is("Household not found with Eircode: INVALID")));
    }

    @Test
    public void testUpdatePet_InvalidData() throws Exception {
        PetDTO petDTO = new PetDTO("", "Dog", "Golden Retriever", -1, "D02XY45");

        mockMvc.perform(put("/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.message.name", is("Name is mandatory")))
                .andExpect(jsonPath("$.message.age", is("Age must be non-negative")));
    }

    // 5. Delete Pet by ID
    @Test
    public void testDeletePetById_Success() throws Exception {
        doNothing().when(petService).deletePetById(1L);

        mockMvc.perform(delete("/pets/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeletePetById_NotFound() throws Exception {
        doThrow(new PetNotFoundException("Pet not found with ID: 2"))
                .when(petService).deletePetById(2L);

        mockMvc.perform(delete("/pets/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Pet Not Found")))
                .andExpect(jsonPath("$.message", is("Pet not found with ID: 2")));
    }

    // 6. Delete Pets by Name
    @Test
    public void testDeletePetsByName_Success() throws Exception {
        doNothing().when(petService).deletePetsByName("Buddy");

        mockMvc.perform(delete("/pets/by-name/Buddy"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeletePetsByName_PetNotFound() throws Exception {
        doThrow(new PetNotFoundException("No pets found with name: Unknown"))
                .when(petService).deletePetsByName("Unknown");

        mockMvc.perform(delete("/pets/by-name/Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Pet Not Found")))
                .andExpect(jsonPath("$.message", is("No pets found with name: Unknown")));
    }

    // 7. Find Pets by Animal Type
    @Test
    public void testFindPetsByAnimalType_Success() throws Exception {
        List<Pet> pets = Arrays.asList(pet);

        when(petService.findPetsByAnimalType("Dog")).thenReturn(pets);

        mockMvc.perform(get("/pets/by-animal-type/Dog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].animalType", is("Dog")));
    }

    @Test
    public void testFindPetsByAnimalType_NotFound() throws Exception {
        when(petService.findPetsByAnimalType("Unknown")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/pets/by-animal-type/Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // 8. Find Pets by Breed
    @Test
    public void testFindPetsByBreed_Success() throws Exception {
        List<Pet> pets = Arrays.asList(pet);

        when(petService.findPetsByBreed("Golden Retriever")).thenReturn(pets);

        mockMvc.perform(get("/pets/by-breed/Golden Retriever"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].breed", is("Golden Retriever")));
    }

    @Test
    public void testFindPetsByBreed_NotFound() throws Exception {
        when(petService.findPetsByBreed("Unknown Breed")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/pets/by-breed/Unknown Breed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // 9. Get Name and Breed Only
    @Test
    public void testGetPetNamesAndBreeds_Success() throws Exception {
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

        List<PetSummary> summaries = Arrays.asList(petSummary);

        when(petService.getPetNamesAndBreeds()).thenReturn(summaries);

        mockMvc.perform(get("/pets/summaries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Buddy")))
                .andExpect(jsonPath("$[0].breed", is("Golden Retriever")));
    }

    // 10. Get Pet Statistics
    @Test
    public void testGetPetStatistics_Success() throws Exception {
        PetStatistics stats = new PetStatistics();
        stats.setTotalCount(10L);
        stats.setAverageAge(3.5);
        stats.setOldestAge(7);

        when(petService.getPetStatistics()).thenReturn(stats);

        mockMvc.perform(get("/pets/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount", is(10)))
                .andExpect(jsonPath("$.averageAge", is(3.5)))
                .andExpect(jsonPath("$.oldestAge", is(7)));
    }
}
