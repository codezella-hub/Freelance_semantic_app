import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface NLPQueryResponse {
  success: boolean;
  type?: string;
  results?: any[];
  count?: number;
  query?: string;
  sparql?: string;
  error?: string;
}

export interface ExamplesResponse {
  eventExamples: string[];
  certificationExamples: string[];
}

@Injectable({
  providedIn: 'root'
})
export class NlpQueryService {
  private apiUrl = 'http://localhost:8089/api/nlp';

  constructor(private http: HttpClient) {}

  /**
   * Envoie une requête en langage naturel au backend
   */
  processQuery(query: string): Observable<NLPQueryResponse> {
    return this.http.post<NLPQueryResponse>(`${this.apiUrl}/query`, { query });
  }

  /**
   * Récupère les exemples de requêtes
   */
  getExamples(): Observable<ExamplesResponse> {
    return this.http.get<ExamplesResponse>(`${this.apiUrl}/examples`);
  }
}

