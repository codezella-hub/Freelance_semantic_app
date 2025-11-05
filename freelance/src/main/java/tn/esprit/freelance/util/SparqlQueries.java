package tn.esprit.freelance.util;

public class SparqlQueries {

    // Contract Queries
    public static final String GET_ALL_CONTRACTS = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX owl: <http://www.w3.org/2002/07/owl#>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
        PREFIX freelance: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        
        SELECT ?contract ?title ?description ?startDate ?endDate ?amount ?status
        WHERE {
            ?contract rdf:type freelance:Contract ;
                     freelance:title ?title ;
                     freelance:description ?description ;
                     freelance:startDate ?startDate ;
                     freelance:endDate ?endDate ;
                     freelance:amount ?amount ;
                     freelance:status ?status .
        }
    """;

    public static final String GET_CONTRACT_BY_ID = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX freelance: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        
        SELECT ?title ?description ?startDate ?endDate ?amount ?status
        WHERE {
            <%s> rdf:type freelance:Contract ;
                 freelance:title ?title ;
                 freelance:description ?description ;
                 freelance:startDate ?startDate ;
                 freelance:endDate ?endDate ;
                 freelance:amount ?amount ;
                 freelance:status ?status .
        }
    """;

    public static final String CREATE_CONTRACT = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX freelance: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        
        INSERT DATA {
            <%s> rdf:type freelance:Contract ;
                 freelance:title "%s" ;
                 freelance:description "%s" ;
                 freelance:startDate "%s"^^xsd:date ;
                 freelance:endDate "%s"^^xsd:date ;
                 freelance:amount "%s"^^xsd:decimal ;
                 freelance:status "%s" .
        }
    """;

    public static final String UPDATE_CONTRACT = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX freelance: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        
        DELETE {
            <%s> freelance:title ?title ;
                 freelance:description ?description ;
                 freelance:startDate ?startDate ;
                 freelance:endDate ?endDate ;
                 freelance:amount ?amount ;
                 freelance:status ?status .
        }
        INSERT {
            <%s> freelance:title "%s" ;
                 freelance:description "%s" ;
                 freelance:startDate "%s"^^xsd:date ;
                 freelance:endDate "%s"^^xsd:date ;
                 freelance:amount "%s"^^xsd:decimal ;
                 freelance:status "%s" .
        }
        WHERE {
            <%s> freelance:title ?title ;
                 freelance:description ?description ;
                 freelance:startDate ?startDate ;
                 freelance:endDate ?endDate ;
                 freelance:amount ?amount ;
                 freelance:status ?status .
        }
    """;

    public static final String DELETE_CONTRACT = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX freelance: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        
        DELETE WHERE {
            <%s> ?p ?o .
        }
    """;

    // Payment Queries
    public static final String GET_ALL_PAYMENTS = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX owl: <http://www.w3.org/2002/07/owl#>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
        PREFIX freelance: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        
        SELECT ?payment ?amount ?paymentDate ?status ?paymentMethod ?contract
        WHERE {
            ?payment rdf:type freelance:Payment ;
                     freelance:amount ?amount ;
                     freelance:paymentDate ?paymentDate ;
                     freelance:status ?status ;
                     freelance:paymentMethod ?paymentMethod .
            OPTIONAL { ?contract freelance:involvesPayment ?payment }
        }
    """;

    public static final String CREATE_PAYMENT = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX freelance: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        
        INSERT DATA {
            <%s> rdf:type freelance:Payment ;
                 freelance:amount "%s"^^xsd:decimal ;
                 freelance:paymentDate "%s"^^xsd:dateTime ;
                 freelance:status "%s" ;
                 freelance:paymentMethod "%s" .
            <%s> freelance:involvesPayment <%s> .
        }
    """;

    public static final String UPDATE_PAYMENT = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX freelance: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        
        DELETE {
            <%s> freelance:amount ?amount ;
                 freelance:paymentDate ?paymentDate ;
                 freelance:status ?status ;
                 freelance:paymentMethod ?paymentMethod .
            ?contract freelance:involvesPayment <%s> .
        }
        INSERT {
            <%s> freelance:amount "%s"^^xsd:decimal ;
                 freelance:paymentDate "%s"^^xsd:dateTime ;
                 freelance:status "%s" ;
                 freelance:paymentMethod "%s" .
            <%s> freelance:involvesPayment <%s> .
        }
        WHERE {
            <%s> freelance:amount ?amount ;
                 freelance:paymentDate ?paymentDate ;
                 freelance:status ?status ;
                 freelance:paymentMethod ?paymentMethod .
            OPTIONAL { ?contract freelance:involvesPayment <%s> }
        }
    """;

    public static final String DELETE_PAYMENT = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX freelance: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        
        DELETE {
            <%s> ?p ?o .
            ?s freelance:involvesPayment <%s> .
        }
        WHERE {
            <%s> ?p ?o .
            OPTIONAL { ?s freelance:involvesPayment <%s> }
        }
    """;
}