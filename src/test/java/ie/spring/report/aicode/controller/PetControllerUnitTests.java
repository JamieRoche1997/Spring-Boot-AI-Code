package ie.spring.report.aicode.controller;

import ie.spring.report.aicode.dto.PetDTO;
import ie.spring.report.aicode.model.Household;
import ie.spring.report.aicode.model.Pet;
import ie.spring.report.aicode.service.HouseholdService;
import ie.spring.report.aicode.service.PetService;
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
public class PetControllerUnitTests {

    @Mock
    private PetService petService;

    @Mock
    private HouseholdService householdService;

    @InjectMocks
    private PetController petController;

    @Test
    void createPet_whenHouseholdExists_returnsCreatedPet() {
        PetDTO petDTO = new PetDTO("Doggo", "Dog", "Labrador", 3, "EIR123");
        Household household = new Household();
        Pet pet = new Pet();

        Mockito.when(householdService.getHouseholdByEircodeNoPets("EIR123")).thenReturn(household);
        Mockito.when(petService.createPet(Mockito.any(Pet.class))).thenReturn(pet);

        Pet createdPet = petController.createPet(petDTO);

        Assertions.assertNotNull(createdPet);
        Mockito.verify(householdService).getHouseholdByEircodeNoPets("EIR123");
        Mockito.verify(petService).createPet(Mockito.any(Pet.class));
    }

    @Test
    void getAllPets_returnsListOfPets() {
        List<Pet> pets = List.of(new Pet(), new Pet());

        Mockito.when(petService.getAllPets()).thenReturn(pets);

        List<Pet> result = petController.getAllPets();

        Assertions.assertEquals(2, result.size());
        Mockito.verify(petService).getAllPets();
    }

    @Test
    void getPetById_validId_returnsPet() {
        Pet pet = new Pet();
        Mockito.when(petService.getPetById(1L)).thenReturn(pet);

        Pet result = petController.getPetById(1L);

        Assertions.assertNotNull(result);
        Mockito.verify(petService).getPetById(1L);
    }
}
