import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FileItem, FileUploader, ParsedResponseHeaders} from "ng2-file-upload";
import {environment} from "../../../environments/environment";
import {MessageService} from "../../services/message.service";
import {JobStatusDTO} from "../../models/job-status-dto";
import {JobService} from "../../services/job.service";


const backendUploadUrl = environment.baseUrl + '/api/reports/upload';

@Component({
  selector: 'app-upload-report',
  templateUrl: './upload-report.component.html',
  styleUrls: ['./upload-report.component.css']
})
export class UploadReportComponent implements OnInit {
  @ViewChild('selectedFile') selectedFile: ElementRef;

  public PROCESSING_NOT_STARTED       : number = 1;
  public PROCESSING_IN_PROGRESS       : number = 2;
  public PROCESSING_FINISHED_SUCCESS  : number = 3;
  public PROCESSING_FINISHED_FAILURE  : number = 4;
  public PROCESSING_UPLOAD_IN_PROGRESS           : number = 5;

  public  selectedFileToUpload:  FileItem | null = null;
  private lastFileItemAddedToQueue: FileItem;

  public  pageState: number = this.PROCESSING_NOT_STARTED;
  private pollingAttempts: number = 0;
  private interval: any;
  public  percentProcessed: number;
  public  userMessage: string;

  // Make sure the itemAlias matches the
  //     @RequestParam(value = "file" in the REST endpoint on backend
  //
  public uploader: FileUploader = new FileUploader(
    {
      url: backendUploadUrl,
      queueLimit: 2,
      itemAlias: 'file'
    });

  constructor(private messageService: MessageService,
              private jobService: JobService) { }


  ngOnInit(): void {
    this.uploader.onAfterAddingFile = (item: FileItem) => this.onAfterAddingFile(item);


    this.uploader.onCompleteItem = (item, response, status, headers) =>
      this.onCompleteItem(item, response, status, headers);
  }


  /*
   * The upload has finished and we got a response back from the back-end
   *  1) If the response = 200, then start polling on the returned jobId
   *  2) Else, show an error
   */
  onCompleteItem(item: any, response: any, status: number, headers: ParsedResponseHeaders): any {

    this.pageState = this.PROCESSING_IN_PROGRESS;

    // An Upload has finished
    if (status == 200) {
      this.pageState = this.PROCESSING_IN_PROGRESS;

      // Get the jobId from the response
      let jobId: number = response;

      // Start polling
      this.startPolling(jobId);
    }
    else {
      // Send a message to the user letting him know if it failed
      this.pageState = this.PROCESSING_FINISHED_FAILURE;

      let message = " status=" + status + "   response=" + response;
      this.messageService.showErrorMessage(message);
    }
  }


  public onFileSelected(aFiles: File[]) {
    if (aFiles.length == 0) {
      // The user cancelled selecting a file -- so stop here
      return;
    }

    if (this.selectedFileToUpload != null) {
      // Remove the old file from the queue
      this.uploader.removeFromQueue(this.selectedFileToUpload)
    }

    this.selectedFileToUpload = this.lastFileItemAddedToQueue;
  }


  /*
   * When using the FileUploader, there is an order of operations
   *  1) User selects file
   *  2) The FileUploader adds it to the queue
   *  3) onAfterAddingFile() is called
   *  4) onFileSelected() is called
   */
  private onAfterAddingFile(aFileItem: FileItem) {
    // This line is required to make upload work with spring security
    aFileItem.withCredentials = false;

    // Get a reference to the last FileItem object added to the queue
    // NOTE:  This reference is used in onFileSelected()
    this.lastFileItemAddedToQueue = aFileItem;
  }


  public clearAll(): void {
    this.uploader.clearQueue();
    this.selectedFileToUpload = null;
    this.pageState = this.PROCESSING_NOT_STARTED;
    this.pollingAttempts = 0;
    this.percentProcessed = 0;

    // -- Fixes the problem in which a user selects the same file twice
    if (this.selectedFile) {
      this.selectedFile.nativeElement.value = '';
    }
  }


  stopPolling(): void {
    if (this.interval != null) {
      clearInterval(this.interval);
    }
  }


  public beginFileUpload(): void {
    this.pageState = this.PROCESSING_UPLOAD_IN_PROGRESS;

    // Tell the FileUploader to send the file to the back-end
    this.uploader.uploadAll();
  }


  /*
   * Start polling the front-end on the passed-in aJobId
   * -- As the REST call comes in, the HTML template should show the progress bar advancing
   */
  private startPolling(aJobId: number): void {
    this.pollingAttempts = 0;

    // Poll every 2 seconds
    this.interval = setInterval(() => {

      this.jobService.getJobStatus(aJobId).subscribe(data => {

        let jobStatusDTO: JobStatusDTO = data;

        this.pollingAttempts++;

        if ((this.pollingAttempts >= 30) && (jobStatusDTO.state == 0)) {
          // We have not seen any increase in 30 attempts -- so something is wrong
          this.pageState = this.PROCESSING_FINISHED_FAILURE;
          this.stopPolling();
          let message: string = "Error processing this file:  It failed to be parsed.";
          this.messageService.showErrorMessage(message);
        }
        else if (jobStatusDTO.state == this.jobService.JOB_STATE_WORK_IN_PROGRESS) {
          // The job is work-in-progress
          this.percentProcessed = jobStatusDTO.progress_as_percent;
        }
        else if (jobStatusDTO.state == this.jobService.JOB_STATE_FINISHED_SUCCESS) {
          // The job finished successfully
          this.pageState = this.PROCESSING_FINISHED_SUCCESS;
          this.percentProcessed = 100;

          // The backend has finished processing -- so stop polling
          this.stopPolling();

          let message: string = "Successfully processed this file.";
          this.messageService.showSuccessMessage(message);
        }
        else if (jobStatusDTO.state == this.jobService.JOB_STATE_FINISHED_ERROR) {
          this.pageState = this.PROCESSING_FINISHED_FAILURE;
          this.userMessage = jobStatusDTO.user_message;

          this.messageService.showErrorMessage('Failed to process this file:\n' + jobStatusDTO.user_message)
          // The job finished with failure
          this.stopPolling();
        }
      })}, 2000);

  }  // end of startPolling()


}
