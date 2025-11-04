import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { EventService, Event } from '../../services/event.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.scss']
})
export class EventListComponent implements OnInit {
  events: Event[] = [];
  filteredEvents: Event[] = [];
  loading = false;
  error: string | null = null;
  searchTerm = '';

  constructor(private eventService: EventService) {}

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.loading = true;
    this.error = null;
    
    this.eventService.getAllEvents().subscribe({
      next: (response) => {
        this.events = response.events || [];
        this.filteredEvents = this.events;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des événements';
        this.loading = false;
        console.error('Error loading events:', err);
      }
    });
  }

  searchEvents(): void {
    if (this.searchTerm.trim()) {
      this.loading = true;
      this.eventService.searchEvents(this.searchTerm).subscribe({
        next: (response) => {
          this.filteredEvents = response.events || [];
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Erreur lors de la recherche';
          this.loading = false;
          console.error('Error searching events:', err);
        }
      });
    } else {
      this.filteredEvents = this.events;
    }
  }

  deleteEvent(uri: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet événement ?')) {
      this.eventService.deleteEvent(uri).subscribe({
        next: () => {
          this.loadEvents();
        },
        error: (err) => {
          this.error = 'Erreur lors de la suppression';
          console.error('Error deleting event:', err);
        }
      });
    }
  }

  getEventTypeClass(type: string): string {
    return type === 'Premium' ? 'badge bg-warning' : 'badge bg-info';
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}

