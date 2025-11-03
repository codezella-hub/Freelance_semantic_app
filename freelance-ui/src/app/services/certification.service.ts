import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Certification {
  uri?: string;
  certificationName: string;
  issuedBy: string;
  issueDate: string;
  expirationDate: string;
  certificationType: string;
}

export interface CertificationResponse {
  certifications?: Certification[];
  certification?: Certification;
  count?: number;
  message?: string;
  error?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CertificationService {
  private apiUrl = 'http://localhost:8089/api/certifications';

  constructor(private http: HttpClient) { }

  /**
   * Get all certifications
   */
  getAllCertifications(): Observable<CertificationResponse> {
    return this.http.get<CertificationResponse>(this.apiUrl);
  }

  /**
   * Get certification by URI (using query parameter)
   */
  getCertificationByUri(uri: string): Observable<CertificationResponse> {
    const params = new HttpParams().set('uri', uri);
    return this.http.get<CertificationResponse>(`${this.apiUrl}/by-uri`, { params });
  }

  /**
   * Create a new certification
   */
  createCertification(certification: Certification): Observable<CertificationResponse> {
    return this.http.post<CertificationResponse>(this.apiUrl, certification);
  }

  /**
   * Update an existing certification (using query parameter)
   */
  updateCertification(uri: string, certification: Certification): Observable<CertificationResponse> {
    const params = new HttpParams().set('uri', uri);
    return this.http.put<CertificationResponse>(this.apiUrl, certification, { params });
  }

  /**
   * Delete a certification (using query parameter)
   */
  deleteCertification(uri: string): Observable<CertificationResponse> {
    const params = new HttpParams().set('uri', uri);
    return this.http.delete<CertificationResponse>(this.apiUrl, { params });
  }

  /**
   * Search certifications by name
   */
  searchCertifications(name: string): Observable<CertificationResponse> {
    const params = new HttpParams().set('name', name);
    return this.http.get<CertificationResponse>(`${this.apiUrl}/search`, { params });
  }

  /**
   * Get certifications by issuer
   */
  getCertificationsByIssuer(issuer: string): Observable<CertificationResponse> {
    return this.http.get<CertificationResponse>(`${this.apiUrl}/issuer/${issuer}`);
  }
}

