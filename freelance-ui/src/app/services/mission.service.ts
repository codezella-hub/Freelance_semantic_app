import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Mission } from '../models/mission';

@Injectable({ providedIn: 'root' })
export class MissionService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/missions';

  getAll(): Observable<Mission[]> { return this.http.get<Mission[]>(this.baseUrl); }
  create(m: Partial<Mission>): Observable<Mission> { return this.http.post<Mission>(this.baseUrl, m); }
  update(id: string, m: Partial<Mission>): Observable<void> { return this.http.put<void>(this.baseUrl, { ...m, id }); }
  delete(id: string): Observable<void> { return this.http.delete<void>(`${this.baseUrl}?id=${encodeURIComponent(id)}`); }

  search(params: { q?: string; status?: string }): Observable<Mission[]> {
    const query = new URLSearchParams();
    if (params.q) query.set('q', params.q);
    if (params.status) query.set('status', params.status);
    return this.http.get<Mission[]>(`${this.baseUrl}/search?${query.toString()}`);
  }
}


