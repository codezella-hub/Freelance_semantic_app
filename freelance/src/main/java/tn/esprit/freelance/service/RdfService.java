package tn.esprit.freelance.service;

import org.apache.jena.query.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class RdfService {

    @Value("${fuseki.endpoint}")
    private String fusekiEndpoint;

    // Get contracts as JSON
    public String getContracts() {
        String sparql = """
            PREFIX : <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            SELECT ?contract ?terms ?contractDate WHERE {
                ?contract a :Contract ;
                          :terms ?terms ;
                          :contractDate ?contractDate .
            }
        """;
        Query query = QueryFactory.create(sparql);
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(fusekiEndpoint, query)) {
            ResultSet results = qexec.execSelect();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ResultSetFormatter.outputAsJSON(out, results);
            return out.toString(StandardCharsets.UTF_8);
        }
    }

    // Get payments as JSON
    public String getPayments() {
        String sparql = """
            PREFIX : <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
            SELECT ?payment ?amount ?date ?status WHERE {
                ?payment a :Payment ;
                         :paymentAmount ?amount ;
                         :paymentDate ?date ;
                         :paymentStatus ?status .
            }
        """;
        Query query = QueryFactory.create(sparql);
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(fusekiEndpoint, query)) {
            ResultSet results = qexec.execSelect();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ResultSetFormatter.outputAsJSON(out, results);
            return out.toString(StandardCharsets.UTF_8);
        }
    }

    public String searchPayments(String searchTerm) {
        String sparql = String.format("""
        PREFIX : <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        SELECT ?payment ?amount ?date ?status WHERE {
            ?payment a :Payment ;
                     :paymentAmount ?amount ;
                     :paymentDate ?date ;
                     :paymentStatus ?status .
            FILTER regex(str(?amount), "%s", "i")
        }
    """, searchTerm);

        Query query = QueryFactory.create(sparql);
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(fusekiEndpoint, query)) {
            ResultSet results = qexec.execSelect();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ResultSetFormatter.outputAsJSON(out, results);
            return out.toString(StandardCharsets.UTF_8);
        }
    }

}
