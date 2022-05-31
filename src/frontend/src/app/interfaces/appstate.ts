import { DataState } from "../enumeration/datastate.enum";

export interface AppState<T> {
  dataState: DataState;
  data?: T;
  error?: string;
}
