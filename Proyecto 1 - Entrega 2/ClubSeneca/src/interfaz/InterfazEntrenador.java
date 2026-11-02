package interfaz;

import java.util.Scanner;

import controladores.ControladorEntrenador;
import excepciones.ClubSenecaException;

public class InterfazEntrenador extends InterfazEmpleado {

	public InterfazEntrenador(ControladorEntrenador controlador, Scanner entrada) {
		super(controlador, entrada);
	}

	private ControladorEntrenador entrenador() {
		return (ControladorEntrenador) controlador;
	}

	@Override
	protected String[] getOpciones() {
		return unir(super.getOpciones(), "Crear sesión de entrenamiento", "Registrar asistencia", "Registrar evaluación");
	}

	public void crearSesionEntrenamiento() throws ClubSenecaException {
		String tipo = leerTexto("Tipo (equipo o raqueta)");
		String idInstalacion = leerTexto("Id de la instalación");
		String[] convocados = tipo.equalsIgnoreCase("equipo") ? new String[] { leerTexto("Id del equipo convocado") }
				: leerLista("Ids de los jugadores convocados");
		entrenador().crearSesionEntrenamiento(tipo, leerFecha("Fecha"), leerEntero("Hora de inicio (0-23)"),
				leerEntero("Duración en minutos"), idInstalacion, convocados);
		mostrarMensaje("Sesión creada");
	}

	public void registrarAsistencia() throws ClubSenecaException {
		entrenador().registrarAsistencia(leerTexto("Id de la sesión"), leerTexto("Id del jugador"),
				leerTexto("Estado (asistió, llegó tarde, faltó)"));
		mostrarMensaje("Asistencia registrada");
	}

	public void registrarEvaluacion() throws ClubSenecaException {
		entrenador().registrarEvaluacion(leerTexto("Id de la sesión"), leerTexto("Id del jugador"), leerEntero("Evaluación (1 a 5)"),
				leerTexto("Comentario"));
		mostrarMensaje("Evaluación registrada");
	}

	@Override
	public void ejecutarOpcion(int opcion) {
		try {
			switch (opcion) {
			case 9 -> crearSesionEntrenamiento();
			case 10 -> registrarAsistencia();
			case 11 -> registrarEvaluacion();
			default -> super.ejecutarOpcion(opcion);
			}
		} catch (ClubSenecaException e) {
			mostrarError(e);
		}
	}
}
