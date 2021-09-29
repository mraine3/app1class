import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {Observable, Subscription} from "rxjs";
import {NavbarService} from "./services/navbar.service";
import {ErrorService} from "./errorHandler/error.service";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ErrorDialogComponent} from "./errorHandler/error-dialog/error-dialog.component";
import {ErrorDialogFormData} from "./errorHandler/error-dialog-form-data";
import {HttpErrorResponse} from "@angular/common/http";
import {BannerService} from "./services/banner.service";
import {animate, style, transition, trigger} from "@angular/animations";
import {tap} from "rxjs/operators";
import {PreferencesDTO} from "./models/preferences-dto";
import {ThemeService} from "./services/theme.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  animations: [
    trigger('fadeInOut', [
      transition(':enter', [   // :enter is alias to 'void => *'
        style({opacity:0}),
        animate(500, style({opacity:1}))
      ]),
      transition(':leave', [   // :leave is alias to '* => void'
        animate(500, style({opacity:0}))
      ])
    ])
  ]
})
export class AppComponent implements OnInit, OnDestroy, AfterViewInit {
  title = 'AngularApp1';

  public isAppNavVisible = true;       // The left nav starts out as visible
  public isUserNavVisible = false;    // The right nav starts out as not visible
  private showNavSubscription: Subscription;
  private bannerSubscription: Subscription;

  private errorSubscription: Subscription;
  private errorDialogIsOpen = false;
  private errorDialogRef: MatDialogRef<ErrorDialogComponent, any>;
  public showBannerOnPage: boolean;
  public disableAnimations: boolean = true;

  public bannerObs: Observable<PreferencesDTO>

  constructor(private navbarService: NavbarService,
              private errorService: ErrorService,
              private bannerService: BannerService,
              private themeService: ThemeService,
              private matDialog: MatDialog)
  { }


  public ngOnInit(): void {

    // Normally, we would get this value from a databsae lookup
    // If the theme name is not found, then the default light mode theme is displayed
    this.themeService.initialize("theme-from-database");

    this.bannerObs = this.bannerService.getLatestValueFromBackend().pipe(
      tap((aData: PreferencesDTO) => {
        // The REST call came back with some data

        // Initialize the banner service
        this.bannerService.initialize(aData.showBanner);
      })
    );


  this.bannerSubscription =
      this.bannerService.getStateAsObservable().subscribe( (aShowBanner: boolean) => {
          // We received a message from the Banner Service
          // If we receive false, then set the flag to false
          // If we receive true,  then set the flag to true
          this.showBannerOnPage = aShowBanner;
      });

    // This app-component will listen for messages from the navbarService
    this.showNavSubscription =
      this.navbarService.getNavbarStateAsObservable().subscribe((navbarState) => {
        // We received a message from the navbarService
        // -- Someone has toggled the one of the navbars

        // Set the public properties based on the navbarState properties returned
        this.isAppNavVisible = navbarState.isAppNavbarDisplayed;
        this.isUserNavVisible = navbarState.isUserNavbarDisplayed;
      });


    this.errorSubscription =
      this.errorService.getErrors().subscribe( (aError: HttpErrorResponse) => {
        // An error came in.  So, display the error in a popup

        // Create the form data object (to pass-in to the dialog box)
        let errorFormData: ErrorDialogFormData = new ErrorDialogFormData();
        errorFormData.error_text = aError.statusText;
        errorFormData.status_code = aError.status
        errorFormData.message = aError.error;      // NOTE:  Use aError.error here
        errorFormData.url = aError.url;

        if (this.errorDialogIsOpen) {
          // The error dialog is already open -- so close it
          this.errorDialogRef.close(false);
        }

        this.errorDialogIsOpen = true;

        // Open the Error Dialog
        // Do not set the height of dialog boxes.  Let them grow
        this.errorDialogRef = this.matDialog.open(ErrorDialogComponent, {
          minWidth: '400px',
          maxWidth: '800px',
          data: errorFormData
        });

        this.errorDialogRef.afterClosed().subscribe((formData: ErrorDialogFormData) => {
          // The error dialog box has closed
          this.errorDialogIsOpen = false;
        });

      });

  }


  public ngAfterViewInit(): void {

    setTimeout(() => {
      // The page has finished loading, so set the flag so that animations proceed
      // NOTE:  This flag must be set in a setTimeout for this trick to work.
      this.disableAnimations = false;
    }, 0);

  }

  public ngOnDestroy() {
    this.showNavSubscription.unsubscribe();

    if (this.errorSubscription) {
      this.errorSubscription.unsubscribe();
    }

    if (this.bannerSubscription) {
      this.bannerSubscription.unsubscribe();
    }

  }

}
