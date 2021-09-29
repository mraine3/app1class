import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdatePriorityDialogComponent } from './update-priority-dialog.component';

describe('UpdatePriorityDialogComponentComponent', () => {
  let component: UpdatePriorityDialogComponent;
  let fixture: ComponentFixture<UpdatePriorityDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UpdatePriorityDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdatePriorityDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
