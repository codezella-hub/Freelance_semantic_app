package tn.esprit.freelance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.freelance.dto.EventDTO;
import tn.esprit.freelance.service.EventService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Event CRUD operations
 */
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:4200")
public class EventController {

    @Autowired
    private EventService eventService;

    /**
     * Get all events
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEvents() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EventDTO> events = eventService.getAllEvents();
            response.put("events", events);
            response.put("count", events.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get event by URI (using query parameter to avoid URL encoding issues)
     */
    @GetMapping("/by-uri")
    public ResponseEntity<Map<String, Object>> getEventByUri(@RequestParam String uri) {
        Map<String, Object> response = new HashMap<>();
        try {
            EventDTO event = eventService.getEventByUri(uri);

            if (event == null) {
                response.put("error", "Event not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("event", event);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Create a new event
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEvent(@RequestBody EventDTO eventDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            EventDTO createdEvent = eventService.createEvent(eventDTO);
            response.put("event", createdEvent);
            response.put("message", "Event created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update an existing event (using query parameter)
     */
    @PutMapping
    public ResponseEntity<Map<String, Object>> updateEvent(
            @RequestParam String uri,
            @RequestBody EventDTO eventDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            EventDTO updatedEvent = eventService.updateEvent(uri, eventDTO);
            response.put("event", updatedEvent);
            response.put("message", "Event updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete an event (using query parameter)
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteEvent(@RequestParam String uri) {
        Map<String, Object> response = new HashMap<>();
        try {
            eventService.deleteEvent(uri);
            response.put("message", "Event deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Search events by title
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchEvents(@RequestParam String title) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EventDTO> events = eventService.searchEventsByTitle(title);
            response.put("events", events);
            response.put("count", events.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get events by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getEventsByCategory(@PathVariable String category) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EventDTO> events = eventService.getEventsByCategory(category);
            response.put("events", events);
            response.put("count", events.size());
            response.put("category", category);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

