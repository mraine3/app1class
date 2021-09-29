
import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavbarService} from "../services/navbar.service";
import {ThemeService} from "../services/theme.service";
import {ThemeOptionDTO} from "../models/theme-option-dto";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {

  private themeStateSubscription: Subscription;
  public  currentTheme: ThemeOptionDTO;


  constructor(private navbarService: NavbarService,
              private themeService: ThemeService)
  {}


  public ngOnInit(): void {

    // Listen for changes from the theme service
    this.themeStateSubscription = this.themeService.getThemeStateAsObservable().subscribe( (aNewTheme: ThemeOptionDTO) => {
      // The theme has changed.
      this.currentTheme = aNewTheme;
    });

  }

  public ngOnDestroy(): void {
    if (this.themeStateSubscription) {
      this.themeStateSubscription.unsubscribe();
    }
  }


  public toggleAppNavbar(): void {
    // Send a message to the navbarService (to tell it to toggle)
    this.navbarService.toggleAppNavbar();
  }

  public toggleUserNavbar(): void {
    this.navbarService.toggleUserNavbar();
  }
}
