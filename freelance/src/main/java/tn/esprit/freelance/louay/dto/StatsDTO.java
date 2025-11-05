package tn.esprit.freelance.louay.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsDTO {
    private long totalFreelancers;
    private Map<String, Long> experienceLevelStats;
    private Map<String, Long> skillStats;
    private Map<String, Long> skillLevelStats;
    private double averageSkillsPerFreelancer;
    private Map<String, Long> freelancersBySkillCount;
}