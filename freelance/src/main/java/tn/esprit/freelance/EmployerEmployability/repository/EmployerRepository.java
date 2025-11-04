package tn.esprit.freelance.EmployerEmployability.repository;

import lombok.RequiredArgsConstructor;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.springframework.stereotype.Repository;
import tn.esprit.freelance.EmployerEmployability.dto.EmployerDto;
import tn.esprit.freelance.EmployerEmployability.sparql.SparqlClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EmployerRepository {

    private final SparqlClient sparql;
    private final String baseNs = "http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#";

    // ✅ Lister tous les employeurs + employabilité liée
    public List<EmployerDto> findAll() {
        String q = """
            SELECT ?id ?finalType ?name ?email ?phone ?empId ?score WHERE {
              ?e a base:Employer .
              OPTIONAL { 
                  ?e a ?t .
                  FILTER(?t IN (base:Company, base:Individual)) 
                  BIND(STRAFTER(STR(?t), "#") AS ?typeLabel)
              }
              OPTIONAL { ?e base:companyName ?name }
              OPTIONAL { ?e base:contactEmail ?email }
              OPTIONAL { ?e base:phoneNumber ?phone }
              OPTIONAL { ?e base:hasEmployability ?emp .
                         BIND(STRAFTER(STR(?emp), "#") AS ?empId)
                         OPTIONAL { ?emp base:employabilityScore ?score }
              }
              BIND(STRAFTER(STR(?e), "#") AS ?id)
              BIND(coalesce(?typeLabel, "Employer") AS ?finalType)
            }
        """;

        ResultSet rs = sparql.select(q);
        List<EmployerDto> out = new ArrayList<>();

        while (rs.hasNext()) {
            QuerySolution s = rs.next();
            EmployerDto d = new EmployerDto();

            d.setId(getStringValue(s, "id"));
            d.setType(getStringValue(s, "finalType"));
            d.setCompanyName(getStringValue(s, "name"));
            d.setEmail(getStringValue(s, "email"));
            d.setPhoneNumber(getStringValue(s, "phone"));
            d.setEmployabilityId(getStringValue(s, "empId"));

            // ✅ Nouveau champ : afficher le score d’employabilité (optionnel)
            if (s.contains("score") && s.get("score").isLiteral()) {
                d.setEmployabilityScore(s.getLiteral("score").getDouble());
            }

            out.add(d);
        }
        return out;
    }

    // ✅ Création d’un employer avec ou sans employabilité
    public EmployerDto create(EmployerDto in) {
        String id = (in.getId() == null || in.getId().isBlank())
                ? "Employer_" + UUID.randomUUID().toString().replace("-", "")
                : in.getId();

        String type = (in.getType() != null && in.getType().equalsIgnoreCase("Individual"))
                ? "Individual" : "Company";

        String employabilityTriple = "";
        if (in.getEmployabilityId() != null && !in.getEmployabilityId().isBlank()) {
            employabilityTriple = "base:hasEmployability base:" + in.getEmployabilityId() + " ;";
        }

        String up = """
        INSERT DATA {
          base:%s a base:Employer , base:%s ;
                   %s
                   %s
        }
    """.formatted(
                id,
                type,
                employabilityTriple,
                buildDataProps(
                        "base:companyName", in.getCompanyName(),
                        "base:contactEmail", in.getEmail(),
                        "base:phoneNumber", in.getPhoneNumber()
                )
        );

        sparql.update(up);

        // ✅ Recharger l’objet complet depuis Fuseki
        return findById(id);
    }


    // ✅ Récupérer un employeur par ID
    public EmployerDto findById(String id) {
        String q = """
            SELECT ?id ?finalType ?name ?email ?phone ?empId ?score WHERE {
              BIND(base:%s AS ?e)
              ?e a base:Employer .
              OPTIONAL { 
                  ?e a ?t .
                  FILTER(?t IN (base:Company, base:Individual))
                  BIND(STRAFTER(STR(?t), "#") AS ?typeLabel)
              }
              OPTIONAL { ?e base:companyName ?name }
              OPTIONAL { ?e base:contactEmail ?email }
              OPTIONAL { ?e base:phoneNumber ?phone }
              OPTIONAL { ?e base:hasEmployability ?emp .
                         BIND(STRAFTER(STR(?emp), "#") AS ?empId)
                         OPTIONAL { ?emp base:employabilityScore ?score }
              }
              BIND(STRAFTER(STR(?e), "#") AS ?id)
              BIND(COALESCE(?typeLabel, "Employer") AS ?finalType)
            }
        """.formatted(id);

        ResultSet rs = sparql.select(q);
        if (!rs.hasNext()) return null;

        QuerySolution s = rs.next();
        EmployerDto d = new EmployerDto();
        d.setId(getStringValue(s, "id"));
        d.setType(getStringValue(s, "finalType"));
        d.setCompanyName(getStringValue(s, "name"));
        d.setEmail(getStringValue(s, "email"));
        d.setPhoneNumber(getStringValue(s, "phone"));
        d.setEmployabilityId(getStringValue(s, "empId"));

        if (s.contains("score") && s.get("score").isLiteral()) {
            d.setEmployabilityScore(s.getLiteral("score").getDouble());
        }

        return d;
    }

    public EmployerDto update(String id, EmployerDto in) {
        delete(id);
        in.setId(id);
        return create(in);
    }

    public void delete(String id) {
        String up = """
            DELETE { ?s ?p ?o }
            WHERE  { BIND(base:%s AS ?s) ?s ?p ?o }
        """.formatted(id);
        sparql.update(up);
    }

    // 🔧 utilitaires
    private static String buildDataProps(Object... kv) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < kv.length; i += 2) {
            String prop = (String) kv[i];
            String val = (String) kv[i + 1];
            if (val != null && !val.isBlank()) {
                b.append(prop).append(" \"").append(escape(val)).append("\" ;\n");
            }
        }
        String s = b.toString().trim();
        if (s.endsWith(";")) s = s.substring(0, s.length() - 1) + ".";
        else if (!s.endsWith(".")) s = s + ".";
        return s;
    }

    private static String escape(String v) {
        return v.replace("\"", "\\\"");
    }

    private static String getStringValue(QuerySolution s, String var) {
        if (!s.contains(var)) return null;
        if (s.get(var).isLiteral()) return s.getLiteral(var).getString();
        return s.get(var).toString();
    }
    // 1) Lister les employeurs triés par employabilité (DESC), avec limite
    public List<EmployerDto> findTopByScore(int limit) {
        String q = """
        PREFIX base: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

        SELECT ?id ?finalType ?name ?email ?phone ?score WHERE {
          ?e a base:Employer ;
             base:hasEmployability ?emp .
          OPTIONAL { 
              ?e a ?t .
              FILTER(?t IN (base:Company, base:Individual))
              BIND(STRAFTER(STR(?t), "#") AS ?typeLabel)
          }
          OPTIONAL { ?e base:companyName ?name }
          OPTIONAL { ?e base:contactEmail ?email }
          OPTIONAL { ?e base:phoneNumber ?phone }
          ?emp base:employabilityScore ?score .
          BIND(STRAFTER(STR(?e), "#") AS ?id)
          BIND(coalesce(?typeLabel, "Employer") AS ?finalType)
        }
        ORDER BY DESC(xsd:decimal(?score))
        LIMIT %d
    """.formatted(limit);

        ResultSet rs = sparql.select(q);
        List<EmployerDto> out = new ArrayList<>();

        while (rs.hasNext()) {
            QuerySolution s = rs.next();
            EmployerDto d = new EmployerDto();
            d.setId(getStringValue(s, "id"));
            d.setType(getStringValue(s, "finalType"));
            d.setCompanyName(getStringValue(s, "name"));
            d.setEmail(getStringValue(s, "email"));
            d.setPhoneNumber(getStringValue(s, "phone"));
            if (s.contains("score") && s.get("score").isLiteral()) {
                d.setEmployabilityScore(s.getLiteral("score").getDouble());
            }
            out.add(d);
        }
        return out;
    }


    // 2) Rechercher tous les employeurs dont le score > minScore
    public List<EmployerDto> findByMinScore(double minScore) {
        String q = """
        PREFIX base: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

        SELECT ?id ?finalType ?name ?email ?phone ?score WHERE {
          ?e a base:Employer ;
             base:hasEmployability ?emp .
          OPTIONAL { 
              ?e a ?t .
              FILTER(?t IN (base:Company, base:Individual))
              BIND(STRAFTER(STR(?t), "#") AS ?typeLabel)
          }
          OPTIONAL { ?e base:companyName ?name }
          OPTIONAL { ?e base:contactEmail ?email }
          OPTIONAL { ?e base:phoneNumber ?phone }
          ?emp base:employabilityScore ?score .
          FILTER(xsd:decimal(?score) > %f)
          BIND(STRAFTER(STR(?e), "#") AS ?id)
          BIND(coalesce(?typeLabel, "Employer") AS ?finalType)
        }
        ORDER BY DESC(xsd:decimal(?score))
    """.formatted(minScore);

        ResultSet rs = sparql.select(q);
        List<EmployerDto> out = new ArrayList<>();

        while (rs.hasNext()) {
            QuerySolution s = rs.next();
            EmployerDto d = new EmployerDto();
            d.setId(getStringValue(s, "id"));
            d.setType(getStringValue(s, "finalType"));
            d.setCompanyName(getStringValue(s, "name"));
            d.setEmail(getStringValue(s, "email"));
            d.setPhoneNumber(getStringValue(s, "phone"));
            if (s.contains("score") && s.get("score").isLiteral()) {
                d.setEmployabilityScore(s.getLiteral("score").getDouble());
            }
            out.add(d);
        }
        return out;
    }


    // 3) Moyenne des scores par type d’employer (Company / Individual / Employer par défaut)
    public List<TypeAverage> avgScoreByEmployerType() {
        String q = """
        SELECT ?type (AVG(xsd:decimal(?score)) AS ?avgScore) WHERE {
          ?e a base:Employer ;
             base:hasEmployability ?emp .
          ?emp base:employabilityScore ?score .
          OPTIONAL { ?e a ?t . FILTER(?t IN (base:Company, base:Individual)) }
          BIND(COALESCE(?t, base:Employer) AS ?t2)
          BIND(STRAFTER(STR(?t2), "#") AS ?type)
        }
        GROUP BY ?type
        ORDER BY ?type
    """;

        ResultSet rs = sparql.select(q);
        List<TypeAverage> out = new ArrayList<>();
        while (rs.hasNext()) {
            QuerySolution s = rs.next();
            TypeAverage ta = new TypeAverage();
            ta.setType(s.getLiteral("type").getString());          // Company / Individual / Employer
            ta.setAvgScore(s.getLiteral("avgScore").getDouble());  // moyenne
            out.add(ta);
        }
        return out;
    }
    // === 4️⃣ Inférence : détecter les employeurs à haut potentiel ===
    public void inferHighPotentialEmployers() {
        String update = """
        PREFIX base: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

        INSERT {
          ?e a base:HighPotentialEmployer .
        }
        WHERE {
          ?e a base:Employer ;
             base:hasEmployability ?emp .
          ?emp base:employabilityScore ?score .
          FILTER(xsd:decimal(?score) > 85)
        }
    """;

        sparql.update(update);
    }
    public void inferLowPotentialEmployers() {
        String update = """
        PREFIX base: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

        INSERT {
          ?e a base:LowPotentialEmployer .
        }
        WHERE {
          ?e a base:Employer ;
             base:hasEmployability ?emp .
          ?emp base:employabilityScore ?score .
          FILTER(xsd:decimal(?score) < 60)
        }
    """;
        sparql.update(update);
    }
    public List<EmployerDto> findEmployersByTypeClass(String typeClass) {
        String q = """
        PREFIX base: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        SELECT ?id ?name ?email ?phone ?score WHERE {
          ?e a base:%s ;
             base:hasEmployability ?emp .
          OPTIONAL { ?e base:companyName ?name }
          OPTIONAL { ?e base:contactEmail ?email }
          OPTIONAL { ?e base:phoneNumber ?phone }
          OPTIONAL { ?emp base:employabilityScore ?score }
          BIND(STRAFTER(STR(?e), "#") AS ?id)
        }
    """.formatted(typeClass);

        ResultSet rs = sparql.select(q);
        List<EmployerDto> out = new ArrayList<>();
        while (rs.hasNext()) {
            QuerySolution s = rs.next();
            EmployerDto d = new EmployerDto();
            d.setId(getStringValue(s, "id"));
            d.setCompanyName(getStringValue(s, "name"));
            d.setEmail(getStringValue(s, "email"));
            d.setPhoneNumber(getStringValue(s, "phone"));
            if (s.contains("score") && s.get("score").isLiteral())
                d.setEmployabilityScore(s.getLiteral("score").getDouble());
            out.add(d);
        }
        return out;
    }


    // petit POJO interne au repo (ou mets-le dans un package dto/vo)
    public static class TypeAverage {
        private String type;
        private Double avgScore;
        // getters/setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Double getAvgScore() { return avgScore; }
        public void setAvgScore(Double avgScore) { this.avgScore = avgScore; }

    }


}
