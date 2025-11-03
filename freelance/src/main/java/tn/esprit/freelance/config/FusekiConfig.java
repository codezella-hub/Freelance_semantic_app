package tn.esprit.freelance.config;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Apache Jena Fuseki integration
 */
@Configuration
public class FusekiConfig {

    @Value("${fuseki.endpoint}")
    private String fusekiEndpoint;

    @Value("${fuseki.update.endpoint}")
    private String fusekiUpdateEndpoint;

    @Value("${fuseki.data.endpoint}")
    private String fusekiDataEndpoint;

    @Value("${fuseki.query.endpoint}")
    private String fusekiQueryEndpoint;

    /**
     * Creates a remote RDF connection to Fuseki server
     * @return RDFConnection configured for the Fuseki dataset
     */
    @Bean
    public RDFConnection rdfConnection() {
        return RDFConnectionRemote.create()
                .destination(fusekiEndpoint)
                .queryEndpoint(fusekiQueryEndpoint)
                .updateEndpoint(fusekiUpdateEndpoint)
                .gspEndpoint(fusekiDataEndpoint)
                .build();
    }

    /**
     * Get the Fuseki SPARQL endpoint URL
     * @return The SPARQL endpoint URL
     */
    public String getFusekiEndpoint() {
        return fusekiEndpoint;
    }

    /**
     * Get the Fuseki update endpoint URL
     * @return The update endpoint URL
     */
    public String getFusekiUpdateEndpoint() {
        return fusekiUpdateEndpoint;
    }

    /**
     * Get the Fuseki data endpoint URL
     * @return The data endpoint URL
     */
    public String getFusekiDataEndpoint() {
        return fusekiDataEndpoint;
    }

    /**
     * Get the Fuseki query endpoint URL
     * @return The query endpoint URL
     */
    public String getFusekiQueryEndpoint() {
        return fusekiQueryEndpoint;
    }
}

