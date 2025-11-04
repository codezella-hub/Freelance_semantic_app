// repository/EvaluationRepository.java
package tn.esprit.freelance.ProjetsEvaluation.repository;

import lombok.RequiredArgsConstructor;
import org.apache.jena.query.*;
import org.springframework.stereotype.Repository;
import tn.esprit.freelance.ProjetsEvaluation.dto.EvaluationDto;
import tn.esprit.freelance.ProjetsEvaluation.sparql.SparqlClient;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class EvaluationRepository {

    private final SparqlClient sparql;

    // 🔍 Liste toutes les évaluations RDF depuis Fuseki
    public List<EvaluationDto> findAll() {
        String q = """
            SELECT ?e ?id ?finalType ?score ?comment ?date WHERE {
              ?e a base:Evaluation .
              OPTIONAL { 
                ?e a ?t .
                FILTER(?t IN (base:ClientReview, base:PeerReview))
                BIND(STRAFTER(STR(?t), "#") AS ?rawType)
              }
              OPTIONAL { ?e base:score ?score }
              OPTIONAL { ?e base:comment ?comment }
              OPTIONAL { ?e base:evaluationDate ?date }
              BIND(STRAFTER(STR(?e), "#") AS ?id)
              BIND(COALESCE(?rawType, "Evaluation") AS ?finalType)
            }
        """;

        ResultSet rs = sparql.select(q);
        List<EvaluationDto> list = new ArrayList<>();

        while (rs.hasNext()) {
            QuerySolution sol = rs.next();
            EvaluationDto dto = new EvaluationDto();

            if (sol.contains("id"))
                dto.setId(sol.getLiteral("id").getString());

            if (sol.contains("finalType"))
                dto.setType(sol.getLiteral("finalType").getString());
            else
                dto.setType("Evaluation");

            if (sol.contains("score"))
                dto.setScore(sol.get("score").asLiteral().getDouble());

            if (sol.contains("comment"))
                dto.setComment(sol.getLiteral("comment").getString());

            if (sol.contains("date"))
                dto.setEvaluationDate(sol.getLiteral("date").getString());

            list.add(dto);
        }

        return list;
    }

    // ➕ Créer une évaluation RDF
    public EvaluationDto create(EvaluationDto in) {
        String id = (in.getId() == null)
                ? "Eval_" + UUID.randomUUID().toString().replace("-", "")
                : in.getId();

        String type = (in.getType() != null && in.getType().equalsIgnoreCase("PeerReview"))
                ? "PeerReview" : "ClientReview";

        StringBuilder triples = new StringBuilder();
        if (in.getScore() != null)
            triples.append("base:score \"").append(in.getScore()).append("\"^^xsd:float ; ");
        if (in.getComment() != null)
            triples.append("base:comment \"").append(in.getComment()).append("\" ; ");
        if (in.getEvaluationDate() != null)
            triples.append("base:evaluationDate \"").append(in.getEvaluationDate()).append("\"^^xsd:dateTime ; ");

        String up = String.format("""
            INSERT DATA {
              base:%s a base:Evaluation , base:%s ;
                       %s .
            }
        """, id, type, triples.toString());

        sparql.update(up);
        in.setId(id);
        in.setType(type);
        return in;
    }

    // ❌ Supprimer une évaluation RDF
    public void delete(String id) {
        String up = """
            DELETE { ?s ?p ?o }
            WHERE  { BIND(base:%s AS ?s) ?s ?p ?o }
        """.formatted(id);
        sparql.update(up);
    }
    // 🔍 Recherche par mot-clé (dans le commentaire ou le type)
    public List<EvaluationDto> search(String keyword) {
        String q = String.format("""
            SELECT ?e ?id ?type ?score ?comment ?date WHERE {
              ?e a base:Evaluation .
              OPTIONAL { ?e a ?t . FILTER(?t IN (base:ClientReview, base:PeerReview)) BIND(STRAFTER(STR(?t), "#") AS ?type) }
              OPTIONAL { ?e base:score ?score }
              OPTIONAL { ?e base:comment ?comment }
              OPTIONAL { ?e base:evaluationDate ?date }
              BIND(STRAFTER(STR(?e), "#") AS ?id)
              FILTER(
                CONTAINS(LCASE(STR(?comment)), LCASE("%s")) ||
                CONTAINS(LCASE(STR(?type)), LCASE("%s"))
              )
            }
        """, keyword, keyword);

        return mapResults(sparql.select(q));
    }

    // 🔽 Tri des évaluations par attribut (score, date, type)
    public List<EvaluationDto> sort(String sortBy, String order) {
        // Validation du champ
        if (!List.of("score", "date", "type").contains(sortBy))
            sortBy = "score"; // par défaut

        boolean asc = order == null || order.equalsIgnoreCase("asc");

        String q = String.format("""
            SELECT ?e ?id ?type ?score ?comment ?date WHERE {
              ?e a base:Evaluation .
              OPTIONAL { ?e a ?t . FILTER(?t IN (base:ClientReview, base:PeerReview)) BIND(STRAFTER(STR(?t), "#") AS ?type) }
              OPTIONAL { ?e base:score ?score }
              OPTIONAL { ?e base:comment ?comment }
              OPTIONAL { ?e base:evaluationDate ?date }
              BIND(STRAFTER(STR(?e), "#") AS ?id)
            }
            ORDER BY %s(?%s)
        """, asc ? "" : "DESC", sortBy);

        return mapResults(sparql.select(q));
    }

    // 📊 Méthode utilitaire — conversion des résultats SPARQL
    private List<EvaluationDto> mapResults(ResultSet rs) {
        List<EvaluationDto> list = new ArrayList<>();
        while (rs.hasNext()) {
            QuerySolution sol = rs.next();
            EvaluationDto dto = new EvaluationDto();
            if (sol.contains("id")) dto.setId(sol.getLiteral("id").getString());
            if (sol.contains("type")) dto.setType(sol.getLiteral("type").getString());
            if (sol.contains("score")) dto.setScore(sol.get("score").asLiteral().getDouble());
            if (sol.contains("comment")) dto.setComment(sol.getLiteral("comment").getString());
            if (sol.contains("date")) dto.setEvaluationDate(sol.getLiteral("date").getString());
            list.add(dto);
        }
        return list;
    }



}
