package tn.esprit.freelance.louay.service;

import lombok.AllArgsConstructor;
import org.apache.jena.query.*;
import org.springframework.stereotype.Service;
import tn.esprit.freelance.louay.dto.FreelancerDTO;
import tn.esprit.freelance.louay.dto.SkillDTO;
import tn.esprit.freelance.louay.dto.StatsDTO;
import tn.esprit.freelance.louay.repository.FusekiRepository;

import java.util.*;

@Service
@AllArgsConstructor
public class FreelancerService {
   FusekiRepository fuseki;
   GeminiService geminiService;
    OllamaService ollamaService;


    private static final String PREFIX = """
        PREFIX : <http://example.com/freelance#>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
        """;


    public FreelancerDTO add(FreelancerDTO dto) {
        String freelancerId = "freelancer-" + UUID.randomUUID();
        dto.setId(freelancerId);

        StringBuilder insert = new StringBuilder(PREFIX + "INSERT DATA {\n");
        insert.append(":" + freelancerId + " a :Freelancer ;\n");
        insert.append("rdfs:label \"" + dto.getName() + "\" ;\n");
        insert.append(":experienceLevel \"" + dto.getExperienceLevel() + "\" .\n");

        if (dto.getSkills() != null) {
            for (SkillDTO skill : dto.getSkills()) {
                // ✅ Génération automatique d’un nouvel ID pour chaque skill
                String skillId = "skill-" + UUID.randomUUID();
                skill.setId(skillId);

                insert.append(":" + freelancerId + " :hasSkill :" + skillId + " .\n");
                insert.append(":" + skillId + " a :Skill ;\n");
                insert.append("rdfs:label \"" + skill.getName() + "\" ;\n");
                insert.append(":skillLevel \"" + skill.getLevel() + "\" .\n");
            }
        }

        insert.append("}");
        fuseki.update(insert.toString());
        return dto;
    }


    // ✅ Read all
    public List<FreelancerDTO> getAll() {
        String query = PREFIX + """
            SELECT ?freelancer ?name ?exp ?skill ?skillName ?skillLevel
            WHERE {
              ?freelancer a :Freelancer ;
                          rdfs:label ?name ;
                          :experienceLevel ?exp .
              OPTIONAL {
                ?freelancer :hasSkill ?skill .
                ?skill rdfs:label ?skillName .
                OPTIONAL { ?skill :skillLevel ?skillLevel . }
              }
            }
        """;

        ResultSet rs = fuseki.select(query);
        Map<String, FreelancerDTO> map = new HashMap<>();

        while (rs.hasNext()) {
            QuerySolution sol = rs.next();
            String uri = sol.get("freelancer").toString();
            String id = uri.substring(uri.indexOf("#") + 1);
            FreelancerDTO dto = map.computeIfAbsent(id,
                    k -> new FreelancerDTO(k, sol.get("name").toString(), sol.get("exp").toString(), new ArrayList<>()));

            if (sol.contains("skill")) {
                SkillDTO skill = new SkillDTO(
                        sol.get("skill").toString().substring(sol.get("skill").toString().indexOf("#") + 1),
                        sol.get("skillName").toString(),
                        sol.contains("skillLevel") ? sol.get("skillLevel").toString() : "N/A"
                );
                dto.getSkills().add(skill);
            }
        }
        return new ArrayList<>(map.values());
    }

    // ✅ Get by ID
    public Optional<FreelancerDTO> getById(String id) {
        String query = PREFIX +
                "SELECT ?name ?exp ?skill ?skillName ?skillLevel WHERE { :" + id + " a :Freelancer ; rdfs:label ?name ; :experienceLevel ?exp . " +
                "OPTIONAL { :" + id + " :hasSkill ?skill . ?skill rdfs:label ?skillName . OPTIONAL { ?skill :skillLevel ?skillLevel . } } }";

        ResultSet rs = fuseki.select(query);
        FreelancerDTO dto = new FreelancerDTO(id, "", "", new ArrayList<>());

        while (rs.hasNext()) {
            QuerySolution sol = rs.next();
            dto.setName(sol.get("name").toString());
            dto.setExperienceLevel(sol.get("exp").toString());

            if (sol.contains("skill")) {
                SkillDTO skill = new SkillDTO(
                        sol.get("skill").toString().substring(sol.get("skill").toString().indexOf("#") + 1),
                        sol.get("skillName").toString(),
                        sol.contains("skillLevel") ? sol.get("skillLevel").toString() : "N/A"
                );
                dto.getSkills().add(skill);
            }
        }
        return dto.getName().isEmpty() ? Optional.empty() : Optional.of(dto);
    }

