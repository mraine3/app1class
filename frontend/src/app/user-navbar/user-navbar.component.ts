import {Component, OnInit} from '@angular/core';
import {BannerService} from "../services/banner.service";
import {Observable, Subscription} from "rxjs";
import {Constants} from "../utilities/constants";
import {UserService} from "../services/user.service";
import {UserInfoDTO} from "../models/user-info-dto";

@Component({
  selector: 'app-user-navbar',
  templateUrl: './user-navbar.component.html',
  styleUrls: ['./user-navbar.component.css']
})
export class UserNavbarComponent implements OnInit {

  public bannerIsVisible: boolean;
  private bannerSubscription: Subscription;

  public userInfoObs: Observable<UserInfoDTO>

  constructor(public bannerService: BannerService,
              private userService: UserService) { }

  public ngOnInit(): void {

    this.bannerSubscription = this.bannerService.getStateAsObservable().subscribe( (aCurrentValue: boolean) => {
      // We received a message from the banner service with the value
      this.bannerIsVisible = aCurrentValue;
    });


    // Setup an observable to get the UserInfo
    // NOTE:  The HTML Template uses an async pipe to subscribe and unsubscribe to this observable
    this.userInfoObs = this.userService.getUserInfo();
  }


  public get constants(): typeof Constants {
    // Get a reference to the enumerated object
    // -- This is needed so that the html page can use the enum class
    return Constants;
  }

}
