import { Injectable } from '@angular/core';
import {
  HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {ErrorService} from "./error.service";
import {catchError} from "rxjs/operators";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private errorService: ErrorService)
  {}

  public intercept(request: HttpRequest<unknown>, next: HttpHandler):
    Observable<HttpEvent<any>>
  {
    return next.handle(request).pipe(
      catchError( (error: HttpErrorResponse) => this.handleErrorRestCall(error))
    );

  } // end of method

  private handleErrorRestCall(err: HttpErrorResponse): Observable<any> {
    if (err.status < 200 || err.status >= 300) {
      // A REST call raised an error.  So, send the info to the ErrorService
      // Send the error message to the errorService
      this.errorService.addError(err);
    }

    // Throw the error
    return throwError(err);
  }
}
