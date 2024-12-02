package ie.spring.report.aicode.repository;

import ie.spring.report.aicode.controller.HouseholdController;
import ie.spring.report.aicode.dto.HouseholdDTO;
import ie.spring.report.aicode.dto.HouseholdStatistics;
import ie.spring.report.aicode.exception.GlobalExceptionHandler;
import ie.spring.report.aicode.exception.HouseholdNotFoundException;
import ie.spring.report.aicode.exception.PetNotFoundException;
import ie.spring.report.aicode.model.Household;
import ie.spring.report.aicode.model.Pet;
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
public class HouseholdControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HouseholdService householdService;

    @InjectMocks
    private HouseholdController householdController;

    private ObjectMapper objectMapper;

    private Household household;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(householdController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        household = new Household();
        household.setEircode("D02XY45");
        household.setNumberOfOccupants(3);
        household.setMaxNumberOfOccupants(5);
        household.setOwnerOccupied(true);
    }

    // 1. Create Household
    @Test
    public void testCreateHousehold_Success() throws Exception {
        HouseholdDTO householdDTO = new HouseholdDTO("D02XY45", 3, 5, true);

        when(householdService.createHousehold(any(Household.class))).thenReturn(household);

        mockMvc.perform(post("/households")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(householdDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eircode", is("D02XY45")))
                .andExpect(jsonPath("$.numberOfOccupants", is(3)));
    }

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
        List<Household> households = Arrays.asList(household);

        when(householdService.getAllHouseholds()).thenReturn(households);

        mockMvc.perform(get("/households"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].eircode", is("D02XY45")))
                .andExpect(jsonPath("$[0].numberOfOccupants", is(3)));
    }

    // 3. Read Household by Eircode - no pets details
    @Test
    public void testGetHouseholdByEircodeNoPets_Success() throws Exception {
        when(householdService.getHouseholdByEircodeNoPets("D02XY45")).thenReturn(household);

        mockMvc.perform(get("/households/D02XY45"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eircode", is("D02XY45")))
                .andExpect(jsonPath("$.numberOfOccupants", is(3)));
    }

    @Test
    public void testGetHouseholdByEircodeNoPets_NotFound() throws Exception {
        when(householdService.getHouseholdByEircodeNoPets("INVALID"))
                .thenThrow(new HouseholdNotFoundException("Household not found with Eircode: INVALID"));

        mockMvc.perform(get("/households/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Household Not Found")))
                .andExpect(jsonPath("$.message", is("Household not found with Eircode: INVALID")));
    }

    // 4. Read Household by Eircode - including pets details
    @Test
    public void testGetHouseholdByEircodeWithPets_Success() throws Exception {
        // Assume household has pets
        Household householdWithPets = new Household();
        householdWithPets.setEircode("D02XY45");
        householdWithPets.setNumberOfOccupants(3);
        householdWithPets.setMaxNumberOfOccupants(5);
        householdWithPets.setOwnerOccupied(true);

        // Create a pet and add to household
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setAnimalType("Dog");
        pet.setBreed("Golden Retriever");
        pet.setAge(3);
        pet.setHousehold(householdWithPets);

        householdWithPets.setPets(Arrays.asList(pet));

        when(householdService.getHouseholdByEircodeWithPets("D02XY45")).thenReturn(householdWithPets);

        mockMvc.perform(get("/households/D02XY45/with-pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eircode", is("D02XY45")))
                .andExpect(jsonPath("$.pets", hasSize(1)))
                .andExpect(jsonPath("$.pets[0].name", is("Buddy")));
    }

    @Test
    public void testGetHouseholdByEircodeWithPets_NotFound() throws Exception {
        when(householdService.getHouseholdByEircodeWithPets("INVALID"))
                .thenThrow(new HouseholdNotFoundException("Household not found with Eircode: INVALID"));

        mockMvc.perform(get("/households/INVALID/with-pets"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Household Not Found")))
                .andExpect(jsonPath("$.message", is("Household not found with Eircode: INVALID")));
    }

    // 5. Update Household Details
    @Test
    public void testUpdateHousehold_Success() throws Exception {
        HouseholdDTO householdDTO = new HouseholdDTO("D02XY45", 4, 5, false);

        Household updatedHousehold = new Household();
        updatedHousehold.setEircode("D02XY45");
        updatedHousehold.setNumberOfOccupants(4);
        updatedHousehold.setMaxNumberOfOccupants(5);
        updatedHousehold.setOwnerOccupied(false);

        when(householdService.updateHousehold(eq("D02XY45"), any(Household.class))).thenReturn(updatedHousehold);

        mockMvc.perform(put("/households/D02XY45")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(householdDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfOccupants", is(4)))
                .andExpect(jsonPath("$.ownerOccupied", is(false)));
    }

    @Test
    public void testUpdateHousehold_NotFound() throws Exception {
        HouseholdDTO householdDTO = new HouseholdDTO("INVALID", 4, 5, false);

        when(householdService.updateHousehold(eq("INVALID"), any(Household.class)))
                .thenThrow(new HouseholdNotFoundException("Household not found with Eircode: INVALID"));

        mockMvc.perform(put("/households/INVALID")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(householdDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Household Not Found")))
                .andExpect(jsonPath("$.message", is("Household not found with Eircode: INVALID")));
    }

    @Test
    public void testUpdateHousehold_InvalidData() throws Exception {
        // Number of occupants negative
        HouseholdDTO householdDTO = new HouseholdDTO("D02XY45", -1, 5, false);

        mockMvc.perform(put("/households/D02XY45")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(householdDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.message.numberOfOccupants", is("Number of occupants must be non-negative")));
    }

    // 6. Delete Household by Eircode
    @Test
    public void testDeleteHousehold_Success() throws Exception {
        doNothing().when(householdService).deleteHousehold("D02XY45");

        mockMvc.perform(delete("/households/D02XY45"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteHousehold_NotFound() throws Exception {
        doThrow(new HouseholdNotFoundException("Household not found with Eircode: INVALID"))
                .when(householdService).deleteHousehold("INVALID");

        mockMvc.perform(delete("/households/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Household Not Found")))
                .andExpect(jsonPath("$.message", is("Household not found with Eircode: INVALID")));
    }

    // 7. Delete Pets by Name
    @Test
    public void testDeletePetsByName_Success() throws Exception {
        doNothing().when(householdService).deletePetsByName("Buddy");

        mockMvc.perform(delete("/households/pets/by-name/Buddy"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeletePetsByName_NotFound() throws Exception {
        doThrow(new PetNotFoundException("No pets found with name: Unknown"))
                .when(householdService).deletePetsByName("Unknown");

        mockMvc.perform(delete("/households/pets/by-name/Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Pet Not Found")))
                .andExpect(jsonPath("$.message", is("No pets found with name: Unknown")));
    }

    // 8. Find Households with no pets
    @Test
    public void testFindHouseholdsWithNoPets_Success() throws Exception {
        List<Household> households = Arrays.asList(household);

        when(householdService.findHouseholdsWithNoPets()).thenReturn(households);

        mockMvc.perform(get("/households/no-pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].eircode", is("D02XY45")));
    }

    @Test
    public void testFindHouseholdsWithNoPets_NotFound() throws Exception {
        when(householdService.findHouseholdsWithNoPets())
                .thenThrow(new HouseholdNotFoundException("No households without pets found"));

        mockMvc.perform(get("/households/no-pets"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Household Not Found")))
                .andExpect(jsonPath("$.message", is("No households without pets found")));
    }

    // 9. Find Households that are owner-occupied
    @Test
    public void testFindOwnerOccupiedHouseholds_Success() throws Exception {
        List<Household> households = Arrays.asList(household);

        when(householdService.findOwnerOccupiedHouseholds()).thenReturn(households);

        mockMvc.perform(get("/households/owner-occupied"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].eircode", is("D02XY45")))
                .andExpect(jsonPath("$[0].ownerOccupied", is(true)));
    }

    @Test
    public void testFindOwnerOccupiedHouseholds_NotFound() throws Exception {
        when(householdService.findOwnerOccupiedHouseholds())
                .thenThrow(new HouseholdNotFoundException("No owner-occupied households found"));

        mockMvc.perform(get("/households/owner-occupied"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Household Not Found")))
                .andExpect(jsonPath("$.message", is("No owner-occupied households found")));
    }

    // 10. Get Household Statistics
    @Test
    public void testGetHouseholdStatistics_Success() throws Exception {
        HouseholdStatistics stats = new HouseholdStatistics(2L, 3L);

        when(householdService.getHouseholdStatistics()).thenReturn(stats);

        mockMvc.perform(get("/households/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfEmptyHouses", is(2)))
                .andExpect(jsonPath("$.numberOfFullHouses", is(3)));
    }
}
