import { Injectable } from '@angular/core';
import {Observable, Subject} from "rxjs";
import {HttpErrorResponse} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  private errors = new Subject<HttpErrorResponse>();

  constructor() { }


  public addError(aError: HttpErrorResponse): void {
    this.errors.next(aError);
  }


  public getErrors(): Observable<HttpErrorResponse> {
    return this.errors.asObservable();
  }

}
