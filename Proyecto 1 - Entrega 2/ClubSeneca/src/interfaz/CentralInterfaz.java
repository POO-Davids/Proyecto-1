package interfaz;

import java.util.NoSuchElementException;
import java.util.Scanner;

import controladores.CentralClubSeneca;
import controladores.ControladorAdministrador;
import controladores.ControladorEmpleado;
import controladores.ControladorEntrenador;
import controladores.ControladorRol;
import controladores.ControladorSocio;
import excepciones.ClubSenecaException;

public class CentralInterfaz {

	private InterfazRol interfazActual;
	private CentralClubSeneca central;
	private Scanner entrada;

	public CentralInterfaz(CentralClubSeneca central, Scanner entrada) {
		this.central = central;
		this.entrada = entrada;
	}

	public static void main(String[] args) {
		CentralClubSeneca central = new CentralClubSeneca(args.length > 0 ? args[0] : "datos");
		try {
			central.cargarInformacion();
		} catch (ClubSenecaException e) {
			System.out.println("Error al cargar la información: " + e.getMessage());
			return;
		}
		try {
			new CentralInterfaz(central, new Scanner(System.in)).iniciar();
		} catch (NoSuchElementException e) {
			System.out.println("\nFin de la entrada");
		}
		try {
			central.guardarInformacion();
		} catch (ClubSenecaException e) {
			System.out.println("Error al guardar la información: " + e.getMessage());
		}
	}

	public void iniciar() {
		System.out.println("=== Club Deportivo Séneca ===");
		while (true) {
			System.out.println("\n1. Iniciar sesión\n0. Salir");
			System.out.print("Opción: ");
			String opcion = entrada.nextLine().trim();
			if (opcion.equals("0")) {
				return;
			}
			if (opcion.equals("1")) {
				iniciarSesion();
				cerrarSesion();
			} else {
				System.out.println("Opción inválida");
			}
		}
	}

	public void iniciarSesion() {
		System.out.print("Login: ");
		String username = entrada.nextLine().trim();
		System.out.print("Password: ");
		String password = entrada.nextLine();
		try {
			ControladorRol controlador = central.iniciarSesion(username, password);
			interfazActual = crearInterfaz(controlador);
			System.out.println("Bienvenido, " + username + " (" + controlador.getRol() + ")");
			interfazActual.mostrarMenu();
		} catch (ClubSenecaException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void cerrarSesion() {
		interfazActual = null;
	}

	private InterfazRol crearInterfaz(ControladorRol controlador) {
		return switch (controlador.getRol()) {
		case "Administrador" -> new InterfazAdministrador((ControladorAdministrador) controlador, entrada);
		case "Socio" -> new InterfazSocio((ControladorSocio) controlador, entrada);
		case "Entrenador" -> new InterfazEntrenador((ControladorEntrenador) controlador, entrada);
		default -> new InterfazEmpleado((ControladorEmpleado) controlador, entrada);
		};
	}
}
