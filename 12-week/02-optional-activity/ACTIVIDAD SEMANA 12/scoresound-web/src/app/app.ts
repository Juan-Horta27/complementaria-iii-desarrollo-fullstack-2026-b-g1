import { Component, inject } from '@angular/core';
import { AuthService } from './auth.service';
import { Login } from './login';
import { Usuarios } from './usuarios';

@Component({
  selector: 'app-root',
  imports: [Login, Usuarios],
  templateUrl: './app.html',
})
export class App {
  protected readonly auth = inject(AuthService);
}
