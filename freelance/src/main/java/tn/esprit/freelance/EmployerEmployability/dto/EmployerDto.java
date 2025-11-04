package tn.esprit.freelance.EmployerEmployability.dto;

import lombok.Data;

@Data
public class EmployerDto {
    private String id;
    private String type;
    private String companyName;
    private String email;
    private String phoneNumber;
    private String employabilityId;
    private Double employabilityScore;
}

