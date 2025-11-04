import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjetDashboardComponent } from './projet-dashboard.component';

describe('ProjetDashboardComponent', () => {
  let component: ProjetDashboardComponent;
  let fixture: ComponentFixture<ProjetDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjetDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjetDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
