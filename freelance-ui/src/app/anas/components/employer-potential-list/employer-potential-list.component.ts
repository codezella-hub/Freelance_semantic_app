import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmployerService } from '../../services/employer.service';
import { Employer } from '../../models/employer.model';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-employer-potential-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './employer-potential-list.component.html',
  styleUrls: ['./employer-potential-list.component.scss']
})
export class EmployerPotentialListComponent implements OnInit {
  highPotential: Employer[] = [];
  lowPotential: Employer[] = [];
  loading = true;

  constructor(private api: EmployerService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    Promise.all([
      this.api.getHighPotentialEmployers().toPromise(),
      this.api.getLowPotentialEmployers().toPromise()
    ])
      .then(([high, low]) => {
        this.highPotential = high || [];
        this.lowPotential = low || [];
      })
      .finally(() => (this.loading = false));
  }
}
