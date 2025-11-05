import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecommendFreelancersComponent } from './recommend-freelancers.component';

describe('RecommendFreelancersComponent', () => {
  let component: RecommendFreelancersComponent;
  let fixture: ComponentFixture<RecommendFreelancersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecommendFreelancersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecommendFreelancersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
