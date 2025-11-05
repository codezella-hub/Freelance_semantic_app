import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RdfApiService } from '../services/rdf-api.service';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent {
  titleQuery = '';
  minAmount: number | null = null;
  status = '';

  results: any[] = [];
  loading = false;
  error: string | null = null;

  statuses = ['','DRAFT','ACTIVE','COMPLETED','CANCELLED','PENDING','COMPLETED','FAILED','REFUNDED'];

  constructor(private api: RdfApiService) {}

  run() {
    this.loading = true;
    this.error = null;
    this.results = [];
    this.api.searchSemantic(this.titleQuery, this.minAmount ?? undefined, this.status || undefined).subscribe({
      next: (res: any) => {
        // backend returns JSON array
        try {
          this.results = Array.isArray(res) ? res : JSON.parse(res);
        } catch (e) {
          // sometimes already parsed
          this.results = res;
        }
        this.loading = false;
      },
      error: err => {
        console.error('Semantic search failed', err);
        this.error = 'Search failed';
        this.loading = false;
      }
    });
  }

  clear() {
    this.titleQuery = '';
    this.minAmount = null;
    this.status = '';
    this.results = [];
  }
}
