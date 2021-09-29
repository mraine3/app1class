import { Injectable } from '@angular/core';
import {Observable, of} from "rxjs";
import {AutoCompleteMatchDTO} from "../models/auto-complete-match-dto";

@Injectable({
  providedIn: 'root'
})
export class ElasticSearchService {

  constructor() { }


  public runSearch(aRawQuery: string, aTotalReturnsToReturn: number): Observable<AutoCompleteMatchDTO[]> {
    if (aRawQuery == '') {
      // The search box is empty so return an empty list (and do not run a search)
      return of( [] );
    }

    else if (aRawQuery.startsWith('a')) {

      // Return hard-coded observable with 3 strings that start with A
      return of([
        {
          id: 1,
          name: "Amazon"
        },
        {
          id: 2,
          name: "Apple",
        },
        {
          id: 3,
          name: "American Airlines"
        }]);
    }

    else if (aRawQuery.startsWith('b')) {

      // Return hard-coded observable with 3 strings that start with B
      return of([
        {
          id: 10,
          name: "Best Buy"
        },
        {
          id: 11,
          name: "Boeing",
        },
        {
          id: 12,
          name: "Bed, Bath, and Beyond"
        }]);
    }

    else {
      // No matches were found, so return an observable with an empty array
      return of( [] );
    }
  }  // end of runSearch()

}
