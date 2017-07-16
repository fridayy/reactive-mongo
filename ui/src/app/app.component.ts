import {Component, NgZone, OnInit} from '@angular/core';
import {SseService} from "./sse-service.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  items: Array<any> = [];
  zone:NgZone;

  constructor(private sseservice: SseService) {
    this.zone = new NgZone({enableLongStackTrace: false});
  }

  ngOnInit(): void {
    this.sseservice.get().subscribe(
      next => { this.zone.run(() =>this.items.push(next))}
    );
  }
}
