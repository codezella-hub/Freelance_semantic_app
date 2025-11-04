package tn.esprit.freelance.EmployerEmployability.dto;

import lombok.Data;

@Data
public class EmployabilityDto {
    private String id;
    private String kind;
    private Double employabilityScore;
}
