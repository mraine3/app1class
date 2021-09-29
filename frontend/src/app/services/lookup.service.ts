import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {EMPTY, Observable} from "rxjs";
import {LookupDTO} from "../models/lookup-dto";
import {catchError, shareReplay} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class LookupService {

  // The cache holds key=type value=list of LookupDTO objects
  private cache: any = {};

  constructor(private httpClient: HttpClient) { }

  /*
   * Return a list of LookupDTO objects that correspond to the passed-in type name -- e.g, 'priority'
   */
  public getLookupWithType(aType: string): Observable<LookupDTO[]>  {

    if (this.cache[aType]) {
      // This observable is in the cache.  So, return it from the cache
      return this.cache[aType];
    }

    const restUrl = environment.baseUrl + '/api/lookups/' + aType;

    this.cache[aType] = this.httpClient.get <LookupDTO[]>(restUrl).pipe(
      shareReplay(1),
      catchError(err => {
        console.error('There was an error getting lookup value ' + aType + '.  Error is ', err);
        delete this.cache[aType];
        return EMPTY;
      }));

    return this.cache[aType];
  }


  /*
   * Return a sorted list of LookupDTO objects that correspond to the passed-in type name -- e.g, 'priority'
   */
  public getLookupWithTypeAndOrder(aType: string, aOrderBy: string): Observable<LookupDTO[]>  {
    let cacheKey = aType + ',' + aOrderBy;

    if (this.cache[cacheKey]) {
      // This observable is in the cache.  So, return it from the cache
      return this.cache[cacheKey];
    }

    const restUrl = environment.baseUrl + '/api/lookups/' + aType + '/' + aOrderBy;

    // Invoke the REST call and add this observable to the cache.
    this.cache[cacheKey] = this.httpClient.get <LookupDTO[]>(restUrl).pipe(
      shareReplay(1),
      catchError(err => {
        console.error('There was an error getting lookup value ' + aType + '.  Error is ', err);
        delete this.cache[aType];
        return EMPTY;
      }));

    return this.cache[cacheKey];
  }

}
