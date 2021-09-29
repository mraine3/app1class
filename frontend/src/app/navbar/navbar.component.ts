import {Component, OnDestroy, OnInit} from '@angular/core';
import {Constants} from "../utilities/constants";
import {UserService} from "../services/user.service";
import {Observable, Subscription} from "rxjs";
import {UserInfoDTO} from "../models/user-info-dto";
import {ThemeService} from "../services/theme.service";
import {ThemeOptionDTO} from "../models/theme-option-dto";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {

  public reportsNavGroupClosed: boolean = false;   // Open Reports  section on page load
  public analyticsGroupClosed:  boolean = true;    // Close Analytics section on page load

  public userInfoObs: Observable<UserInfoDTO>

  private themeStateSubscription: Subscription;
  public currentTheme: ThemeOptionDTO;


  constructor(private userService: UserService,
              private themeService: ThemeService) { }


  public ngOnInit(): void {

    // Listen for changes from the theme service
    this.themeStateSubscription = this.themeService.getThemeStateAsObservable().subscribe( (aNewTheme: ThemeOptionDTO) => {
      // The theme has changed.
      this.currentTheme = aNewTheme;
    });

    // Setup an observable to get the UserInfo
    // NOTE:  The HTML Template uses an async pipe to subscribe and unsubscribe to this observable
    this.userInfoObs = this.userService.getUserInfo();
  }


  public ngOnDestroy(): void {
    if (this.themeStateSubscription) {
      this.themeStateSubscription.unsubscribe();
    }
  }



  public toggleNavGroup(aNavGroupNumber: number) {
    if (aNavGroupNumber == 1) {
      // User clicked on the Reports navgroup (so hide the other nav-group)
      this.analyticsGroupClosed = true;

      // Toggle the Reports navgroup (So, it switches from opened to close)(
      this.reportsNavGroupClosed = ! this.reportsNavGroupClosed;
    }
    else if (aNavGroupNumber == 2) {
      // User clicked on the Analytics navgroup (so hide the other nav-group)
      this.reportsNavGroupClosed = true;

      // Toggle the Analytics navgroup
      this.analyticsGroupClosed = ! this.analyticsGroupClosed;
    }
  }

  public downloadHelpFile(): void {
    // Open the help.pdf in another tab
    window.open('./assets/help.pdf', "_blank");
  }

  public get constants(): typeof Constants {
    // Get a reference to the enumerated object
    // -- This is needed so that the html page can use the enum class
    return Constants;
  }
}
