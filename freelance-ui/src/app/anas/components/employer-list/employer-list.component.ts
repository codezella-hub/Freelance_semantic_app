import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmployerService } from '../../services/employer.service';
import { Router, RouterModule } from '@angular/router';
import { Employer } from '../../models/employer.model';

@Component({
  selector: 'app-employer-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './employer-list.component.html',
  styleUrls: ['./employer-list.component.scss']
})
export class EmployerListComponent implements OnInit {
  data: Employer[] = [];
  loading = true;
  minScore = 80;
  mode: 'all' | 'sorted' | 'filter' = 'all';
  menuOpen: string | null = null;
  showConfirmPopup = false;
  selectedId: string | null = null;

  constructor(private api: EmployerService,private router: Router) {}

  ngOnInit(): void {
    this.loadAll();
  }
  toggleMenu(id?: string) {
  this.menuOpen = this.menuOpen === id ? null : id ?? null;
}
  loadAll() {
    this.loading = true;
    this.api.list().subscribe({
      next: res => { this.data = res; this.loading = false; },
      error: () => this.loading = false
    });
  }

  loadSorted() {
    this.loading = true;
    this.api.listSortedByScore().subscribe({
      next: res => { this.data = res; this.loading = false; },
      error: () => this.loading = false
    });
  }

  loadByScore() {
    this.loading = true;
    this.api.scoreGreaterThan(this.minScore).subscribe({
      next: res => { this.data = res; this.loading = false; },
      error: () => this.loading = false
    });
  }

  remove(id?: string) {
    if (!id) return;
    if (!confirm('Voulez-vous supprimer cet employeur ?')) return;
    this.api.delete(id).subscribe(() => this.data = this.data.filter(e => e.id !== id));
  }
  edit(id?: string) {
  if (!id) return;
  this.router.navigate(['/employers/edit', id]);
}



askDelete(id?: string) {
  if (!id) return;
  this.selectedId = id;
  this.showConfirmPopup = true;
}

cancelDelete() {
  this.showConfirmPopup = false;
  this.selectedId = null;
}

confirmDelete() {
  if (!this.selectedId) return;

  this.api.delete(this.selectedId).subscribe(() => {
    this.data = this.data.filter(e => e.id !== this.selectedId);
    this.showConfirmPopup = false;
    this.selectedId = null;
  });
}

}
