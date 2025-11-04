import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApplicationDto } from '../models/application';

@Injectable({ providedIn: 'root' })
export class ApplicationService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8089/api/applications';

  getAll(): Observable<ApplicationDto[]> {
    return this.http.get<ApplicationDto[]>(this.baseUrl);
  }
  create(a: Partial<ApplicationDto>): Observable<ApplicationDto> {
    return this.http.post<ApplicationDto>(this.baseUrl, a);
  }
  update(id: string, a: Partial<ApplicationDto>): Observable<void> {
    return this.http.put<void>(this.baseUrl, { ...a, id });
  }
  delete(id: string): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}?id=${encodeURIComponent(id)}`
    );
  }

  search(params: {
    status?: string;
    missionUri?: string;
  }): Observable<ApplicationDto[]> {
    const query = new URLSearchParams();
    if (params.status) query.set('status', params.status);
    if (params.missionUri) query.set('missionUri', params.missionUri);
    return this.http.get<ApplicationDto[]>(
      `${this.baseUrl}/search?${query.toString()}`
    );
  }
}
