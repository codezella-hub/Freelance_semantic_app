package tn.esprit.freelance.ProjetsEvaluation.sparql;

import org.apache.jena.query.*;
import org.apache.jena.update.*;
import org.springframework.stereotype.Component;

@Component
public class SparqlClient1 {

    private final String queryEndpoint;
    private final String updateEndpoint;
    private final String prefixes;

    public SparqlClient1(String sparqlQueryEndpoint, String sparqlUpdateEndpoint, String prefixes) {
        this.queryEndpoint = sparqlQueryEndpoint;
        this.updateEndpoint = sparqlUpdateEndpoint;
        this.prefixes = prefixes;
    }

    // Requête SELECT
    public ResultSet select(String queryBody) {
        String q = prefixes + "\n" + queryBody;
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(queryEndpoint, q)) {
            return ResultSetFactory.copyResults(qexec.execSelect());
        }
    }

    // Requête ASK
    public boolean ask(String queryBody) {
        String q = prefixes + "\n" + queryBody;
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(queryEndpoint, q)) {
            return qexec.execAsk();
        }
    }

    // Requête UPDATE (INSERT/DELETE)
    public void update(String updateBody) {
        String up = prefixes + "\n" + updateBody;
        UpdateRequest req = UpdateFactory.create(up);
        UpdateProcessor proc = UpdateExecutionFactory.createRemote(req, updateEndpoint);
        proc.execute();
    }
}
