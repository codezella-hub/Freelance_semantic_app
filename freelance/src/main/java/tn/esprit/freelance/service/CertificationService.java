package tn.esprit.freelance.service;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.freelance.dto.CertificationDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for Certification CRUD operations with RDF/SPARQL
 */
@Service
public class CertificationService {

    private static final Logger logger = LoggerFactory.getLogger(CertificationService.class);
    private static final String NAMESPACE = "http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#";

    @Autowired
    private RDFService rdfService;

    /**
     * Get all certifications
     */
    public List<CertificationDTO> getAllCertifications() {
        String query = """
            PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            
            SELECT ?cert ?name ?issuedBy ?issueDate ?expirationDate ?type
            WHERE {
                ?cert rdf:type onto:Certification .
                OPTIONAL { ?cert onto:certificationName ?name }
                OPTIONAL { ?cert onto:issuedBy ?issuedBy }
                OPTIONAL { ?cert onto:issueDate ?issueDate }
                OPTIONAL { ?cert onto:expirationDate ?expirationDate }
                OPTIONAL { 
                    ?cert rdf:type ?typeClass .
                    FILTER(?typeClass = onto:FormalCertification || ?typeClass = onto:InformalCertification)
                    BIND(IF(?typeClass = onto:FormalCertification, "FormalCertification", "InformalCertification") AS ?type)
                }
            }
            """;
        
        List<CertificationDTO> certifications = new ArrayList<>();
        
        try {
            rdfService.rdfConnection.querySelect(query, (querySolution) -> {
                CertificationDTO cert = new CertificationDTO();
                cert.setUri(getStringValue(querySolution, "cert"));
                cert.setCertificationName(getStringValue(querySolution, "name"));
                cert.setIssuedBy(getStringValue(querySolution, "issuedBy"));
                cert.setIssueDate(getStringValue(querySolution, "issueDate"));
                cert.setExpirationDate(getStringValue(querySolution, "expirationDate"));
                cert.setCertificationType(getStringValue(querySolution, "type"));
                certifications.add(cert);
            });
            
            logger.info("Retrieved {} certifications", certifications.size());
        } catch (Exception e) {
            logger.error("Error retrieving certifications: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve certifications", e);
        }
        
        return certifications;
    }

    /**
     * Get certification by URI
     */
    public CertificationDTO getCertificationByUri(String uri) {
        String query = String.format("""
            PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            
            SELECT ?name ?issuedBy ?issueDate ?expirationDate ?type
            WHERE {
                <%s> rdf:type onto:Certification .
                OPTIONAL { <%s> onto:certificationName ?name }
                OPTIONAL { <%s> onto:issuedBy ?issuedBy }
                OPTIONAL { <%s> onto:issueDate ?issueDate }
                OPTIONAL { <%s> onto:expirationDate ?expirationDate }
                OPTIONAL { 
                    <%s> rdf:type ?typeClass .
                    FILTER(?typeClass = onto:FormalCertification || ?typeClass = onto:InformalCertification)
                    BIND(IF(?typeClass = onto:FormalCertification, "FormalCertification", "InformalCertification") AS ?type)
                }
            }
            """, uri, uri, uri, uri, uri, uri);
        
        List<CertificationDTO> certifications = new ArrayList<>();
        
        try {
            rdfService.rdfConnection.querySelect(query, (querySolution) -> {
                CertificationDTO cert = new CertificationDTO();
                cert.setUri(uri);
                cert.setCertificationName(getStringValue(querySolution, "name"));
                cert.setIssuedBy(getStringValue(querySolution, "issuedBy"));
                cert.setIssueDate(getStringValue(querySolution, "issueDate"));
                cert.setExpirationDate(getStringValue(querySolution, "expirationDate"));
                cert.setCertificationType(getStringValue(querySolution, "type"));
                certifications.add(cert);
            });
            
            if (certifications.isEmpty()) {
                logger.warn("Certification not found: {}", uri);
                return null;
            }
            
            logger.info("Retrieved certification: {}", uri);
            return certifications.get(0);
        } catch (Exception e) {
            logger.error("Error retrieving certification: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve certification", e);
        }
    }

    /**
     * Create a new certification
     */
    public CertificationDTO createCertification(CertificationDTO certDTO) {
        String uri = NAMESPACE + "Certification_" + UUID.randomUUID().toString();
        certDTO.setUri(uri);
        
        StringBuilder updateQuery = new StringBuilder();
        updateQuery.append("PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>\n");
        updateQuery.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
        updateQuery.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n\n");
        updateQuery.append("INSERT DATA {\n");
        updateQuery.append(String.format("  <%s> rdf:type onto:Certification .\n", uri));
        
        // Add certification type (FormalCertification or InformalCertification)
        if (certDTO.getCertificationType() != null && !certDTO.getCertificationType().isEmpty()) {
            if ("FormalCertification".equalsIgnoreCase(certDTO.getCertificationType())) {
                updateQuery.append(String.format("  <%s> rdf:type onto:FormalCertification .\n", uri));
            } else if ("InformalCertification".equalsIgnoreCase(certDTO.getCertificationType())) {
                updateQuery.append(String.format("  <%s> rdf:type onto:InformalCertification .\n", uri));
            }
        }
        
        if (certDTO.getCertificationName() != null && !certDTO.getCertificationName().isEmpty()) {
            updateQuery.append(String.format("  <%s> onto:certificationName \"%s\" .\n", uri, escapeString(certDTO.getCertificationName())));
        }
        
        if (certDTO.getIssuedBy() != null && !certDTO.getIssuedBy().isEmpty()) {
            updateQuery.append(String.format("  <%s> onto:issuedBy \"%s\" .\n", uri, escapeString(certDTO.getIssuedBy())));
        }
        
        if (certDTO.getIssueDate() != null && !certDTO.getIssueDate().isEmpty()) {
            updateQuery.append(String.format("  <%s> onto:issueDate \"%s\"^^xsd:dateTime .\n", uri, certDTO.getIssueDate()));
        }
        
        if (certDTO.getExpirationDate() != null && !certDTO.getExpirationDate().isEmpty()) {
            updateQuery.append(String.format("  <%s> onto:expirationDate \"%s\"^^xsd:dateTime .\n", uri, certDTO.getExpirationDate()));
        }
        
        updateQuery.append("}");
        
        try {
            rdfService.executeUpdate(updateQuery.toString());
            logger.info("Created certification: {}", uri);
            return certDTO;
        } catch (Exception e) {
            logger.error("Error creating certification: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create certification", e);
        }
    }

