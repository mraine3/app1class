import {Component, OnDestroy, OnInit} from '@angular/core';
import "ag-grid-enterprise";
import {GridService} from "../../../services/grid.service";
import {ReportRowDataDTO} from "../../../models/report-row-data-dto";
import {ColumnApi, GridApi, GridOptions, ICellRendererParams} from "ag-grid-community";
import {PriorityCellCustomRendererComponent} from "../priority-cell-custom-renderer/priority-cell-custom-renderer.component";
import {ReportGridActionCellRendererComponent} from "../report-grid-action-cell-renderer/report-grid-action-cell-renderer.component";
import {MatDialog} from "@angular/material/dialog";
import {UpdatePriorityDialogFormData} from "../../../models/update-priority-dialog-form-data";
import {UpdatePriorityDialogComponent} from "../update-priority-dialog-component/update-priority-dialog.component";
import {ThemeService} from "../../../services/theme.service";
import {Subscription} from "rxjs";
import {ThemeOptionDTO} from "../../../models/theme-option-dto";

@Component({
  selector: 'app-report-grid-view',
  templateUrl: './report-grid-view.component.html',
  styleUrls: ['./report-grid-view.component.css']
})
export class ReportGridViewComponent implements OnInit, OnDestroy {

  public gridOptions: GridOptions = {
    debug: true,
    suppressCellSelection: true,
    rowSelection: 'multiple',      // Possible values are 'single' and 'multiple'
    domLayout: 'normal'
  };

  public defaultColDefs: any = {
    flex: 1,
    sortable: true,
    filter: true,
    floatingFilter: true,    // Causes the filter row to appear below column names
    autoHeight: true
  };

  public columnDefs = [
    {
      field: 'id',
      cellClass: 'grid-text-cell-format',
      cellRenderer: 'actionCellRenderer',
      cellRendererParams: {
        deleteButtonGridMethod: (params: ICellRendererParams) => this.openDeleteDialog(params),
        editButtonGridMethod: (params: ICellRendererParams) => this.openEditDialog(params)
      },

      headerName: '',
      filter: false,
      suppressMenu: true,
      sortable: false,
      checkboxSelection: true
    },
    {
      field: 'name',
      cellClass: 'grid-text-cell-format'
    },
    {
      field: 'priority',
      cellRenderer: 'priorityCellRenderer',
    },
    {
      field: 'start_date',
      cellClass: 'grid-text-cell-format'
    },
    {
      field: 'end_date',
      cellClass: 'grid-text-cell-format'
    }
  ];

  // Tell ag-grid which cell-renderers will be available
  // This is a map of component names that correspond to components that implement ICellRendererAngularComp
  public frameworkComponents: any = {
    priorityCellRenderer: PriorityCellCustomRendererComponent,
    actionCellRenderer: ReportGridActionCellRendererComponent
  };

  public  rowData: ReportRowDataDTO[];
  private gridApi: GridApi;
  private gridColumnApi: ColumnApi;
  public  totalRowsSelected: number;
  public  updateButtonLabel: string;
  private themeStateSubscription: Subscription;
  public currentTheme: ThemeOptionDTO;

  constructor(private gridService: GridService,
              private themeService: ThemeService,
              private matDialog: MatDialog) {}


  public ngOnInit(): void {

    // Listen for changes from the theme service
    this.themeStateSubscription = this.themeService.getThemeStateAsObservable().subscribe( (aNewTheme: ThemeOptionDTO) => {
      // The theme has changed.
      this.currentTheme = aNewTheme;
    });

  }

  public ngOnDestroy(): void {
    if (this.themeStateSubscription) {
      this.themeStateSubscription.unsubscribe();
    }
  }


  public onGridReady(params: any): void {
    // Get a reference to the gridApi and gridColumnApi (which we will need later to get selected rows)
    this.gridApi = params.api;
    this.gridColumnApi = params.columnApi;

    // Reload the page
    this.reloadPage();
  }


  private reloadPage(): void {

    // Show the loading overlay
    this.gridApi.showLoadingOverlay();

    // Invoke a REST call to get data for the initial page load
    this.gridService.getReportData().subscribe((aData: ReportRowDataDTO[]) => {
      // We got data from the REST call

      // Put the data into the grid
      this.rowData = aData;

      // Unselect all values
      this.gridApi.deselectAll();

      // Regenerate derived values
      this.generateDerivedValuesOnUserSelection()

      // Resize the columns
      this.gridApi.sizeColumnsToFit();

      // Reset row heights
      this.gridApi.resetRowHeights();

      // Tell the grid to resize when user resizes the browser window
      window.onresize = () => {
        this.gridApi.sizeColumnsToFit();
      }

    });

  }  // end of reloadPage()



  public openDeleteDialog(params: ICellRendererParams): void {
    console.log('openDeleteDialog()  params=', params);
  }

  public openEditDialog(params: ICellRendererParams): void {
    console.log('openEditDialog() params=', params);
  }


  public generateDerivedValuesOnUserSelection(): void {
    // Get the total number of rows that are selected
    this.totalRowsSelected = this.gridApi.getSelectedRows().length;

    if (this.totalRowsSelected == 0) {
      this.updateButtonLabel = "Update Entries";
    }
    else if (this.totalRowsSelected == 1) {
      // User has checked one entry
      this.updateButtonLabel = "Update 1 Entry";
    }
    else {
      // User has checked multiple entries
      this.updateButtonLabel = "Update " + this.totalRowsSelected + " Entries";
    }

  }

  public openUpdatePriorityPopup(): void {
    console.log('openUpdatePriorityPopup');

    // Create an empty array of numbers
    let selectedReportIds: number[] = [];

    // Loop through the array of selected rows and add the selected ids to the list
    this.gridApi.getSelectedRows().forEach((row: ReportRowDataDTO) => {
      selectedReportIds.push(row.id);
    });

    let formData: UpdatePriorityDialogFormData = new UpdatePriorityDialogFormData();
    formData.reportIds = selectedReportIds;

    // Open the dialog box in modal mode
    let dialogRef = this.matDialog.open(UpdatePriorityDialogComponent, {
      data: formData
      // minWidth: 400,
      // minHeight: 200
    });

    // Listen for the dialog box to close
    dialogRef.afterClosed().subscribe((returnedFormData: UpdatePriorityDialogFormData) => {
      // The dialog box has closed

      if (returnedFormData) {
        // The user pressed OK.  So, update the data

        // TODO: Invoke the REST call

        // Reload the list
        this.reloadPage();
      }
    });

  }   // end of openUpdatePriorityPopup()


}
