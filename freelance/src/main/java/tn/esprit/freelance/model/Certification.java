package tn.esprit.freelance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Certification model representing a certification in the RDF store
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Certification {
    private String uri;
    private String certificationName;
    private String issuedBy;
    private LocalDateTime issueDate;
    private LocalDateTime expirationDate;
    private String certificationType; // FormalCertification or InformalCertification
}

