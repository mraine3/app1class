import { Injectable } from '@angular/core';
import {ThemeOptionDTO} from "../models/theme-option-dto";
import {BehaviorSubject, Observable, of} from "rxjs";
import {StyleManagerService} from "./style-manager.service";

@Injectable({
  providedIn: 'root'
})
export class ThemeService {

  private themeStateSubject: BehaviorSubject<ThemeOptionDTO>;

  private readonly DEFAULT_THEME_NAME: string = "deeppurple-amber-light1";

  private options: ThemeOptionDTO[] = [
    {
      label: "Light Mode",
      themeName: "deeppurple-amber-light1",
      isLightMode: true
    },
    {
      label: "Dark Mode",
      themeName: "purple-green-dark1",
      isLightMode: false
    }
  ];

  constructor(private styleManagerService: StyleManagerService) {  }


  public getThemeStateAsObservable(): Observable<ThemeOptionDTO> {
    return this.themeStateSubject.asObservable();
  }


  /*
   * Initialize the theme service by setting an initial theme name
   * And, then use the BehaviorSubject to emit that new value to the header, navbar, grids, ....
   */
  public initialize(aThemeName: string) {

    for (const option of this.options) {
      if (option.themeName == aThemeName) {
        // Use the StyleManagerService to tell Angular Material to change the theme
        this.styleManagerService.setStyle(
          "theme",
          `assets/themes/${option.themeName}.css`
        );

        this.themeStateSubject = new BehaviorSubject<ThemeOptionDTO>(option);
        return;
      }
    }

    // I did not find the theme name from the database.  So, emit the default theme
    for (const option of this.options) {
      if (option.themeName == this.DEFAULT_THEME_NAME) {
        // Use the StyleManagerService to tell Angular Material to change the theme
        this.styleManagerService.setStyle(
          "theme",
          `assets/themes/${option.themeName}.css`
        );

        this.themeStateSubject = new BehaviorSubject<ThemeOptionDTO>(option);
        return;
      }
    }

  }



  /*
   * Get a list of themes to display in the popup menu
   */
  public getThemeOptions(): Observable<ThemeOptionDTO[]> {

    return of(this.options);
  }


  /*
   * The user has clicked on a different theme
   */
  public setTheme(aNewTheme: ThemeOptionDTO): void {

    // Use the StyleManagerService to tell Angular Material to change the theme
    this.styleManagerService.setStyle(
      "theme",
      `assets/themes/${aNewTheme.themeName}.css`
    );

    // Send a message out to the header/navbar/grid pages (telling them that the theme has changed)
    this.themeStateSubject.next(aNewTheme);
  }


}
