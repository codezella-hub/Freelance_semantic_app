import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Freelancer, StatsDTO} from './freelancer.model';



@Injectable({
  providedIn: 'root'
})
export class FreelancerService {
  private apiUrl = 'http://localhost:8089/api/freelancers';
  private http = inject(HttpClient); // Alternative au constructor

  // Ou utilisez le constructor traditionnel :
  // constructor(private http: HttpClient) { }

  getAllFreelancers(): Observable<Freelancer[]> {
    return this.http.get<Freelancer[]>(this.apiUrl);
  }

  getFreelancerById(id: string): Observable<Freelancer> {
    return this.http.get<Freelancer>(`${this.apiUrl}/${id}`);
  }

  addFreelancer(freelancer: Freelancer): Observable<Freelancer> {
    return this.http.post<Freelancer>(this.apiUrl, freelancer);
  }

  updateFreelancer(id: string, freelancer: Freelancer): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, freelancer);
  }

  deleteFreelancer(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchFreelancers(query: string): Observable<Freelancer[]> {
    const params = new HttpParams().set('query', query);
    return this.http.get<Freelancer[]>(`${this.apiUrl}/search`, { params });
  }

  /**
   * Récupère toutes les statistiques complètes
   */
  getStats(): Observable<StatsDTO> {
    return this.http.get<StatsDTO>(`${this.apiUrl}/stats`);
  }

  /**
   * Récupère les statistiques par niveau d'expérience
   */
  getExperienceLevelStats(): Observable<{ [key: string]: number }> {
    return this.http.get<{ [key: string]: number }>(`${this.apiUrl}/stats/experience-level`);
  }

  /**
   * Récupère les statistiques des compétences
   */
  getSkillStats(): Observable<{ [key: string]: number }> {
    return this.http.get<{ [key: string]: number }>(`${this.apiUrl}/stats/skills`);
  }

  /**
   * Récupère les statistiques des niveaux de compétences
   */
  getSkillLevelStats(): Observable<{ [key: string]: number }> {
    return this.http.get<{ [key: string]: number }>(`${this.apiUrl}/stats/skill-levels`);
  }

  /**
   * Récupère les statistiques de recommandation (si disponible)
   */
  recommendFreelancers(projectDescription: string): Observable<Freelancer[]> {
    const params = new HttpParams().set('project', projectDescription);
    return this.http.get<Freelancer[]>(`${this.apiUrl}/recommend`, { params });
  }

  /**
   * Récupère les statistiques globales (méthode utilitaire)
   */
  getStatsSummary(): Observable<{
    total: number;
    byExperience: { [key: string]: number };
    bySkills: { [key: string]: number };
  }> {
    return new Observable(observer => {
      this.getStats().subscribe({
        next: (stats) => {
          observer.next({
            total: stats.totalFreelancers,
            byExperience: stats.experienceLevelStats,
            bySkills: stats.skillStats
          });
          observer.complete();
        },
        error: (error) => observer.error(error)
      });
    });
  }
}
