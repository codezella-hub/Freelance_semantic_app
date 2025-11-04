package tn.esprit.freelance.ProjetsEvaluation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SemanticConfig {

    @Value("${fuseki.query-endpoint}")
    private String queryEndpoint;

    @Value("${fuseki.update-endpoint}")
    private String updateEndpoint;

    private static final String BASE = "http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#";

    @Bean
    public String sparqlQueryEndpoint() { return queryEndpoint; }

    @Bean
    public String sparqlUpdateEndpoint() { return updateEndpoint; }

    @Bean
    public String baseNs() { return BASE; }

    @Bean
    public String prefixes() {
        return String.join("\n",
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>",
                "PREFIX base: <" + BASE + ">"
        );
    }
}
