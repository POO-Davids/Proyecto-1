package entrenamientos;

import java.time.LocalDate;

import jugadores.Jugador;
import jugadores.JugadorConjunto;
import reservas.Reserva;

public class SesionEntrenamientoConjunto extends SesionEntrenamiento {

	private String equipoConvocadoId;

	public SesionEntrenamientoConjunto(String idSesion, LocalDate fecha, int horaInicio, int duracion, String entrenadorUsername,
			Reserva reserva, String equipoConvocadoId) {
		super(idSesion, fecha, horaInicio, duracion, entrenadorUsername, reserva);
		this.equipoConvocadoId = equipoConvocadoId;
	}

	public String getEquipoConvocadoId() {
		return equipoConvocadoId;
	}

	@Override
	public Boolean esConvocado(String idJugador) {
		return asistencias.containsKey(idJugador);
	}

	@Override
	public Boolean esConvocado(Jugador jugador) {
		return jugador instanceof JugadorConjunto conjunto && equipoConvocadoId.equals(conjunto.getEquipoId());
	}

	@Override
	public String getResumen() {
		return getResumen(" | equipo convocado: " + equipoConvocadoId);
	}
}
