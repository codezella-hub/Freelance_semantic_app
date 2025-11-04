package tn.esprit.freelance.service;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.freelance.dto.EventDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for Event CRUD operations with RDF/SPARQL
 */
@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private static final String NAMESPACE = "http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#";

    @Autowired
    private RDFService rdfService;

    /**
     * Get all events
     */
    public List<EventDTO> getAllEvents() {
        String query = """
            PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            
            SELECT ?event ?title ?description ?category ?date ?type
            WHERE {
                ?event rdf:type onto:Event .
                OPTIONAL { ?event onto:EventTitle ?title }
                OPTIONAL { ?event onto:Eventdescription ?description }
                OPTIONAL { ?event onto:EventCategory ?category }
                OPTIONAL { ?event onto:EventDate ?date }
                OPTIONAL { 
                    ?event rdf:type ?typeClass .
                    FILTER(?typeClass = onto:Public || ?typeClass = onto:Premium)
                    BIND(IF(?typeClass = onto:Public, "Public", "Premium") AS ?type)
                }
            }
            """;
        
        List<EventDTO> events = new ArrayList<>();
        
        try {
            rdfService.rdfConnection.querySelect(query, (querySolution) -> {
                EventDTO event = new EventDTO();
                event.setUri(getStringValue(querySolution, "event"));
                event.setEventTitle(getStringValue(querySolution, "title"));
                event.setEventDescription(getStringValue(querySolution, "description"));
                event.setEventCategory(getStringValue(querySolution, "category"));
                event.setEventDate(getStringValue(querySolution, "date"));
                event.setEventType(getStringValue(querySolution, "type"));
                events.add(event);
            });
            
            logger.info("Retrieved {} events", events.size());
        } catch (Exception e) {
            logger.error("Error retrieving events: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve events", e);
        }
        
        return events;
    }

    /**
     * Get event by URI
     */
    public EventDTO getEventByUri(String uri) {
        String query = String.format("""
            PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            
            SELECT ?title ?description ?category ?date ?type
            WHERE {
                <%s> rdf:type onto:Event .
                OPTIONAL { <%s> onto:EventTitle ?title }
                OPTIONAL { <%s> onto:Eventdescription ?description }
                OPTIONAL { <%s> onto:EventCategory ?category }
                OPTIONAL { <%s> onto:EventDate ?date }
                OPTIONAL { 
                    <%s> rdf:type ?typeClass .
                    FILTER(?typeClass = onto:Public || ?typeClass = onto:Premium)
                    BIND(IF(?typeClass = onto:Public, "Public", "Premium") AS ?type)
                }
            }
            """, uri, uri, uri, uri, uri, uri);
        
        List<EventDTO> events = new ArrayList<>();
        
        try {
            rdfService.rdfConnection.querySelect(query, (querySolution) -> {
                EventDTO event = new EventDTO();
                event.setUri(uri);
                event.setEventTitle(getStringValue(querySolution, "title"));
                event.setEventDescription(getStringValue(querySolution, "description"));
                event.setEventCategory(getStringValue(querySolution, "category"));
                event.setEventDate(getStringValue(querySolution, "date"));
                event.setEventType(getStringValue(querySolution, "type"));
                events.add(event);
            });
            
            if (events.isEmpty()) {
                logger.warn("Event not found: {}", uri);
                return null;
            }
            
            logger.info("Retrieved event: {}", uri);
            return events.get(0);
        } catch (Exception e) {
            logger.error("Error retrieving event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve event", e);
        }
    }

    /**
     * Create a new event
     */
    public EventDTO createEvent(EventDTO eventDTO) {
        String uri = NAMESPACE + "Event_" + UUID.randomUUID().toString();
        eventDTO.setUri(uri);
        
        StringBuilder updateQuery = new StringBuilder();
        updateQuery.append("PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>\n");
        updateQuery.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
        updateQuery.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n\n");
        updateQuery.append("INSERT DATA {\n");
        updateQuery.append(String.format("  <%s> rdf:type onto:Event .\n", uri));
        
        // Add event type (Public or Premium)
        if (eventDTO.getEventType() != null && !eventDTO.getEventType().isEmpty()) {
            if ("Public".equalsIgnoreCase(eventDTO.getEventType())) {
                updateQuery.append(String.format("  <%s> rdf:type onto:Public .\n", uri));
            } else if ("Premium".equalsIgnoreCase(eventDTO.getEventType())) {
                updateQuery.append(String.format("  <%s> rdf:type onto:Premium .\n", uri));
            }
        }
        
        if (eventDTO.getEventTitle() != null && !eventDTO.getEventTitle().isEmpty()) {
            updateQuery.append(String.format("  <%s> onto:EventTitle \"%s\" .\n", uri, escapeString(eventDTO.getEventTitle())));
        }
        
        if (eventDTO.getEventDescription() != null && !eventDTO.getEventDescription().isEmpty()) {
            updateQuery.append(String.format("  <%s> onto:Eventdescription \"%s\" .\n", uri, escapeString(eventDTO.getEventDescription())));
        }
        
        if (eventDTO.getEventCategory() != null && !eventDTO.getEventCategory().isEmpty()) {
            updateQuery.append(String.format("  <%s> onto:EventCategory \"%s\" .\n", uri, escapeString(eventDTO.getEventCategory())));
        }
        
        if (eventDTO.getEventDate() != null && !eventDTO.getEventDate().isEmpty()) {
            updateQuery.append(String.format("  <%s> onto:EventDate \"%s\"^^xsd:dateTime .\n", uri, eventDTO.getEventDate()));
        }
        
        updateQuery.append("}");
        
        try {
            rdfService.executeUpdate(updateQuery.toString());
            logger.info("Created event: {}", uri);
            return eventDTO;
        } catch (Exception e) {
            logger.error("Error creating event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create event", e);
        }
    }

    /**
     * Update an existing event
     */
    public EventDTO updateEvent(String uri, EventDTO eventDTO) {
        // First delete existing properties
        deleteEvent(uri);
        
        // Then insert updated data
        eventDTO.setUri(uri);
        return createEvent(eventDTO);
    }

    /**
     * Delete an event
     */
    public void deleteEvent(String uri) {
        String updateQuery = String.format("""
            PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            
            DELETE WHERE {
                <%s> ?p ?o .
            }
            """, uri);
        
        try {
            rdfService.executeUpdate(updateQuery);
            logger.info("Deleted event: {}", uri);
        } catch (Exception e) {
            logger.error("Error deleting event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete event", e);
        }
    }

    /**
     * Search events by title
     */
    public List<EventDTO> searchEventsByTitle(String title) {
        String query = String.format("""
            PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            
            SELECT ?event ?title ?description ?category ?date ?type
            WHERE {
                ?event rdf:type onto:Event .
                ?event onto:EventTitle ?title .
                FILTER(CONTAINS(LCASE(?title), LCASE("%s")))
                OPTIONAL { ?event onto:Eventdescription ?description }
                OPTIONAL { ?event onto:EventCategory ?category }
                OPTIONAL { ?event onto:EventDate ?date }
                OPTIONAL { 
                    ?event rdf:type ?typeClass .
                    FILTER(?typeClass = onto:Public || ?typeClass = onto:Premium)
                    BIND(IF(?typeClass = onto:Public, "Public", "Premium") AS ?type)
                }
            }
            """, title);
        
        List<EventDTO> events = new ArrayList<>();
        
        try {
            rdfService.rdfConnection.querySelect(query, (querySolution) -> {
                EventDTO event = new EventDTO();
                event.setUri(getStringValue(querySolution, "event"));
                event.setEventTitle(getStringValue(querySolution, "title"));
                event.setEventDescription(getStringValue(querySolution, "description"));
                event.setEventCategory(getStringValue(querySolution, "category"));
                event.setEventDate(getStringValue(querySolution, "date"));
                event.setEventType(getStringValue(querySolution, "type"));
                events.add(event);
            });
            
            logger.info("Found {} events matching title: {}", events.size(), title);
        } catch (Exception e) {
            logger.error("Error searching events: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search events", e);
        }
        
        return events;
    }

    /**
     * Get events by category
     */
    public List<EventDTO> getEventsByCategory(String category) {
        String query = String.format("""
            PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            
            SELECT ?event ?title ?description ?category ?date ?type
            WHERE {
                ?event rdf:type onto:Event .
                ?event onto:EventCategory "%s" .
                OPTIONAL { ?event onto:EventTitle ?title }
                OPTIONAL { ?event onto:Eventdescription ?description }
                OPTIONAL { ?event onto:EventDate ?date }
                OPTIONAL { 
                    ?event rdf:type ?typeClass .
                    FILTER(?typeClass = onto:Public || ?typeClass = onto:Premium)
                    BIND(IF(?typeClass = onto:Public, "Public", "Premium") AS ?type)
                }
            }
            """, category);
        
        List<EventDTO> events = new ArrayList<>();
        
        try {
            rdfService.rdfConnection.querySelect(query, (querySolution) -> {
                EventDTO event = new EventDTO();
                event.setUri(getStringValue(querySolution, "event"));
                event.setEventTitle(getStringValue(querySolution, "title"));
                event.setEventDescription(getStringValue(querySolution, "description"));
                event.setEventCategory(category);
                event.setEventDate(getStringValue(querySolution, "date"));
                event.setEventType(getStringValue(querySolution, "type"));
                events.add(event);
            });
            
            logger.info("Found {} events in category: {}", events.size(), category);
        } catch (Exception e) {
            logger.error("Error retrieving events by category: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve events by category", e);
        }
        
        return events;
    }

    // Helper methods
    private String getStringValue(QuerySolution solution, String varName) {
        RDFNode node = solution.get(varName);
        if (node != null) {
            return node.toString().replaceAll("\\^\\^.*$", "").replaceAll("\"", "");
        }
        return null;
    }

    private String escapeString(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }
}

