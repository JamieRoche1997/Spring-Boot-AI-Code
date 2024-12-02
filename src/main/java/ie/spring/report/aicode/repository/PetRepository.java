package ie.spring.report.aicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ie.spring.report.aicode.model.Pet;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import ie.spring.report.aicode.dto.PetSummary;

public interface PetRepository extends JpaRepository<Pet, Long> {

    // Find pets by name (ignoring case)
    List<Pet> findByNameIgnoreCase(String name);

    // Delete pets by name (ignoring case)
    void deleteByNameIgnoreCase(String name);

    // Find pets by animal type (ignoring case)
    List<Pet> findByAnimalTypeIgnoreCase(String animalType);

    // Find pets by breed (ignoring case) and order by age
    List<Pet> findByBreedIgnoreCaseOrderByAgeAsc(String breed);

    // Get only name, animal type, and breed
    @Query("SELECT p.name AS name, p.animalType AS animalType, p.breed AS breed FROM Pet p")
    List<PetSummary> findAllPetSummaries();

    // Get the average age
    @Query("SELECT AVG(p.age) FROM Pet p")
    Double findAverageAge();

    // Get the oldest age
    @Query("SELECT MAX(p.age) FROM Pet p")
    Integer findMaxAge();
}

