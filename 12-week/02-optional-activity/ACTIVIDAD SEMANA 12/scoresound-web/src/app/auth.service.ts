import { Injectable, signal } from '@angular/core';
import { API_URL } from './api.config';

const CLAVE_TOKEN = 'token';

/**
 * Login contra la API y manejo del token.
 * El token se guarda en localStorage para que siga ahi si se recarga la pagina.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  /** Token actual (null si no hay sesion). Al ser signal, la pantalla reacciona sola. */
  readonly token = signal<string | null>(localStorage.getItem(CLAVE_TOKEN));

  /** POST /auth/login. Si sale bien guarda el token; si no, lanza un Error con el motivo. */
  async login(usuario: string, clave: string): Promise<void> {
    let respuesta: Response;
    try {
      respuesta = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ usuario, clave }),
      });
    } catch {
      // fetch solo falla asi cuando no hay conexion o el navegador bloquea la peticion (CORS)
      throw new Error('No se pudo conectar con la API. Revisa que este encendida.');
    }

    if (respuesta.status === 401) {
      throw new Error('Usuario o clave incorrectos.');
    }
    if (!respuesta.ok) {
      throw new Error(`La API respondió con un error (${respuesta.status}).`);
    }

    const { token } = (await respuesta.json()) as { token: string };
    localStorage.setItem(CLAVE_TOKEN, token);
    this.token.set(token);
  }

  /** Borra el token guardado (se usa cuando la API lo rechaza). */
  borrarToken(): void {
    localStorage.removeItem(CLAVE_TOKEN);
    this.token.set(null);
  }
}
