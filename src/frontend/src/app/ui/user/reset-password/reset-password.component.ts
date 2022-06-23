import { Role } from './../../../enumeration/role.enum';
import { AuthService } from 'src/app/services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { NotificationService } from 'src/app/services/notification.service';
import { ResetPasswordService } from './../../../services/reset-password.service';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CustomHttpResponse } from 'src/app/interfaces/custom-http-response';
import { Subscription } from 'rxjs';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit, OnDestroy {

  public refreshing: boolean;
  private subscriptions: Subscription[] = [];

  constructor(
    private resetPasswordService: ResetPasswordService,
    private notificationService: NotificationService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
  }

  public onResetPassword(resetPasswordForm: NgForm): void {
    this.refreshing = true;
    const emailAddress = resetPasswordForm.value['reset-password'];
    this.subscriptions.push(
      this.resetPasswordService.resetPassword(emailAddress).subscribe(
        {
          next: (res: CustomHttpResponse) => {
            this.notificationService.onSuccess(res.message);
            this.refreshing = false;
            resetPasswordForm.reset();
          },
          error: (error: HttpErrorResponse) => {
            this.notificationService.onError(error.error.message);
            this.refreshing = false;
          }
        }
      )
    )
  }

  private getUserRole(): string {
    return this.authService.getUserFromLocalCache().role;
  }

  public get isAdminOrSuperAdmin(): boolean {
    return this.getUserRole() === Role.ADMIN || this.getUserRole() === Role.SUPER_ADMIN;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

}
