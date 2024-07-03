import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-back-button',
  standalone: true,
  templateUrl: './back-button.component.html',
  styleUrl: './back-button.component.css',
})
export class BackButtonComponent {
  @Input() destination: string = '/chats';

  constructor(private router: Router) {}

  goBack() {
    this.router.navigate([this.destination]);
  }
}
