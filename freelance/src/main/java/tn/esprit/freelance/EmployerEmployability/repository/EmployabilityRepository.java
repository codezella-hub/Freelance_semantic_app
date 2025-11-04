package tn.esprit.freelance.EmployerEmployability.repository;

import lombok.RequiredArgsConstructor;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.springframework.stereotype.Repository;
import tn.esprit.freelance.EmployerEmployability.dto.EmployabilityDto;
import tn.esprit.freelance.EmployerEmployability.sparql.SparqlClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EmployabilityRepository {

    private final SparqlClient sparql;

    public List<EmployabilityDto> findAll() {
        String q = """
            SELECT ?id ?finalKind ?score WHERE {
              ?e a base:Employability .
              
              OPTIONAL { 
                  ?e a ?k . 
                  FILTER(?k IN (base:ExperienceLevel, base:MarketDemandScore)) 
                  BIND(STRAFTER(STR(?k), "#") AS ?kindLabel)
              }
              
              OPTIONAL { ?e base:employabilityScore ?score }
              BIND(STRAFTER(STR(?e), "#") AS ?id)
              
              # valeur par défaut si aucune catégorie
              BIND(COALESCE(?kindLabel, "Employability") AS ?finalKind)
            }
        """;

        ResultSet rs = sparql.select(q);
        List<EmployabilityDto> out = new ArrayList<>();

        while (rs.hasNext()) {
            QuerySolution s = rs.next();
            EmployabilityDto d = new EmployabilityDto();

            // ✅ Sécurité pour éviter NullPointerException
            d.setId(getStringValue(s, "id"));
            d.setKind(getStringValue(s, "finalKind"));

            if (s.contains("score") && s.get("score").isLiteral()) {
                d.setEmployabilityScore(s.getLiteral("score").getDouble());
            }

            out.add(d);
        }
        return out;
    }

    public EmployabilityDto create(EmployabilityDto in) {
        String id = (in.getId() == null || in.getId().isBlank())
                ? "Emp_" + UUID.randomUUID().toString().replace("-", "")
                : in.getId();

        String kind = (in.getKind() != null && in.getKind().equalsIgnoreCase("MarketDemandScore"))
                ? "MarketDemandScore" : "ExperienceLevel"; // valeur par défaut

        String literalScore = (in.getEmployabilityScore() != null)
                ? "\"" + in.getEmployabilityScore() + "\"^^xsd:decimal"
                : null;

        // ✅ Structure RDF correcte et sûre
        String up = """
            INSERT DATA {
              base:%s a base:Employability , base:%s ;
                       %s
            }
        """.formatted(
                id,
                kind,
                (literalScore != null
                        ? "base:employabilityScore " + literalScore + " ."
                        : ".")
        );

        sparql.update(up);
        in.setId(id);
        in.setKind(kind);
        return in;
    }

    public void delete(String id) {
        String up = """
            DELETE { ?s ?p ?o }
            WHERE  { BIND(base:%s AS ?s) ?s ?p ?o }
        """.formatted(id);
        sparql.update(up);
    }


    private static String getStringValue(QuerySolution s, String var) {
        if (!s.contains(var)) return null;
        if (s.get(var).isLiteral()) return s.getLiteral(var).getString();
        return s.get(var).toString();
    }
    public EmployabilityDto findById(String id) {
        String q = """
        SELECT ?id ?finalKind ?score WHERE {
          BIND(base:%s AS ?e)
          ?e a base:Employability .
          OPTIONAL { 
              ?e a ?k . 
              FILTER(?k IN (base:ExperienceLevel, base:MarketDemandScore))
              BIND(STRAFTER(STR(?k), "#") AS ?kindLabel)
          }
          OPTIONAL { ?e base:employabilityScore ?score }
          BIND(STRAFTER(STR(?e), "#") AS ?id)
          BIND(COALESCE(?kindLabel, "Employability") AS ?finalKind)
        }
    """.formatted(id);

        ResultSet rs = sparql.select(q);
        if (!rs.hasNext()) return null;

        QuerySolution s = rs.next();
        EmployabilityDto d = new EmployabilityDto();
        d.setId(getStringValue(s, "id"));
        d.setKind(getStringValue(s, "finalKind"));
        if (s.contains("score") && s.get("score").isLiteral())
            d.setEmployabilityScore(s.getLiteral("score").getDouble());
        return d;
    }

    public EmployabilityDto update(String id, EmployabilityDto in) {
        delete(id);
        in.setId(id);
        return create(in);
    }


}
