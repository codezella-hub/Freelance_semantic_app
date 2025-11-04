import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Employer } from '../models/employer.model';

@Injectable({ providedIn: 'root' })
export class EmployerService {
  private base = 'http://localhost:8089/api/employers';

  constructor(private http: HttpClient) {}

  list(): Observable<Employer[]> {
    return this.http.get<Employer[]>(this.base);
  }

  get(id: string): Observable<Employer> {
    return this.http.get<Employer>(`${this.base}/${id}`);
  }

  create(body: Employer): Observable<Employer> {
    return this.http.post<Employer>(this.base, body);
  }

  update(id: string, body: Employer): Observable<Employer> {
    return this.http.put<Employer>(`${this.base}/${id}`, body);
  }

  delete(id: string) {
    return this.http.delete(`${this.base}/${id}`);
  }

  // ✅ Triés par score (backend → /api/employers/top)
  listSortedByScore(limit: number = 50): Observable<Employer[]> {
    return this.http.get<Employer[]>(`${this.base}/top?limit=${limit}`);
  }

  // ✅ Score > X (backend → /api/employers/by-score)
  scoreGreaterThan(min: number): Observable<Employer[]> {
    const params = new HttpParams().set('min', min);
    return this.http.get<Employer[]>(`${this.base}/by-score`, { params });
  }

  // ✅ Moyenne par type
  avgScoreByType(): Observable<{ type: string; avgScore: number }[]> {
    return this.http.get<{ type: string; avgScore: number }[]>(`${this.base}/avg-score-by-type`);
  }
  getTopEmployers(limit: number = 5): Observable<Employer[]> {
  return this.http.get<Employer[]>(`${this.base}/top?limit=${limit}`);
}
getHighPotentialEmployers(): Observable<Employer[]> {
  return this.http.get<Employer[]>(`${this.base}/high-potential`);
}

getLowPotentialEmployers(): Observable<Employer[]> {
  return this.http.get<Employer[]>(`${this.base}/low-potential`);
}

}
