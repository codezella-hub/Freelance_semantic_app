import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmployabilityService } from '../../services/employability.service';
import { Employability } from '../../models/employability.model';

@Component({
  selector: 'app-employability-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './employability-list.component.html',
  styleUrls: ['./employability-list.component.scss']
})
export class EmployabilityListComponent implements OnInit {
  data: Employability[] = [];
  loading = true;

  constructor(private api: EmployabilityService) {}

  ngOnInit(): void {
    this.api.list().subscribe({
      next: res => {
        this.data = res;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  remove(id?: string) {
    if (!id) return;
    if (!confirm('Voulez-vous vraiment supprimer cette employabilité ?')) return;

    this.api.delete(id).subscribe(() => {
      this.data = this.data.filter(e => e.id !== id);
    });
  }
}
