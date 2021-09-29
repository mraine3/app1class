import {ColumnVO} from "ag-grid-community/dist/lib/interfaces/iColumnVO";
import {IServerSideGetRowsRequest} from "ag-grid-community";

export class GridGetRowsRequestDTO implements IServerSideGetRowsRequest {
  public endRow: number;
  public filterModel: any;
  public groupKeys: string[];
  public pivotCols: ColumnVO[];
  public pivotMode: boolean;
  public rowGroupCols: ColumnVO[];
  public sortModel: any;
  public startRow: number;
  public valueCols: ColumnVO[];
  public searchAfterClause: string | null;      // Holds information about the last row so ElasticSearch can get page2, page3, ...
  public rawSearchQuery: string;


  public constructor(aParams: IServerSideGetRowsRequest, aSearchAfterClause: string | null, aRawSearchQuery: string) {
    this.endRow = aParams.endRow;
    this.filterModel = aParams.filterModel;
    this.groupKeys = aParams.groupKeys;
    this.pivotCols = aParams.pivotCols;
    this.pivotMode = aParams.pivotMode;
    this.rowGroupCols = aParams.rowGroupCols;
    this.sortModel = aParams.sortModel;
    this.startRow = aParams.startRow;
    this.valueCols = aParams.valueCols;
    this.searchAfterClause = aSearchAfterClause;
    this.rawSearchQuery = aRawSearchQuery;
  }
}
