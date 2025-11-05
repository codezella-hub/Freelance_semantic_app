package tn.esprit.freelance.louay.dto;


import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FreelancerDTO {
    private String id;
    private String name;
    private String experienceLevel;
    private List<SkillDTO> skills;

}