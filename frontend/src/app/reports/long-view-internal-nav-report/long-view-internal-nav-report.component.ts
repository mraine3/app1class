import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-long-view-internal-nav-report',
  templateUrl: './long-view-internal-nav-report.component.html',
  styleUrls: ['./long-view-internal-nav-report.component.css']
})
export class LongViewInternalNavReportComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }


  /*
   * Scroll the page into view so the user can see the tag that has id=" "
   */
  public scrollToTargetId(aElementId: string): void {
    // Get a reference to the DOM element
    const el: HTMLElement|null = document.getElementById(aElementId);

    if (el) {
      // The DOM element exists.  So, scroll to it.
      setTimeout(() =>
        el.scrollIntoView({behavior: 'smooth', block: 'start', inline: 'nearest'}), 0);
    }
  }


  /*
   * Scroll the page into view so the user can see the *FIRST* tag that has class=" "
   * NOTE:  If you need to scroll to a class item in an innerHtml, then this would work
   */
  public scrollToTargetClass(aClassName: string): void {
    const elementList = document.getElementsByClassName(aClassName);
    const element = elementList[0] as HTMLElement;

    if (element) {
      setTimeout(() =>
        element.scrollIntoView({behavior: 'smooth', block: 'start', inline: 'nearest'}), 0);
    }
  }


}
