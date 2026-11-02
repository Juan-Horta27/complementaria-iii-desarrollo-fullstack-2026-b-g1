import { TestBed } from '@angular/core/testing';
import { Usuarios } from './usuarios';
import { ApiError, Usuario, UsuariosService } from './usuarios.service';

/**
 * Pruebas del componente Usuarios.
 * Se reemplaza UsuariosService por uno falso para no llamar a la API real:
 * asi la prueba solo comprueba lo que el componente muestra en pantalla.
 */
describe('Usuarios', () => {
  async function crear(listar: () => Promise<Usuario[]>) {
    TestBed.configureTestingModule({
      providers: [{ provide: UsuariosService, useValue: { listar } }],
    });
    const fixture = TestBed.createComponent(Usuarios);
    await fixture.whenStable();
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  it('muestra los usuarios cuando la API responde bien', async () => {
    const pantalla = await crear(() =>
      Promise.resolve([{ id: 1, nombre: 'Ana', correo: 'ana@x.com', fechaRegistro: '2026-10-20' }]),
    );

    expect(pantalla.textContent).toContain('Ana');
    expect(pantalla.textContent).toContain('ana@x.com');
  });

  it('muestra el error y el boton para volver al login cuando la API responde 401', async () => {
    const pantalla = await crear(() =>
      Promise.reject(new ApiError('La sesión no es válida o expiró. Inicia sesión de nuevo.', 401)),
    );

    expect(pantalla.textContent).toContain('La sesión no es válida');
    expect(pantalla.textContent).toContain('Volver a iniciar sesión');
    expect(pantalla.querySelector('table')).toBeNull();
  });
});
