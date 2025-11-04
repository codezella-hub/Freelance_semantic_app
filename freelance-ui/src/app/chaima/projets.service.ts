import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Projet {
  id?: string;
  type?: string;
  projectTitle?: string;
  projectSummary?: string;
  deliveryDate?: string;
  evaluations?: string[]; // ✅ IDs d’évaluations existantes
}

@Injectable({
  providedIn: 'root',
})
export class ProjetsService {
  private baseUrl = 'http://localhost:8089/api/projets';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Projet[]> {
    return this.http.get<Projet[]>(this.baseUrl);
  }

  create(projet: Projet): Observable<Projet> {
    return this.http.post<Projet>(this.baseUrl, projet);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
   /** 📊 Récupérer les statistiques globales */
  getStats(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/stats`);
  }

  /** 🔍 Trouver les projets similaires */
  getSimilar(projectId: string): Observable<Projet[]> {
    return this.http.get<Projet[]>(`${this.baseUrl}/${projectId}/similar`);
}}
