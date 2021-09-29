import { Component, OnInit } from '@angular/core';
import {FormControl} from "@angular/forms";
import {Observable} from "rxjs";
import {AutoCompleteMatchDTO} from "../../models/auto-complete-match-dto";
import {debounceTime, startWith, switchMap} from "rxjs/operators";
import {ElasticSearchService} from "../../services/elastic-search.service";
import {Router} from "@angular/router";
import {Constants} from "../../utilities/constants";

@Component({
  selector: 'app-search-box',
  templateUrl: './search-box.component.html',
  styleUrls: ['./search-box.component.css']
})
export class SearchBoxComponent implements OnInit {

  public searchTextBox: FormControl = new FormControl();
  public searchMatchesToShowObs: Observable<AutoCompleteMatchDTO[]>;

  constructor(private elasticSearchService: ElasticSearchService,
              private router: Router) { }

  public ngOnInit(): void {

    // Listen for changes on the search text box
    this.searchMatchesToShowObs = this.searchTextBox.valueChanges
      .pipe(
        startWith(''),
        debounceTime(250),                  // Wait 250 msecs to give the user some time to type
        switchMap((aRawQuery: string) => {   // Use switchMap for its cancelling effect:  On each observable, the previous observable is cancelled
          // The user has typed-in something

          // Return an observable to the search (but only return up to 5 results)
          // NOTE:  The <mat-options> tag has an async pipe that will *invoke* this REST call
          return this.elasticSearchService.runSearch(aRawQuery, 5);
        })
      );

  }  // end of ngOnInit()

  /*
   * The user selected an auto-complete entry
   * So, clear the textbox and take the user to the details page
   */
  public goToDetailsPage(aMatch: AutoCompleteMatchDTO): void {
    console.log('aMatch.id='+ aMatch?.id + '  aMatch.name=' + aMatch?.name);

    // Clear the search box
    this.searchTextBox.setValue('');

    // Navigate to the new page
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(()=> {
      // This is needed to ensure that the details page gets reloaded
      this.router.navigate([Constants.SEARCH_DETAILS_ROUTE, aMatch.id]).then(() =>{} )
    });

  }

}
