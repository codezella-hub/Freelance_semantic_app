package tn.esprit.freelance.MissionApplication.service;

import org.apache.jena.query.*;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.esprit.freelance.MissionApplication.dto.ApplicationDto;
import tn.esprit.freelance.MissionApplication.dto.Mission;

import java.util.ArrayList;
import java.util.List;

@Service
public class SparqlService {

    @Value("${fuseki.queryEndpoint}")
    private String queryEndpoint;

    @Value("${fuseki.updateEndpoint}")
    private String updateEndpoint;

    private static final String PREFIXES = String.join("\n",
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>",
            "PREFIX onto: <http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#>");

    // ===== Missions =====
    public List<Mission> getAllMissions() {
        String select = PREFIXES + "\n" +
                "SELECT ?m ?titre ?desc ?budget ?status WHERE {\n" +
                "  ?m rdf:type onto:Mission .\n" +
                "  OPTIONAL { ?m onto:title ?titre }\n" +
                "  OPTIONAL { ?m onto:description ?desc }\n" +
                "  OPTIONAL { ?m onto:budget ?budget }\n" +
                "  OPTIONAL { ?m onto:status ?status }\n" +
                "}";

        List<Mission> result = new ArrayList<>();
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(queryEndpoint, select)) {
            ResultSet rs = qexec.execSelect();
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                String uri = qs.getResource("m").getURI();
                String titre = qs.contains("titre") ? qs.get("titre").asLiteral().getString() : null;
                String desc = qs.contains("desc") ? qs.get("desc").asLiteral().getString() : null;
                Double budget = qs.contains("budget") ? qs.getLiteral("budget").getDouble() : null;
                String status = qs.contains("status") ? qs.getLiteral("status").getString() : null;
                result.add(new Mission(uri, titre, desc, budget, status));
            }
        }
        return result;
    }

    public void addMission(Mission m) {
        // Assume provided id is a full URI; if null, use a base and a UUID
        String missionUri = m.getId();
        if (missionUri == null || missionUri.isBlank()) {
            missionUri = "http://example.com/freelance#Mission-" + java.util.UUID.randomUUID();
        }
        String update = PREFIXES + "\n" +
                "INSERT DATA {\n" +
                "  <" + missionUri + "> rdf:type onto:Mission ;\n" +
                (m.getTitre() != null ? "  onto:title \"" + escape(m.getTitre()) + "\" ;\n" : "") +
                (m.getDescription() != null ? "  onto:description \"" + escape(m.getDescription()) + "\" ;\n" : "") +
                (m.getBudget() != null ? "  onto:budget \"" + m.getBudget() + "\"^^xsd:decimal ;\n" : "") +
                (m.getStatut() != null ? "  onto:status \"" + escape(m.getStatut()) + "\" ;\n" : "") +
                "  .\n" +
                "}";
        runUpdate(update);
        m.setId(missionUri);
    }

    public void updateMission(Mission m) {
        String missionUri = m.getId();
        if (missionUri == null || missionUri.isBlank()) return;
        String update = PREFIXES + "\n" +
                "DELETE {\n" +
                "  <" + missionUri + "> onto:title ?t ; onto:description ?d ; onto:budget ?b ; onto:status ?s .\n" +
                "}\n" +
                "INSERT {\n" +
                (m.getTitre() != null ? "  <" + missionUri + "> onto:title \"" + escape(m.getTitre()) + "\" .\n" : "") +
                (m.getDescription() != null ? "  <" + missionUri + "> onto:description \"" + escape(m.getDescription()) + "\" .\n" : "") +
                (m.getBudget() != null ? "  <" + missionUri + "> onto:budget \"" + m.getBudget() + "\"^^xsd:decimal .\n" : "") +
                (m.getStatut() != null ? "  <" + missionUri + "> onto:status \"" + escape(m.getStatut()) + "\" .\n" : "") +
                "}\n" +
                "WHERE {\n" +
                "  OPTIONAL { <" + missionUri + "> onto:title ?t }\n" +
                "  OPTIONAL { <" + missionUri + "> onto:description ?d }\n" +
                "  OPTIONAL { <" + missionUri + "> onto:budget ?b }\n" +
                "  OPTIONAL { <" + missionUri + "> onto:status ?s }\n" +
                "}";
        runUpdate(update);
    }

    public void deleteMission(String missionUri) {
        if (missionUri == null || missionUri.isBlank()) return;
        String update = PREFIXES + "\n" +
                "DELETE WHERE { <" + missionUri + "> ?p ?o }";
        runUpdate(update);
    }

    public List<Mission> searchMissions(String query, String status) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIXES).append("\n");
        sb.append("SELECT ?m ?titre ?desc ?budget ?status WHERE {\n");
        sb.append("  ?m rdf:type onto:Mission .\n");
        sb.append("  OPTIONAL { ?m onto:title ?titre }\n");
        sb.append("  OPTIONAL { ?m onto:description ?desc }\n");
        sb.append("  OPTIONAL { ?m onto:budget ?budget }\n");
        sb.append("  OPTIONAL { ?m onto:status ?status }\n");
        if (query != null && !query.isBlank()) {
            String escaped = escape(query);
            sb.append("  FILTER(\n");
            sb.append("    (BOUND(?titre) && regex(str(?titre), \"").append(escaped).append("\", \"i\")) ||\n");
            sb.append("    (BOUND(?desc) && regex(str(?desc), \"").append(escaped).append("\", \"i\"))\n");
            sb.append("  )\n");
        }
        if (status != null && !status.isBlank()) {
            sb.append("  FILTER(BOUND(?status) && str(?status) = \"").append(escape(status)).append("\")\n");
        }
        sb.append("}\n");

        List<Mission> result = new ArrayList<>();
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(queryEndpoint, sb.toString())) {
            ResultSet rs = qexec.execSelect();
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                String uri = qs.getResource("m").getURI();
                String titre = qs.contains("titre") ? qs.getLiteral("titre").getString() : null;
                String desc = qs.contains("desc") ? qs.getLiteral("desc").getString() : null;
                Double b = qs.contains("budget") ? qs.getLiteral("budget").getDouble() : null;
                String s = qs.contains("status") ? qs.getLiteral("status").getString() : null;
                result.add(new Mission(uri, titre, desc, b, s));
            }
        }
        return result;
    }

    // ===== Applications =====
    public List<ApplicationDto> getAllApplications() {
        String select = PREFIXES + "\n" +
                "SELECT ?a ?status ?date ?mission ?applicant WHERE {\n" +
                "  ?a rdf:type onto:Application .\n" +
                "  OPTIONAL { ?a onto:status ?status }\n" +
                "  OPTIONAL { ?a onto:applicationDate ?date }\n" +
                "  OPTIONAL { ?a onto:appliedTo ?mission }\n" +
                "  OPTIONAL { ?a onto:submittedBy ?applicant }\n" +
                "}";

        List<ApplicationDto> result = new ArrayList<>();
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(queryEndpoint, select)) {
            ResultSet rs = qexec.execSelect();
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                String uri = qs.getResource("a").getURI();
                String status = qs.contains("status") ? qs.getLiteral("status").getString() : null;
                String date = qs.contains("date") ? qs.getLiteral("date").getString() : null;
                String mission = qs.contains("mission") ? qs.getResource("mission").getURI() : null;
                String applicant = qs.contains("applicant") ? qs.getResource("applicant").getURI() : null;
                result.add(new ApplicationDto(uri, status, date, mission, applicant));
            }
        }
        return result;
    }

    public List<ApplicationDto> searchApplications(String status, String missionUri) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIXES).append("\n");
        sb.append("SELECT ?a ?status ?date ?mission ?applicant WHERE {\n");
        sb.append("  ?a rdf:type onto:Application .\n");
        sb.append("  OPTIONAL { ?a onto:status ?status }\n");
        sb.append("  OPTIONAL { ?a onto:applicationDate ?date }\n");
        sb.append("  OPTIONAL { ?a onto:appliedTo ?mission }\n");
        sb.append("  OPTIONAL { ?a onto:submittedBy ?applicant }\n");
        if (status != null && !status.isBlank()) {
            sb.append("  FILTER(BOUND(?status) && str(?status) = \"").append(escape(status)).append("\")\n");
        }
        if (missionUri != null && !missionUri.isBlank()) {
            sb.append("  FILTER(BOUND(?mission) && str(?mission) = \"").append(escape(missionUri)).append("\")\n");
        }
        sb.append("}\n");

        List<ApplicationDto> result = new ArrayList<>();
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(queryEndpoint, sb.toString())) {
            ResultSet rs = qexec.execSelect();
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                String uri = qs.getResource("a").getURI();
                String s = qs.contains("status") ? qs.getLiteral("status").getString() : null;
                String date = qs.contains("date") ? qs.getLiteral("date").getString() : null;
                String mission = qs.contains("mission") ? qs.getResource("mission").getURI() : null;
                String applicant = qs.contains("applicant") ? qs.getResource("applicant").getURI() : null;
                result.add(new ApplicationDto(uri, s, date, mission, applicant));
            }
        }
        return result;
    }

    public void addApplication(ApplicationDto a) {
        String appUri = a.getId();
        if (appUri == null || appUri.isBlank()) {
            appUri = "http://example.com/freelance#Application-" + java.util.UUID.randomUUID();
        }
        String update = PREFIXES + "\n" +
                "INSERT DATA {\n" +
                "  <" + appUri + "> rdf:type onto:Application ;\n" +
                (a.getStatus() != null ? "  onto:status \"" + escape(a.getStatus()) + "\" ;\n" : "") +
                (a.getDate() != null ? "  onto:applicationDate \"" + escape(a.getDate()) + "\" ;\n" : "") +
                (a.getMissionUri() != null ? "  onto:appliedTo <" + a.getMissionUri() + "> ;\n" : "") +
                (a.getApplicantUri() != null ? "  onto:submittedBy <" + a.getApplicantUri() + "> ;\n" : "") +
                "  .\n" +
                "}";
        runUpdate(update);
        a.setId(appUri);
    }

    public void updateApplication(ApplicationDto a) {
        String appUri = a.getId();
        if (appUri == null || appUri.isBlank()) return;
        String update = PREFIXES + "\n" +
                "DELETE {\n" +
                "  <" + appUri + "> onto:status ?s ; onto:applicationDate ?d ; onto:appliedTo ?cm ; onto:submittedBy ?ap .\n" +
                "}\n" +
                "INSERT {\n" +
                (a.getStatus() != null ? "  <" + appUri + "> onto:status \"" + escape(a.getStatus()) + "\" .\n" : "") +
                (a.getDate() != null ? "  <" + appUri + "> onto:applicationDate \"" + escape(a.getDate()) + "\" .\n" : "") +
                (a.getMissionUri() != null ? "  <" + appUri + "> onto:appliedTo <" + a.getMissionUri() + "> .\n" : "") +
                (a.getApplicantUri() != null ? "  <" + appUri + "> onto:submittedBy <" + a.getApplicantUri() + "> .\n" : "") +
                "}\n" +
                "WHERE {\n" +
                "  OPTIONAL { <" + appUri + "> onto:status ?s }\n" +
                "  OPTIONAL { <" + appUri + "> onto:applicationDate ?d }\n" +
                "  OPTIONAL { <" + appUri + "> onto:appliedTo ?cm }\n" +
                "  OPTIONAL { <" + appUri + "> onto:submittedBy ?ap }\n" +
                "}";
        runUpdate(update);
    }

    public void deleteApplication(String applicationUri) {
        if (applicationUri == null || applicationUri.isBlank()) return;
        String update = PREFIXES + "\n" +
                "DELETE WHERE { <" + applicationUri + "> ?p ?o }";
        runUpdate(update);
    }

    private void runUpdate(String updateString) {
        UpdateProcessor processor = UpdateExecutionFactory.createRemote(
                UpdateFactory.create(updateString), updateEndpoint);
        processor.execute();
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}


