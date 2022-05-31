import {Task} from "./task";
export interface CustomHttpResponse {
  message: string;
  status: string;
  statusCode: number;
  reason: string;
  timeStamp: Date;
  tasks?: Task[]
}
