package controladores;

import java.time.LocalDate;

import entrenamientos.SesionEntrenamiento;
import excepciones.AccionNoPermitidaException;
import excepciones.ElementoNoEncontradoException;
import excepciones.EntrenamientoException;
import excepciones.FueraDeTurnoException;
import excepciones.InstalacionNoDisponibleException;
import excepciones.ValorInvalidoException;
import instalaciones.Instalacion;
import jugadores.Jugador;
import reservas.Reserva;
import usuarios.Entrenador;
import usuarios.Usuario;

public class ControladorEntrenador extends ControladorEmpleado {

	public ControladorEntrenador(CentralClubSeneca central, Usuario usuario) {
		super(central, usuario);
	}

	public void crearSesionEntrenamiento(String tipo, LocalDate fecha, int horaInicio, int duracion, String idInstalacion,
			String[] convocados) throws FueraDeTurnoException, ElementoNoEncontradoException, InstalacionNoDisponibleException,
			ValorInvalidoException {
		boolean esEquipo = tipo.equalsIgnoreCase("equipo");
		if (!esEquipo && !tipo.equalsIgnoreCase("raqueta")) {
			throw new ValorInvalidoException("El tipo de sesión debe ser equipo o raqueta");
		}
		int numeroHoras = (int) Math.ceil(duracion / 60.0);
		if (duracion <= 0 || horaInicio < 0 || horaInicio + numeroHoras > 24) {
			throw new ValorInvalidoException("La hora de inicio y la duración no son válidas");
		}
		int[] horas = new int[numeroHoras];
		Entrenador entrenador = (Entrenador) usuario;
		for (int i = 0; i < numeroHoras; i++) {
			horas[i] = horaInicio + i;
			if (!entrenador.getTurno(fecha)[horas[i]]) {
				throw new FueraDeTurnoException("La hora " + horas[i] + " está fuera del turno del entrenador");
			}
		}
		Instalacion instalacion = central.getInstalaciones().getInstalacion(idInstalacion);
		if (instalacion == null) {
			throw new ElementoNoEncontradoException("No existe la instalación " + idInstalacion);
		}
		if (!instalacion.estaDisponible(fecha, horas)) {
			throw new InstalacionNoDisponibleException("La instalación no está disponible en ese horario");
		}
		if (esEquipo && (convocados.length != 1 || central.getEquipos().getEquipo(convocados[0]) == null)) {
			throw new ElementoNoEncontradoException("Debe convocar un equipo existente");
		}
		Reserva reserva = central.getReservas().crearReserva(instalacion, fecha, horas, usuario.getUsername());
		if (esEquipo) {
			central.getSesionesEntrenamiento().crearSesionEquipo(entrenador, fecha, horaInicio, duracion, reserva, convocados[0]);
		} else {
			central.getSesionesEntrenamiento().crearSesionRaqueta(entrenador, fecha, horaInicio, duracion, reserva, convocados);
		}
	}

	public void registrarAsistencia(String idSesion, String idJugador, String asistio)
			throws ElementoNoEncontradoException, AccionNoPermitidaException, EntrenamientoException {
		verificarSesionPropia(idSesion);
		Jugador jugador = central.getJugadores().getJugadorPorId(idJugador);
		if (jugador == null) {
			throw new ElementoNoEncontradoException("No existe el jugador " + idJugador);
		}
		central.getSesionesEntrenamiento().registrarAsistencia(idSesion, jugador, asistio);
	}

	public void registrarEvaluacion(String idSesion, String idJugador, int evaluacion, String comentario)
			throws ElementoNoEncontradoException, AccionNoPermitidaException, EntrenamientoException {
		verificarSesionPropia(idSesion);
		central.getSesionesEntrenamiento().registrarEvaluacion(idSesion, idJugador, evaluacion, comentario);
	}

	private void verificarSesionPropia(String idSesion) throws ElementoNoEncontradoException, AccionNoPermitidaException {
		SesionEntrenamiento sesion = central.getSesionesEntrenamiento().getSesion(idSesion);
		if (sesion == null) {
			throw new ElementoNoEncontradoException("No existe la sesión " + idSesion);
		}
		if (!sesion.getEntrenadorUsername().equals(usuario.getUsername())) {
			throw new AccionNoPermitidaException("La sesión " + idSesion + " es de otro entrenador");
		}
	}

	@Override
	public String getRol() {
		return "Entrenador";
	}
}
