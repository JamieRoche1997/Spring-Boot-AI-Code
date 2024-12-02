package ie.spring.report.aicode.graphql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetInput {
    private String name;
    private String animalType;
    private String breed;
    private int age;
    private String eircode;

}

