import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { EventService, Event } from '../../services/event.service';

@Component({
  selector: 'app-event-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './event-form.component.html',
  styleUrls: ['./event-form.component.scss']
})
export class EventFormComponent implements OnInit {
  event: Event = {
    eventTitle: '',
    eventDescription: '',
    eventCategory: '',
    eventDate: '',
    eventType: 'Public'
  };
  
  isEditMode = false;
  loading = false;
  error: string | null = null;
  success: string | null = null;
  eventUri: string | null = null;

  eventTypes = ['Public', 'Premium'];
  categories = ['Conférence', 'Atelier', 'Webinaire', 'Networking', 'Formation', 'Autre'];

  constructor(
    private eventService: EventService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const uri = this.route.snapshot.paramMap.get('uri');
    if (uri && uri !== 'new') {
      this.isEditMode = true;
      this.eventUri = decodeURIComponent(uri);
      this.loadEvent(this.eventUri);
    }
  }

  loadEvent(uri: string): void {
    this.loading = true;
    this.eventService.getEventByUri(uri).subscribe({
      next: (response) => {
        if (response.event) {
          this.event = response.event;
        }
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement de l\'événement';
        this.loading = false;
        console.error('Error loading event:', err);
      }
    });
  }

  onSubmit(): void {
    this.loading = true;
    this.error = null;
    this.success = null;

    // Format date to ISO 8601
    if (this.event.eventDate) {
      const date = new Date(this.event.eventDate);
      this.event.eventDate = date.toISOString();
    }

    if (this.isEditMode && this.eventUri) {
      this.eventService.updateEvent(this.eventUri, this.event).subscribe({
        next: (response) => {
          this.success = 'Événement mis à jour avec succès';
          this.loading = false;
          setTimeout(() => this.router.navigate(['/events']), 1500);
        },
        error: (err) => {
          this.error = 'Erreur lors de la mise à jour';
          this.loading = false;
          console.error('Error updating event:', err);
        }
      });
    } else {
      this.eventService.createEvent(this.event).subscribe({
        next: (response) => {
          this.success = 'Événement créé avec succès';
          this.loading = false;
          setTimeout(() => this.router.navigate(['/events']), 1500);
        },
        error: (err) => {
          this.error = 'Erreur lors de la création';
          this.loading = false;
          console.error('Error creating event:', err);
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/events']);
  }
}

