import { Injectable } from '@angular/core';
import {ReportRowDataDTO} from "../models/report-row-data-dto";
import {Observable, of} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {GridGetRowsRequestDTO} from "../models/grid/grid-get-rows-request-dto";
import {GridGetRowsResponseDTO} from "../models/grid/grid-get-rows-response-dto";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class GridService {

  constructor(private httpClient: HttpClient) { }

  public getReportData(): Observable<ReportRowDataDTO[]> {
    let data: ReportRowDataDTO[] = [
        { id: 1, name: 'Report 1', priority: 'low', 'start_date': '05/01/2019', 'end_date': '05/05/2019'},
        { id: 2, name: 'Report 2', priority: 'medium', 'start_date': '06/01/2019', 'end_date': '06/06/2019'},
        { id: 3, name: 'Report 3', priority: 'high', 'start_date': '07/01/2019', 'end_date': '07/07/2019'},
        { id: 1, name: 'Report 4', priority: 'low', 'start_date': '05/01/2019', 'end_date': '05/05/2019'},
        { id: 2, name: 'Report 5', priority: 'medium', 'start_date': '06/01/2019', 'end_date': '06/06/2019'},
        { id: 3, name: 'Report 6', priority: 'high', 'start_date': '07/01/2019', 'end_date': '07/07/2019'},
        { id: 1, name: 'Report 7', priority: 'low', 'start_date': '05/01/2019', 'end_date': '05/05/2019'},
        { id: 2, name: 'Report 8', priority: 'medium', 'start_date': '06/01/2019', 'end_date': '06/06/2019'},
        { id: 3, name: 'Report 9', priority: 'high', 'start_date': '07/01/2019', 'end_date': '07/07/2019'},
        { id: 1, name: 'Report 10', priority: 'low', 'start_date': '05/01/2019', 'end_date': '05/05/2019'},
        { id: 2, name: 'Report 11', priority: 'medium', 'start_date': '06/01/2019', 'end_date': '06/06/2019'},
        { id: 3, name: 'Report 12', priority: 'high', 'start_date': '07/01/2019', 'end_date': '07/07/2019'},
        { id: 1, name: 'Report 13', priority: 'low', 'start_date': '05/01/2019', 'end_date': '05/05/2019'},
        { id: 2, name: 'Report 14', priority: 'medium', 'start_date': '06/01/2019', 'end_date': '06/06/2019'},
        { id: 3, name: 'Report 15', priority: 'high', 'start_date': '07/01/2019', 'end_date': '07/07/2019'},
        { id: 1, name: 'Report 16', priority: 'low', 'start_date': '05/01/2019', 'end_date': '05/05/2019'},
        { id: 2, name: 'Report 17', priority: 'medium', 'start_date': '06/01/2019', 'end_date': '06/06/2019'},
        { id: 3, name: 'Report 18', priority: 'high', 'start_date': '07/01/2019', 'end_date': '07/07/2019'},
        { id: 1, name: 'Report 19', priority: 'low', 'start_date': '05/01/2019', 'end_date': '05/05/2019'},
        { id: 2, name: 'Report 20', priority: 'medium', 'start_date': '06/01/2019', 'end_date': '06/06/2019'},
        { id: 3, name: 'Report 21', priority: 'high', 'start_date': '07/01/2019', 'end_date': '07/07/2019'},
        { id: 1, name: 'Report 22', priority: 'low', 'start_date': '05/01/2019', 'end_date': '05/05/2019'},
        { id: 2, name: 'Report 23', priority: 'medium', 'start_date': '06/01/2019', 'end_date': '06/06/2019'},
        { id: 3, name: 'Report 24', priority: 'high', 'start_date': '07/01/2019', 'end_date': '07/07/2019'},
        { id: 3, name: 'Report 25', priority: 'high', 'start_date': '07/01/2019', 'end_date': '07/07/2019'}
    ];

    // Return the hard-coded data as an observable (to simulate a REST call)
    return of(data);
  }

  /*
   * This is the REST endpoint used for the server-side ag-grid
   */
  public getServerSideData(aGridGetRowsRequestDTO: GridGetRowsRequestDTO): Observable<GridGetRowsResponseDTO> {
    // Construct the URL of the REST call
    const restUrl = environment.baseUrl + '/api/grid/getRows';

    // Use a POST call to send a JSON body of info
    return this.httpClient.post <GridGetRowsResponseDTO> (restUrl, aGridGetRowsRequestDTO, {} );
  }

}
