package tn.esprit.freelance.service;

import org.apache.jena.query.*;
import org.springframework.stereotype.Service;
import tn.esprit.freelance.util.AdvancedSparqlQueries;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDate;

@Service
public class AdvancedSearchService {
    @org.springframework.beans.factory.annotation.Value("${fuseki.endpoint}")
    private String fusekiEndpoint;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getContractsWithPayments() {
        try {
            Query query = QueryFactory.create(AdvancedSparqlQueries.GET_CONTRACTS_WITH_PAYMENTS);
            try (QueryExecution qexec = QueryExecutionFactory.sparqlService(fusekiEndpoint, query)) {
                ResultSet results = qexec.execSelect();
                return convertResultSetToJson(results);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public String searchContractsAndPayments(String titleQuery, double minAmount, String status) {
        try {
            String queryStr = String.format(
                AdvancedSparqlQueries.SEARCH_CONTRACTS_AND_PAYMENTS,
                titleQuery, // for title regex
                titleQuery, // for description regex
                minAmount,  // numeric comparison
                status
            );
            Query query = QueryFactory.create(queryStr);
            try (QueryExecution qexec = QueryExecutionFactory.sparqlService(fusekiEndpoint, query)) {
                ResultSet results = qexec.execSelect();
                return convertResultSetToJson(results);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public String getPaymentStatistics() {
        try {
            Query query = QueryFactory.create(AdvancedSparqlQueries.GET_PAYMENT_STATISTICS);
            try (QueryExecution qexec = QueryExecutionFactory.sparqlService(fusekiEndpoint, query)) {
                ResultSet results = qexec.execSelect();
                return convertResultSetToJson(results);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public String getContractsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            String queryStr = String.format(
                AdvancedSparqlQueries.GET_CONTRACTS_BY_DATE_RANGE,
                startDate, endDate
            );
            Query query = QueryFactory.create(queryStr);
            try (QueryExecution qexec = QueryExecutionFactory.sparqlService(fusekiEndpoint, query)) {
                ResultSet results = qexec.execSelect();
                return convertResultSetToJson(results);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public String getContractRelations(String contractUri) {
        try {
            Query query = QueryFactory.create(AdvancedSparqlQueries.GET_CONTRACT_RELATIONS);
            try (QueryExecution qexec = QueryExecutionFactory.sparqlService(fusekiEndpoint, query)) {
                ResultSet results = qexec.execSelect();
                return convertResultSetToJson(results);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    private String convertResultSetToJson(ResultSet resultSet) {
        try {
            ArrayNode results = objectMapper.createArrayNode();
            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.next();
                ObjectNode result = objectMapper.createObjectNode();
                solution.varNames().forEachRemaining(varName -> {
                    if (solution.get(varName) != null) {
                        result.put(varName, solution.get(varName).toString());
                    }
                });
                results.add(result);
            }
            return objectMapper.writeValueAsString(results);
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }
}