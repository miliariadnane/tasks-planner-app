import { Role } from 'src/app/enumeration/role.enum';
import { User } from './../../../interfaces/user';
import { Router } from '@angular/router';
import { AuthService } from './../../../services/auth.service';
import { Component, OnInit } from '@angular/core';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  public firstName: String;
  public lastName: String;
  readonly discordUrl = 'https://discord.gg/6BTwtQDh';

  constructor(
    private router: Router,
    private authService: AuthService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.firstName = this.authService.getUserFromLocalCache().firstName;
    this.lastName = this.authService.getUserFromLocalCache().lastName;
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

}
