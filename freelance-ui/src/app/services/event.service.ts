import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Event {
  uri?: string;
  eventTitle: string;
  eventDescription: string;
  eventCategory: string;
  eventDate: string;
  eventType: string;
}

export interface EventResponse {
  events?: Event[];
  event?: Event;
  count?: number;
  message?: string;
  error?: string;
}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = 'http://localhost:8089/api/events';

  constructor(private http: HttpClient) { }

  /**
   * Get all events
   */
  getAllEvents(): Observable<EventResponse> {
    return this.http.get<EventResponse>(this.apiUrl);
  }

  /**
   * Get event by URI (using query parameter)
   */
  getEventByUri(uri: string): Observable<EventResponse> {
    const params = new HttpParams().set('uri', uri);
    return this.http.get<EventResponse>(`${this.apiUrl}/by-uri`, { params });
  }

  /**
   * Create a new event
   */
  createEvent(event: Event): Observable<EventResponse> {
    return this.http.post<EventResponse>(this.apiUrl, event);
  }

  /**
   * Update an existing event (using query parameter)
   */
  updateEvent(uri: string, event: Event): Observable<EventResponse> {
    const params = new HttpParams().set('uri', uri);
    return this.http.put<EventResponse>(this.apiUrl, event, { params });
  }

  /**
   * Delete an event (using query parameter)
   */
  deleteEvent(uri: string): Observable<EventResponse> {
    const params = new HttpParams().set('uri', uri);
    return this.http.delete<EventResponse>(this.apiUrl, { params });
  }

  /**
   * Search events by title
   */
  searchEvents(title: string): Observable<EventResponse> {
    const params = new HttpParams().set('title', title);
    return this.http.get<EventResponse>(`${this.apiUrl}/search`, { params });
  }

  /**
   * Get events by category
   */
  getEventsByCategory(category: string): Observable<EventResponse> {
    return this.http.get<EventResponse>(`${this.apiUrl}/category/${category}`);
  }
}

