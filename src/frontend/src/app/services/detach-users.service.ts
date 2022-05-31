import { Observable } from 'rxjs';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class DetachUsersService {

  constructor(
    private http: HttpClient
  ) { }

  public detachUser(taskId: number, userId: number): Observable<String> {
    return this.http.post<String>(`${environment.apiUrl}/detachUser`, {taskId, userId});
  }
}
