package entrenamientos;

import java.time.LocalDate;
import java.util.Arrays;

import reservas.Reserva;

public class SesionEntrenamientoIndividual extends SesionEntrenamiento {

	private String[] jugadoresConvocados;

	public SesionEntrenamientoIndividual(String idSesion, LocalDate fecha, int horaInicio, int duracion, String entrenadorUsername,
			Reserva reserva, String[] jugadoresConvocados) {
		super(idSesion, fecha, horaInicio, duracion, entrenadorUsername, reserva);
		this.jugadoresConvocados = jugadoresConvocados;
	}

	public String[] getJugadoresConvocados() {
		return jugadoresConvocados;
	}

	@Override
	public Boolean esConvocado(String idJugador) {
		return Arrays.asList(jugadoresConvocados).contains(idJugador);
	}

	@Override
	public String getResumen() {
		return getResumen(" | convocados: " + String.join(", ", jugadoresConvocados));
	}
}
