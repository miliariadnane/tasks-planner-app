import { NotificationService } from 'src/app/services/notification.service';
import { HttpErrorResponse } from '@angular/common/http';
import { MonitoringService } from './../../services/monitoring.service';
import { SystemCpu } from './../../interfaces/system-cpu';
import { SystemHealth } from './../../interfaces/system-health';
import { Component, OnInit } from '@angular/core';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-monitoring',
  templateUrl: './monitoring.component.html',
  styleUrls: ['./monitoring.component.css']
})
export class MonitoringComponent implements OnInit {

  public systemHealth: SystemHealth;
  public systemCpu: SystemCpu;
  public processUpTime: string;
  private timestamp: number;

  public httpTraceList: any[] = [];
  public selectedHttpTrace: any;

  public http200Status: any[] = [];
  public http400Status: any[] = [];
  public http404Status: any[] = [];
  public http500Status: any[] = [];
  public defaultHttpStatus: any[] = [];

  public nmbrOfFeatures: number = 0;
  public nmbrOfBugs: number = 0;
  public nmbrOfImprovements: number = 0;
  public nmbrOfTests: number = 0;
  public nmbrOfDocumentations: number = 0;


  public notStarted: number = 0;
  public inProgress: number = 0;
  public done: number = 0;
  public closed: number = 0;

  dtOptions: DataTables.Settings = {};


  constructor(
    private monitoringService: MonitoringService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.dtOptions = {
      pagingType: 'full_numbers',
      pageLength: 5,
      lengthMenu : [5, 10, 25],
      processing: true,
      scrollX: false
    };
    
    this.getHttpTraces();
    this.getNumberOfTasksByStatus();
    this.getNumberOfTasksByType();
    this.getSystemHealth();
    this.getCpuUsage();
    this.getProcessUpTime(true);
  }

  public onRefresh(): void {
    console.log('refreshing');
    this.http200Status = [];
    this.http400Status = [];
    this.http404Status = [];
    this.http500Status = [];
    this.getHttpTraces();
    this.getSystemHealth();
    this.getCpuUsage();
    this.getProcessUpTime(false);
  }

  private getSystemHealth() {
    this.monitoringService.getSystemHealth().subscribe(
      {
        next: (response: SystemHealth) => {
          this.systemHealth = response;
          this.systemHealth.components.diskSpace.details.free = 
                  this.convertToFormatBytes(this.systemHealth.components.diskSpace.details.free);
        },

        error: (error: HttpErrorResponse) => {
          this.systemHealth = error.error;
          this.systemHealth.components.diskSpace.details.free = 
                  this.convertToFormatBytes(this.systemHealth.components.diskSpace.details.free);
        }
      }
    );
  }

  private getCpuUsage() {
    this.monitoringService.getSystemCpu().subscribe(
      {
        next: (res: SystemCpu) => {
          this.systemCpu = res;
        },
        error: (error: HttpErrorResponse) => {
          this.notificationService.onError(error.message);
        }
      }
    );
  }

  private getProcessUpTime(isUpdateTime: boolean) { // uptime of the Java virtual machine
    this.monitoringService.getProcessUptime().subscribe(
      {
        next: (res: any) => {
          this.timestamp = Math.round(res.measurements[0].value); // rounded value from measurements
          this.processUpTime = this.formateUptime(this.timestamp); // format timestamp to readable format with hours
          if (isUpdateTime) {
            this.updateTime();
          }
        },
        error: (error: HttpErrorResponse) => {
          this.notificationService.onError(error.message);
        }
      }
    );
  }

  private processTraces(tracesList: any) {
    this.httpTraceList = tracesList.filter((trace) => {
      return !trace.request.uri.includes('actuator');
    });
    this.httpTraceList.forEach(trace => {
      switch (trace.response.status) {
        case 200:
          this.http200Status.push(trace);
          break;
        case 400:
          this.http400Status.push(trace);
          break;
        case 404:
          this.http404Status.push(trace);
          break;
        case 500:
          this.http500Status.push(trace);
          break;
        default:
          this.defaultHttpStatus.push(trace);
      }
    });
  }

  private getHttpTraces() {
    this.monitoringService.getHttpTraces().subscribe(
      {
        next: (res: any) => {
          this.processTraces(res.traces);
          this.barChartInit();
        },
        error: (error: HttpErrorResponse) => {
          this.notificationService.onError(error.message);
        }
      }
    );
  }

