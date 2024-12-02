package ie.spring.report.aicode.service;

import ie.spring.report.aicode.model.Household;
import ie.spring.report.aicode.model.Pet;
import ie.spring.report.aicode.repository.HouseholdRepository;
import ie.spring.report.aicode.repository.PetRepository;
import ie.spring.report.aicode.exception.HouseholdNotFoundException;
import ie.spring.report.aicode.dto.HouseholdStatistics;
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
public class HouseholdServiceTest {

    @Mock
    private HouseholdRepository householdRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private HouseholdServiceImpl householdService;

    private Household household;

    @BeforeEach
    public void setUp() {
        household = new Household();
        household.setEircode("D02XY45");
        household.setNumberOfOccupants(3);
        household.setMaxNumberOfOccupants(5);
        household.setOwnerOccupied(true);
    }

    // 1. Create Household
    @Test
    public void testCreateHousehold() {
        when(householdRepository.save(any(Household.class))).thenReturn(household);
        Household createdHousehold = householdService.createHousehold(household);
        assertNotNull(createdHousehold);
        assertEquals("D02XY45", createdHousehold.getEircode());
    }

    // 2. Read All Households
    @Test
    public void testGetAllHouseholds() {
        when(householdRepository.findAll()).thenReturn(Arrays.asList(household));
        List<Household> households = householdService.getAllHouseholds();
        assertFalse(households.isEmpty());
        assertEquals(1, households.size());
    }

    // 3. Read Household by ID - no pets details
    @Test
    public void testGetHouseholdByEircodeNoPets() {
        when(householdRepository.findById("D02XY45")).thenReturn(Optional.of(household));
        Household foundHousehold = householdService.getHouseholdByEircodeNoPets("D02XY45");
        assertNotNull(foundHousehold);
        assertEquals("D02XY45", foundHousehold.getEircode());
    }

    // 4. Read Household by ID - including pets details
    @Test
    public void testGetHouseholdByEircodeWithPets() {
        when(householdRepository.findHouseholdWithPetsByEircode("D02XY45")).thenReturn(household);
        Household foundHousehold = householdService.getHouseholdByEircodeWithPets("D02XY45");
        assertNotNull(foundHousehold);
        assertEquals("D02XY45", foundHousehold.getEircode());
    }

    // 5. Update Household Details
    @Test
    public void testUpdateHousehold() {
        Household updatedDetails = new Household();
        updatedDetails.setNumberOfOccupants(4);
        updatedDetails.setMaxNumberOfOccupants(5);
        updatedDetails.setOwnerOccupied(false);

        when(householdRepository.findById("D02XY45")).thenReturn(Optional.of(household));
        when(householdRepository.save(any(Household.class))).thenReturn(updatedDetails);

        Household updatedHousehold = householdService.updateHousehold("D02XY45", updatedDetails);
        assertNotNull(updatedHousehold);
        assertEquals(4, updatedHousehold.getNumberOfOccupants());
        assertFalse(updatedHousehold.isOwnerOccupied());
    }

    // 6. Delete Household by ID
    @Test
    public void testDeleteHousehold() {
        when(householdRepository.findById("D02XY45")).thenReturn(Optional.of(household));
        doNothing().when(householdRepository).delete(household);
        assertDoesNotThrow(() -> householdService.deleteHousehold("D02XY45"));
    }

    // 8. Find Households with no pets
    @Test
    public void testFindHouseholdsWithNoPets() {
        when(householdRepository.findHouseholdsWithNoPets()).thenReturn(Arrays.asList(household));
        List<Household> households = householdService.findHouseholdsWithNoPets();
        assertFalse(households.isEmpty());
        assertEquals(1, households.size());
    }

    // 9. Find Households that are owner-occupied
    @Test
    public void testFindOwnerOccupiedHouseholds() {
        when(householdRepository.findByOwnerOccupied(true)).thenReturn(Arrays.asList(household));
        List<Household> households = householdService.findOwnerOccupiedHouseholds();
        assertFalse(households.isEmpty());
        assertEquals(1, households.size());
    }

    // 10. Get Household Statistics
    @Test
    public void testGetHouseholdStatistics() {
        when(householdRepository.countEmptyHouses()).thenReturn(2L);
        when(householdRepository.countFullHouses()).thenReturn(3L);
        HouseholdStatistics stats = householdService.getHouseholdStatistics();
        assertNotNull(stats);
        assertEquals(2L, stats.getNumberOfEmptyHouses());
        assertEquals(3L, stats.getNumberOfFullHouses());
    }
}
