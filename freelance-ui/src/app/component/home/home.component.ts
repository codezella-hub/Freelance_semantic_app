import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // <-- add this
import { RdfApiService } from '../../services/rdf-api.service';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-home',
  imports: [CommonModule, FormsModule], // <-- add in the decorator
  templateUrl: './home.component.html',
  standalone: true,
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  searchTerm = '';
  results: any[] = [];
  constructor(private rdfService: RdfApiService) {}

  search() {
    if (this.searchTerm.trim() === '') {
      this.results = [];
      return;
    }
    this.rdfService.searchPayments(this.searchTerm).subscribe(data => {
      this.results = data.results?.bindings || [];
    });
  }
}
