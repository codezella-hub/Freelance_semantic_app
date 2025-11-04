package tn.esprit.freelance.service;

import org.apache.jena.query.*;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NaturalLanguageQueryService {

    private static final String FUSEKI_URL = "http://localhost:3030/employer_db";
    private static final String NAMESPACE = "http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#";

    /**
     * Traite une requête en langage naturel et retourne les résultats
     */
    public Map<String, Object> processNaturalLanguageQuery(String query) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Normaliser la requête
            String normalizedQuery = query.toLowerCase().trim();
            
            // Déterminer le type de requête (Event ou Certification)
            boolean isEventQuery = isEventRelated(normalizedQuery);
            boolean isCertificationQuery = isCertificationRelated(normalizedQuery);
            
            String sparqlQuery;
            
            if (isEventQuery) {
                sparqlQuery = buildEventSparqlQuery(normalizedQuery);
                List<Map<String, String>> results = executeSparqlQuery(sparqlQuery, "event");
                response.put("type", "events");
                response.put("results", results);
                response.put("count", results.size());
            } else if (isCertificationQuery) {
                sparqlQuery = buildCertificationSparqlQuery(normalizedQuery);
                List<Map<String, String>> results = executeSparqlQuery(sparqlQuery, "certification");
                response.put("type", "certifications");
                response.put("results", results);
                response.put("count", results.size());
            } else {
                response.put("error", "Je n'ai pas compris votre requête. Essayez des phrases comme 'donne moi les événements premium' ou 'liste les certifications formelles'");
                return response;
            }
            
            response.put("query", query);
            response.put("sparql", sparqlQuery);
            response.put("success", true);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Erreur lors du traitement de la requête: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * Vérifie si la requête concerne les événements
     */
    private boolean isEventRelated(String query) {
        String[] eventKeywords = {
            "événement", "evenement", "event", "events",
            "formation", "atelier", "conference", "conférence",
            "workshop", "séminaire", "seminaire"
        };
        
        for (String keyword : eventKeywords) {
            if (query.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si la requête concerne les certifications
     */
    private boolean isCertificationRelated(String query) {
        String[] certificationKeywords = {
            "certification", "certifications", "certif", "certifs",
            "diplome", "diplôme", "attestation"
        };
        
        for (String keyword : certificationKeywords) {
            if (query.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Construit une requête SPARQL pour les événements
     */
    private String buildEventSparqlQuery(String query) {
        StringBuilder sparql = new StringBuilder();
        sparql.append("PREFIX ns: <").append(NAMESPACE).append(">\n");
        sparql.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
        sparql.append("SELECT ?event ?title ?description ?category ?date ?type\n");
        sparql.append("WHERE {\n");
        sparql.append("  ?event rdf:type ns:Event .\n");
        sparql.append("  OPTIONAL { ?event ns:EventTitle ?title }\n");
        sparql.append("  OPTIONAL { ?event ns:Eventdescription ?description }\n");
        sparql.append("  OPTIONAL { ?event ns:EventCategory ?category }\n");
        sparql.append("  OPTIONAL { ?event ns:EventDate ?date }\n");

        // Filtres basés sur le contenu de la requête
        List<String> filters = new ArrayList<>();

        // Type d'événement (Premium/Public) - basé sur la classe RDF
        if (query.contains("premium")) {
            sparql.append("  OPTIONAL { \n");
            sparql.append("    ?event rdf:type ?typeClass .\n");
            sparql.append("    FILTER(?typeClass = ns:Premium)\n");
            sparql.append("    BIND(\"Premium\" AS ?type)\n");
            sparql.append("  }\n");
            filters.add("BOUND(?type)");
        } else if (query.contains("public") || query.contains("publique")) {
            sparql.append("  OPTIONAL { \n");
            sparql.append("    ?event rdf:type ?typeClass .\n");
            sparql.append("    FILTER(?typeClass = ns:Public)\n");
            sparql.append("    BIND(\"Public\" AS ?type)\n");
            sparql.append("  }\n");
            filters.add("BOUND(?type)");
        } else {
            // Si pas de filtre de type, récupérer le type quand même
            sparql.append("  OPTIONAL { \n");
            sparql.append("    ?event rdf:type ?typeClass .\n");
            sparql.append("    FILTER(?typeClass = ns:Public || ?typeClass = ns:Premium)\n");
            sparql.append("    BIND(IF(?typeClass = ns:Public, \"Public\", \"Premium\") AS ?type)\n");
            sparql.append("  }\n");
        }

        // Catégorie d'événement
        if (query.contains("formation")) {
            filters.add("CONTAINS(LCASE(STR(?category)), 'formation')");
        } else if (query.contains("atelier")) {
            filters.add("CONTAINS(LCASE(STR(?category)), 'atelier')");
        } else if (query.contains("conference") || query.contains("conférence")) {
            filters.add("CONTAINS(LCASE(STR(?category)), 'conference')");
        } else if (query.contains("workshop")) {
            filters.add("CONTAINS(LCASE(STR(?category)), 'workshop')");
        } else if (query.contains("seminaire") || query.contains("séminaire")) {
            filters.add("CONTAINS(LCASE(STR(?category)), 'seminaire')");
        }

        // Recherche par mot-clé dans le titre
        if (query.contains("aws")) {
            filters.add("CONTAINS(LCASE(STR(?title)), 'aws')");
        } else if (query.contains("docker")) {
            filters.add("CONTAINS(LCASE(STR(?title)), 'docker')");
        } else if (query.contains("kubernetes")) {
            filters.add("CONTAINS(LCASE(STR(?title)), 'kubernetes')");
        } else if (query.contains("react")) {
            filters.add("CONTAINS(LCASE(STR(?title)), 'react')");
        } else if (query.contains("angular")) {
            filters.add("CONTAINS(LCASE(STR(?title)), 'angular')");
        } else if (query.contains("python")) {
            filters.add("CONTAINS(LCASE(STR(?title)), 'python')");
        } else if (query.contains("java")) {
            filters.add("CONTAINS(LCASE(STR(?title)), 'java')");
        } else if (query.contains("securite") || query.contains("sécurité") || query.contains("security")) {
            filters.add("(CONTAINS(LCASE(STR(?title)), 'securite') || CONTAINS(LCASE(STR(?title)), 'security'))");
        }

        // Ajouter les filtres à la requête
        if (!filters.isEmpty()) {
            sparql.append("  FILTER (");
            sparql.append(String.join(" && ", filters));
            sparql.append(")\n");
        }

        sparql.append("}\n");
        sparql.append("ORDER BY ?date");

        return sparql.toString();
    }

    /**
     * Construit une requête SPARQL pour les certifications
     */
    private String buildCertificationSparqlQuery(String query) {
        StringBuilder sparql = new StringBuilder();
        sparql.append("PREFIX ns: <").append(NAMESPACE).append(">\n");
        sparql.append("SELECT ?cert ?name ?issuer ?issueDate ?expirationDate ?type\n");
        sparql.append("WHERE {\n");
        sparql.append("  ?cert a ns:Certification .\n");
        sparql.append("  ?cert ns:certificationName ?name .\n");
        sparql.append("  ?cert ns:issuedBy ?issuer .\n");
        sparql.append("  ?cert ns:issueDate ?issueDate .\n");
        sparql.append("  ?cert ns:expirationDate ?expirationDate .\n");
        sparql.append("  ?cert ns:certificationType ?type .\n");
        
        // Filtres basés sur le contenu de la requête
        List<String> filters = new ArrayList<>();

        // Type de certification
        if (query.contains("formelle") || query.contains("formal")) {
            filters.add("CONTAINS(LCASE(STR(?type)), 'formal')");
        } else if (query.contains("informelle") || query.contains("informal")) {
            filters.add("CONTAINS(LCASE(STR(?type)), 'informal')");
        }

        // Statut (valide/expirée)
        if (query.contains("valide") || query.contains("valid") || query.contains("active")) {
            filters.add("?expirationDate > NOW()");
        } else if (query.contains("expiree") || query.contains("expirée") || query.contains("expired")) {
            filters.add("?expirationDate < NOW()");
        }
        
        // Émetteur
        if (query.contains("aws")) {
            filters.add("CONTAINS(LCASE(?issuer), 'aws')");
        } else if (query.contains("docker")) {
            filters.add("CONTAINS(LCASE(?issuer), 'docker')");
        } else if (query.contains("microsoft")) {
            filters.add("CONTAINS(LCASE(?issuer), 'microsoft')");
        } else if (query.contains("google")) {
            filters.add("CONTAINS(LCASE(?issuer), 'google')");
        } else if (query.contains("oracle")) {
            filters.add("CONTAINS(LCASE(?issuer), 'oracle')");
        }
        
        // Recherche par mot-clé dans le nom
        if (query.contains("kubernetes")) {
            filters.add("CONTAINS(LCASE(?name), 'kubernetes')");
        } else if (query.contains("react")) {
            filters.add("CONTAINS(LCASE(?name), 'react')");
        } else if (query.contains("angular")) {
            filters.add("CONTAINS(LCASE(?name), 'angular')");
        } else if (query.contains("python")) {
            filters.add("CONTAINS(LCASE(?name), 'python')");
        } else if (query.contains("securite") || query.contains("sécurité") || query.contains("security")) {
            filters.add("(CONTAINS(LCASE(?name), 'securite') || CONTAINS(LCASE(?name), 'security'))");
        }
        
        // Ajouter les filtres à la requête
        if (!filters.isEmpty()) {
            sparql.append("  FILTER (");
            sparql.append(String.join(" && ", filters));
            sparql.append(")\n");
        }
        
        sparql.append("}\n");
        sparql.append("ORDER BY ?issueDate");
        
        return sparql.toString();
    }

    /**
     * Exécute une requête SPARQL et retourne les résultats
     */
    private List<Map<String, String>> executeSparqlQuery(String sparqlQuery, String type) {
        List<Map<String, String>> results = new ArrayList<>();
        
        try (RDFConnection conn = RDFConnectionRemote.create()
                .destination(FUSEKI_URL)
                .queryEndpoint("sparql")
                .build()) {
            
            try (QueryExecution qExec = conn.query(sparqlQuery)) {
                ResultSet resultSet = qExec.execSelect();
                
                while (resultSet.hasNext()) {
                    QuerySolution solution = resultSet.nextSolution();
                    Map<String, String> item = new HashMap<>();
                    
                    if (type.equals("event")) {
                        item.put("uri", solution.getResource("event").getURI());
                        item.put("eventTitle", solution.getLiteral("title").getString());
                        item.put("eventDescription", solution.getLiteral("description").getString());
                        item.put("eventCategory", solution.getLiteral("category").getString());
                        item.put("eventDate", solution.getLiteral("date").getString());
                        item.put("eventType", solution.getLiteral("type").getString());
                    } else if (type.equals("certification")) {
                        item.put("uri", solution.getResource("cert").getURI());
                        item.put("certificationName", solution.getLiteral("name").getString());
                        item.put("issuedBy", solution.getLiteral("issuer").getString());
                        item.put("issueDate", solution.getLiteral("issueDate").getString());
                        item.put("expirationDate", solution.getLiteral("expirationDate").getString());
                        item.put("certificationType", solution.getLiteral("type").getString());
                    }
                    
                    results.add(item);
                }
            }
        }
        
        return results;
    }
}

