package tn.esprit.freelance.EmployerEmployability.sparql;

import org.apache.jena.query.*;
import org.apache.jena.update.*;
import org.springframework.stereotype.Component;

@Component
public class SparqlClient {

    private final String endpoint;
    private final String prefixes;

    public SparqlClient(String sparqlEndpoint, String prefixes) {
        this.endpoint = sparqlEndpoint;
        this.prefixes = prefixes;
    }

    public ResultSet select(String queryBody) {
        String q = prefixes + "\n" + queryBody;
        // Remplace /update par /query si ton endpoint principal est /update
        String readEndpoint = endpoint.replace("/update", "/query");
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(readEndpoint, q)) {
            return ResultSetFactory.copyResults(qexec.execSelect());
        }
    }

    public boolean ask(String queryBody) {
        String q = prefixes + "\n" + queryBody;
        String readEndpoint = endpoint.replace("/update", "/query");
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(readEndpoint, q)) {
            return qexec.execAsk();
        }
    }

    public void update(String updateBody) {
        String up = prefixes + "\n" + updateBody;
        UpdateRequest req = UpdateFactory.create(up);
        UpdateProcessor proc = UpdateExecutionFactory.createRemote(req, endpoint);
        proc.execute();
    }
}