    // ✅ Update
    public void update(String id, FreelancerDTO dto) {
        delete(id);
        dto.setId(id);
        add(dto);
    }

    // ✅ Delete
    public void delete(String id) {
        String update = PREFIX + "DELETE WHERE { :" + id + " ?p ?o . }";
        fuseki.update(update);
    }
    private static final Map<String, List<String>> SYNONYMS = Map.of(
            "développeur", List.of("Python", "Java", "C#", "backend"),
            "django", List.of("Django", "Python"),
            "frontend", List.of("React", "Angular", "Vue"),
            "react", List.of("React", "JavaScript"),
            "java", List.of("Java","Spring boot"),
            "python", List.of("Python")
    );
    private Set<String> extractKeywords(String phrase) {
        Set<String> keywords = new HashSet<>();
        if (phrase == null || phrase.isEmpty()) return keywords;

        String[] words = phrase.toLowerCase().split("\\s+");
        for (String word : words) {
            keywords.add(word); // mot exact
            if (SYNONYMS.containsKey(word)) {
                keywords.addAll(SYNONYMS.get(word)); // ajouter synonymes
            }
        }
        return keywords;
    }
    public List<FreelancerDTO> searchSmart(String phrase) {
        Set<String> keywords = extractKeywords(phrase);
        if (keywords.isEmpty()) return getAll();

        // construire FILTER dynamique
        StringBuilder filter = new StringBuilder("FILTER (");
        int i = 0;
        for (String kw : keywords) {
            if (i > 0) filter.append(" || ");
            filter.append(
                    "CONTAINS(LCASE(?name), \"" + kw.toLowerCase() + "\") || " +
                            "CONTAINS(LCASE(?exp), \"" + kw.toLowerCase() + "\") || " +
                            "CONTAINS(LCASE(?skillName), \"" + kw.toLowerCase() + "\") || " +
                            "CONTAINS(LCASE(?skillLevel), \"" + kw.toLowerCase() + "\")"
            );
            i++;
        }
        filter.append(")");

        String sparql = PREFIX + """
        SELECT ?freelancer ?name ?exp ?skill ?skillName ?skillLevel
        WHERE {
          ?freelancer a :Freelancer ;
                      rdfs:label ?name ;
                      :experienceLevel ?exp .
          OPTIONAL {
            ?freelancer :hasSkill ?skill .
            ?skill rdfs:label ?skillName .
            OPTIONAL { ?skill :skillLevel ?skillLevel . }
          }
          """ + filter.toString() + """
        }
    """;

        ResultSet rs = fuseki.select(sparql);
        Map<String, FreelancerDTO> map = new HashMap<>();

        while (rs.hasNext()) {
            QuerySolution sol = rs.next();
            String uri = sol.get("freelancer").toString();
            String id = uri.substring(uri.indexOf("#") + 1);

            FreelancerDTO dto = map.computeIfAbsent(id,
                    k -> new FreelancerDTO(
                            k,
                            sol.contains("name") ? sol.get("name").toString() : "N/A",
                            sol.contains("exp") ? sol.get("exp").toString() : "N/A",
                            new ArrayList<>()
                    ));

            if (sol.contains("skill")) {
                SkillDTO skill = new SkillDTO(
                        sol.get("skill").toString().substring(sol.get("skill").toString().indexOf("#") + 1),
                        sol.contains("skillName") ? sol.get("skillName").toString() : "N/A",
                        sol.contains("skillLevel") ? sol.get("skillLevel").toString() : "N/A"
                );
                dto.getSkills().add(skill);
            }
        }

        return new ArrayList<>(map.values());
    }

