package ie.spring.report.aicode.controller;

import ie.spring.report.aicode.dto.HouseholdDTO;
import ie.spring.report.aicode.model.Household;
import ie.spring.report.aicode.service.HouseholdService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class HouseholdControllerUnitTests {
    @Mock
    private HouseholdService householdService;

    @InjectMocks
    private HouseholdController householdController;

    @Test
    void createHousehold_returnsCreatedHousehold() {
        HouseholdDTO householdDTO = new HouseholdDTO("EIR123", 4, 5, true);
        Household household = new Household();

        Mockito.when(householdService.createHousehold(Mockito.any(Household.class))).thenReturn(household);

        Household result = householdController.createHousehold(householdDTO);

        Assertions.assertNotNull(result);
        Mockito.verify(householdService).createHousehold(Mockito.any(Household.class));
    }

    @Test
    void getAllHouseholds_returnsListOfHouseholds() {
        List<Household> households = List.of(new Household(), new Household());

        Mockito.when(householdService.getAllHouseholds()).thenReturn(households);

        List<Household> result = householdController.getAllHouseholds();

        Assertions.assertEquals(2, result.size());
        Mockito.verify(householdService).getAllHouseholds();
    }
}