    /**
     * Update an existing certification
     */
    public CertificationDTO updateCertification(String uri, CertificationDTO certDTO) {
        // First delete existing properties
        deleteCertification(uri);
        
        // Then insert updated data
        certDTO.setUri(uri);
        return createCertification(certDTO);
    }

    /**
     * Delete a certification
     */
    public void deleteCertification(String uri) {
        String updateQuery = String.format("""
            PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            
            DELETE WHERE {
                <%s> ?p ?o .
            }
            """, uri);
        
        try {
            rdfService.executeUpdate(updateQuery);
            logger.info("Deleted certification: {}", uri);
        } catch (Exception e) {
            logger.error("Error deleting certification: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete certification", e);
        }
    }

    /**
     * Search certifications by name
     */
    public List<CertificationDTO> searchCertificationsByName(String name) {
        String query = String.format("""
            PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            
            SELECT ?cert ?name ?issuedBy ?issueDate ?expirationDate ?type
            WHERE {
                ?cert rdf:type onto:Certification .
                ?cert onto:certificationName ?name .
                FILTER(CONTAINS(LCASE(?name), LCASE("%s")))
                OPTIONAL { ?cert onto:issuedBy ?issuedBy }
                OPTIONAL { ?cert onto:issueDate ?issueDate }
                OPTIONAL { ?cert onto:expirationDate ?expirationDate }
                OPTIONAL { 
                    ?cert rdf:type ?typeClass .
                    FILTER(?typeClass = onto:FormalCertification || ?typeClass = onto:InformalCertification)
                    BIND(IF(?typeClass = onto:FormalCertification, "FormalCertification", "InformalCertification") AS ?type)
                }
            }
            """, name);
        
        List<CertificationDTO> certifications = new ArrayList<>();
        
        try {
            rdfService.rdfConnection.querySelect(query, (querySolution) -> {
                CertificationDTO cert = new CertificationDTO();
                cert.setUri(getStringValue(querySolution, "cert"));
                cert.setCertificationName(getStringValue(querySolution, "name"));
                cert.setIssuedBy(getStringValue(querySolution, "issuedBy"));
                cert.setIssueDate(getStringValue(querySolution, "issueDate"));
                cert.setExpirationDate(getStringValue(querySolution, "expirationDate"));
                cert.setCertificationType(getStringValue(querySolution, "type"));
                certifications.add(cert);
            });
            
            logger.info("Found {} certifications matching name: {}", certifications.size(), name);
        } catch (Exception e) {
            logger.error("Error searching certifications: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search certifications", e);
        }
        
        return certifications;
    }

    /**
     * Get certifications by issuer
     */
    public List<CertificationDTO> getCertificationsByIssuer(String issuer) {
        String query = String.format("""
            PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            
            SELECT ?cert ?name ?issuedBy ?issueDate ?expirationDate ?type
            WHERE {
                ?cert rdf:type onto:Certification .
                ?cert onto:issuedBy "%s" .
                OPTIONAL { ?cert onto:certificationName ?name }
                OPTIONAL { ?cert onto:issueDate ?issueDate }
                OPTIONAL { ?cert onto:expirationDate ?expirationDate }
                OPTIONAL { 
                    ?cert rdf:type ?typeClass .
                    FILTER(?typeClass = onto:FormalCertification || ?typeClass = onto:InformalCertification)
                    BIND(IF(?typeClass = onto:FormalCertification, "FormalCertification", "InformalCertification") AS ?type)
                }
            }
            """, issuer);
        
        List<CertificationDTO> certifications = new ArrayList<>();
        
        try {
            rdfService.rdfConnection.querySelect(query, (querySolution) -> {
                CertificationDTO cert = new CertificationDTO();
                cert.setUri(getStringValue(querySolution, "cert"));
                cert.setCertificationName(getStringValue(querySolution, "name"));
                cert.setIssuedBy(issuer);
                cert.setIssueDate(getStringValue(querySolution, "issueDate"));
                cert.setExpirationDate(getStringValue(querySolution, "expirationDate"));
                cert.setCertificationType(getStringValue(querySolution, "type"));
                certifications.add(cert);
            });
            
            logger.info("Found {} certifications from issuer: {}", certifications.size(), issuer);
        } catch (Exception e) {
            logger.error("Error retrieving certifications by issuer: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve certifications by issuer", e);
        }
        
        return certifications;
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

