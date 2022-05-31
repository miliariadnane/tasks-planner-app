import { NotificationTitle } from './../enumeration/notification-title.enum';
import { Injectable } from '@angular/core';
import { IndividualConfig, ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private readonly options: Partial<IndividualConfig> = {
    positionClass: 'toast-bottom-left',
    progressBar: false,
    disableTimeOut: false
  };

  constructor(private toast: ToastrService) { }

  onSuccess(message: string): void {
    this.toast.success(message, NotificationTitle.SUCCESS, this.options);
  }

  onInfo(message: string): void {
    this.toast.info(message, NotificationTitle.INFO, this.options);
  }

  onWarning(message: string): void {
    this.toast.warning(message, NotificationTitle.WARNING, this.options);
  }

  onError(message: string): void {
    this.toast.error(message, NotificationTitle.ERROR, this.options);
  }
}
