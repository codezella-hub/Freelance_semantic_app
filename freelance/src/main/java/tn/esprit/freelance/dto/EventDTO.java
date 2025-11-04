package tn.esprit.freelance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Event operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private String uri;
    private String eventTitle;
    private String eventDescription;
    private String eventCategory;
    private String eventDate; // ISO 8601 format: yyyy-MM-dd'T'HH:mm:ss
    private String eventType; // Public or Premium
}

