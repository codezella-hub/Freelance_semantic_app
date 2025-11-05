import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateFreelancersComponent } from './update-freelancers.component';

describe('UpdateFreelancersComponent', () => {
  let component: UpdateFreelancersComponent;
  let fixture: ComponentFixture<UpdateFreelancersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateFreelancersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateFreelancersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
