package tn.esprit.freelance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event model representing an event in the RDF store
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private String uri;
    private String eventTitle;
    private String eventDescription;
    private String eventCategory;
    private LocalDateTime eventDate;
    private String eventType; // Public or Premium
}

