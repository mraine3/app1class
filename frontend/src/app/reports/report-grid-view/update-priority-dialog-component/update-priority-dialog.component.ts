import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {UpdatePriorityDialogFormData} from "../../../models/update-priority-dialog-form-data";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {LookupService} from "../../../services/lookup.service";
import {LookupDTO} from "../../../models/lookup-dto";
import {Observable} from "rxjs";

@Component({
  selector: 'app-update-priority-dialog-component',
  templateUrl: './update-priority-dialog.component.html',
  styles: []
})
export class UpdatePriorityDialogComponent implements OnInit {
  public myForm: FormGroup;
  public priorityObs: Observable<LookupDTO[]>

  /*
   * Inject Useful Dialog Elements:
   *  1) Inject a reference to this dialog itself    (dialogRef)
   *  2) Inject a reference to the passed-in data    (data)
   */
  constructor(private lookupService: LookupService,
              private formBuilder: FormBuilder,
              private dialogRef: MatDialogRef<UpdatePriorityDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: UpdatePriorityDialogFormData)
  { }

  public ngOnInit(): void {
    // Initialize the form
    this.myForm = this.formBuilder.group({
      newPriority: [null, Validators.required]
    });

    // Get the observable to the List of LookupDTO objects
    // NOTE:  The AsyncPipe will subscribe and unsubscribe automatically
    this.priorityObs = this.lookupService.getLookupWithTypeAndOrder("priority", "display_order");
  }

  /*
   * User pressed Cancel so close this dialog box and return null
   */
  public cancelClicked() {
    // Close this dialog box and return null back
    this.dialogRef.close(null)
  }

  /*
   * User pressed OK so close this dialog box and return the formData object with info
   */
  public okClicked() {
    // Show the form validation errors.
    this.myForm.markAllAsTouched();

    if (this.myForm.invalid) {
      // The form is invalid.  So, stop here and let the user see the error message
      return;
    }

    // Get the priorityId from the form
    this.data.newPriorityId = this.myForm.controls.newPriority.value;

    // Close this dialog box and return the formData object back
    this.dialogRef.close(this.data)
  }

}
