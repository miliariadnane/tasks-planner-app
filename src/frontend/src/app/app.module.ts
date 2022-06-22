import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AngularEditorModule } from '@kolkov/angular-editor';
import { ToastrModule } from 'ngx-toastr';
import { DataTablesModule } from "angular-datatables";

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ContentComponent } from './ui/layouts/content/content.component';
import { FooterComponent } from './ui/layouts/footer/footer.component';
import { HeaderComponent } from './ui/layouts/header/header.component';
import { ListTasksComponent } from './ui/task/list-tasks/list-tasks.component';
import { TaskDetailsComponent } from './ui/task/task-details/task-details.component';
import { LoginComponent } from './ui/auth/login/login.component';
import { AdminLayoutsComponent } from './ui/admin-layouts/admin-layouts.component';
import { ListUsersComponent } from './ui/user/list-users/list-users.component';
import { UserProfileComponent } from './ui/user/user-profile/user-profile.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { MonitoringComponent } from './ui/monitoring/monitoring.component';
import { ResetPasswordComponent } from './ui/user/reset-password/reset-password.component';

@NgModule({
  declarations: [
    AppComponent,
    ContentComponent,
    FooterComponent,
    HeaderComponent,
    ListTasksComponent,
    TaskDetailsComponent,
    LoginComponent,
    AdminLayoutsComponent,
    ListUsersComponent,
    UserProfileComponent,
    MonitoringComponent,
    ResetPasswordComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularEditorModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    DataTablesModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
