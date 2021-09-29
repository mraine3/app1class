import { Component, OnInit } from '@angular/core';
import {ICellRendererAngularComp} from "ag-grid-angular";
import {ICellRendererParams} from "ag-grid-community";

@Component({
  selector: 'app-priority-cell-custom-renderer',
  templateUrl: './priority-cell-custom-renderer.component.html',
  styleUrls: ['./priority-cell-custom-renderer.component.css']
})
export class PriorityCellCustomRendererComponent implements OnInit, ICellRendererAngularComp {

  public params: ICellRendererParams

  constructor() { }

  public ngOnInit(): void {
  }

  public agInit(params: ICellRendererParams): void {
    this.params = params;
  }

  public refresh(params: ICellRendererParams): boolean {
    return false;
  }

}
