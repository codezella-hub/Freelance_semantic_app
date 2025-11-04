package tn.esprit.freelance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Certification operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificationDTO {
    private String uri;
    private String certificationName;
    private String issuedBy;
    private String issueDate; // ISO 8601 format: yyyy-MM-dd'T'HH:mm:ss
    private String expirationDate; // ISO 8601 format: yyyy-MM-dd'T'HH:mm:ss
    private String certificationType; // FormalCertification or InformalCertification
}

