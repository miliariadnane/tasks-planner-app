import { ResetPasswordComponent } from './ui/user/reset-password/reset-password.component';
import { MonitoringComponent } from './ui/monitoring/monitoring.component';
import { UserProfileComponent } from './ui/user/user-profile/user-profile.component';
import { LoginComponent } from './ui/auth/login/login.component';
import { ListTasksComponent } from './ui/task/list-tasks/list-tasks.component';
import { TaskDetailsComponent } from './ui/task/task-details/task-details.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminLayoutsComponent } from './ui/admin-layouts/admin-layouts.component';
import { ListUsersComponent } from './ui/user/list-users/list-users.component';
import { AuthenticatedGuard } from './guard/authenticated.guard';

const routes: Routes = [
  {path: '',   redirectTo: 'login', pathMatch: 'full' }, 
  {path: 'login',component: LoginComponent },
  {path: 'dashboard',component: AdminLayoutsComponent,  canActivate: [AuthenticatedGuard],
    children: [
      { path:"tasks", component: ListTasksComponent},
      { path:"tasks/:id/show", component: TaskDetailsComponent},
      { path:"users", component: ListUsersComponent},
      { path:"user/profile", component: UserProfileComponent},
      { path:"user/reset-password", component: ResetPasswordComponent},
      { path:"monitoring", component: MonitoringComponent}
  ]}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
