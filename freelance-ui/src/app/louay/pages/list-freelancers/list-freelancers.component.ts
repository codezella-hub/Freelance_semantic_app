import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {Freelancer} from '../../services/freelancer.model';
import {FreelancerService} from '../../services/freelancer.service';


@Component({
  selector: 'app-list-freelancers',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './list-freelancers.component.html',
  styleUrls: ['./list-freelancers.component.scss']
})
export class ListFreelancersComponent implements OnInit {
  freelancers: Freelancer[] = [];
  loading = false;
  error = '';

  constructor(private freelancerService: FreelancerService) { }

  ngOnInit(): void {
    this.loadFreelancers();
  }

  loadFreelancers(): void {
    this.loading = true;
    this.freelancerService.getAllFreelancers().subscribe({
      next: (data) => {
        this.freelancers = data;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Erreur lors du chargement des freelancers';
        this.loading = false;
        console.error('Error loading freelancers:', error);
      }
    });
  }

  deleteFreelancer(id: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce freelancer ?')) {
      this.freelancerService.deleteFreelancer(id).subscribe({
        next: () => {
          this.freelancers = this.freelancers.filter(f => f.id !== id);
        },
        error: (error) => {
          console.error('Error deleting freelancer:', error);
          alert('Erreur lors de la suppression');
        }
      });
    }
  }

  getExperienceColor(level: string): string {
    switch (level.toLowerCase()) {
      case 'junior': return '#28a745';
      case 'intermediaire': return '#ffc107';
      case 'senior': return '#fd7e14';
      case 'expert': return '#dc3545';
      default: return '#6c757d';
    }
  }
}
