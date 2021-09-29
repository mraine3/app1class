import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ReportService} from "../../services/report.service";
import {NgForm} from "@angular/forms";

export class Report {
  name: string | null;
  priority: number | null;
  source: number | null;
  authors: string | null;
  start_date: Date | null;
  end_date: Date | null;
}

@Component({
  selector: 'app-add-report',
  templateUrl: './add-report.component.html',
  styleUrls: ['./add-report.component.css']
})
export class AddReportComponent implements OnInit, AfterViewInit {
  @ViewChild('name',  { read: ElementRef }) reportNameTextbox: ElementRef;

  public report: Report;
  public formSubmitted: boolean = false;
  public defaultReportStartDate: Date = this.getFirstDayOfPreviousMonth();

  // Inject the ReportService into this component
  constructor(private reportService: ReportService) { }

  ngOnInit() {
    this.report = new Report();
    this.report.name = null;
    this.report.priority = null;
    this.report.source = null;
    this.report.authors = null;
    this.report.start_date = this.getFirstDayOfPreviousMonth();
    this.report.end_date = null;


    // Use the ReportService
    this.reportService.showMessage('Init called');
  }

  ngAfterViewInit(): void {
    // Set the focus to the report name textbox
    setTimeout(() => this.reportNameTextbox.nativeElement.focus(), 0);
  }

  public reset(aForm:  NgForm): void {
    this.formSubmitted = false;

    // Reset the form back to pristine/untouched condition
    aForm.resetForm();

    this.report.start_date = this.getFirstDayOfPreviousMonth();
  }

  public save(aForm: NgForm): void {
    this.formSubmitted = true;

    // Mark all form fields as touched -- so that error validation displays
    aForm.form.markAllAsTouched();

    if (aForm.valid) {
      // Invoke a service to save the record
      console.log("Save record.");

      // Reset the form
      aForm.resetForm();
      this.formSubmitted = false;
    }
  }

  private getFirstDayOfPreviousMonth(): Date {
    let now = new Date();
    let firstDayPrevMonth = new Date(now.getFullYear(), now.getMonth() - 1, 1);
    return firstDayPrevMonth;
  }

}
