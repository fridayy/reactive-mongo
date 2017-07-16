import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import * as EventSource from 'eventsource';
@Injectable()
export class SseService {

  constructor() {
  }

  public get(): Observable<any> {
    return Observable.create(observer => {
      let source = new EventSource('http://localhost:8080/');
      source.onmessage = x => observer.next(x.data);
      source.onerror = x => observer.error(x);
      return () => source.close();
    });
  }


}
