import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatsFreelancersComponent } from './stats-freelancers.component';

describe('StatsFreelancersComponent', () => {
  let component: StatsFreelancersComponent;
  let fixture: ComponentFixture<StatsFreelancersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatsFreelancersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StatsFreelancersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
