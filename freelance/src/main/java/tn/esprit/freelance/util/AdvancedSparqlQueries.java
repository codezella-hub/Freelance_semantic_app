package tn.esprit.freelance.util;

public class AdvancedSparqlQueries {

    // Query to get contracts with their associated payments
    public static final String GET_CONTRACTS_WITH_PAYMENTS = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX owl: <http://www.w3.org/2002/07/owl#>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
        PREFIX freelance: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        
        SELECT ?contract ?title ?description ?startDate ?endDate ?amount ?status 
               (GROUP_CONCAT(?paymentAmount; separator=",") as ?payments)
               (COUNT(?payment) as ?paymentCount)
               (SUM(?paymentAmount) as ?totalPaid)
        WHERE {
            ?contract rdf:type freelance:Contract ;
                     freelance:title ?title ;
                     freelance:description ?description ;
                     freelance:startDate ?startDate ;
                     freelance:endDate ?endDate ;
                     freelance:amount ?amount ;
                     freelance:status ?status .
            OPTIONAL {
                ?contract freelance:involvesPayment ?payment .
                ?payment freelance:amount ?paymentAmount .
            }
        }
        GROUP BY ?contract ?title ?description ?startDate ?endDate ?amount ?status
    """;

    // Query to search contracts and payments by multiple criteria
    public static final String SEARCH_CONTRACTS_AND_PAYMENTS = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
        PREFIX freelance: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        
        SELECT ?contract ?title ?description ?amount ?status ?paymentCount ?totalPaid
        WHERE {
            ?contract rdf:type freelance:Contract ;
                     freelance:title ?title ;
                     freelance:amount ?amount ;
                     freelance:status ?status .
            OPTIONAL { ?contract freelance:description ?description }
            {
                SELECT ?contract (COUNT(?payment) as ?paymentCount) (SUM(?paymentAmount) as ?totalPaid)
                WHERE {
                    OPTIONAL {
                        ?contract freelance:involvesPayment ?payment .
                        ?payment freelance:amount ?paymentAmount .
                    }
                }
                GROUP BY ?contract
            }
            FILTER(
                REGEX(?title, "%s", "i") ||
                REGEX(?description, "%s", "i") ||
                (?amount >= %s) ||
                str(?status) = "%s"
            )
        }
        ORDER BY ?title
    """;

    // Query to get payment statistics by contract
    public static final String GET_PAYMENT_STATISTICS = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX freelance: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        
        SELECT ?contract ?title 
               (COUNT(?payment) as ?totalPayments)
               (SUM(?paymentAmount) as ?totalPaid)
               (AVG(?paymentAmount) as ?averagePayment)
               (MAX(?paymentAmount) as ?maxPayment)
               (MIN(?paymentAmount) as ?minPayment)
        WHERE {
            ?contract rdf:type freelance:Contract ;
                     freelance:title ?title ;
                     freelance:involvesPayment ?payment .
            ?payment freelance:amount ?paymentAmount .
        }
        GROUP BY ?contract ?title
        HAVING(COUNT(?payment) > 0)
    """;

    // Query to get contracts by date range
    public static final String GET_CONTRACTS_BY_DATE_RANGE = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX freelance: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
        
        SELECT ?contract ?title ?startDate ?endDate ?amount ?status
        WHERE {
            ?contract rdf:type freelance:Contract ;
                     freelance:title ?title ;
                     freelance:startDate ?startDate ;
                     freelance:endDate ?endDate ;
                     freelance:amount ?amount ;
                     freelance:status ?status .
            FILTER(
                ?startDate >= "%s"^^xsd:date &&
                ?endDate <= "%s"^^xsd:date
            )
        }
        ORDER BY ?startDate
    """;

    // Query to get related entities for a contract
    public static final String GET_CONTRACT_RELATIONS = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX freelance: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        
        SELECT ?contract ?title 
               ?mission ?missionTitle
               ?application ?applicationStatus
               ?payment ?paymentAmount ?paymentStatus
        WHERE {
            ?contract rdf:type freelance:Contract ;
                     freelance:title ?title .
            OPTIONAL {
                ?mission freelance:hasContract ?contract ;
                        freelance:title ?missionTitle .
            }
            OPTIONAL {
                ?application freelance:hasContract ?contract ;
                           freelance:status ?applicationStatus .
            }
            OPTIONAL {
                ?contract freelance:involvesPayment ?payment .
                ?payment freelance:amount ?paymentAmount ;
                        freelance:status ?paymentStatus .
            }
        }
    """;
}