import { User } from './user';
import { Status } from './../enumeration/status.enum';
import {Type} from "../enumeration/type.enum";
import {Priority} from "../enumeration/priority.enum";

export interface Task {
  id: number;
  taskId: string;
  title: string;
  details: string;
  priority: Priority;
  type: Type;
  status: Status;
  startDate: Date;
  endDate: Date;
  createdAt: Date;
  users: User[];
}