    public List<FreelancerDTO> searchSemantic(String naturalQuery) {
        System.out.println("🔍 Recherche contextuelle: " + naturalQuery);

        try {
            // 1. Analyse contextuelle avec Ollama
            List<String> technologies = ollamaService.extractTechnologies(naturalQuery);
            System.out.println("🎯 Technologies contextuelles: " + technologies);

            // 2. Nettoyer les technologies pour SPARQL
            List<String> cleanTechnologies = cleanTechnologiesForSparql(technologies);
            System.out.println("🔧 Technologies nettoyées pour SPARQL: " + cleanTechnologies);

            // 3. Générer la requête SPARQL
            String sparql = generateContextualSparql(cleanTechnologies);
            System.out.println("🧠 SPARQL généré: " + sparql);

            // 4. Exécuter la requête
            ResultSet rs = fuseki.select(sparql);

            // 5. Mapper les résultats
            List<FreelancerDTO> results = mapResultsToFreelancers(rs);
            System.out.println("✅ " + results.size() + " freelancers trouvés");

            return results;

        } catch (Exception e) {
            System.err.println("❌ Erreur recherche: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private List<String> cleanTechnologiesForSparql(List<String> technologies) {
        List<String> clean = new ArrayList<>();

        for (String tech : technologies) {
            // Supprimer les caractères problématiques pour SPARQL
            String cleanTech = tech.replaceAll("[\"']", "") // Guillemets
                    .replaceAll("[^a-zA-Z0-9#.+\\- ]", "") // Caractères spéciaux
                    .trim();

            if (!cleanTech.isEmpty() && cleanTech.length() > 1) {
                clean.add(cleanTech.toLowerCase());
            }
        }

        return clean;
    }

    private String generateContextualSparql(List<String> technologies) {
        StringBuilder sparql = new StringBuilder(PREFIX);
        sparql.append("""
            SELECT ?freelancer ?name ?exp ?skill ?skillName ?skillLevel
            WHERE {
              ?freelancer a :Freelancer ;
                         rdfs:label ?name ;
                         :experienceLevel ?exp .
              ?freelancer :hasSkill ?skill .
              ?skill rdfs:label ?skillName .
            """);

        // Ajouter le filtre pour chaque technologie
        if (!technologies.isEmpty()) {
            sparql.append("  FILTER (");
            for (int i = 0; i < technologies.size(); i++) {
                if (i > 0) sparql.append(" || ");
                String tech = technologies.get(i);
                sparql.append("CONTAINS(LCASE(?skillName), \"").append(tech).append("\")");
            }
            sparql.append(")\n");
        }

        sparql.append("""
              OPTIONAL { ?skill :skillLevel ?skillLevel . }
            }
            ORDER BY ?name
            """);

        return sparql.toString();
    }

    private List<FreelancerDTO> mapResultsToFreelancers(ResultSet rs) {
        Map<String, FreelancerDTO> map = new HashMap<>();

        while (rs.hasNext()) {
            QuerySolution sol = rs.next();
            String uri = sol.get("freelancer").toString();
            String id = extractIdFromUri(uri);

            String name = sol.get("name").toString();
            String exp = sol.get("exp").toString();

            FreelancerDTO dto = map.computeIfAbsent(id,
                    k -> new FreelancerDTO(k, name, exp, new ArrayList<>()));

            if (sol.contains("skill")) {
                String skillUri = sol.get("skill").toString();
                String skillId = extractIdFromUri(skillUri);
                String skillName = sol.get("skillName").toString();
                String skillLevel = sol.contains("skillLevel") ?
                        sol.get("skillLevel").toString() : "N/A";

                SkillDTO skill = new SkillDTO(skillId, skillName, skillLevel);

                // Éviter les doublons
                boolean skillExists = dto.getSkills().stream()
                        .anyMatch(s -> s.getId().equals(skillId));
                if (!skillExists) {
                    dto.getSkills().add(skill);
                }
            }
        }

        return new ArrayList<>(map.values());
    }

    private String extractIdFromUri(String uri) {
        int hashIndex = uri.lastIndexOf('#');
        if (hashIndex != -1) {
            return uri.substring(hashIndex + 1);
        }
        int slashIndex = uri.lastIndexOf('/');
        if (slashIndex != -1) {
            return uri.substring(slashIndex + 1);
        }
        return uri;
    }
    public StatsDTO getStats() {
        return StatsDTO.builder()
                .totalFreelancers(getTotalFreelancers())
                .experienceLevelStats(getExperienceLevelStats())
                .skillStats(getSkillStats())
                .skillLevelStats(getSkillLevelStats())
                .averageSkillsPerFreelancer(calculateAverageSkillsPerFreelancer())
                .freelancersBySkillCount(getFreelancersBySkillCount())
                .build();
    }

    public long getTotalFreelancers() {
        String query = PREFIX + """
            SELECT (COUNT(DISTINCT ?freelancer) as ?count)
            WHERE {
                ?freelancer a :Freelancer .
            }
        """;

        ResultSet rs = fuseki.select(query);
        if (rs.hasNext()) {
            QuerySolution sol = rs.next();
            return sol.get("count").asLiteral().getLong();
        }
        return 0;
    }

    public Map<String, Long> getExperienceLevelStats() {
        String query = PREFIX + """
            SELECT ?level (COUNT(DISTINCT ?freelancer) as ?count)
            WHERE {
                ?freelancer a :Freelancer ;
                           :experienceLevel ?level .
            }
            GROUP BY ?level
            ORDER BY DESC(?count)
        """;

        ResultSet rs = fuseki.select(query);
        Map<String, Long> stats = new LinkedHashMap<>();

        while (rs.hasNext()) {
            QuerySolution sol = rs.next();
            String level = sol.get("level").toString();
            long count = sol.get("count").asLiteral().getLong();
            stats.put(level, count);
        }
        return stats;
    }

    public Map<String, Long> getSkillStats() {
        String query = PREFIX + """
            SELECT ?skillName (COUNT(DISTINCT ?freelancer) as ?count)
            WHERE {
                ?freelancer a :Freelancer ;
                           :hasSkill ?skill .
                ?skill rdfs:label ?skillName .
            }
            GROUP BY ?skillName
            ORDER BY DESC(?count)
            LIMIT 10
        """;

        ResultSet rs = fuseki.select(query);
        Map<String, Long> stats = new LinkedHashMap<>();

        while (rs.hasNext()) {
            QuerySolution sol = rs.next();
            String skillName = sol.get("skillName").toString();
            long count = sol.get("count").asLiteral().getLong();
            stats.put(skillName, count);
        }
        return stats;
    }

    public Map<String, Long> getSkillLevelStats() {
        String query = PREFIX + """
            SELECT ?level (COUNT(DISTINCT ?skill) as ?count)
            WHERE {
                ?skill a :Skill ;
                       :skillLevel ?level .
            }
            GROUP BY ?level
            ORDER BY DESC(?count)
        """;

        ResultSet rs = fuseki.select(query);
        Map<String, Long> stats = new LinkedHashMap<>();

        while (rs.hasNext()) {
            QuerySolution sol = rs.next();
            String level = sol.get("level").toString();
            long count = sol.get("count").asLiteral().getLong();
            stats.put(level, count);
        }
        return stats;
    }

    public double calculateAverageSkillsPerFreelancer() {
        String query = PREFIX + """
            SELECT (AVG(?skillCount) as ?avg)
            WHERE {
                SELECT ?freelancer (COUNT(?skill) as ?skillCount)
                WHERE {
                    ?freelancer a :Freelancer ;
                               :hasSkill ?skill .
                }
                GROUP BY ?freelancer
            }
        """;

        ResultSet rs = fuseki.select(query);
        if (rs.hasNext()) {
            QuerySolution sol = rs.next();
            return sol.get("avg").asLiteral().getDouble();
        }
        return 0.0;
    }

    public Map<String, Long> getFreelancersBySkillCount() {
        String query = PREFIX + """
            SELECT ?skillCount (COUNT(?freelancer) as ?freelancerCount)
            WHERE {
                SELECT ?freelancer (COUNT(?skill) as ?skillCount)
                WHERE {
                    ?freelancer a :Freelancer .
                    OPTIONAL { ?freelancer :hasSkill ?skill . }
                }
                GROUP BY ?freelancer
            }
            GROUP BY ?skillCount
            ORDER BY ?skillCount
        """;

        ResultSet rs = fuseki.select(query);
        Map<String, Long> stats = new LinkedHashMap<>();

        while (rs.hasNext()) {
            QuerySolution sol = rs.next();
            String skillCount = sol.get("skillCount").toString();
            long freelancerCount = sol.get("freelancerCount").asLiteral().getLong();
            stats.put(skillCount + " compétence(s)", freelancerCount);
        }
        return stats;
    }
}
