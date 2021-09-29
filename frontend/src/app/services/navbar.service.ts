
import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";

class NavbarState {
  isAppNavbarDisplayed: boolean;
  isUserNavbarDisplayed: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class NavbarService  {

  private navbarStateSubject: BehaviorSubject<NavbarState>;
  private navbarState: NavbarState = new NavbarState();


  public constructor() {
    // Initialize the navbarState

    // The AppNavBar will be visible on startup
    this.navbarState.isAppNavbarDisplayed = true;

    // The UserNavBar will not be visible on startup
    this.navbarState.isUserNavbarDisplayed = false;

    // Start of the navbar state with an initial value
    this.navbarStateSubject = new BehaviorSubject(this.navbarState)
  }


  public getNavbarStateAsObservable(): Observable<NavbarState> {
    return this.navbarStateSubject.asObservable();
  }

  public toggleUserNavbar(): void {
    this.navbarState.isUserNavbarDisplayed = !this.navbarState.isUserNavbarDisplayed;

    if ((this.navbarState.isUserNavbarDisplayed) && (this.navbarState.isAppNavbarDisplayed)) {
      // The user is showing the User Navbar and the App Navbar is presently visible

      // So, hide the App Navbar (so that only *ONE* navbar is visible at a time)
      this.navbarState.isAppNavbarDisplayed = false;
    }

    // Send a message to the user-navbar (to tell the navbar to show or hide)
    this.navbarStateSubject.next(this.navbarState);
  }



  public toggleAppNavbar(): void {
    this.navbarState.isAppNavbarDisplayed = !this.navbarState.isAppNavbarDisplayed;

    if ((this.navbarState.isUserNavbarDisplayed) && (this.navbarState.isAppNavbarDisplayed)) {
      // The user is showing the App Navbar and the User Navbar is presently visible

      // So, hide the User Navbar (so that only *ONE* navbar is visible at a time)
      this.navbarState.isUserNavbarDisplayed = false;
    }

    // Send a message to the user-navbar (to tell the navbar to show or hide)
    this.navbarStateSubject.next(this.navbarState);
  }

}
