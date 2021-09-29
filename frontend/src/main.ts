import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';
import { environment } from './environments/environment';

import "ag-grid-enterprise";                  // Give us access to the License Manager
import {LicenseManager} from "ag-grid-enterprise";

if (environment.production) {
  enableProdMode();
}

// Set the license key for ag-grid-enterprise
LicenseManager.setLicenseKey(environment.agGridLicenseKey);

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
