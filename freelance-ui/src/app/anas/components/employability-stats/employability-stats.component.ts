import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmployerService } from '../../services/employer.service';
import { Chart, registerables } from 'chart.js';
import { Employer } from '../../models/employer.model';
import { RouterModule } from '@angular/router';

Chart.register(...registerables);

@Component({
  selector: 'app-employability-stats',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './employability-stats.component.html',
  styleUrls: ['./employability-stats.component.scss']
})
export class EmployabilityStatsComponent implements OnInit {
  loading = true;
  data: { type: string; avgScore: number }[] = [];
  topEmployers: Employer[] = [];
  chart: any;

  constructor(private api: EmployerService) {}

  ngOnInit(): void {
    // Charger les deux jeux de données en parallèle
    this.loadStats();
    this.loadTopEmployers();
  }

loadTopEmployers() {
  this.api.getTopEmployers(5).subscribe({
    next: (res: Employer[]) => (this.topEmployers = res),
    error: (err: any) => console.error(err)
  });
}

loadStats() {
  this.api.avgScoreByType().subscribe({
    next: (res: { type: string; avgScore: number }[]) => {
      this.data = res;
      this.loading = false;
      this.renderChart();
    },
    error: (err: any) => (this.loading = false)
  });
}


  renderChart() {
    const ctx = document.getElementById('employabilityChart') as HTMLCanvasElement;
    if (!ctx) return;

    const labels = this.data.map((d) => d.type);
    const scores = this.data.map((d) => d.avgScore);

    this.chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Score moyen d’employabilité',
            data: scores,
            backgroundColor: ['#4e73df', '#1cc88a', '#f6c23e'],
            borderRadius: 8
          }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: false },
          title: {
            display: true,
            text: 'Moyenne des scores par type d’employeur',
            font: { size: 18 }
          }
        },
        scales: {
          y: { beginAtZero: true, max: 100 }
        }
      }
    });
  }
}
