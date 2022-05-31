import { Observable } from 'rxjs';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class UserProfileImageService {

  constructor(
    private http: HttpClient
  ) { }

  public uploadProfileImage(formData: FormData): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/image/upload`, formData);
  }

  public updateProfileImage(formData: FormData): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/image/updateProfile`, formData);
  }

  public createUserFormData(userId, profileImage: File): FormData {
    const formData = new FormData();
    formData.append('userId', userId);
    formData.append('profileImage', profileImage);
    return formData;
  }
}
