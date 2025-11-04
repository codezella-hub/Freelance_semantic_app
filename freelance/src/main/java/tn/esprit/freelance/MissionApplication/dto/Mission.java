package tn.esprit.freelance.MissionApplication.dto;

public class Mission {
    private String id; // full URI
    private String titre;
    private String description;
    private Double budget;
    private String statut;

    public Mission() {}

    public Mission(String id, String titre, String description, Double budget, String statut) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.budget = budget;
        this.statut = statut;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}


