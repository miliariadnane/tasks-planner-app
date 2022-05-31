import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { User } from './../../../interfaces/user';
import { AuthService } from './../../../services/auth.service';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationService } from 'src/app/services/notification.service';
import { Subscription } from 'rxjs';
import { HeaderType } from 'src/app/enumeration/header-type.enum';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  public showLoading: boolean;

  constructor(
    private router: Router, 
    private authService: AuthService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    if (this.authService.isUserLoggedIn()) {
      this.router.navigateByUrl('/dashboard/tasks');
    } else {
      this.router.navigateByUrl('/login');
    }
  }

  public login(user: User): void {
    this.showLoading = true;
    this.subscriptions.push(
      this.authService.login(user).subscribe(
        {
          next: (response: HttpResponse<User>) => {
            const token = response.headers.get(HeaderType.JWT_TOKEN);
            this.authService.saveToken(token);
            this.authService.addUserToLocalCache(response.body);
            this.router.navigateByUrl('/dashboard/tasks');
            this.notificationService.onSuccess("Login successful.");
            this.showLoading = false;
          },
          error: (errorResponse: HttpErrorResponse) => {
            this.notificationService.onError(errorResponse.error.message);
            this.showLoading = false;
          }
        }
        
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

}
