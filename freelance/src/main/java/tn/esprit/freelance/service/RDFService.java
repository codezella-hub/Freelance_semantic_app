package tn.esprit.freelance.service;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for RDF operations with Fuseki
 */
@Service
public class RDFService {

    private static final Logger logger = LoggerFactory.getLogger(RDFService.class);

    @Autowired
    protected RDFConnection rdfConnection;

    /**
     * Execute a SPARQL SELECT query
     * @param sparqlQuery The SPARQL query string
     * @return List of results as maps
     */
    public List<Map<String, String>> executeSelectQuery(String sparqlQuery) {
        List<Map<String, String>> results = new ArrayList<>();
        
        try {
            rdfConnection.querySelect(sparqlQuery, (querySolution) -> {
                Map<String, String> row = new HashMap<>();
                querySolution.varNames().forEachRemaining(varName -> {
                    if (querySolution.get(varName) != null) {
                        row.put(varName, querySolution.get(varName).toString());
                    }
                });
                results.add(row);
            });
            logger.info("Query executed successfully. Results count: {}", results.size());
        } catch (Exception e) {
            logger.error("Error executing SELECT query: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to execute SPARQL query", e);
        }
        
        return results;
    }

    /**
     * Execute a SPARQL CONSTRUCT query
     * @param sparqlQuery The SPARQL CONSTRUCT query
     * @return Model containing the constructed triples
     */
    public Model executeConstructQuery(String sparqlQuery) {
        try {
            Model model = rdfConnection.queryConstruct(sparqlQuery);
            logger.info("CONSTRUCT query executed successfully. Triples count: {}", model.size());
            return model;
        } catch (Exception e) {
            logger.error("Error executing CONSTRUCT query: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to execute CONSTRUCT query", e);
        }
    }

    /**
     * Execute a SPARQL ASK query
     * @param sparqlQuery The SPARQL ASK query
     * @return boolean result
     */
    public boolean executeAskQuery(String sparqlQuery) {
        try {
            boolean result = rdfConnection.queryAsk(sparqlQuery);
            logger.info("ASK query executed successfully. Result: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Error executing ASK query: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to execute ASK query", e);
        }
    }

    /**
     * Execute a SPARQL UPDATE query
     * @param sparqlUpdate The SPARQL UPDATE query
     */
    public void executeUpdate(String sparqlUpdate) {
        try {
            rdfConnection.update(sparqlUpdate);
            logger.info("UPDATE query executed successfully");
        } catch (Exception e) {
            logger.error("Error executing UPDATE query: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to execute UPDATE query", e);
        }
    }

    /**
     * Load RDF data from a model into Fuseki
     * @param model The RDF model to load
     */
    public void loadModel(Model model) {
        try {
            rdfConnection.load(model);
            logger.info("Model loaded successfully. Triples count: {}", model.size());
        } catch (Exception e) {
            logger.error("Error loading model: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load model", e);
        }
    }

    /**
     * Get all freelancers from the RDF store
     * @return List of freelancers
     */
    public List<Map<String, String>> getAllFreelancers() {
        String query = """
            PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            
            SELECT ?freelancer ?name ?email ?phone
            WHERE {
                ?freelancer rdf:type onto:Freelancer .
                OPTIONAL { ?freelancer onto:name ?name }
                OPTIONAL { ?freelancer onto:email ?email }
                OPTIONAL { ?freelancer onto:phoneNumber ?phone }
            }
            """;
        return executeSelectQuery(query);
    }

    /**
     * Get all projects from the RDF store
     * @return List of projects
     */
    public List<Map<String, String>> getAllProjects() {
        String query = """
            PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            
            SELECT ?project ?title ?summary ?budget
            WHERE {
                ?project rdf:type onto:Project .
                OPTIONAL { ?project onto:projectTitle ?title }
                OPTIONAL { ?project onto:projectSummary ?summary }
                OPTIONAL { ?project onto:budget ?budget }
            }
            """;
        return executeSelectQuery(query);
    }

    /**
     * Get all skills from the RDF store
     * @return List of skills
     */
    public List<Map<String, String>> getAllSkills() {
        String query = """
            PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            
            SELECT ?skill ?skillName ?level
            WHERE {
                ?skill rdf:type onto:Skill .
                OPTIONAL { ?skill onto:skillName ?skillName }
                OPTIONAL { ?skill onto:proficiencyLevel ?level }
            }
            """;
        return executeSelectQuery(query);
    }

    /**
     * Get freelancers with a specific skill
     * @param skillName The skill name to search for
     * @return List of freelancers with the skill
     */
    public List<Map<String, String>> getFreelancersBySkill(String skillName) {
        String query = String.format("""
            PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            
            SELECT ?freelancer ?name ?email
            WHERE {
                ?freelancer rdf:type onto:Freelancer .
                ?freelancer onto:hasSkill ?skill .
                ?skill onto:skillName "%s" .
                OPTIONAL { ?freelancer onto:name ?name }
                OPTIONAL { ?freelancer onto:email ?email }
            }
            """, skillName);
        return executeSelectQuery(query);
    }

    /**
     * Test connection to Fuseki
     * @return true if connection is successful
     */
    public boolean testConnection() {
        try {
            String query = "SELECT * WHERE { ?s ?p ?o } LIMIT 1";
            executeSelectQuery(query);
            logger.info("Fuseki connection test successful");
            return true;
        } catch (Exception e) {
            logger.error("Fuseki connection test failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get count of all triples in the dataset
     * @return Count of triples
     */
    public long getTripleCount() {
        String query = "SELECT (COUNT(*) as ?count) WHERE { ?s ?p ?o }";
        List<Map<String, String>> results = executeSelectQuery(query);
        if (!results.isEmpty() && results.get(0).containsKey("count")) {
            String countStr = results.get(0).get("count");
            // Extract numeric value from the string (format: "123"^^http://www.w3.org/2001/XMLSchema#integer)
            return Long.parseLong(countStr.replaceAll("[^0-9]", ""));
        }
        return 0;
    }

    /**
     * Convert a Model to JSON-LD string
     * @param model The RDF model
     * @return JSON-LD string representation
     */
    public String modelToJsonLd(Model model) {
        StringWriter writer = new StringWriter();
        model.write(writer, "JSON-LD");
        return writer.toString();
    }
}

