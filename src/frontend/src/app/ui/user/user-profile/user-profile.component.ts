import { UserProfileImageService } from './../../../services/user-profile-image.service';
import { Job } from './../../../enumeration/job.enum';
import { Router } from '@angular/router';
import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { User } from './../../../interfaces/user';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { NotificationService } from 'src/app/services/notification.service';
import { UserService } from 'src/app/services/user.service';
import { Role } from 'src/app/enumeration/role.enum';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit, OnDestroy {

  public user: User;
  private subscriptions: Subscription[] = [];
  public refreshing: boolean;
  readonly Role = Role;
  readonly Job = Job;
  readonly discordUrl = 'https://discord.gg/6BTwtQDh';

  public currentUser: string;
  public currentUsername: string;

  public fileName: string;
  public profileImage: File;

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private userProfileImageService: UserProfileImageService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.getUser();
  }

  getUser() {
    let username = this.authService.getUserFromLocalCache().username;
    this.userService.getUser(username).subscribe(
      {
        next: (response: User) => {
          this.user = response;
        },
        error: (errorResponse: HttpErrorResponse) => {
          this.notificationService.onError(errorResponse.error.message);
        }
      }
    )
  }

  public onUpdateProfileImage(): void {
    document.getElementById('profile-image-input').click();
  }

  updateProfileImage() {
    const formData = new FormData();
    formData.append('userId', this.user.userId);
    formData.append('profileImage', this.profileImage);
    this.subscriptions.push(
      this.userProfileImageService.uploadProfileImage(formData).subscribe(
        {
          next: (res) => {
            this.notificationService.onSuccess('Profile image updated successfully');
            this.getUser();
          },
          error: (errorResponse: HttpErrorResponse) => {
            this.notificationService.onError(errorResponse.error.message);
          }
        }
        
      )
    );
  }

  public updateCurrentUser(user: User): void {
    this.currentUsername = this.authService.getUserFromLocalCache().username;
    const formData = this.userService.updateUserFormData(this.currentUsername, user);

    this.subscriptions.push(
      this.userService.updateUserProfile(formData).subscribe(
        {
          next: (response: User) => {
            this.notificationService.onSuccess(`${response.firstName} ${response.lastName} updated successfully`);
          },

          error: (errorResponse: HttpErrorResponse) => {
            this.notificationService.onError(errorResponse.error.message);
          }
        }
      )
    );
  }

  public logOut(): void {
    this.authService.logOut();
    this.router.navigate(['/login']);
    this.notificationService.onSuccess(`You've been successfully logged out`);
  }

   public get isSuperAdmin(): boolean {
    return this.getUserRole() === Role.SUPER_ADMIN;
  }

  private getUserRole(): string {
    return this.authService.getUserFromLocalCache().role;
  }

  public onProfileImageChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.fileName = target.files[0].name;
    console.log(this.fileName);
    this.profileImage = target.files[0];
    console.log(this.profileImage);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
}
