export interface Employability {
  id?: string;
  kind?: 'ExperienceLevel' | 'MarketDemandScore' | string;
  employabilityScore?: number | null;
}
