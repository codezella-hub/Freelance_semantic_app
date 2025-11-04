import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EvaluationListComponent } from './evaluation-list.component';

describe('EvaluationListComponent', () => {
  let component: EvaluationListComponent;
  let fixture: ComponentFixture<EvaluationListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EvaluationListComponent]
    });
    fixture = TestBed.createComponent(EvaluationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
