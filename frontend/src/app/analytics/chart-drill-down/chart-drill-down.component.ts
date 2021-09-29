import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';

import * as Highcharts from "highcharts";    // needed

window.Highcharts = Highcharts;

// Turn on the highchart context menu view/print/download options
import HC_exporting from "highcharts/modules/exporting";
HC_exporting(Highcharts);

// Turn on the highchart context menu *export* options
// NOTE:  This provides these menu options: Download CSV, Download XLS, View Data Table
import HC_exportData from "highcharts/modules/export-data";
HC_exportData(Highcharts);

// Do client-side exporting (so that the exporting does *NOT* go to https://export.highcharts.com/
// NOTE:  This does not work on all web browsers
import HC_offlineExport from "highcharts/modules/offline-exporting";
HC_offlineExport(Highcharts);

// Turn on the drill-down capabilities
import HC_drillDown from "highcharts/modules/drilldown";
import {Chart} from "highcharts";
HC_drillDown(Highcharts);


@Component({
  selector: 'app-chart-drill-down',
  templateUrl: './chart-drill-down.component.html',
  styleUrls: ['./chart-drill-down.component.css']
})
export class ChartDrillDownComponent implements OnInit, OnDestroy, AfterViewInit {

  public dataIsLoading: boolean = false;

  private chartOptions1: any = {
    chart: {
      type: 'column',
      displayErrors: true
    },
    credits: {
      enabled: false        // Hide the highcharts.com label
    },
    title: {
      text: 'Basic drilldown'
    },
    xAxis: {
      type: 'category'
    },

    legend: {
      enabled: false
    },

    plotOptions: {
      series: {
        borderWidth: 0,
        dataLabels: {
          enabled: true
        }
      }
    },

    exporting: {
      buttons: {
        contextButton: {
          menuItems: [
            'viewFullscreen',
            'printChart',
            'separator',
            'downloadPNG',
            'downloadJPEG',
            'downloadPDF',
            'downloadSVG',
            'separator',
            'downloadCSV',
            'downloadXLS'
          ]
        }
      }
    }
  };



  constructor() { }


  public ngOnInit(): void {

    // Set options for all highchart menus on this page
    Highcharts.setOptions( {
      lang: {
        thousandsSep: ','    // Set the thousand separator as a comma
      }
    });

  }  // end of ngOnOnit()


  public ngOnDestroy(): void {
    // Destroy all charts
    Highcharts.charts.forEach(function (chart: Chart | undefined) {
      if (chart) {
        chart.destroy();
        console.log('drill-down:  Destroying the chart');
      }
    });
  }


  public ngAfterViewInit(): void {
    // Reload chart data
    // NOTE:  This call must be in ngAfterViewInit() and not in ngOnInit()
    this.reloadData();
  }


  public reloadData(): void {
    this.dataIsLoading = true;

    // Load Chart Data
    this.chartOptions1.series = [{
      name: 'Main Chart',      // Named used for the "Back to <>" when drilled-in
      colorByPoint: true,
      data: [{
        name: 'Animals',
        y: 5,
        drilldown: 'animals'
      }, {
        name: 'Fruits',
        y: 2,
        drilldown: 'fruits'
      }, {
        name: 'Cars',
        y: 4,
        drilldown: 'cars'
      }]
    }];

    // Load Drilldown Chart Info
    this.chartOptions1.drilldown = {
      series: [{
        id: 'animals',
        data: [
          ['Cats', 4],
          ['Dogs', 2],
          ['Cows', 1],
          ['Sheep', 2],
          ['Pigs', 1]
        ]
      }, {
        id: 'fruits',
        data: [
          ['Apples', 4],
          ['Oranges', 2]
        ]
      }, {
        id: 'cars',
        data: [
          ['Toyota', 4],
          ['Opel', 2],
          ['Volkswagen', 2]
        ]
      }]
    };

    // Render the chart
    Highcharts.chart('drillDownChart1', this.chartOptions1);

    this.dataIsLoading = false;
  }

}
