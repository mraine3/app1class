import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {JobStatusDTO} from "../models/job-status-dto";

@Injectable({
  providedIn: 'root'
})
export class JobService {

  constructor(private httpClient: HttpClient) { }

  /*
   * These JobStates must correspond to the back-end JobService
   */
  public JOB_STATE_WORK_IN_PROGRESS: number = 2;
  public JOB_STATE_FINISHED_SUCCESS: number = 3;
  public JOB_STATE_FINISHED_ERROR: number = 4;

  /*
   * Returns an observable that holds the JobStatusDTO object
   */
  public getJobStatus(aJobId: number): Observable<JobStatusDTO> {
    // Construct the URL of the REST call
    const restUrl = environment.baseUrl + '/api/jobs/status/' + aJobId;

    // Return an observable
    return this.httpClient.get <JobStatusDTO>(restUrl);
  }

}
