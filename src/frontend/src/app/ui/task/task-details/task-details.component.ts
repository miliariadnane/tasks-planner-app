import { AuthService } from 'src/app/services/auth.service';
import { Role } from 'src/app/enumeration/role.enum';
import { DetachUsersService } from './../../../services/detach-users.service';
import { Priority } from './../../../enumeration/priority.enum';
import { Task } from 'src/app/interfaces/task';
import { NotificationService } from './../../../services/notification.service';
import { TaskService } from './../../../services/task.service';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import Swal from 'sweetalert2';

@Component({
  selector: 'app-task-details',
  templateUrl: './task-details.component.html',
  styleUrls: ['./task-details.component.css']
})
export class TaskDetailsComponent implements OnInit {

  readonly Priority = Priority;
  task: Task = null;

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private detachUsersService: DetachUsersService,
    private notificationService: NotificationService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.getTask();
  }

  getTask() {
    this.route.params.subscribe(params => {
      this.taskService.getOneTask(params['id'])
        .subscribe(
          {
            next: (res: Task) => {
              this.notificationService.onSuccess("Task retrieved successfully");
              this.task = res;
            }, 
            error: (err) => {
              this.notificationService.onError(err);
            }
          }
          
        );
    });
  };

  detachUser(userId: number) {
    Swal.fire( {
      title: 'Are you sure to detach this user ?',
      text: 'You will not be able to recover this user !',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, detach it!',
      cancelButtonText: 'No, keep it'
    }).then((result) => {
      if (result.value) {
        this.detachUsersService.detachUser(this.task.id, userId)
          .subscribe(res => {
            this.task.users = this.task.users.filter(user => user.id !== userId);
            Swal.fire(
              'Detach !',
              'The detach has been do it successfully.',
              'success'
            )
        });
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        Swal.fire(
          'Cancelled',
          'The user is safe :)',
          'error'
        )
      }
    })
  }

  public get isSuperAdmin(): boolean {
    return this.getUserRole() === Role.SUPER_ADMIN;
  }

  private getUserRole(): string {
    return this.authService.getUserFromLocalCache().role;
  }
}
