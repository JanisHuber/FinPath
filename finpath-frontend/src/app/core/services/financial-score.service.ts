import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FinancialScore } from '@models/financial-score.models';
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FinancialScoreService {

  constructor(private http: HttpClient) {}

  getFinancialScore(): Observable<FinancialScore> {
    return this.http.get<FinancialScore>('api/v1/financial-score');
  }
}