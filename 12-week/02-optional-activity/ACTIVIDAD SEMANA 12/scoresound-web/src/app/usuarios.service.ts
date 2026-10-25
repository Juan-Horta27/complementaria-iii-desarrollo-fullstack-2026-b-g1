import { Injectable, inject } from '@angular/core';
import { API_URL } from './api.config';
import { AuthService } from './auth.service';

export interface Usuario {
  id: number;
  nombre: string;
  correo: string;
  fechaRegistro: string;
}

/** Error con el codigo HTTP, para que la pantalla sepa si fue un 401. */
export class ApiError extends Error {
  constructor(mensaje: string, readonly status: number) {
    super(mensaje);
  }
}

/** Consume el endpoint protegido GET /usuarios enviando el token. */
@Injectable({ providedIn: 'root' })
export class UsuariosService {
  private readonly auth = inject(AuthService);

  async listar(): Promise<Usuario[]> {
    let respuesta: Response;
    try {
      respuesta = await fetch(`${API_URL}/usuarios`, {
        // El token viaja en la cabecera Authorization
        headers: { Authorization: `Bearer ${this.auth.token()}` },
      });
    } catch {
      throw new ApiError('No se pudo conectar con la API. Revisa que este encendida.', 0);
    }

    if (respuesta.status === 401) {
      throw new ApiError('La sesión no es válida o expiró. Inicia sesión de nuevo.', 401);
    }
    if (!respuesta.ok) {
      throw new ApiError(`La API respondió con un error (${respuesta.status}).`, respuesta.status);
    }
    return (await respuesta.json()) as Usuario[];
  }
}
