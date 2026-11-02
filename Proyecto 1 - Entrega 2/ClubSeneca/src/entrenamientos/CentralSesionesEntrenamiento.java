package entrenamientos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import excepciones.ElementoNoEncontradoException;
import excepciones.EstadoAsistenciaInvalidoException;
import excepciones.EvaluacionInvalidaException;
import excepciones.JugadorNoConvocadoException;
import excepciones.SesionIncompletaException;
import jugadores.Jugador;
import reservas.Reserva;
import usuarios.Entrenador;

public class CentralSesionesEntrenamiento {

	private static final Map<String, String> ESTADOS = Map.of("asistió", "asistió", "asistio", "asistió", "llegó tarde",
			"llegó tarde", "llego tarde", "llegó tarde", "faltó", "faltó", "falto", "faltó");

	private LinkedHashMap<String, SesionEntrenamiento> sesiones;
	private int contador;

	public CentralSesionesEntrenamiento() {
		sesiones = new LinkedHashMap<>();
	}

	public SesionEntrenamiento crearSesionEquipo(Entrenador entrenador, LocalDate fecha, int horaInicio, int duracion,
			Reserva reserva, String idEquipo) {
		SesionEntrenamiento sesion = new SesionEntrenamientoConjunto(generarId(), fecha, horaInicio, duracion,
				entrenador.getUsername(), reserva, idEquipo);
		sesiones.put(sesion.getIdSesion(), sesion);
		return sesion;
	}

	public SesionEntrenamiento crearSesionRaqueta(Entrenador entrenador, LocalDate fecha, int horaInicio, int duracion,
			Reserva reserva, String[] jugadores) {
		SesionEntrenamiento sesion = new SesionEntrenamientoIndividual(generarId(), fecha, horaInicio, duracion,
				entrenador.getUsername(), reserva, jugadores);
		sesiones.put(sesion.getIdSesion(), sesion);
		return sesion;
	}

	public void agregarSesion(SesionEntrenamiento sesion) {
		sesiones.put(sesion.getIdSesion(), sesion);
	}

	public SesionEntrenamiento getSesion(String idSesion) {
		return sesiones.get(idSesion);
	}

	public List<SesionEntrenamiento> getSesiones() {
		return new ArrayList<>(sesiones.values());
	}

	public List<SesionEntrenamiento> getSesionesEntre(LocalDate inicio, LocalDate fin) {
		List<SesionEntrenamiento> lista = new ArrayList<>();
		for (SesionEntrenamiento sesion : sesiones.values()) {
			if (!sesion.getFecha().isBefore(inicio) && !sesion.getFecha().isAfter(fin)) {
				lista.add(sesion);
			}
		}
		lista.sort(Comparator.comparing(SesionEntrenamiento::getFecha).thenComparingInt(SesionEntrenamiento::getHoraInicio));
		return lista;
	}

	public void registrarAsistencia(String idSesion, Jugador jugador, String asistio)
			throws ElementoNoEncontradoException, JugadorNoConvocadoException, EstadoAsistenciaInvalidoException {
		SesionEntrenamiento sesion = buscarSesion(idSesion);
		if (jugador == null || !sesion.esConvocado(jugador)) {
			throw new JugadorNoConvocadoException("El jugador no fue convocado a la sesión " + idSesion);
		}
		String estado = ESTADOS.get(asistio.toLowerCase());
		if (estado == null) {
			throw new EstadoAsistenciaInvalidoException("La asistencia debe ser asistió, llegó tarde o faltó");
		}
		AsistenciaJugador existente = sesion.getAsistencia(jugador.getIdJugador());
		if (existente != null) {
			existente.setAsistio(estado);
			return;
		}
		AsistenciaJugador asistencia = new AsistenciaJugador(jugador.getIdJugador(), estado, sesion);
		sesion.agregarAsistencia(asistencia);
		jugador.agregarAsistencia(asistencia);
	}

	public void registrarEvaluacion(String idSesion, String idJugador, int evaluacion, String comentario)
			throws ElementoNoEncontradoException, SesionIncompletaException, EvaluacionInvalidaException {
		AsistenciaJugador asistencia = buscarSesion(idSesion).getAsistencia(idJugador);
		if (asistencia == null || asistencia.getAsistio().equals("faltó")) {
			throw new SesionIncompletaException("Solo se evalúa a jugadores que asistieron a la sesión");
		}
		if (evaluacion < 1 || evaluacion > 5) {
			throw new EvaluacionInvalidaException("La evaluación debe estar entre 1 y 5");
		}
		asistencia.setEvaluacion(evaluacion);
		asistencia.setComentario(comentario);
	}

	public void cancelarSesionPorReserva(String idReserva) {
		sesiones.values().removeIf(sesion -> sesion.getReserva().getIdReserva().equals(idReserva));
	}

	private SesionEntrenamiento buscarSesion(String idSesion) throws ElementoNoEncontradoException {
		SesionEntrenamiento sesion = getSesion(idSesion);
		if (sesion == null) {
			throw new ElementoNoEncontradoException("No existe la sesión " + idSesion);
		}
		return sesion;
	}

	private String generarId() {
		String id;
		do {
			id = "SES-" + (++contador);
		} while (sesiones.containsKey(id));
		return id;
	}
}
