import { Job } from "../enumeration/job.enum";

export class User {
    public id: number;
    public userId: string;
    public firstName: string;
    public lastName: string;
    public username: string;
    public password: String;
    public email: string;
    public joinDate: Date;
    public job: Job;
    public profileImage: string;
    public enable: boolean;
    public discordAccount: string;
    public role: string;;
    public authorities: [];
  
    constructor() {
      this.userId = '';
      this.firstName = '';
      this.lastName = '';
      this.username = '';
      this.password = '';
      this.email = '';
      this.job = null;
      this.joinDate = null;
      this.profileImage = '';
      this.enable = false;
      this.discordAccount = '';
      this.role = '';
      this.authorities = [];
    }
}