  private barChartInit(): Chart {
    const barChartElement = document.getElementById('barChart');
    return new Chart(barChartElement, {
      type: 'bar',
      data: {
          labels: ['200', '404', '400', '500'],
          datasets: 
          [{
              data: [this.http200Status.length, this.http404Status.length, this.http400Status.length, this.http500Status.length],
              backgroundColor: ['rgb(40,167,69)', 'rgb(0,123,255)', 'rgb(253,126,20)', 'rgb(220,53,69)'],
              borderColor: ['rgb(40,167,69)', 'rgb(0,123,255)', 'rgb(253,126,20)', 'rgb(220,53,69)'],
              borderWidth: 3
          }]
      },
      options: {
        title: { display: true, text: [`Requests of ${this.formatDate(new Date())}`] },
        legend: { display: false },
        scales: {
          yAxes: [{ticks: { beginAtZero: true } }]
        }
      }
    });
  }

  private getNumberOfTasksByType() {
    this.monitoringService.countTasksByType().subscribe(
      {
        next: (res: any) => {
          this.nmbrOfBugs = res.BUG;
          this.nmbrOfFeatures = res.FEATURE;
          this.nmbrOfImprovements = res.IMPROVEMENT;
          this.nmbrOfTests = res.TEST;
          this.nmbrOfDocumentations = res.DOCUMENTATION;
          this.pieChart1Init();
        },
        error: (error: HttpErrorResponse) => {
          this.notificationService.onError(error.message);
        }
      }
    );
  }


  private getNumberOfTasksByStatus() {
    this.monitoringService.countTasksByStatus().subscribe(
      {
        next: (res: any) => {
          this.notStarted = res.NOT_STARTED;
          this.inProgress = res.IN_PROGRESS;
          this.done = res.DONE;
          this.closed = res.CLOSED;
          this.pieChart2Init();
        },
        error: (error: HttpErrorResponse) => {
          this.notificationService.onError(error.message);
        }
      }
    );
  }

  private pieChart1Init(): Chart {
    const element = document.getElementById('pieChart-TasksByType');
    return new Chart(element, {
      type: 'pie',
      data: {
          labels: ['Feature', 'Bug', 'Improvement', 'Test', 'Documentation'],
          datasets: [{data: [this.nmbrOfFeatures, this.nmbrOfBugs, this.nmbrOfImprovements, this.nmbrOfTests, this.nmbrOfDocumentations],
            backgroundColor: ['#2cd07e80', '#ff505080', '#2cabe380', '#ffc107', '#361a0a'],
            borderColor: ['#2cd07e80', '#ff505080', '#2cabe380', '#ffc107', '#361a0a'],
            borderWidth: 3
          }]
      },
      options: {
        legend: { display: true },
        display: true
      }
    });
  }

  private pieChart2Init(): Chart {
    const element = document.getElementById('pieChart-TasksByStatus');
    return new Chart(element, {
      type: 'pie',
      data: {
          labels: ['Not Started', 'In progress', 'Done', 'Closed'],
          datasets: [{data: [this.notStarted, this.inProgress, this.done, this.closed],
            backgroundColor: ['#2cd07e80', '#ff505080', '#2cabe380', '#ffc107', '#361a0a'],
            borderColor: ['#2cd07e80', '#ff505080', '#2cabe380', '#ffc107', '#361a0a'],
            borderWidth: 3
          }]
      },
      options: {
        legend: { display: true },
        display: true
      }
    });
  }

  private convertToFormatBytes(bytes): string {
    if (bytes === 0) {
      return '0 Bytes';
    }

    const k = 1024;
    const dm = 2 < 0 ? 0 : 2;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
  }

  public onSelectHttpTrace(httpTrace: any): void {
    this.selectedHttpTrace = httpTrace;
    document.getElementById('http-trace-modal').click();
  }

  private updateTime(): void {
    setInterval(() => {
      this.processUpTime = this.formateUptime(this.timestamp + 1);
      this.timestamp++;
    }, 1000);
  }

  private formateUptime(timestamp: number): string {
    const hours = Math.floor(timestamp / 60 / 60);
    const minutes = Math.floor(timestamp / 60) - (hours * 60);
    const seconds = timestamp % 60;
    return hours.toString().padStart(2, '0') + 'h' +
    minutes.toString().padStart(2, '0') + 'm' + seconds.toString().padStart(2, '0') + 's';
  }

  private formatDate(date: Date): string {
    const dd = date.getDate();
    const mm = date.getMonth() + 1;
    const year = date.getFullYear();
    if (dd < 10) {
      const day = `0${dd}`;
    }
    if (mm < 10) {
      const month = `0${mm}`;
    }
    return `${mm}/${dd}/${year}`;
  }

}
