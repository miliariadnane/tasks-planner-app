import { SystemCpu } from './../interfaces/system-cpu';
import { SystemHealth } from './../interfaces/system-health';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class MonitoringService {

  constructor(private http: HttpClient) {}

  public getSystemHealth(): Observable<SystemHealth> {
    return this.http.get<SystemHealth>(`${environment.actuatorurl}/health`);
  }

  public getSystemCpu(): Observable<SystemCpu> {
    return this.http.get<SystemCpu>(`${environment.actuatorurl}/metrics/system.cpu.count`);
  }

  public getProcessUptime(): Observable<any> {
    return this.http.get<any>(`${environment.actuatorurl}/metrics/process.uptime`);
  }

  public getHttpTraces(): Observable<any> {
    return this.http.get<any>(`${environment.actuatorurl}/httptrace`);
  }

  public countTasksByType(): Observable<any> {
    return this.http.get<any>(`${environment.actuatorurl}/count-tasks-by-type`);
  }

  public countTasksByStatus(): Observable<any> {
    return this.http.get<any>(`${environment.actuatorurl}/count-tasks-by-status`);
  }
}
