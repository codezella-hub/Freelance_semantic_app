// dto/EvaluationDto.java
package tn.esprit.freelance.ProjetsEvaluation.dto;

import lombok.Data;

@Data
public class EvaluationDto {
    private String id;
    private String type;            // ClientReview ou PeerReview
    private Double score;
    private String comment;
    private String evaluationDate;
}
