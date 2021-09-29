import {Component, OnInit} from '@angular/core';
import {ThemeService} from "../services/theme.service";
import {Observable} from "rxjs";
import {ThemeOptionDTO} from "../models/theme-option-dto";
import {first} from "rxjs/operators";

@Component({
  selector: 'app-theme-changer-menu',
  templateUrl: './theme-changer-menu.component.html',
  styleUrls: ['./theme-changer-menu.component.css']
})
export class ThemeChangerMenuComponent implements OnInit {

  public selectedThemeName: string;
  public themeOptionsObs: Observable<ThemeOptionDTO[]>;


  constructor(private themeService: ThemeService) {
    this.themeOptionsObs = this.themeService.getThemeOptions();

    // This component is initializing.  So, get the *frst* value from the themeService
    // (so we can show the correct radio button pre-selected)
    this.themeService.getThemeStateAsObservable().pipe(
      first()).subscribe( (aNewTheme: ThemeOptionDTO) => {
        // We got a first value from the theme service

        // Use that initial value to set the selected theme name (so the radio button is selected in the html)
        this.selectedThemeName = aNewTheme.themeName;
    });
  }

  ngOnInit(): void {
  }

  public changeTheme(aThemeName: ThemeOptionDTO) {
    this.selectedThemeName = aThemeName.themeName;

    this.themeService.setTheme(aThemeName);
  }

}
