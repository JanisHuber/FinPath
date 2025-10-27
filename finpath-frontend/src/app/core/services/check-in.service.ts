import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CheckIn } from '@models/check-in.models';
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class CheckInService {

  constructor(private http: HttpClient) {}

  getCheckIn(): Observable<CheckIn> {
    return this.http.get<CheckIn>('/api/v1/check-in');
  }
}
