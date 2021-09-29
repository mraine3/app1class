import { Injectable } from '@angular/core';
import {UserInfoDTO} from "../models/user-info-dto";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {map, shareReplay} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  // Internal cache of the userInfo object
  private cachedObservable: Observable<UserInfoDTO> | null = null;

  constructor(private httpClient: HttpClient) { }

  /*
   * Return an observable that holds information about the user
   * -- The UserInfoDTO holds the user's name and map of routes
   */
  public getUserInfo(): Observable<UserInfoDTO> {

    if (this.cachedObservable != null) {
      // This observable is in the cache.  So, return it from the cache
      return this.cachedObservable;
    }

    // Construct the URL of the REST call
    const restUrl = environment.baseUrl + '/api/user/me';

    // Get the observable and store it in the internal cache
    this.cachedObservable = this.httpClient.get <UserInfoDTO>(restUrl).pipe(
        map( (userInfoDTO: UserInfoDTO) => {

          // Convert the userInfoDTO.pageRoutes into a map
          // So that the PageGuard does not have to do it repeatedly
          let mapPageRoutes: Map<string, boolean> = new Map(Object.entries(userInfoDTO.pageRoutes));

          userInfoDTO.pageRoutes = mapPageRoutes;

          return userInfoDTO;
        }),
       shareReplay(1)
      );

    // Return the cached observable
    return this.cachedObservable;

  } // end of getUserInfo

}
