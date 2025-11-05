import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RdfApiService {
  private baseUrl = 'http://localhost:8089/api/rdf';
  // JPA API base (contracts/payments controllers)
  private apiBase = 'http://localhost:8089/api';

  constructor(private http: HttpClient) {}

  getContracts(): Observable<any> {
    return this.http.get(`${this.baseUrl}/contracts`);
  }

  getPayments(): Observable<any> {
    return this.http.get(`${this.baseUrl}/payments`);
  }
  searchPayments(query: string): Observable<any> {
  return this.http.get(`${this.baseUrl}/search/payments?query=${query}`);
}

  // --- JPA endpoints (used for CRUD) ---
  getJpaContracts(): Observable<any> {
    return this.http.get(`${this.apiBase}/contracts`);
  }

  getContractById(id: number): Observable<any> {
    return this.http.get(`${this.apiBase}/contracts/${id}`);
  }

  updateContract(id: number, contract: any): Observable<any> {
    return this.http.put(`${this.apiBase}/contracts/${id}`, contract);
  }

  deleteContract(id: number): Observable<any> {
    return this.http.delete(`${this.apiBase}/contracts/${id}`);
  }

  createContract(contract: any): Observable<any> {
    return this.http.post(`${this.apiBase}/contracts`, contract);
  }

  createPayment(payment: any): Observable<any> {
    return this.http.post(`${this.apiBase}/payments`, payment);
  }

  getJpaPayments(): Observable<any> {
    return this.http.get(`${this.apiBase}/payments`);
  }

  searchSemantic(titleQuery?: string, minAmount?: number, status?: string): Observable<any> {
    const params: string[] = [];
    if (titleQuery) params.push(`titleQuery=${encodeURIComponent(titleQuery)}`);
    if (minAmount != null) params.push(`minAmount=${minAmount}`);
    if (status) params.push(`status=${encodeURIComponent(status)}`);
    const qs = params.length ? `?${params.join('&')}` : '';
    return this.http.get(`${this.baseUrl}/search/semantic${qs}`);
  }

  getPaymentById(id: number): Observable<any> {
    return this.http.get(`${this.apiBase}/payments/${id}`);
  }

  updatePayment(id: number, payment: any): Observable<any> {
    return this.http.put(`${this.apiBase}/payments/${id}`, payment);
  }

  deletePayment(id: number): Observable<any> {
    return this.http.delete(`${this.apiBase}/payments/${id}`);
  }

}
