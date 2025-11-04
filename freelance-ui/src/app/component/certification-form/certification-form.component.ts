import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CertificationService, Certification } from '../../services/certification.service';
import { EventService, Event } from '../../services/event.service';

@Component({
  selector: 'app-certification-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './certification-form.component.html',
  styleUrls: ['./certification-form.component.scss']
})
export class CertificationFormComponent implements OnInit {
  certification: Certification = {
    certificationName: '',
    issuedBy: '',
    issueDate: '',
    expirationDate: '',
    certificationType: 'FormalCertification'
  };

  isEditMode = false;
  loading = false;
  loadingEvents = false;
  error: string | null = null;
  success: string | null = null;
  certificationUri: string | null = null;

  // Liste des événements pour le dropdown
  events: Event[] = [];
  selectedEventTitle: string = '';

  certificationTypes = [
    { value: 'FormalCertification', label: 'Formelle' },
    { value: 'InformalCertification', label: 'Informelle' }
  ];

  constructor(
    private certificationService: CertificationService,
    private eventService: EventService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Charger la liste des événements
    this.loadEvents();

    const uri = this.route.snapshot.paramMap.get('uri');
    if (uri && uri !== 'new') {
      this.isEditMode = true;
      this.certificationUri = decodeURIComponent(uri);
      this.loadCertification(this.certificationUri);
    }
  }

  loadEvents(): void {
    this.loadingEvents = true;
    this.eventService.getAllEvents().subscribe({
      next: (response) => {
        if (response.events) {
          this.events = response.events;
        }
        this.loadingEvents = false;
      },
      error: (err) => {
        console.error('Error loading events:', err);
        this.loadingEvents = false;
      }
    });
  }

  onEventSelected(eventTitle: string): void {
    this.certification.certificationName = eventTitle;
  }

  loadCertification(uri: string): void {
    this.loading = true;
    this.certificationService.getCertificationByUri(uri).subscribe({
      next: (response) => {
        if (response.certification) {
          this.certification = response.certification;
        }
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement de la certification';
        this.loading = false;
        console.error('Error loading certification:', err);
      }
    });
  }

  onSubmit(): void {
    this.loading = true;
    this.error = null;
    this.success = null;

    // Format dates to ISO 8601
    if (this.certification.issueDate) {
      const issueDate = new Date(this.certification.issueDate);
      this.certification.issueDate = issueDate.toISOString();
    }
    
    if (this.certification.expirationDate) {
      const expirationDate = new Date(this.certification.expirationDate);
      this.certification.expirationDate = expirationDate.toISOString();
    }

    if (this.isEditMode && this.certificationUri) {
      this.certificationService.updateCertification(this.certificationUri, this.certification).subscribe({
        next: (response) => {
          this.success = 'Certification mise à jour avec succès';
          this.loading = false;
          setTimeout(() => this.router.navigate(['/certifications']), 1500);
        },
        error: (err) => {
          this.error = 'Erreur lors de la mise à jour';
          this.loading = false;
          console.error('Error updating certification:', err);
        }
      });
    } else {
      this.certificationService.createCertification(this.certification).subscribe({
        next: (response) => {
          this.success = 'Certification créée avec succès';
          this.loading = false;
          setTimeout(() => this.router.navigate(['/certifications']), 1500);
        },
        error: (err) => {
          this.error = 'Erreur lors de la création';
          this.loading = false;
          console.error('Error creating certification:', err);
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/certifications']);
  }
}

