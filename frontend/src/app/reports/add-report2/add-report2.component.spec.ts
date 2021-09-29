import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddReport2Component } from './add-report2.component';

describe('AddReport2Component', () => {
  let component: AddReport2Component;
  let fixture: ComponentFixture<AddReport2Component>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddReport2Component ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddReport2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
