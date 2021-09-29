import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ColumnApi, GridApi, GridOptions, IServerSideDatasource, IServerSideGetRowsParams, ServerSideStoreType} from "ag-grid-community";
import {GridGetRowsResponseDTO} from "../../models/grid/grid-get-rows-response-dto";
import {ServerSideGridRowDataDTO} from "../../models/grid/server-side-grid-row-data-dto";
import {GridService} from "../../services/grid.service";
import {GridGetRowsRequestDTO} from "../../models/grid/grid-get-rows-request-dto";
import {Subscription} from "rxjs";
import {ThemeOptionDTO} from "../../models/theme-option-dto";
import {ThemeService} from "../../services/theme.service";

@Component({
  selector: 'app-server-side-grid',
  templateUrl: './server-side-grid.component.html',
  styleUrls: ['./server-side-grid.component.css']
})
export class ServerSideGridComponent implements OnInit, OnDestroy, AfterViewInit {

  private themeStateSubscription: Subscription;
  public  currentTheme: ThemeOptionDTO;

  private searchAfterClause: string | null;
  public  totalMatches: number = 0;
  public  rawSearchQuery: string = "";
  public  isValidQuery: boolean = true;

  public gridOptions: GridOptions = {
    debug: false,
    suppressCellSelection: true,
    rowSelection: 'multiple',      // Possible values are 'single' and 'multiple'
    domLayout: 'normal',
    rowModelType: 'serverSide',    // Possible values are 'clientSide', 'infinite', 'viewport', and 'serverSide'
    pagination: false,             // Do not show the 1 of 20 of 20, page 1 of 1 (as we are doing infinite scrolling)

    serverSideStoreType: ServerSideStoreType.Partial,   // Use partial Server Side Store Type so that pages of data are loaded
    cacheBlockSize: 50,                                 // Load 50 records at a time with each REST call
    blockLoadDebounceMillis: 100,
    debounceVerticalScrollbar: true,
    overlayNoRowsTemplate: "<span class='no-matches-found-message'>No matches were found</span>",

    onFilterChanged: () => {
      // The user changed a filer.  So, clear the grid cache before the REST endpoint is invoked
      this.clearGridCache();
    },

    onSortChanged: () => {
      // The user changed a sort.  So, clear the grid cache before the REST endpoint is invoked
      this.clearGridCache();
    }
  }


  private clearGridCache(): void {
    // Move the scrollbar to the top
    this.gridApi.ensureIndexVisible(0, 'top');

    // Clear the cache
    this.gridApi?.setServerSideDatasource(this.serverSideDataSource);
  }


  /*
   * User pressed the Reset Button
   *  1. Clear the grid cache
   *  2. Clear all sorting
   *  3. Clear all filters
   *  4. Empty the search box
   *  5. Force the grid to invoke the REST endpoint by calling onFilterChanged()
   */
  public resetGrid(): void {
    // Clear the grid cache and move the vertical scrollbar to the top
    this.clearGridCache();

    // Clear all sorting
    this.gridApi.setSortModel(null);

    // Clear the filters
    this.gridApi.setFilterModel(null);

    // Clear the search box
    this.rawSearchQuery = "";

    // Force the grid to invoke the REST endpoint
    this.gridApi.onFilterChanged();
  }


  /*
   * User clicked to run a search
   *  1. Clear the grid cache
   *  2. Clear all sorting
   *  3. Clear all filters
   *  4. Force the grid to invoke the REST endpoint by calling onFilterChanged()
   */
  public runSearch(): void {
    this.clearGridCache();

    // Clear all sorting
    this.gridApi.setSortModel(null);

    // Clear the filters
    this.gridApi.setFilterModel(null);

    // Force the grid to invoke the REST endpoint
    this.gridApi.onFilterChanged();
  }

