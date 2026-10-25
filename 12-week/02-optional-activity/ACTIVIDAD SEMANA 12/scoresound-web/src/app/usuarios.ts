import { Component, inject, signal } from '@angular/core';
import { AuthService } from './auth.service';
import { ApiError, Usuario, UsuariosService } from './usuarios.service';

/** Lista los usuarios que devuelve el endpoint protegido GET /usuarios. */
@Component({
  selector: 'app-usuarios',
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css',
})
export class Usuarios {
  private readonly servicio = inject(UsuariosService);
  private readonly auth = inject(AuthService);

  // Estados: cargando -> datos (ok) o error
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly sesionInvalida = signal(false);
  protected readonly usuarios = signal<Usuario[]>([]);

  constructor() {
    this.cargar();
  }

  protected async cargar(): Promise<void> {
    this.cargando.set(true);
    this.error.set(null);
    try {
      this.usuarios.set(await this.servicio.listar());
    } catch (e) {
      this.error.set((e as Error).message);
      this.sesionInvalida.set(e instanceof ApiError && e.status === 401);
    } finally {
      this.cargando.set(false);
    }
  }

  /** Token rechazado por la API: se borra para volver al login. */
  protected volverAlLogin(): void {
    this.auth.borrarToken();
  }
}
