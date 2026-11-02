import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from './auth.service';

/** Pantalla de login: pide usuario y clave y guarda el token. */
@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly auth = inject(AuthService);

  protected usuario = '';
  protected clave = '';

  // Estados de la pantalla: cargando / error (si no hay ninguno, esta en reposo)
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);

  protected async entrar(): Promise<void> {
    this.cargando.set(true);
    this.error.set(null);
    try {
      await this.auth.login(this.usuario, this.clave);
    } catch (e) {
      this.error.set((e as Error).message);
    } finally {
      this.cargando.set(false);
    }
  }
}
