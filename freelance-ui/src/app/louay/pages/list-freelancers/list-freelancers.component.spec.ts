import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListFreelancersComponent } from './list-freelancers.component';

describe('ListFreelancersComponent', () => {
  let component: ListFreelancersComponent;
  let fixture: ComponentFixture<ListFreelancersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListFreelancersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListFreelancersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
