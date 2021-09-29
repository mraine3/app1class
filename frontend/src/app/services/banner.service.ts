import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {PreferencesDTO} from "../models/preferences-dto";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class BannerService {
  private bannerStateSubject = new BehaviorSubject<boolean>(true);   // Initialize the banner state to true

  constructor(private httpClient: HttpClient) { }


  public getStateAsObservable(): Observable<boolean> {
    return this.bannerStateSubject.asObservable();
  }

  public hideBanner(): void {
    this.setLatestValue(false).subscribe( () => {
      // REST call came back successfully

      // Send a message with false  (to tell anyone listening to hide the banner)
      this.bannerStateSubject.next(false);
    })
  }


  public showBanner(): void {
    this.setLatestValue(true).subscribe( () => {
      // REST call came back successfully

      // Send a message with false  (to tell anyone listening to hide the banner)
      this.bannerStateSubject.next(true);
    })
  }

  public getLatestValueFromBackend(): Observable<PreferencesDTO> {
    // Construct the URL for the REST endpoint  (to get all preferences)
    const restUrl = environment.baseUrl + '/api/preferences/all';

    // NOTE:  The REST call is not invoked you call subscribe() on this observable
    return this.httpClient.get <PreferencesDTO> (restUrl);
  }

  private setLatestValue(aBannerInfo: boolean): Observable<object> {
    // Construct the URL for the REST endpoint  (to set the banner preference only)
    const restUrl = environment.baseUrl + '/api/preferences/banner/set/' + aBannerInfo;

    // Return an observable to this POST REST call
    // -- The 2nd {} is the empty json body sent to the REST call
    // -- The 3rd {} is the empty map of options
    return this.httpClient.post(restUrl, {}, {} );
  }


  public initialize(aBannerInfo: boolean) {
    // Send out a message that (to anyone listening) with the current value
    // Anyone who listens later, gets this initial message
    this.bannerStateSubject.next(aBannerInfo);
  }


}
