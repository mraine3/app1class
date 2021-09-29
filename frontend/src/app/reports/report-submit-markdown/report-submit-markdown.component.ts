import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {ThemeService} from "../../services/theme.service";
import {Subscription} from "rxjs";
import {ThemeOptionDTO} from "../../models/theme-option-dto";

@Component({
  selector: 'app-report-submit-markdown',
  templateUrl: './report-submit-markdown.component.html',
  styleUrls: ['./report-submit-markdown.component.css']
})
export class ReportSubmitMarkdownComponent implements OnInit, OnDestroy {

  public myForm: FormGroup;
  private themeStateSubscription: Subscription;
  public currentTheme: ThemeOptionDTO;

  constructor(private formBuilder: FormBuilder,
              private themeService: ThemeService) { }


  public ngOnInit(): void {

    // Listen for changes from the theme service
    this.themeStateSubscription = this.themeService.getThemeStateAsObservable().subscribe( (aNewTheme: ThemeOptionDTO) => {
      // The theme has changed.
      this.currentTheme = aNewTheme;
    });

    // Initialize the form
    this.myForm = this.formBuilder.group({
      markdownEditor:       [null, null],
    });

  }  // end of ngOnInit()


  public ngOnDestroy(): void {
    if (this.themeStateSubscription) {
      this.themeStateSubscription.unsubscribe();
    }
  }


}
