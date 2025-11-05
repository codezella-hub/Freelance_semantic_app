import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartType } from 'chart.js';
import { FreelancerService } from '../../services/freelancer.service';
import {StatsDTO} from '../../services/freelancer.model';


@Component({
  selector: 'app-stats-freelancers',
  standalone: true,
  imports: [CommonModule, RouterModule, BaseChartDirective],
  templateUrl: './stats-freelancers.component.html',
  styleUrl: './stats-freelancers.component.scss'
})
export class StatsFreelancersComponent implements OnInit {
  private freelancerService = inject(FreelancerService);

  loading = true;
  stats: StatsDTO | null = null;

  // Configuration des graphiques
  public experienceLevelChart: ChartConfiguration<'pie'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      backgroundColor: [
        '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'
      ]
    }]
  };

  public skillChart: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: 'Nombre de freelancers',
      backgroundColor: '#4285F4',
      borderColor: '#3367D6',
      borderWidth: 1
    }]
  };

  public skillLevelChart: ChartConfiguration<'doughnut'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      backgroundColor: [
        '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF'
      ]
    }]
  };

  public skillsPerFreelancerChart: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: 'Nombre de freelancers',
      backgroundColor: '#34A853',
      borderColor: '#2E8B47',
      borderWidth: 1
    }]
  };

  // Options des graphiques
  public pieChartOptions: ChartConfiguration<'pie'>['options'] = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: 'Répartition par niveau d\'expérience'
      }
    }
  };

  public barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        position: 'top',
      }
    },
    scales: {
      x: {
        ticks: {
          maxRotation: 45,
          minRotation: 45
        }
      },
      y: {
        beginAtZero: true
      }
    }
  };

  public doughnutChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: 'Niveaux de compétences'
      }
    }
  };

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.loading = true;
    this.freelancerService.getStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.updateCharts();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading stats:', error);
        this.loading = false;
      }
    });
  }

  private updateCharts(): void {
    if (!this.stats) return;

    // Graphique des niveaux d'expérience (Pie Chart)
    this.experienceLevelChart = {
      labels: Object.keys(this.stats.experienceLevelStats),
      datasets: [{
        data: Object.values(this.stats.experienceLevelStats),
        backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF']
      }]
    };

    // Graphique des compétences populaires (Bar Chart)
    const skillEntries = Object.entries(this.stats.skillStats)
      .sort(([, a], [, b]) => b - a)
      .slice(0, 8);

    this.skillChart = {
      labels: skillEntries.map(([name]) => name),
      datasets: [{
        data: skillEntries.map(([, count]) => count),
        label: 'Compétences les plus populaires',
        backgroundColor: '#4285F4',
        borderColor: '#3367D6',
        borderWidth: 1
      }]
    };

    // Graphique des niveaux de compétences (Doughnut Chart)
    this.skillLevelChart = {
      labels: Object.keys(this.stats.skillLevelStats),
      datasets: [{
        data: Object.values(this.stats.skillLevelStats),
        backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0']
      }]
    };

    // Graphique du nombre de compétences par freelancer (Bar Chart)
    this.skillsPerFreelancerChart = {
      labels: Object.keys(this.stats.freelancersBySkillCount),
      datasets: [{
        data: Object.values(this.stats.freelancersBySkillCount),
        label: 'Répartition par nombre de compétences',
        backgroundColor: '#34A853',
        borderColor: '#2E8B47',
        borderWidth: 1
      }]
    };
  }

  getTopSkills(limit: number = 5): { name: string; count: number }[] {
    if (!this.stats?.skillStats) return [];

    return Object.entries(this.stats.skillStats)
      .sort(([, a], [, b]) => b - a)
      .slice(0, limit)
      .map(([name, count]) => ({ name, count }));
  }

  getExperienceLevels(): { level: string; count: number }[] {
    if (!this.stats?.experienceLevelStats) return [];

    return Object.entries(this.stats.experienceLevelStats)
      .map(([level, count]) => ({ level, count }))
      .sort((a, b) => b.count - a.count);
  }

  refreshStats(): void {
    this.loadStats();
  }
}
