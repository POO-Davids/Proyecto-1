package interfaz;

import java.util.Scanner;

import controladores.ControladorUsuario;
import excepciones.ClubSenecaException;

public abstract class InterfazUsuario extends InterfazRol {

	public InterfazUsuario(ControladorUsuario controlador, Scanner entrada) {
		super(controlador, entrada);
	}

	private ControladorUsuario usuario() {
		return (ControladorUsuario) controlador;
	}

	@Override
	protected String[] getOpciones() {
		return new String[] { "Comprar en la tienda o cafetería", "Cotizar compra en la cafetería", "Consultar alérgenos",
				"Consumir bebida", "Consultar puntos de fidelidad" };
	}

	public void comprar() throws ClubSenecaException {
		String tienda = leerTexto("Tienda o Cafeteria");
		String[] articulos = leerLista("Artículos");
		int[] cantidades = leerEnteros("Cantidades");
		String codigo = "";
		float propina = 0;
		if (tienda.toLowerCase().startsWith("cafeter")) {
			String[] alergenos = usuario().consultarAlergenos(articulos);
			mostrarMensaje("Alérgenos: " + (alergenos.length == 0 ? "ninguno" : String.join(", ", alergenos)));
			mostrarMensaje(usuario().cotizarCompraCafeteria(articulos, cantidades));
			propina = leerDecimal("Propina");
		} else {
			codigo = leerTexto("Código de descuento (vacío si no tiene)");
		}
		mostrarMensaje("Puntos disponibles: " + usuario().consultarPuntosFidelidad());
		float puntos = leerDecimal("Puntos a redimir (0 si no)");
		mostrarMensaje(usuario().comprar(tienda, articulos, cantidades, codigo, propina, puntos));
	}

	public void cotizarCompraCafeteria() throws ClubSenecaException {
		mostrarMensaje(usuario().cotizarCompraCafeteria(leerLista("Artículos"), leerEnteros("Cantidades")));
	}

	public void consultarAlergenos() throws ClubSenecaException {
		String[] alergenos = usuario().consultarAlergenos(leerLista("Artículos de la cafetería"));
		mostrarMensaje("Alérgenos: " + (alergenos.length == 0 ? "ninguno" : String.join(", ", alergenos)));
	}

	public void consumirBebida() throws ClubSenecaException {
		usuario().consumirBebida(leerTexto("Bebida"));
		mostrarMensaje("Bebida consumida");
	}

	public void consultarPuntosFidelidad() {
		mostrarMensaje("Puntos de fidelidad: " + usuario().consultarPuntosFidelidad());
	}
}
