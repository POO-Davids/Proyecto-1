package interfaz;

import java.util.Scanner;

import controladores.ControladorEmpleado;
import excepciones.ClubSenecaException;
import tiendas.Articulo;
import tiendas.ArticuloTienda;
import tiendas.Bebida;
import tiendas.Snack;

public class InterfazEmpleado extends InterfazUsuario {

	public InterfazEmpleado(ControladorEmpleado controlador, Scanner entrada) {
		super(controlador, entrada);
	}

	private ControladorEmpleado empleado() {
		return (ControladorEmpleado) controlador;
	}

	@Override
	protected String[] getOpciones() {
		return unir(super.getOpciones(), "Compartir código de descuento", "Solicitar cambio de turno", "Sugerir artículo");
	}

	public void compartirCodigoDescuento() throws ClubSenecaException {
		empleado().compartirCodigoDescuento(leerTexto("Username del socio"));
		mostrarMensaje("Código compartido");
	}

	public void solicitarCambioTurno() throws ClubSenecaException {
		empleado().solicitarCambioTurno(leerFecha("Fecha"), leerHorario("Horas del turno nuevo"));
		mostrarMensaje("Solicitud enviada");
	}

	public void sugerirArticulo() throws ClubSenecaException {
		String tienda = leerTexto("Tienda o Cafeteria");
		String nombre = leerTexto("Nombre");
		float precio = leerDecimal("Precio");
		Articulo articulo;
		if (tienda.equalsIgnoreCase("Tienda")) {
			articulo = new ArticuloTienda(nombre, precio, leerTexto("Categoría"));
		} else if (leerTexto("Bebida o Snack").equalsIgnoreCase("Bebida")) {
			articulo = new Bebida(nombre, precio, leerSiNo("¿Es caliente?"));
		} else {
			articulo = new Snack(nombre, precio, leerLista("Alérgenos"));
		}
		empleado().sugerirArticulo(tienda, articulo);
		mostrarMensaje("Sugerencia enviada");
	}

	@Override
	public void ejecutarOpcion(int opcion) {
		try {
			switch (opcion) {
			case 1 -> comprar();
			case 2 -> cotizarCompraCafeteria();
			case 3 -> consultarAlergenos();
			case 4 -> consumirBebida();
			case 5 -> consultarPuntosFidelidad();
			case 6 -> compartirCodigoDescuento();
			case 7 -> solicitarCambioTurno();
			case 8 -> sugerirArticulo();
			default -> super.ejecutarOpcion(opcion);
			}
		} catch (ClubSenecaException e) {
			mostrarError(e);
		}
	}
}