  /*
   * Create a server-side data source object
   *
   * The getRows() method is invoked when a user scrolls down (to get more rows)
   * The getRows() method is invoked when a user changes a filter
   * The getRows() method is invoked when a user changes sorting
   * The getRows() method is invoked manually when the code calls this.gridApi.onFilterChanged()
   */
  private serverSideDataSource: IServerSideDatasource = {
    getRows: (params: IServerSideGetRowsParams) => {
      // The grid needs to load data.  So, subscribe to gridService.getServerSideData() and load the data

      if (params.request.startRow == 0) {
        // The user is requesting a first page (so we are not getting a 2nd or 3rd page)
        // -- Reset the additional sort fields  (needed for the 2nd, 3rd, 4th pages)
        this.searchAfterClause = null;
      }

      if (this.totalMatches == 0) {
        this.gridApi.hideOverlay();
      }

      // Add the additional sort fields to the request object
      let getRowsRequestDTO: GridGetRowsRequestDTO = new GridGetRowsRequestDTO(params.request, this.searchAfterClause, this.rawSearchQuery)

      // Subscribe to this service method to get the data
      this.gridService.getServerSideData(getRowsRequestDTO)
        .subscribe((response: GridGetRowsResponseDTO) => {
          // REST Call finished successfully

          this.isValidQuery = response.isValidQuery;

          if (! response.isValidQuery) {
            // The user entered an invalid search

            // Set the flag to false (so the search box changes color)
            this.isValidQuery = false;

            // Update total matches on the screen
            this.totalMatches = 0;

            // Show the 'no matches were found'
            this.gridApi.showNoRowsOverlay();

            // Tell the ag-grid that there were no results
            params.successCallback([], 0);
            return;
          }

          // Save the additional sort fields  (we will use when getting the next page)
          this.searchAfterClause = response.searchAfterClause;

          // Update total matches on the screen
          this.totalMatches = response.totalMatches;

          if (this.totalMatches == 0) {
            this.gridApi.showNoRowsOverlay();
          }

          // Load the data into the grid and turn on/off infinite scrolling
          // If lastRow == -1,           then Infinite-Scrolling is turned ON
          // if lastRow == totalMatches, then infinite-scrolling is turned OFF
          params.successCallback(response.data, response.lastRow)
        });

    }
  };


  public defaultColDefs: any = {
    flex: 1,
    sortable: true,
    filter: true,
    floatingFilter: true,    // Causes the filter row to appear below column names
    autoHeight: true,
    resizable: true
  };


  private textFilterParams = {
    filterOptions: ['contains', 'notContains'],
    caseSensitive: false,
    debounceMs: 200,
    suppressAndOrCondition: true,
  };

  public columnDefs = [
    {
      headerName: 'Id',
      field: 'id',
      filter: 'agNumberColumnFilter',           // numeric filter
      filterParams: this.textFilterParams,
      cellClass: 'grid-text-cell-format',
      checkboxSelection: true
    },
    {
      headerName: 'Report Name',
      field: 'display_name',
      filter: 'agTextColumnFilter',
      filterParams: this.textFilterParams,
      cellClass: 'grid-text-cell-format'
    },
    {
      headerName: 'Priority',
      field: 'priority',
      filter: 'agTextColumnFilter',
      filterParams: this.textFilterParams,
    },
    {
      headerName: 'Description',
      field: 'description',
      sortable: false,                      // The description field is not sortable
      filter: 'agTextColumnFilter',
      filterParams: this.textFilterParams,
      cellClass: 'grid-text-cell-format'
    }
  ];

  // This is a map of component names that correspond to components that implement ICellRendererAngularComp
  public  frameworkComponents: any = {  };

  public  rowData: ServerSideGridRowDataDTO[];
  private gridApi: GridApi;
  private gridColumnApi: ColumnApi;

  @ViewChild('searchBox',  { read: ElementRef }) searchBox: ElementRef;

  constructor(private gridService: GridService,
              private themeService: ThemeService) {}

  public ngOnInit(): void {

    // Listen for changes from the theme service
    this.themeStateSubscription = this.themeService.getThemeStateAsObservable().subscribe( (aNewTheme: ThemeOptionDTO) => {
      // The theme has changed.
      this.currentTheme = aNewTheme;
    });

  }


  public ngAfterViewInit(): void {
    // Set the focus on the search box
    setTimeout(() => this.searchBox.nativeElement.focus(), 10);
  }


  public ngOnDestroy(): void {
    if (this.themeStateSubscription) {
      this.themeStateSubscription.unsubscribe();
    }
  }


  /*
   * The grid calls onGridReady() once it is fully initialized.  This is the start of this page.
   */
  public onGridReady(params: any): void {
    // Get a reference to the gridApi and gridColumnApi (which we will need later to get selected rows)
    this.gridApi = params.api;
    this.gridColumnApi = params.columnApi;


    // Set the server-side data source
    // NOTE:  The grid will asynchronously call getRows() as it needs to load data
    this.gridApi.setServerSideDatasource(this.serverSideDataSource);
  }


}
