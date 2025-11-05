import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {Freelancer} from '../../services/freelancer.model';
import {FreelancerService} from '../../services/freelancer.service';


@Component({
  selector: 'app-search-freelancers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search-freelancers.component.html',
  styleUrls: ['./search-freelancers.component.scss']
})
export class SearchFreelancersComponent {
  searchQuery = '';
  searchResults: Freelancer[] = [];
  loading = false;
  searched = false;

  constructor(private freelancerService: FreelancerService) { }

  search(): void {
    if (!this.searchQuery.trim()) {
      return;
    }

    this.loading = true;
    this.searched = true;

    this.freelancerService.searchFreelancers(this.searchQuery).subscribe({
      next: (results) => {
        this.searchResults = results;
        this.loading = false;
      },
      error: (error) => {
        console.error('Search error:', error);
        this.loading = false;
        alert('Erreur lors de la recherche');
      }
    });
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.searchResults = [];
    this.searched = false;
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
