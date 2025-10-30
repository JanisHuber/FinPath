import {HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from '@angular/core';
import { FinancialSummary } from "../models/financial-summary.models";
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FinancialSummaryService {

  constructor(private http: HttpClient) {}

  getFinancialSummary(): Observable<FinancialSummary> {
    return this.http.get<FinancialSummary>('/api/v1/financial-summary');
  }
}
