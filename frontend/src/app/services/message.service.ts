import { Injectable } from '@angular/core';
import {MatSnackBar} from "@angular/material/snack-bar";

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  public constructor(private snackBar: MatSnackBar) { }


  public showSuccessMessage(message: string): void {

    this.snackBar.open(message, 'Done',
      {
        duration: 6000,        // Close the popup after 6 seconds
        verticalPosition: 'bottom',
        horizontalPosition: 'right',
        panelClass: ['success-snackbar']
      });
  }


  public showWarningMessage(message: string): void {

    this.snackBar.open(message, 'Done',
      {
        duration: 6000,        // Close the popup after 6 seconds
        verticalPosition: 'bottom',
        horizontalPosition: 'right',
        panelClass: ['warning-snackbar']
      });
  }


  public showErrorMessage(message: string): void {

    this.snackBar.open(message, 'Done',
      {
        duration: 6000,        // Close the popup after 6 seconds
        verticalPosition: 'bottom',
        horizontalPosition: 'right',
        panelClass: ['error-snackbar']
      });
  }




}

