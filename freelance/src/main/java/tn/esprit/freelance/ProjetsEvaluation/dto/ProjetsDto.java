package tn.esprit.freelance.ProjetsEvaluation.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProjetsDto {
    private String id;
    private String type;               // CompletedProject ou OngoingProject
    private String projectTitle;
    private String projectSummary;
    private String deliveryDate;       // "2025-10-26T00:00:00"
    private List<String> evaluations;  // ✅ IDs ou URIs des évaluations liées
}
