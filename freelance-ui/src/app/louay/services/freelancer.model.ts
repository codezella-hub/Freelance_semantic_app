export interface Skill {
  id: string;
  name: string;
  level: string;
}

export interface Freelancer {
  id: string;
  name: string;
  experienceLevel: string;
  skills: Skill[];
}
export interface StatsDTO {
  totalFreelancers: number;
  experienceLevelStats: { [key: string]: number };
  skillStats: { [key: string]: number };
  skillLevelStats: { [key: string]: number };
  averageSkillsPerFreelancer: number;
  freelancersBySkillCount: { [key: string]: number };
}
