import { User } from './../interfaces/user';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.prod';
import { CustomHttpResponse } from '../interfaces/custom-http-response';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(
    private http: HttpClient
  ) { }

  public getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.apiUrl}/users/list`);
  }

  public addUser(user: User): Observable<User> {
    return this.http.post<User>(`${environment.apiUrl}/users/add`, user);
  }

  public updateUser(formData: FormData): Observable<User> {
    return this.http.post<User>(`${environment.apiUrl}/users/update`, formData);
  }

  public updateUserProfile(formData: FormData): Observable<User> {
    return this.http.post<User>(`${environment.apiUrl}/users/update/profile`, formData);
  }

  public getUser(username: String): Observable<User> {
    return this.http.get<User>(`${environment.apiUrl}/users/find/${username}`);
  }

  public deleteUser(username: String): Observable<CustomHttpResponse> {
    return this.http.delete<CustomHttpResponse>(`${environment.apiUrl}/users/${username}/delete`);
  }

  public addUsersToLocalCache(users: User[]): void {
    localStorage.setItem('users', JSON.stringify(users));
  }

  public getUsersFromLocalCache(): User[] {
    if (localStorage.getItem('users')) {
        return JSON.parse(localStorage.getItem('users'));
    }
    return null;
  }

  public updateUserFormData(loggedInUsername: string, user: User): FormData {
    const formData = new FormData();
    formData.append('currentUsername', loggedInUsername);
    formData.append('firstName', user.firstName);
    formData.append('lastName', user.lastName);
    formData.append('username', user.username);
    formData.append('email', user.email);
    formData.append('role', user.role);
    formData.append('enable', JSON.stringify(user.enable));
    formData.append('job', user.job);
    formData.append('discordAccount', user.discordAccount);
    return formData;
  }
}
