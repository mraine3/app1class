import {
  AfterViewInit,
  Component,
  ElementRef,
  HostListener,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';
import {environment} from "../../../environments/environment";
import {CdkDragEnd} from "@angular/cdk/drag-drop";
import {NavbarService} from "../../services/navbar.service";
import {Subscription} from "rxjs";
import {ThemeOptionDTO} from "../../models/theme-option-dto";
import {ThemeService} from "../../services/theme.service";

@Component({
  selector: 'app-pdf-viewer',
  templateUrl: './pdf-viewer.component.html',
  styleUrls: ['./pdf-viewer.component.css']
})
export class PdfViewerComponent implements OnInit, OnDestroy, AfterViewInit {

  @ViewChild("leftDiv") leftDiv: ElementRef;   // Used to get the width of left column
  @ViewChild("rightDiv") rightDiv: ElementRef; // used to get the width of right column

  public rightFlexValue: string = "50%";    // Used to set the width of the right column
  public leftFlexValue: string = "50%";     // Used to set the width of the left column
  public showDivider: boolean = true;       // Used to refresh the divider

  public  pdfSrc: string | null = null;           // Has the full path of the pdf file that the viewer will open
  private pdfSrcSampleDirectoryPath: string;      // Holds the url path to get to the assets/sample/ directory


  private secondResize: boolean = false;
  private columnResize: boolean = false;
  private navbarSubscription: Subscription;
  private adjustWidthOffsetForNavbar: number;
  private previousPageWidth: number;
  private previousLeftPageWidth: number;
  private previousRightPageWidth: number;


  private themeStateSubscription: Subscription;
  public  pdfViewerBackgroundColor: string;


  constructor(private navbarService: NavbarService,
              private themeService: ThemeService) { }



  public ngAfterViewInit(): void {
    this.previousLeftPageWidth = this.leftDiv.nativeElement.offsetWidth;
    this.previousRightPageWidth = this.rightDiv.nativeElement.offsetWidth;
  }


  public ngOnInit(): void {

    // Listen for changes from the theme service
    this.themeStateSubscription = this.themeService.getThemeStateAsObservable().subscribe( (aNewTheme: ThemeOptionDTO) => {
      // The theme has changed.

      if (aNewTheme.isLightMode) {
        // Set a light background color for PDF Viewer
        this.pdfViewerBackgroundColor = "#dcdcdc";
      }
      else {
        // Set a dark background color for PDF Viewer
        this.pdfViewerBackgroundColor = "#303030";
      }
    });

    // Setup the path of the sample directory path
    if (environment.production) {
      this.pdfSrcSampleDirectoryPath = "/app1/assets/sample/";
    }
    else {
      this.pdfSrcSampleDirectoryPath = environment.baseUrl + "/assets/sample/";
    }

    // This app-component will listen for messages from the navbarService
    this.navbarSubscription =
      this.navbarService.getNavbarStateAsObservable().subscribe((navbarState) => {
        // We received a message from the navbarService
        // -- Someone has toggled the one of the navbars

        if (navbarState.isAppNavbarDisplayed) {
          // The left-side navbar is visible (so the we need to subtract it from our calculations)
          this.adjustWidthOffsetForNavbar = -200;
        }
        else {
          this.adjustWidthOffsetForNavbar = 0;
        }

        this.previousPageWidth = window.innerWidth + this.adjustWidthOffsetForNavbar;

        this.refreshDivider();
      });

  }  // end of ngOnInit()




  public ngOnDestroy(): void {
    if (this.themeStateSubscription) {
      this.themeStateSubscription.unsubscribe();
    }

    if (this.navbarSubscription) {
      this.navbarSubscription.unsubscribe();
    }
  }

  public showSampleFileInPdfViewer(aFilename: string): void {
    // Set the pdfSrc file path
    // NOTE:  IF the pdfSrc file path is valid, then pdf viewer will display the PDF file
    this.pdfSrc = this.pdfSrcSampleDirectoryPath + aFilename;
  }


  public clearPdfViewer(): void {
    this.pdfSrc = null;
  }


  private refreshDivider(): void {
    // Hide the divider
    this.showDivider = false;

    // use setTimeout to show the divider
    // -- Show the divider (which will cause the divider to be placed in the correct spot)
    setTimeout(() => {
      // Show the divider (inside setTimeout, Angular change detection will pick this up)
      this.showDivider = true;

      // Set the columnResize flag to false so that the onResize() method knows to ignore this
      this.columnResize = false;
    }, 1);
  }


  /*
   * The drag-and-drop operation finished
   *
   *  1) Calculate the new width of the left and right columns
   *  2) Set the new widths of the left and right columns
   *  3) Send a resize event (so the pdf-viewer resizes)
   *  4) Show & Hide the divider (so it appears in the correct place)
   */
  public dragEnded(aEvent: CdkDragEnd): void {

    const SMALLEST_LEFT_SIDE_IN_PIXELS: number = 100;
    const SMALLEST_RIGHT_SIDE_IN_PIXELS: number = 325;

    // Get the total number of pixels that were dragged
    let changeInX: number = aEvent.distance.x;

    let oldWidthTotal =  this.leftDiv.nativeElement.offsetWidth + this.rightDiv.nativeElement.offsetWidth;

    // Calculate the new widths of the left and right columns
    let leftSideNewWidth = this.leftDiv.nativeElement.offsetWidth + changeInX;
    let rightSideNewWidth = this.rightDiv.nativeElement.offsetWidth - changeInX;


    if (leftSideNewWidth < SMALLEST_LEFT_SIDE_IN_PIXELS) {
      // The left side is too small.

      // Set the left side=(smallest size) and right side=(what's left)
      leftSideNewWidth = SMALLEST_LEFT_SIDE_IN_PIXELS;
      rightSideNewWidth = oldWidthTotal - leftSideNewWidth;
    }
    else if (rightSideNewWidth < SMALLEST_RIGHT_SIDE_IN_PIXELS) {
      // The right side is too small.

      // Set the right side=(smallest size) and left side=(what's left)
      rightSideNewWidth = SMALLEST_RIGHT_SIDE_IN_PIXELS;
      leftSideNewWidth = oldWidthTotal - rightSideNewWidth;
    }

    // Set the new widths of the left and right columns
    this.leftFlexValue = leftSideNewWidth + "px";
    this.rightFlexValue = rightSideNewWidth + "px";

    // Store the previous left and right widths  (we need this if the user resizes the browser)
    this.previousLeftPageWidth = leftSideNewWidth;
    this.previousRightPageWidth = rightSideNewWidth;

    // Set columnResize = true (so our own resize listener does nothing)
    this.columnResize = true;

    // Send a resize event so that the pdf viewer will resize
    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 1);

    // Refresh the divider (so it appears the correct location)
    this.refreshDivider();


  }  // end of dragEnded()



  /*
   * We received a resize event
   *  1) If columnResize==true, then we kicked-off this resize event (so ignore it)
   *  2) If secondResize==true, then just refresh the divider
   *  3) Else
   *     a. Calculate the ratio of the old-width to new-width
   *     b. Calculate the newLeftWidth = ratio * oldLeftWidth
   *     c. Calculate the newRightWidth = ratio * oldRightWidth
   *     d. Set the new leftWidth and newRightWidths
   */
  @HostListener('window:resize', ['$event'])
  public onResize(event: any): void {
    if (this.columnResize) {
      return;
    }

    if (this.secondResize) {
      this.showDivider = true;
      this.secondResize = false;
      return;
    }

    // Calculate the ratio of the old width to new-width
    let newPageWidth: number = event.target.innerWidth + this.adjustWidthOffsetForNavbar;
    let ratioOfPageWidths: number = newPageWidth / this.previousPageWidth;

    // Calculate the new left and right side widths
    let newLeftSideWidth: number = this.previousLeftPageWidth * ratioOfPageWidths;
    let newRightSideWidth: number = this.previousRightPageWidth * ratioOfPageWidths;

    // Set the new widths of the left and right columns
    this.leftFlexValue = String(newLeftSideWidth) + "px";
    this.rightFlexValue = String(newRightSideWidth) + "px";

    // Refresh the divider by hiding it and showing it
    this.showDivider = false;

    // Send a resize event so that the pdf viewer will resize
    setTimeout(() => {
      this.secondResize = true;
      window.dispatchEvent(new Event('resize'));
    }, 1);

    // Store the current page width as (previousPageWidth)
    this.previousLeftPageWidth = newLeftSideWidth;
    this.previousRightPageWidth = newRightSideWidth;
    this.previousPageWidth = newPageWidth;
  }


}
