package interfaz;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import controladores.ControladorRol;
import excepciones.ClubSenecaException;

public abstract class InterfazRol {

	protected ControladorRol controlador;
	private Scanner entrada;
	private boolean sesionActiva;

	public InterfazRol(ControladorRol controlador, Scanner entrada) {
		this.controlador = controlador;
		this.entrada = entrada;
	}

	public void mostrarMenu() {
		sesionActiva = true;
		while (sesionActiva) {
			mostrarMensaje("\n== Menú " + controlador.getRol() + " ==");
			String[] opciones = getOpciones();
			for (int i = 0; i < opciones.length; i++) {
				mostrarMensaje((i + 1) + ". " + opciones[i]);
			}
			mostrarMensaje("0. Cerrar sesión");
			ejecutarOpcion(leerEntero("Opción"));
		}
	}

	public void ejecutarOpcion(int opcion) {
		if (opcion != 0) {
			mostrarMensaje("Opción inválida");
			return;
		}
		try {
			controlador.cerrarSesion();
			mostrarMensaje("Sesión cerrada");
		} catch (ClubSenecaException e) {
			mostrarError(e);
		}
		sesionActiva = false;
	}

	protected abstract String[] getOpciones();

	protected static String[] unir(String[] base, String... extra) {
		String[] todas = Arrays.copyOf(base, base.length + extra.length);
		System.arraycopy(extra, 0, todas, base.length, extra.length);
		return todas;
	}

	protected String leerTexto(String mensaje) {
		System.out.print(mensaje + ": ");
		return entrada.nextLine().trim();
	}

	protected int leerEntero(String mensaje) {
		while (true) {
			try {
				return Integer.parseInt(leerTexto(mensaje));
			} catch (NumberFormatException e) {
				mostrarMensaje("Debe escribir un número entero");
			}
		}
	}

	protected float leerDecimal(String mensaje) {
		while (true) {
			try {
				return Float.parseFloat(leerTexto(mensaje).replace(',', '.'));
			} catch (NumberFormatException e) {
				mostrarMensaje("Debe escribir un número");
			}
		}
	}

	protected LocalDate leerFecha(String mensaje) {
		while (true) {
			try {
				return LocalDate.parse(leerTexto(mensaje + " (yyyy-MM-dd)"));
			} catch (DateTimeParseException e) {
				mostrarMensaje("La fecha debe tener el formato yyyy-MM-dd");
			}
		}
	}

	protected LocalDateTime leerFechaHora(String mensaje) {
		LocalDate fecha = leerFecha(mensaje);
		int hora = leerEntero("Hora (0-23)");
		while (hora < 0 || hora > 23) {
			hora = leerEntero("Hora (0-23)");
		}
		return fecha.atTime(hora, 0);
	}

	protected String[] leerLista(String mensaje) {
		String texto = leerTexto(mensaje + " (separados por coma)");
		if (texto.isEmpty()) {
			return new String[0];
		}
		return Arrays.stream(texto.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
	}

	protected int[] leerEnteros(String mensaje) {
		while (true) {
			try {
				List<Integer> valores = new ArrayList<>();
				for (String parte : leerLista(mensaje)) {
					if (parte.contains("-")) {
						String[] rango = parte.split("-");
						for (int v = Integer.parseInt(rango[0].trim()); v <= Integer.parseInt(rango[1].trim()); v++) {
							valores.add(v);
						}
					} else {
						valores.add(Integer.parseInt(parte));
					}
				}
				return valores.stream().mapToInt(Integer::intValue).toArray();
			} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
				mostrarMensaje("Escriba números o rangos, por ejemplo 8-11,14");
			}
		}
	}

	protected Boolean[] leerHorario(String mensaje) {
		Boolean[] horario = new Boolean[24];
		Arrays.fill(horario, false);
		for (int hora : leerEnteros(mensaje + " [horas 0-23, ej. 8-11]")) {
			if (hora >= 0 && hora < 24) {
				horario[hora] = true;
			}
		}
		return horario;
	}

	protected Boolean leerSiNo(String mensaje) {
		return leerTexto(mensaje + " (s/n)").toLowerCase().startsWith("s");
	}

	protected void mostrarMensaje(String mensaje) {
		System.out.println(mensaje);
	}

	protected void mostrarError(ClubSenecaException error) {
		System.out.println("Error: " + error.getMessage());
	}
}
