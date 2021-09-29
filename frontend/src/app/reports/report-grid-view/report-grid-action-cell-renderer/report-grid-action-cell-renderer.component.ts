import { Component, OnInit } from '@angular/core';
import {ICellRendererAngularComp} from "ag-grid-angular";
import {ICellRendererParams} from "ag-grid-community";

@Component({
  selector: 'app-report-grid-action-cell-renderer',
  templateUrl: './report-grid-action-cell-renderer.component.html',
  styleUrls: ['./report-grid-action-cell-renderer.component.css']
})
export class ReportGridActionCellRendererComponent implements OnInit, ICellRendererAngularComp {

  public params: ICellRendererParams

  constructor() { }

  ngOnInit(): void {
  }

  agInit(params: ICellRendererParams): void {
    this.params = params
  }

  refresh(params: ICellRendererParams): boolean {
    return false;
  }

  public editClick(): void {
    console.log('editClick()');

    // @ts-ignore
    // Call the "edit" method back on the grid page
    this.params.editButtonGridMethod(this.params);
  }

  public deleteClick(): void {
    console.log('deleteClick()');

    // @ts-ignore
    // Call the "delete" method back on the grid page
    this.params.deleteButtonGridMethod(this.params);
  }
}
