import { AuthService } from 'src/app/services/auth.service';
import { Role } from 'src/app/enumeration/role.enum';
import { UserService } from 'src/app/services/user.service';
import { User } from './../../../interfaces/user';
import { NotificationService } from './../../../services/notification.service';
import { catchError } from 'rxjs/operators';
import { AppState } from './../../../interfaces/appstate';
import { CustomHttpResponse } from '../../../interfaces/custom-http-response';
import { DataState } from './../../../enumeration/datastate.enum';
import { Priority } from '../../../enumeration/priority.enum';
import { Type } from './../../../enumeration/type.enum';
import { Status } from './../../../enumeration/status.enum';
import { TaskService } from './../../../services/task.service';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AngularEditorConfig } from '@kolkov/angular-editor';
import { BehaviorSubject, map, Observable, of, startWith, Subject } from 'rxjs';
import { NgForm } from '@angular/forms';
import { Task } from 'src/app/interfaces/task';

import Swal from 'sweetalert2';
import { IDropdownSettings } from 'ng-multiselect-dropdown';

@Component({
  selector: 'app-list-tasks',
  templateUrl: './list-tasks.component.html',
  styleUrls: ['./list-tasks.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ListTasksComponent implements OnInit {

  appState$: Observable<AppState<CustomHttpResponse>>;
  readonly Type = Type;
  readonly Priority = Priority;
  readonly Status = Status;
  readonly DataState = DataState;
  private dataSubject = new BehaviorSubject<CustomHttpResponse>(null);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  private selectedTaskSubject = new Subject<Task>();
  selectedTask$ = this.selectedTaskSubject.asObservable();
  private filteredSubject = new BehaviorSubject<Type>(Type.ALL);
  filteredType$ = this.filteredSubject.asObservable();

  editorConfig: AngularEditorConfig = {
    editable: true,
    height: '170px',
    placeholder: 'Enter details here...',
  };

  public users: User[] = [];
  public usersList = [];
  dropdownSettings:IDropdownSettings={};

  selectedUsers: User[] = [];
  selectedUsersView = [];

  constructor(
    private taskService: TaskService,
    private notificationService: NotificationService,
    private userService: UserService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {

    this.appState$ = this.taskService.tasks$
      .pipe(
        map(response => {
          this.dataSubject.next(response);
          this.notificationService.onSuccess(response.message);
          this.filteredSubject.next(Type.ALL);
          return { dataState: DataState.LOADED, data: response }
        }),
        startWith({ dataState: DataState.LOADING }),
        catchError((error: string) => {
          this.notificationService.onError(error);
          return of({ dataState: DataState.ERROR, error })
        })
      );
    
    // retrieve all users
    this.getAllUsers();

    // dropdown settings
    this.dropdownSettings = {
      singleSelection: false,
      idField: "user_id",
      textField: "user_text",
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      allowSearchFilter: true
    };
  }

  filterTasks(type: Type): void {
    this.filteredSubject.next(type);
    this.appState$ = this.taskService.filterTasks$(type, this.dataSubject.value)
      .pipe(
        map(response => {
          this.notificationService.onSuccess(response.message);
          return { dataState: DataState.LOADED, data: response }
        }),
        startWith({ dataState: DataState.LOADED, data: this.dataSubject.value }),
        catchError((error: string) => {
          this.notificationService.onError(error);
          return of({ dataState: DataState.ERROR, error })
        })
      );
  }

  saveTask(taskForm: NgForm): void {
    this.isLoadingSubject.next(true);
    
    this.appState$ = this.taskService.save$(taskForm.value)
      .pipe(
        map(response => {
          this.dataSubject
          .next({...response, tasks: [response.tasks[0], ...this.dataSubject.value.tasks]});
          taskForm.reset();
          document.getElementById('closeModal').click();
          this.isLoadingSubject.next(false);
          this.filteredSubject.next(Type.ALL);
          this.notificationService.onSuccess(response.message);
          return { dataState: DataState.LOADED, data: this.dataSubject.value }
        }),
        startWith({ dataState: DataState.LOADED, data: this.dataSubject.value }),
        catchError((error: string) => {
          this.isLoadingSubject.next(false);
          this.notificationService.onError(error);
          return of({ dataState: DataState.ERROR, error })
        })
      );
  }

  updateTask(task: Task): void {
    this.isLoadingSubject.next(true);
    this.appState$ = this.taskService.update$(task)
      .pipe(
        map(response => {
          this.dataSubject.value.tasks[this.dataSubject.value.tasks.findIndex(task =>
            task.id === response.tasks[0].id)] = response.tasks[0];
          this.dataSubject
          .next({...response, tasks: this.dataSubject.value.tasks });
          document.getElementById('closeModalEdit').click();
          this.isLoadingSubject.next(false);
          this.filteredSubject.next(Type.ALL);
          this.notificationService.onSuccess(response.message);
          return { dataState: DataState.LOADED, data: this.dataSubject.value }
        }),
        startWith({ dataState: DataState.LOADED, data: this.dataSubject.value }),
        catchError((error: string) => {
          this.isLoadingSubject.next(false);
          this.notificationService.onError(error);
          return of({ dataState: DataState.ERROR, error })
        })
      );
  }

  deleteTask(taskId: number): void {

    Swal.fire( {
      title: 'Are you sure to remove this task?',
      text: 'You will be able to recover this task !',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'No, keep it'
    }).then((result) => {
      if (result.value) {
        this.appState$ = this.taskService.delete$(taskId)
          .pipe(
            map(response => {
              this.dataSubject
              .next({ ...response, 
                    tasks: this.dataSubject.value.tasks.filter(task => task.id !== response.tasks[0].id) });
              Swal.fire(
                'Deleted!',
                'The task has been deleted successfully.',
                'success'
              )
              this.filteredSubject.next(Type.ALL);
              return { dataState: DataState.LOADED, data: this.dataSubject.value }
            }),
            startWith({ dataState: DataState.LOADED, data: this.dataSubject.value }),
          )
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        catchError((error: string) => {
          Swal.fire(
            'Cancelled',
            'The guest is safe :)',
            'error'
          )
          return of({ dataState: DataState.ERROR, error })
        })
      }
    })
  }

  selectTask(task: Task): void {
    this.selectedTaskSubject.next(task);
    document.getElementById('editTaskButton').click();
  }

  getAllUsers() {
    let tmpUsers = [];
    this.userService.getUsers().subscribe(
      (res: User[]) => {
        this.users = res.filter(r => r.enable == true )
        this.users.map((el) => {
          tmpUsers.push({ user_id: el.id, user_text: el.lastName+' '+el.firstName });
        })
        this.usersList = tmpUsers;
      }
    );
  }

  onSelectUsers(item: any) {
    this.selectedUsers.push(item['user_id']);
    this.selectedUsersView.push(item);
  }

  onDeSelectAllUsers() {
    this.selectedUsers = [];
    this.selectedUsersView = [];
  }

  onDeSelectUsers(item: any) {
    let index = this.selectedUsers.indexOf(item['user_id']);
    this.selectedUsers.splice(index, 1);
    this.selectedUsersView.splice(index, 1);
  }

  onSelectAllUsers(items: any) {
    items.map(e => {
      this.selectedUsers.push(e['user_id']);
      this.selectedUsersView.push(e);
    });
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
}
