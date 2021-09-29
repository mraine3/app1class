import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {BreakpointObserver, BreakpointState} from "@angular/cdk/layout";
import {Subscription} from "rxjs";

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
import {Chart, Options} from "highcharts";
import {TileSizeDTO} from "../../models/tile-size-dto";
import {DashboardService} from "../../services/dashboard.service";
import {DashboardDataDTO} from "../../models/dashboard-data-dto";
import {Router} from "@angular/router";
import {ThemeService} from "../../services/theme.service";
import {ThemeOptionDTO} from "../../models/theme-option-dto";
HC_drillDown(Highcharts);

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy, AfterViewInit {

  public  totalColumns: number;
  private cardLayoutSubscription: Subscription;
  public  dataIsLoading: boolean = false;
  private themeStateSubscription: Subscription;
  public  currentTheme: ThemeOptionDTO;



  private darkTheme: Options = {
    colors: ['#2b908f', '#90ee7e', '#f45b5b', '#7798BF', '#aaeeee', '#ff0066',
      '#eeaaee', '#55BF3B', '#DF5353', '#7798BF', '#aaeeee'],
    chart: {
      backgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 1, y2: 1 },
        stops: [
          [0, '#2a2a2b'],
          [1, '#3e3e40']
        ]
      },
      style: {
        fontFamily: '\'Unica One\', sans-serif'
      },
      plotBorderColor: '#606063'
    },
    title: {
      style: {
        color: '#E0E0E3',
        textTransform: 'uppercase',
        fontSize: '20px'
      }
    },
    subtitle: {
      style: {
        color: '#E0E0E3',
        textTransform: 'uppercase'
      }
    },
    xAxis: {
      gridLineColor: '#707073',
      labels: {
        style: {
          color: '#E0E0E3'
        }
      },
      lineColor: '#707073',
      minorGridLineColor: '#505053',
      tickColor: '#707073',
      title: {
        style: {
          color: '#A0A0A3'
        }
      }
    },
    yAxis: {
      gridLineColor: '#707073',
      labels: {
        style: {
          color: '#E0E0E3'
        }
      },
      lineColor: '#707073',
      minorGridLineColor: '#505053',
      tickColor: '#707073',
      tickWidth: 1,
      title: {
        style: {
          color: '#A0A0A3'
        }
      }
    },
    tooltip: {
      backgroundColor: 'rgba(0, 0, 0, 0.85)',
      style: {
        color: '#F0F0F0'
      }
    },
    plotOptions: {
      series: {
        dataLabels: {
          color: '#F0F0F3',
          style: {
            fontSize: '13px'
          }
        },
        marker: {
          lineColor: '#333'
        }
      },
      boxplot: {
        fillColor: '#505053'
      },
      candlestick: {
        lineColor: 'white'
      },
      errorbar: {
        color: 'white'
      }
    },
    legend: {
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      itemStyle: {
        color: '#E0E0E3'
      },
      itemHoverStyle: {
        color: '#FFF'
      },
      itemHiddenStyle: {
        color: '#606063'
      },
      title: {
        style: {
          color: '#C0C0C0'
        }
      }
    },
    credits: {
      style: {
        color: '#666'
      }
    },
    drilldown: {
      activeAxisLabelStyle: {
        color: '#F0F0F3'
      },
      activeDataLabelStyle: {
        color: '#F0F0F3'
      }
    },
    navigation: {
      buttonOptions: {
        symbolStroke: '#DDDDDD',
        theme: {
          fill: '#505053'
        }
      }
    },
    // scroll charts
    rangeSelector: {
      buttonTheme: {
        fill: '#505053',
        stroke: '#000000',
        style: {
          color: '#CCC'
        },
        states: {
          hover: {
            fill: '#707073',
            stroke: '#000000',
            style: {
              color: 'white'
            }
          },
          select: {
            fill: '#000003',
            stroke: '#000000',
            style: {
              color: 'white'
            }
          }
        }
      },
      inputBoxBorderColor: '#505053',
      inputStyle: {
        backgroundColor: '#333',
        color: 'silver'
      },
      labelStyle: {
        color: 'silver'
      }
    },
    navigator: {
      handles: {
        backgroundColor: '#666',
        borderColor: '#AAA'
      },
      outlineColor: '#CCC',
      maskFill: 'rgba(255,255,255,0.1)',
      series: {
        color: '#7798BF',
        lineColor: '#A6C7ED'
      },
      xAxis: {
        gridLineColor: '#505053'
      }
    },
    scrollbar: {
      barBackgroundColor: '#808083',
      barBorderColor: '#808083',
      buttonArrowColor: '#CCC',
      buttonBackgroundColor: '#606063',
      buttonBorderColor: '#606063',
      rifleColor: '#FFF',
      trackBackgroundColor: '#404043',
      trackBorderColor: '#404043'
    }
  };




  private chartOptions1: any = {
    credits: {
      enabled: false        // Hide the highcharts.com label
    },
    caption: {
      text: ''
    },
    chart: {
      type: 'pie'
    },
    title: {
      text: 'Pending Case Distribution'
    },
    subtitle: {
      text: ''
    },
    accessibility: {
      announceNewData: {
        enabled: true
      },
      point: {
        valueSuffix: '%'
      }
    },
    plotOptions: {
      series: {
        dataLabels: {
          enabled: true,
          format: '<b>{point.name}</b>:<br>{point.percentage:.1f} %<br>value: {point.y}'
        }
      }
    },
    tooltip: {
      headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
      pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y}</b> or <b>{point.percentage:.1f}%</b> of total<br/>'
    },
    series: [
      {
        name: "Browsers",
        colorByPoint: true,
        data: [],

        point:{
          events:{
            click: (event: any) => {
              this.logPointInfo(event)
            }
          }
        }

      }
    ],
    exporting: {
      buttons: {
        contextButton: {
          menuItems:  [
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


  public logPointInfo(event: any): void {
    console.log('name=' + event.point.name + '  x=' + event.point.x + '  y=' + event.point.y + '  percent=' + event.point.percentage);
  }

  // Chart 2 is a bar chart2
  private chartOptions2: any = {
    chart: {
      type: 'column'   // Uuse type:'bar' for horizontal chart.  Use type:'column' for vertical bar chart
    },
    credits: {
      enabled: false        // Hide the highcharts.com label
    },
    title: {
      text: 'Case Timeliness of Closes Cases (Days)'
    },
    xAxis: {
      categories: ['0-30', '31-60', '61-90', '91-120', '121-150', '151-180', '181-210', '211-240', '241-270', '271-300', '301+']
    },
    yAxis: {
      min: 0,
      title: {
        text: 'Number of Cases'
      }
    },
    legend: {
      reversed: false
    },
    plotOptions: {
      series: {
        stacking: 'normal'
      }
    },

    series: [],
    exporting: {
      buttons: {
        contextButton: {
          menuItems:  [
            'viewFullscreen',
            'printChart',
            'separator',
            'downloadPNG',
            'downloadJPEG',
            'downloadPDF',
            'downloadSVG',
            'separator',
            'downloadCSV',
            'downloadXLS',
            'separator',
            {
              text: 'Go to Home Page',
              onclick: () => {
                this.goToWelcomePage()
              }
            }
          ]
        }
      }
    }
  };


  public goToWelcomePage(): void {

    // Navigate to the Welcome Page
    this.router.navigate(["/"]).then();
  }

  // Chart 3 is a line chart
  private chartOptions3: any = {
    credits: {
      enabled: false        // Hide the highcharts.com label
    },
    title: {
      text: 'Case Timeliness (Closed Investigations)'
    },
    subtitle: {
      text: ''
    },
    yAxis: {
      title: {
        text: 'Avg Case Timeliness (Days)'
      }
    },
    xAxis: {
      type: 'datetime',
      tickInterval: 30 * 24 * 3600 * 1000,
      labels: {
        rotation: 45,
        step: 1,
        style: {
          fontSize: '13px',
          fontFamily: 'Arial,sans-serif'
        }
      },
      dateTimeLabelFormats: {
        month: '%b \'%y',
        year: '%Y'
      },
      accessibility: {
        rangeDescription: 'Range: The last 12 months'
      }
    },
    legend: {
      align: 'center',
      verticalAlign: 'bottom'
    },
    plotOptions: {
      series: {
        label: {
          connectorAllowed: false
        }
      }
    },
    tooltip: {

      formatter: function (): any {
        // @ts-ignore
        let date = new Date(this.x);

        // Get the formatted date as mm/dd/yyyy
        // NOTE:  We must add 1 to the date.getMonth() as January has value of zero
        let formattedDate: string = String(date.getMonth() + 1).padStart(2, "0") + '/' +
                                    String(date.getDay()).padStart(2, "0") + '/' +
                                    date.getFullYear();

        // @ts-ignore
        return '<span style="color:{this.color}">' + this.series.name + '</span>: <b>' + this.y + '</b> on ' + formattedDate + '<br/>';
      }

    },
    series: []
  }



  public tileSizes: TileSizeDTO[] = [
      {
        chartNumber: 1,
        rowSpan: 1,
        colSpan: 1
      },
    {
      chartNumber: 2,
      rowSpan: 1,
      colSpan: 1
    },
    {
      chartNumber: 3,
      rowSpan: 1,
      colSpan: 1
    },
    {
      chartNumber: 4,
      rowSpan: 1,
      colSpan: 1
    },
    {
      chartNumber: 5,
      rowSpan: 1,
      colSpan: 1
    },
    {
      chartNumber: 6,
      rowSpan: 1,
      colSpan: 1
    }
    ];


  constructor(private breakpointObserver: BreakpointObserver,
              private dashboardService: DashboardService,
              private themeService: ThemeService,
              private router: Router) { }


  public ngOnInit(): void {

    // Set options for all highchart menus on this page
    Highcharts.setOptions( {
      lang: {
        thousandsSep: ','    // Set the thousand separator as a comma
      }
    });

    // Listen on an array of size breakpoints
    // NOTE:  The breakpoints can be min-width or max-width
    this.cardLayoutSubscription = this.breakpointObserver.observe([
      '(min-width: 1px)', '(min-width: 800px)', '(min-width: 1100px)'
    ]).subscribe( (state: BreakpointState) => {

      if (state.breakpoints['(min-width: 1100px)']) {
        console.log("Screen is 1100px or more.  state=", state);
        this.totalColumns = 3;
      }
      else if (state.breakpoints['(min-width: 800px)']) {
        console.log("Screen is 800px-1100px.  state=", state);
        this.totalColumns = 2;
      }
      else if (state.breakpoints['(min-width: 1px)']) {
        console.log("Screen is 1 to 800px.  state=", state);
        this.totalColumns = 1;
      }

    });


    // Listen for changes from the theme service
    this.themeStateSubscription = this.themeService.getThemeStateAsObservable().subscribe( (aNewTheme: ThemeOptionDTO) => {
      this.currentTheme = aNewTheme;

      this.reloadData()
    });

  }  // end of ngOnOnit()


  public ngOnDestroy(): void {
    if (this.cardLayoutSubscription) {
      // Unsubscribe from the subscription (to avoid memory leaks)
      this.cardLayoutSubscription.unsubscribe();
    }

    if (this.themeStateSubscription) {
      this.themeStateSubscription.unsubscribe();
    }

    // Destroy all charts
    Highcharts.charts.forEach(function (chart: Chart | undefined) {
      if (chart) {
        chart.destroy();
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

    this.dashboardService.getAllChartData().subscribe( (aData: DashboardDataDTO) => {
        // The REST call came back with data

        if (this.currentTheme.isLightMode) {
          // Render the charts in light mode

          // Set the data for chart 1 and *render* chart 1
          this.chartOptions1.series[0].data = aData.chartData1;
          Highcharts.chart('chart1', this.chartOptions1);

          this.chartOptions2.series = aData.chartData2;
          Highcharts.chart('chart2', this.chartOptions2);

          this.chartOptions3.series = aData.chartData3;
          Highcharts.chart('chart3', this.chartOptions3);
        }
        else {
          // Render the charts in dark mode

          // Set the data for chart 1 and *render* chart 1
          this.chartOptions1.series[0].data = aData.chartData1;
          Highcharts.chart('chart1',  Highcharts.merge(this.chartOptions1, this.darkTheme));

          this.chartOptions2.series = aData.chartData2;
          Highcharts.chart('chart2',  Highcharts.merge(this.chartOptions2, this.darkTheme));

          this.chartOptions3.series = aData.chartData3;
          Highcharts.chart('chart3',  Highcharts.merge(this.chartOptions3, this.darkTheme));
        }

    }).add(  () => {
        // REST call finally block

      // Redraw all charts on this page (so they fit perfectly in the <mat-card> tags)
      Highcharts.charts.forEach(function (chart: Chart | undefined) {
          chart?.reflow();
        });

        this.dataIsLoading = false;
    });

  }


  /*
   * Send a 'resize' event
   * This will cause HighCharts to resize all charts to fit inside their parent containers
   */
  private resizeChartsToFitContainers(): void {

    setTimeout(()=> {
      // Send a 'resize' event
      // NOTE:  The window.dispatchEvent() call MUST be in a setTimeout or it will not work
      window.dispatchEvent(new Event('resize'));
    }, 1);

  }


  public toggleSize(aChartNumber: number) {

    // Reset all other tiles to be 1x1
    this.tileSizes.forEach( (tile: TileSizeDTO) => {
      if (tile.chartNumber != aChartNumber) {
        tile.rowSpan = 1;
        tile.colSpan = 1;
      }
    })

    // Get the indexNumber in the array from the chartNumber
    let indexNumber: number = aChartNumber - 1;

    if (this.tileSizes[indexNumber].rowSpan == 1) {
      // This tile is already 1x1.  So, change it to 2x2
      this.tileSizes[indexNumber].rowSpan = 2;
      this.tileSizes[indexNumber].colSpan = 2;
    }
    else {
      // This tile is already 2x2.  So, change it to 1x1
      this.tileSizes[indexNumber].rowSpan = 1;
      this.tileSizes[indexNumber].colSpan = 1;
    }

    // Resize the charts to fit their parent containers
    this.resizeChartsToFitContainers();
  }
}
