import {Observable, of, Subject} from "rxjs";
import {catchError, shareReplay} from "rxjs/operators";

export class LoadingWrapper<T> {
  private readonly _errorLoadingObs = new Subject<boolean>();

  public readonly errorLoadingObs: Observable<boolean> = this._errorLoadingObs.pipe(
    shareReplay(1)
  );

  public readonly dataObs: Observable<T | null>;

  public constructor(aData: Observable<T>) {
    this.dataObs = aData.pipe(
      shareReplay(1),
      catchError(error => {
        // Log an error in the console here
        console.log(error);

        // Emit a "truthy" value for errorLoadingObs
        this._errorLoadingObs.next(true);

        // Recover the observable and give it a "falsy" value of null
        return of(null);
      })
    );

  }  // end of constructor

}
