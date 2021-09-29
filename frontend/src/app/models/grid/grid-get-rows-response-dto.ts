export class GridGetRowsResponseDTO {
  public data: any[];
  public lastRow: number;             // If lastRow==-1, then infinite scrolling is ON.  If lastRow==totalMatches, then infinite scrolling is OFF
  public totalMatches: number;
  public secondaryColumnFields: string[];
  public searchAfterClause: string;  // Holds information about the last row so ElasticSearch can get page2, page3, ...
  public isValidQuery: boolean;
}
