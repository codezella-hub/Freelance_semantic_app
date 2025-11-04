import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Employability } from '../models/employability.model';

@Injectable({ providedIn: 'root' })
export class EmployabilityService {
  private base = 'http://localhost:8089/api/employability';
  constructor(private http: HttpClient) {}

  list(): Observable<Employability[]> { return this.http.get<Employability[]>(this.base); }
  get(id: string): Observable<Employability> { return this.http.get<Employability>(`${this.base}/${id}`); }
  create(body: Employability): Observable<Employability> { return this.http.post<Employability>(this.base, body); }
  update(id: string, body: Employability): Observable<Employability> { return this.http.put<Employability>(`${this.base}/${id}`, body); }
  delete(id: string) { return this.http.delete(`${this.base}/${id}`); }
}
