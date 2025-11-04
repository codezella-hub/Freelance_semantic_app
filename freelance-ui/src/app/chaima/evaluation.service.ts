import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Evaluation {
  id?: string;
  type?: string;
  score?: number;
  comment?: string;
  evaluationDate?: string;
}

@Injectable({
  providedIn: 'root',
})
export class EvaluationService {
  private baseUrl = 'http://localhost:8089/api/evaluations';

  constructor(private http: HttpClient) {}

  /** 🔹 Récupérer toutes les évaluations */
  getAll(): Observable<Evaluation[]> {
    return this.http.get<Evaluation[]>(this.baseUrl);
  }

  /** 🔍 Rechercher par mot-clé (commentaire ou type) */
  search(keyword: string): Observable<Evaluation[]> {
    return this.http.get<Evaluation[]>(
      `${this.baseUrl}/search?keyword=${keyword}`
    );
  }

  /** 🔽 Trier par champ (score, date, type) */
  sort(sortBy: string, order: string): Observable<Evaluation[]> {
    return this.http.get<Evaluation[]>(
      `${this.baseUrl}/sort?sortBy=${sortBy}&order=${order}`
    );
  }

  /** ➕ Créer une nouvelle évaluation */
  create(evaluation: Evaluation): Observable<Evaluation> {
    return this.http.post<Evaluation>(this.baseUrl, evaluation);
  }

  /** ❌ Supprimer une évaluation */
  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
