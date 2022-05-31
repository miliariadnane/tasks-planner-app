import { Role } from 'src/app/enumeration/role.enum';
import { Job } from './../../../enumeration/job.enum';
import { UserProfileImageService } from './../../../services/user-profile-image.service';
import { NotificationService } from './../../../services/notification.service';
import { HttpErrorResponse } from '@angular/common/http';
import { User } from './../../../interfaces/user';
import { AuthService } from './../../../services/auth.service';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { Subscription } from 'rxjs';
import { NgForm } from '@angular/forms';

import Swal from 'sweetalert2';

@Component({
  selector: 'app-list-users',
  templateUrl: './list-users.component.html',
  styleUrls: ['./list-users.component.css']
})
export class ListUsersComponent implements OnInit, OnDestroy {

  public user: User;
  public users: User[];
  public newUserForm: NgForm;
  public refreshing: boolean;
  private subscriptions: Subscription[] = [];
  public profileImage: File;
  public fileName: string;
  public editUser = new User();
  readonly Job = Job;
  public selectedUser: User;
  
  public currentUsername: string;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private userProfileImageService: UserProfileImageService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.user = this.authService.getUserFromLocalCache();
    this.getUsers(true);
  }

  public onSelectUser(selectedUser: User): void {
    this.selectedUser = selectedUser;
  }

  public getUsers(showNotification: boolean): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.userService.getUsers().subscribe(
        {
          next: (response: User[]) => {
            this.userService.addUsersToLocalCache(response);
            this.users = response;
            this.refreshing = false;
            if (showNotification) {
              this.notificationService.onSuccess(`${response.length} user(s) loaded successfully.`);
            }
          },
          error: (errorResponse: HttpErrorResponse) => {
            this.notificationService.onError(errorResponse.error.message);
            this.refreshing = false;
          }
        }
      )
    );
  }

  public searchUsers(searchTerm: string): void {
    const results: User[] = [];
    for (const user of this.userService.getUsersFromLocalCache()) {
      if (user.firstName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          user.lastName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          user.email.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          user.job.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1) {
          results.push(user);
      }
    }
    this.users = results;
    if (results.length === 0 || !searchTerm) {
      this.users = this.userService.getUsersFromLocalCache();
    }
  }

  public onSaveNewUser(): void {
    this.clickButton('save-user');
  }

  public addNewUser(userForm: NgForm): void {
    this.subscriptions.push(
      this.userService.addUser(userForm.value).subscribe(
        {
          next: (response: User) => {
            if(this.profileImage) {
              const formData = this.userProfileImageService.createUserFormData(response.userId, this.profileImage);
              this.userProfileImageService.uploadProfileImage(formData).subscribe(
                { 
                  next: (res) => {
                    this.getUsers(true);
                    this.notificationService.onSuccess(`image uploaded successfully`);
                  },
                  error: (errorResponse: HttpErrorResponse) => {
                    this.notificationService.onError(errorResponse.error.message);
                  }
                }
              )
            }
  
            this.clickButton('close-modal');
            this.getUsers(false);
            this.fileName = null;
            this.profileImage = null;
            userForm.reset();
            this.notificationService.onSuccess(`${response.firstName} ${response.lastName} added successfully`);
          },

          error: (errorResponse: HttpErrorResponse) => {
            this.notificationService.onError(errorResponse.error.message);
            this.profileImage = null;
          }
        }
      )
    );
  }

  public onEditUser(editUser: User): void {
    this.editUser = editUser;
    console.log(this.editUser)
    this.currentUsername = editUser.username;

  }

  public onUpdateUser(): void {
    this.clickButton('edit-user');
  }

  public updateUser(editUserForm: NgForm): void {
    console.log(editUserForm.value);
    this.subscriptions.push(
      this.userService.updateUser(editUserForm.value).subscribe(
        {
          next: (response: User) => {
            console.log("response"+response.enable);
            if(this.profileImage) {
              const formData = this.userProfileImageService.createUserFormData(response.userId, this.profileImage);
              this.userProfileImageService.uploadProfileImage(formData).subscribe(
                { 
                  next: (res) => {
                    this.getUsers(true);
                    this.notificationService.onSuccess(`image updated successfully`);
                  },
                  error: (errorResponse: HttpErrorResponse) => {
                    this.notificationService.onError(errorResponse.error.message);
                    this.profileImage = null;
                  }
                }
              )
            }
  
            this.clickButton('close-edit-modal');
            this.getUsers(false);
            this.fileName = null;
            this.profileImage = null;
            this.notificationService.onSuccess(`${response.firstName} ${response.lastName} updated successfully`);
          },

          error: (errorResponse: HttpErrorResponse) => {
            this.notificationService.onError(errorResponse.error.message);
            this.profileImage = null;
          }
        }
      )
    );
  }

  public deleteUser(username: String): void {
    Swal.fire( {
      title: 'Are you sure to remove this user?',
      text: 'You will be able to recover this user !',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'No, keep it'
    }).then((result) => {
      if (result.value) {
        this.userService.deleteUser(username)
          .subscribe(res => {
            Swal.fire(
              'Deleted!',
              'The user has been deleted successfully.',
              'success'
            )
            this.getUsers(false);
        });
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        Swal.fire(
          'Cancelled',
          'The guest is safe :)',
          'error'
        )
      }
    })
  }

  public onProfileImageChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.fileName = target.files[0].name;
    this.profileImage = target.files[0];
  }

  public clickButton(buttonId: string): void {
    document.getElementById(buttonId).click();
  }

  public get isAdminOrSuperAdmin(): boolean {
    return this.getUserRole() === Role.ADMIN || this.getUserRole() === Role.SUPER_ADMIN;
  }

  public get isSuperAdmin(): boolean {
    return this.getUserRole() === Role.SUPER_ADMIN;
  }

  private getUserRole(): string {
    return this.authService.getUserFromLocalCache().role;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

}
