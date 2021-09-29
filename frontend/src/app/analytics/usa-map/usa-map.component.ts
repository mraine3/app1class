import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import * as Highcharts from "highcharts";
import MapModule from 'highcharts/modules/map';

declare var require: any;
const usaMapDataAsJson = require("@highcharts/map-collection/countries/us/custom/us-all-territories.geo.json");
MapModule(Highcharts);


// Turn on the highchart context menu *View/Print/Download* options
//  -- Gives you these menu options: View in Full Screen, Print Chart, Download PNG, Download JPEG, Download PDF, Download SVG
import HC_exporting from 'highcharts/modules/exporting';
HC_exporting(Highcharts);

// Turn on the highchart context menu *export* options
// -- Gives you these menu options: Download CSV, Download XLS, View Data Table
import HC_exportData from 'highcharts/modules/export-data';
HC_exportData(Highcharts);

// Do client-side exporting (so that calls do *NOT* go to https://export.highcharts.com/ but does not work on all browsers
import HC_offlineExport from 'highcharts/modules/offline-exporting';
import {Chart} from "highcharts";
HC_offlineExport(Highcharts);



@Component({
  selector: 'app-usa-map',
  templateUrl: './usa-map.component.html',
  styleUrls: ['./usa-map.component.css']
})
export class UsaMapComponent implements OnInit, OnDestroy, AfterViewInit {

  public dataIsLoading: boolean = false;

  private mapOptions: any = {
    chart: {
      map: usaMapDataAsJson as any
    },
    title: {
      text: "Total Pending Items Per State"
    },
    credits: {
      enabled: false        // Hide the highcharts.com label
    },
    subtitle: {
      text:
        'Source map: United States, FeatureCollection</a>'
    },
    mapNavigation: {
      enabled: true,
      buttonOptions: {
        alignTo: "spacingBox"
      }
    },
    legend: {
      enabled: true
    },
    colorAxis: {
      min: 0
    },
    exporting: {
      buttons: {
        contextButton: {
          menuItems:  [
            'viewFullscreen',
            {
              text: 'Reset Zoom',
              onclick: () => {
                this.reloadData();
              }
            },
            'printChart',
            'separator',
            'downloadPNG',
            'downloadJPEG',
            'downloadPDF',    // Remove the Download PDF from the map
            'downloadSVG',
            'separator',
            'downloadCSV',
            'downloadXLS'
          ]
        }
      }
    }
  };


  ngOnInit(): void {

    // Set the thousands separator as a comma for all charts
    Highcharts.setOptions({
      lang: {
        thousandsSep: ','
      }
    });

  }

  ngAfterViewInit(): void {
    this.reloadData();
  }

  public ngOnDestroy(): void {
    // Destroy all charts
    Highcharts.charts.forEach(function (chart: Chart | undefined) {
      if (chart) {
        chart.destroy();
      }
    });
  }


  public reloadData(): void {

    // Tell the template page that data is loading (so it will show the spinners)
    this.dataIsLoading = true;

    // Update the Map with the state information
    this.mapOptions.series = [
      {
        type: "map",
        name: "Total Pending Items",
        states: {
          hover: {
            color: "#BADA55"
          }
        },
        dataLabels: {
          enabled: true,
          format: "{point.name}<br>{point.value:,.0f}"    // Format the point.value with commas
        },
        allAreas: false,
        data: []
      }
    ];

    this.mapOptions.series[0].data =  [
      ['us-ma', 0],
      ['us-wa', 1],
      ['us-ca', 2],
      ['us-or', 3],
      ['us-wi', 4],
      ['us-me', 5],
      ['us-mi', 6],
      ['us-nv', 7],
      ['us-nm', 8],
      ['us-co', 9],
      ['us-wy', 10],
      ['us-ks', 11],
      ['us-ne', 12],
      ['us-ok', 13],
      ['us-mo', 14],
      ['us-il', 15],
      ['us-in', 16],
      ['us-vt', 17],
      ['us-ar', 18],
      ['us-tx', 19],
      ['us-ri', 20],
      ['us-al', 21],
      ['us-ms', 22],
      ['us-nc', 23],
      ['us-va', 24],
      ['us-ia', 25],
      ['us-md', 26],
      ['us-de', 27],
      ['us-pa', 28],
      ['us-nj', 29],
      ['us-ny', 30],
      ['us-id', 31],
      ['us-sd', 32],
      ['us-ct', 33],
      ['us-nh', 34],
      ['us-ky', 35],
      ['us-oh', 36],
      ['us-tn', 37],
      ['us-wv', 38],
      ['us-dc', 39],
      ['us-la', 40],
      ['us-fl', 41],
      ['us-ga', 42],
      ['us-sc', 43],
      ['us-mn', 44],
      ['us-mt', 45],
      ['us-nd', 46],
      ['us-az', 47],
      ['us-ut', 48],
      ['us-hi', 49],   // Hawaii
      ['us-ak', 50],   // Alaska
      ['gu-3605', 51],
      ['mp-ti', 52],
      ['mp-sa', 53],
      ['mp-ro', 54],
      ['as-6515', 55],
      ['as-6514', 56],
      ['pr-3614', 57],
      ['vi-3617', 58],
      ['vi-6398', 59],
      ['vi-6399', 60]
    ];

    // Render the map
    Highcharts.mapChart('mapContainer', this.mapOptions);

    this.dataIsLoading = false;
  }


}
