import { Injectable } from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import { Observable } from 'rxjs';
import {UserService} from "../services/user.service";
import {UserInfoDTO} from "../models/user-info-dto";
import {map} from "rxjs/operators";
import {Constants} from "../utilities/constants";

@Injectable({
  providedIn: 'root'
})
export class PageGuard implements CanActivate {

  public constructor(private router: Router,
                     private userService: UserService) {
  }



  public canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {

    return this.userService.getUserInfo().pipe(
      map( (userInfoDTO: UserInfoDTO) => {
        // Use the map operator to convert the Observable<UserInfoDTO> into an Observable<boolean>

        // Get the next url from the next variable
        let nextUrl: string | undefined = next.routeConfig?.path;
        if (! nextUrl) {
          // The user is going so an undefined page.  So, return an observable<false> so the router does not proceed
          return false;
        }

        // Check if the url is allowed
        let routeAllowed: boolean | undefined = userInfoDTO.pageRoutes.get(nextUrl);

        if (! routeAllowed) {
          // The route was not found in the map or holds False.  So, redirect the user to the Forbidden Page
          this.router.navigate([Constants.FORBIDDEN_ROUTE]).then();

          // Return false so that the router will not route the user to the new page
          return false;
        }

        // The route was allowed.  So, return an observable holding true
        return true;

      })  // end of map
    );  // end of pipe

  }  // end of canActivate()


}
