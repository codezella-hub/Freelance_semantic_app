package tn.esprit.freelance.component;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tn.esprit.freelance.service.RDFService;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Component to load RDF data into Fuseki on application startup
 */
@Component
public class RDFDataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RDFDataLoader.class);

    @Autowired
    private RDFService rdfService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting RDF data loader...");
        
        try {
            // First, test the connection
            if (!rdfService.testConnection()) {
                logger.warn("Cannot connect to Fuseki. Please ensure Fuseki server is running at http://localhost:3030");
                logger.warn("You can start Fuseki and create a dataset named 'rassil' manually.");
                return;
            }

            // Check if data already exists
            long tripleCount = rdfService.getTripleCount();
            if (tripleCount > 0) {
                logger.info("Dataset already contains {} triples. Skipping data load.", tripleCount);
                return;
            }

            // Try to load from external file first
            Path externalRdfPath = Paths.get("useki-server/dataset/semantique10.rdf");
            if (Files.exists(externalRdfPath)) {
                logger.info("Loading RDF data from external file: {}", externalRdfPath.toAbsolutePath());
                loadRdfFile(externalRdfPath.toFile());
            } else {
                logger.warn("RDF file not found at: {}", externalRdfPath.toAbsolutePath());
                logger.info("Please ensure the RDF file exists at the specified location.");
                logger.info("You can manually load the data using Fuseki web interface at http://localhost:3030");
            }

        } catch (Exception e) {
            logger.error("Error during RDF data loading: {}", e.getMessage(), e);
            logger.warn("Application will continue, but RDF data may not be loaded.");
            logger.info("You can manually load the data later using the Fuseki web interface.");
        }
    }

    /**
     * Load RDF data from a file
     * @param rdfFile The RDF file to load
     */
    private void loadRdfFile(File rdfFile) {
        try {
            logger.info("Reading RDF file: {}", rdfFile.getAbsolutePath());
            Model model = ModelFactory.createDefaultModel();
            
            // Read the RDF file
            RDFDataMgr.read(model, rdfFile.getAbsolutePath());
            
            logger.info("RDF file loaded into model. Triple count: {}", model.size());
            
            // Load the model into Fuseki
            rdfService.loadModel(model);
            
            logger.info("RDF data successfully loaded into Fuseki dataset 'rassil'");
            
            // Verify the load
            long tripleCount = rdfService.getTripleCount();
            logger.info("Verification: Dataset now contains {} triples", tripleCount);
            
        } catch (Exception e) {
            logger.error("Failed to load RDF file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load RDF data", e);
        }
    }

    /**
     * Load RDF data from classpath resource
     * @param resourcePath The classpath resource path
     */
    @SuppressWarnings("unused")
    private void loadRdfFromClasspath(String resourcePath) {
        try {
            logger.info("Loading RDF data from classpath: {}", resourcePath);
            Resource resource = new ClassPathResource(resourcePath);
            
            Model model = ModelFactory.createDefaultModel();
            try (InputStream inputStream = resource.getInputStream()) {
                RDFDataMgr.read(model, inputStream, null, org.apache.jena.riot.Lang.RDFXML);
            }
            
            logger.info("RDF data loaded into model. Triple count: {}", model.size());
            
            // Load the model into Fuseki
            rdfService.loadModel(model);
            
            logger.info("RDF data successfully loaded into Fuseki dataset 'rassil'");
            
        } catch (Exception e) {
            logger.error("Failed to load RDF from classpath: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load RDF data from classpath", e);
        }
    }
}

