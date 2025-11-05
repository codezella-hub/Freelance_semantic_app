import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AIService {
  private apiUrl = `${environment.apiUrl}/api/ai`;

  constructor(private http: HttpClient) { }

  analyzePricing(description: string, skills: string, duration: number): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/analyze-pricing`, null, {
      params: {
        description,
        skills,
        duration: duration.toString()
      }
    });
  }

  improveDescription(description: string): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/improve-description`, null, {
      params: { description }
    });
  }
}