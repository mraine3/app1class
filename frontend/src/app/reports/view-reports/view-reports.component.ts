import { Component, OnInit } from '@angular/core';
import {ReportService} from "../../services/report.service";
import {GetReportDTO} from "../../models/get-report-dto";
import {Observable} from "rxjs";
import {Router} from "@angular/router";
import {environment} from "../../../environments/environment";
import {Constants} from "../../utilities/constants";

@Component({
  selector: 'app-view-reports',
  templateUrl: './view-reports.component.html',
  styleUrls: ['./view-reports.component.css']
})
export class ViewReportsComponent implements OnInit {

  public allReportsObs: Observable<GetReportDTO[]>;

  constructor(private reportService: ReportService,
              private router: Router) { }

  public ngOnInit(): void {
    // Get an observable to the REST call that will retrieve all reports
    // NOTE:  The Async Pipe will subscribe and unsubscribe from this automatically
    this.allReportsObs = this.reportService.getAllReports();
  }

  public goToEditReport(aReportId: number): void {
    // Take the user to the Edit Report page and pass-in the reportId
    this.router.navigate([Constants.EDIT_REPORT_ROUTE,   aReportId]).then();
  }

  public goToLongView(aReportId: number): void {
    // Take the user to the Long View page and pass-in the reportId
    this.router.navigate([Constants.LONGVIEW_INTERNAL_NAV_REPORT,   aReportId]).then();
  }

  public download(aFileId: number): void {
    const url = environment.baseUrl + `/api/reports/download/excel/${aFileId}`;
    window.open(url);
  }

}
