import { Type } from './../enumeration/type.enum';
import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {environment} from "../../environments/environment.prod";
import {Observable, throwError} from "rxjs";
import { tap, catchError } from 'rxjs/operators';
import {CustomHttpResponse} from "../interfaces/custom-http-response";
import { Task } from '../interfaces/task';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  constructor(private http: HttpClient) { }

  getOneTask(resource) {
    return this.http.get(environment.apiUrl+'/task/'+resource)
      .pipe(
        catchError(this.handleError)
      );
  }

  tasks$ = <Observable<CustomHttpResponse>>this.http.get<CustomHttpResponse>
    (`${environment.apiUrl}/task/all`)
    .pipe(
      catchError(this.handleError)
    );

  save$ = (task: Task) => <Observable<CustomHttpResponse>>
    this.http.post<CustomHttpResponse>
    (`${environment.apiUrl}/task/add`, task)
    .pipe(
      catchError(this.handleError)
    );

  filterTasks$ = (type: Type, data: CustomHttpResponse) => <Observable<CustomHttpResponse>>
    new Observable<CustomHttpResponse>(subscriber => {
      subscriber.next(type === Type.ALL ? { ...data, message: data.tasks.length > 0 ? `${data.tasks.length} tasks retrieved` : `No task to display`} :
        <CustomHttpResponse>{
          ...data,
          message: data.tasks.filter(task => task.type === type).length > 0 ? `Tasks filtered by ${type.toLowerCase()} type` : `No tasks of type ${type.toLowerCase()} found`,
          tasks: data.tasks.filter(task => task.type === type)
        });
      subscriber.complete();
    }).pipe(
      catchError(this.handleError)
    );

  update$ = (task: Task) => <Observable<CustomHttpResponse>>
    this.http.put<CustomHttpResponse>
      (`${environment.apiUrl}/task/update`, task)
      .pipe(
        catchError(this.handleError)
      );

  delete$ = (taskId: number) => <Observable<CustomHttpResponse>>
    this.http.delete<CustomHttpResponse>
      (`${environment.apiUrl}/task/delete/${taskId}`)
      .pipe(
        catchError(this.handleError)
      );

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage: string;
    if (error.error instanceof ErrorEvent) {
      errorMessage = `A client error occurred - ${error.error.message}`;
    } else {
      if (error.error.reason) {
        errorMessage = `${error.error.reason} - Error code ${error.status}`;
      } else {
        errorMessage = `An error occurred - Error code ${error.status}`;
      }
    }
    return throwError(errorMessage);
  }
}
