import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { EventService, Event } from '../../services/event.service';

@Component({
  selector: 'app-event-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './event-detail.component.html',
  styleUrls: ['./event-detail.component.scss']
})
export class EventDetailComponent implements OnInit {
  event: Event | null = null;
  loading = false;
  error: string | null = null;

  constructor(
    private eventService: EventService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const uri = this.route.snapshot.paramMap.get('uri');
    if (uri) {
      this.loadEvent(decodeURIComponent(uri));
    }
  }

  loadEvent(uri: string): void {
    this.loading = true;
    this.error = null;

    this.eventService.getEventByUri(uri).subscribe({
      next: (response) => {
        if (response.event) {
          this.event = response.event;
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading event:', err);
        this.error = 'Erreur lors du chargement de l\'événement';
        this.loading = false;
      }
    });
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

  goBack(): void {
    this.router.navigate(['/events']);
  }

  editEvent(): void {
    if (this.event?.uri) {
      this.router.navigate(['/events/edit', this.event.uri]);
    }
  }

  deleteEvent(): void {
    if (this.event?.uri && confirm('Êtes-vous sûr de vouloir supprimer cet événement ?')) {
      this.eventService.deleteEvent(this.event.uri).subscribe({
        next: () => {
          this.router.navigate(['/events']);
        },
        error: (err) => {
          console.error('Error deleting event:', err);
          this.error = 'Erreur lors de la suppression de l\'événement';
        }
      });
    }
  }
}

