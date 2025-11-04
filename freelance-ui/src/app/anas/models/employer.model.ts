export interface Employer {
  id?: string;
  type?: 'Company' | 'Individual' | string;
  companyName?: string;
  email?: string;
  phoneNumber?: string;
  employabilityId?: string;
  employabilityScore?: number | null;
}
