import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {DashboardDataDTO} from "../models/dashboard-data-dto";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  constructor(private httpClient: HttpClient) { }


  public getAllChartData(): Observable<DashboardDataDTO> {
    // Construct the URL of the REST call
    const restUrl = environment.baseUrl + '/api/dashboard/chart/data';

    // Return an observable
    return this.httpClient.get <DashboardDataDTO>(restUrl);
  }


}
