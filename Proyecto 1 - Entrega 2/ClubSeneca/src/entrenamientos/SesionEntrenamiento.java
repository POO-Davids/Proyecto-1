package entrenamientos;

import java.time.LocalDate;
import java.util.LinkedHashMap;

import jugadores.Jugador;
import reservas.Reserva;

public abstract class SesionEntrenamiento {

	protected String idSesion;
	protected LocalDate fecha;
	protected int horaInicio;
	protected int duracion;
	protected String entrenadorUsername;
	protected LinkedHashMap<String, AsistenciaJugador> asistencias;
	protected Reserva reserva;

	public SesionEntrenamiento(String idSesion, LocalDate fecha, int horaInicio, int duracion, String entrenadorUsername,
			Reserva reserva) {
		this.idSesion = idSesion;
		this.fecha = fecha;
		this.horaInicio = horaInicio;
		this.duracion = duracion;
		this.entrenadorUsername = entrenadorUsername;
		this.reserva = reserva;
		this.asistencias = new LinkedHashMap<>();
	}

	public String getIdSesion() {
		return idSesion;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public int getHoraInicio() {
		return horaInicio;
	}

	public int getDuracion() {
		return duracion;
	}

	public String getEntrenadorUsername() {
		return entrenadorUsername;
	}

	public Reserva getReserva() {
		return reserva;
	}

	public abstract Boolean esConvocado(String idJugador);

	public Boolean esConvocado(Jugador jugador) {
		return esConvocado(jugador.getIdJugador());
	}

	public void agregarAsistencia(AsistenciaJugador asistencia) {
		asistencias.put(asistencia.getJugadorId(), asistencia);
	}

	public AsistenciaJugador getAsistencia(String idJugador) {
		return asistencias.get(idJugador);
	}

	public LinkedHashMap<String, AsistenciaJugador> getAsistencias() {
		return asistencias;
	}

	public String getResumen() {
		return getResumen("");
	}

	protected String getResumen(String convocados) {
		StringBuilder texto = new StringBuilder(fecha + " " + horaInicio + ":00 | Sesión " + idSesion + convocados + " | entrenador: "
				+ entrenadorUsername + " | instalación: " + reserva.getInstalacion().getInstalacionId() + " ("
				+ reserva.getInstalacion().getTipo() + ") | duración: " + duracion + " min");
		for (AsistenciaJugador asistencia : asistencias.values()) {
			texto.append("\n    ").append(asistencia);
		}
		return texto.toString();
	}
}
