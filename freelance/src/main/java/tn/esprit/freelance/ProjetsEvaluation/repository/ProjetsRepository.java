package tn.esprit.freelance.ProjetsEvaluation.repository;

import lombok.RequiredArgsConstructor;
import org.apache.jena.query.*;
import org.springframework.stereotype.Repository;
import tn.esprit.freelance.ProjetsEvaluation.dto.ProjetsDto;
import tn.esprit.freelance.ProjetsEvaluation.sparql.SparqlClient1;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ProjetsRepository {

    private final SparqlClient1 sparql;

    // 🔹 Récupérer tous les projets avec leurs évaluations
    public List<ProjetsDto> findAll() {
        String q = """
            SELECT ?p ?id ?rawType ?title ?summary ?delivery ?eval WHERE {
              ?p a base:Project .
              OPTIONAL { ?p a ?t . 
                         FILTER(?t IN (base:CompletedProject, base:OngoingProject))
                         BIND(STRAFTER(STR(?t), "#") AS ?rawType) }
              OPTIONAL { ?p base:projectTitle ?title }
              OPTIONAL { ?p base:projectSummary ?summary }
              OPTIONAL { ?p base:deliveryDate ?delivery }
              OPTIONAL { ?p base:hasEvaluation ?eval }
              BIND(STRAFTER(STR(?p), "#") AS ?id)
            }
        """;

        ResultSet rs = sparql.select(q);
        Map<String, ProjetsDto> map = new HashMap<>();

        while (rs.hasNext()) {
            QuerySolution sol = rs.next();
            String id = sol.getLiteral("id").getString();
            ProjetsDto dto = map.getOrDefault(id, new ProjetsDto());
            dto.setId(id);

            if (sol.contains("rawType"))
                dto.setType(sol.getLiteral("rawType").getString());
            else
                dto.setType("Project");

            if (sol.contains("title"))
                dto.setProjectTitle(sol.getLiteral("title").getString());
            if (sol.contains("summary"))
                dto.setProjectSummary(sol.getLiteral("summary").getString());
            if (sol.contains("delivery"))
                dto.setDeliveryDate(sol.getLiteral("delivery").getString());

            if (sol.contains("eval")) {
                if (dto.getEvaluations() == null)
                    dto.setEvaluations(new ArrayList<>());
                dto.getEvaluations().add(sol.getResource("eval").getLocalName());
            }

            map.put(id, dto);
        }

        return new ArrayList<>(map.values());
    }

    // ➕ Créer un projet avec ses évaluations liées
    public ProjetsDto create(ProjetsDto in) {
        String id = (in.getId() == null)
                ? "Project_" + UUID.randomUUID().toString().replace("-", "")
                : in.getId();

        String type = (in.getType() != null && in.getType().equalsIgnoreCase("CompletedProject"))
                ? "CompletedProject"
                : "OngoingProject";

        StringBuilder triples = new StringBuilder();
        if (in.getProjectTitle() != null)
            triples.append("base:projectTitle \"").append(in.getProjectTitle()).append("\" ; ");
        if (in.getProjectSummary() != null)
            triples.append("base:projectSummary \"").append(in.getProjectSummary()).append("\" ; ");
        if (in.getDeliveryDate() != null)
            triples.append("base:deliveryDate \"").append(in.getDeliveryDate()).append("\"^^xsd:dateTime ; ");

        // ✅ Ajouter les liens vers les évaluations existantes
        if (in.getEvaluations() != null && !in.getEvaluations().isEmpty()) {
            for (String evalId : in.getEvaluations()) {
                triples.append("base:hasEvaluation base:").append(evalId).append(" ; ");
            }
        }

        String up = String.format("""
            INSERT DATA {
              base:%s a base:Project , base:%s ;
                       %s .
            }
        """, id, type, triples.toString());

        sparql.update(up);
        in.setId(id);
        in.setType(type);
        return in;
    }

    // ❌ Supprimer un projet (et son lien vers les évaluations)
    public void delete(String id) {
        String up = """
            DELETE { ?s ?p ?o }
            WHERE  { BIND(base:%s AS ?s) ?s ?p ?o }
        """.formatted(id);
        sparql.update(up);
    }
    // 🔍 Trouver des projets similaires (partageant des évaluations en commun)
    public List<ProjetsDto> findSimilarProjects(String projectId) {
        String q = String.format("""
        SELECT DISTINCT ?other ?id ?title ?summary ?type ?delivery WHERE {
          base:%s base:hasEvaluation ?eval .
          ?other base:hasEvaluation ?eval .
          FILTER(?other != base:%s)
          OPTIONAL { ?other base:projectTitle ?title }
          OPTIONAL { ?other base:projectSummary ?summary }
          OPTIONAL { ?other base:deliveryDate ?delivery }
          OPTIONAL { ?other a ?t .
                     FILTER(?t IN (base:CompletedProject, base:OngoingProject))
                     BIND(STRAFTER(STR(?t), "#") AS ?type) }
          BIND(STRAFTER(STR(?other), "#") AS ?id)
        }
    """, projectId, projectId);

        ResultSet rs = sparql.select(q);
        List<ProjetsDto> results = new ArrayList<>();

        while (rs.hasNext()) {
            QuerySolution sol = rs.next();
            ProjetsDto dto = new ProjetsDto();
            dto.setId(sol.getLiteral("id").getString());
            dto.setProjectTitle(sol.contains("title") ? sol.getLiteral("title").getString() : null);
            dto.setProjectSummary(sol.contains("summary") ? sol.getLiteral("summary").getString() : null);
            dto.setDeliveryDate(sol.contains("delivery") ? sol.getLiteral("delivery").getString() : null);
            dto.setType(sol.contains("type") ? sol.getLiteral("type").getString() : "Project");
            results.add(dto);
        }

        return results;
    }

    // 📊 Statistiques globales sur les projets
    public Map<String, Object> getProjectStats() {
        String q = """
        SELECT (COUNT(DISTINCT ?p) AS ?totalProjects)
               (COUNT(DISTINCT ?c) AS ?completedProjects)
               (AVG(?score) AS ?avgScore)
        WHERE {
          ?p a base:Project .
          OPTIONAL { ?p a base:CompletedProject . BIND(?p AS ?c) }
          OPTIONAL { ?p base:hasEvaluation ?e .
                     ?e base:score ?score }
        }
    """;

        ResultSet rs = sparql.select(q);
        Map<String, Object> stats = new HashMap<>();

        if (rs.hasNext()) {
            QuerySolution sol = rs.next();
            stats.put("totalProjects", sol.contains("totalProjects") ? sol.getLiteral("totalProjects").getInt() : 0);
            stats.put("completedProjects", sol.contains("completedProjects") ? sol.getLiteral("completedProjects").getInt() : 0);
            stats.put("avgScore", sol.contains("avgScore") ? sol.getLiteral("avgScore").getDouble() : 0.0);
        }

        return stats;
    }
    // repository/ProjetsRepository.java
    public void linkEvaluationToProject(String projectId, String evalId) {
        String update = String.format("""
        PREFIX base: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>
        INSERT DATA {
          base:%s base:hasEvaluation base:%s .
        }
    """, projectId, evalId);

        sparql.update(update);
    }


}
