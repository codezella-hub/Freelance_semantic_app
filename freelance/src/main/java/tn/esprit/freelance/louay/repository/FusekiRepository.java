package tn.esprit.freelance.louay.repository;



import org.apache.jena.query.*;
import org.apache.jena.update.*;

import org.springframework.stereotype.Repository;

@Repository
public class FusekiRepository {
    private static final String FUSEKI_URL = "http://localhost:3030/myDataset";

    public ResultSet select(String queryString) {
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(FUSEKI_URL + "/query", query)) {
            return ResultSetFactory.copyResults(qexec.execSelect());
        }
    }

    public void update(String updateString) {
        UpdateRequest update = UpdateFactory.create(updateString);
        UpdateProcessor processor = UpdateExecutionFactory.createRemote(update, FUSEKI_URL + "/update");
        processor.execute();
    }
}
